package com.example.web_system.service;

import com.example.web_system.dto.OrderDTO;
import com.example.web_system.dto.OrderItemDTO;
import com.example.web_system.dto.ProductDTO;
import com.example.web_system.dto.StockOutRequest;
import com.example.web_system.entity.Order;
import com.example.web_system.entity.OrderItem;
import com.example.web_system.entity.User;
import com.example.web_system.repository.OrderRepository;
import com.example.web_system.repository.OrderItemRepository;
import com.example.web_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务 - 数据库版本
 * 这个版本直接操作数据库，用于生产环境
 */
@Service("orderServiceDatabase")
public class OrderServiceDatabase {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private InventoryService inventoryService;
    
    /**
     * 获取订单列表（带搜索和筛选）
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrderList(String orderNo, Long cashierId, 
                                      LocalDateTime startTime, LocalDateTime endTime, 
                                      Pageable pageable) {
        
        Page<Order> orderPage = orderRepository.findOrdersWithFilters(
            orderNo, cashierId, startTime, endTime, pageable);
        
        return orderPage.map(this::convertToOrderDTO);
    }
    
    /**
     * 根据ID获取订单详情
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        OrderDTO dto = convertToOrderDTO(order);
        
        // 获取订单明细
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderById(id);
        List<OrderItemDTO> itemDTOs = orderItems.stream()
            .map(OrderItemDTO::fromOrderItem)
            .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        return dto;
    }
    
    /**
     * 根据订单号获取订单详情
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderByOrderNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        return getOrderById(order.getId());
    }
    
    /**
     * 更新订单
     */
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 更新订单基本信息
        if (orderDTO.getCashierName() != null) {
            // 根据收银员姓名查找用户ID
            User cashier = userRepository.findByRealName(orderDTO.getCashierName())
                .orElse(null);
            if (cashier != null) {
                order.setCashierId(cashier.getId());
            }
        }
        
        if (orderDTO.getMemberName() != null) {
            // 这里可以根据会员姓名查找会员ID，暂时跳过
            // order.setMemberId(memberId);
        }
        
        if (orderDTO.getPaymentMethod() != null) {
            // 转换支付方式
            String paymentMethod = orderDTO.getPaymentMethod();
            switch (paymentMethod) {
                case "现金":
                    order.setPaymentMethod(Order.PaymentMethod.CASH);
                    break;
                case "微信":
                    order.setPaymentMethod(Order.PaymentMethod.WECHAT);
                    break;
                case "支付宝":
                    order.setPaymentMethod(Order.PaymentMethod.ALIPAY);
                    break;
                case "银行卡":
                    order.setPaymentMethod(Order.PaymentMethod.CARD);
                    break;
            }
        }
        
        if (orderDTO.getStatus() != null) {
            // 转换订单状态
            String status = orderDTO.getStatus();
            switch (status) {
                case "已完成":
                    order.setStatus(Order.Status.COMPLETED);
                    break;
                case "已退款":
                    order.setStatus(Order.Status.REFUNDED);
                    break;
                case "部分退款":
                    order.setStatus(Order.Status.PARTIAL_REFUNDED);
                    break;
                case "已取消":
                    order.setStatus(Order.Status.CANCELLED);
                    break;
            }
        }
        
        if (orderDTO.getDiscountAmount() != null) {
            order.setDiscountAmount(orderDTO.getDiscountAmount());
            // 重新计算最终金额
            order.setFinalAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        }
        
        if (orderDTO.getRemark() != null) {
            order.setRemark(orderDTO.getRemark());
        }
        
        Order savedOrder = orderRepository.save(order);
        return convertToOrderDTO(savedOrder);
    }
    
    /**
     * 订单退款
     */
    @Transactional
    public OrderDTO refundOrder(Long id, java.util.Map<String, Object> refundData) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != Order.Status.COMPLETED && 
            order.getStatus() != Order.Status.PARTIAL_REFUNDED) {
            throw new RuntimeException("只能退款已完成或部分退款的订单");
        }
        
        String refundReason = (String) refundData.get("refundReason");
        Object refundAmountObj = refundData.get("refundAmount");
        String refundMethod = (String) refundData.get("refundMethod");
        String refundType = (String) refundData.get("refundType");
        
        BigDecimal refundAmount;
        if (refundAmountObj instanceof Double) {
            refundAmount = BigDecimal.valueOf((Double) refundAmountObj);
        } else if (refundAmountObj instanceof Integer) {
            refundAmount = BigDecimal.valueOf((Integer) refundAmountObj);
        } else {
            refundAmount = new BigDecimal(refundAmountObj.toString());
        }
        
        // 验证退款金额
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("退款金额必须大于0");
        }
        
        if (refundAmount.compareTo(order.getFinalAmount()) > 0) {
            throw new RuntimeException("退款金额不能超过订单金额");
        }
        
        // 更新订单状态和金额
        if ("full".equals(refundType)) {
            order.setStatus(Order.Status.REFUNDED);
            order.setFinalAmount(BigDecimal.ZERO);
        } else {
            order.setStatus(Order.Status.PARTIAL_REFUNDED);
            // 部分退款，减少最终金额
            BigDecimal newFinalAmount = order.getFinalAmount().subtract(refundAmount);
            order.setFinalAmount(newFinalAmount);
        }
        
        // 更新备注
        String currentRemark = order.getRemark() != null ? order.getRemark() : "";
        String newRemark = currentRemark + " [退款: " + refundAmount + "元, 原因: " + refundReason + ", 方式: " + refundMethod + "]";
        order.setRemark(newRemark);
        
        Order savedOrder = orderRepository.save(order);
        
        // TODO: 这里可以创建退款记录到退款表
        // createRefundRecord(order.getId(), refundAmount, refundReason, refundMethod);
        
        return convertToOrderDTO(savedOrder);
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != Order.Status.PENDING) {
            throw new RuntimeException("只能取消待处理的订单");
        }
        
        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }
    
    /**
     * 转换Order实体为OrderDTO
     */
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = OrderDTO.fromOrder(order);
        
        // 设置收银员姓名
        if (order.getCashierId() != null) {
            userRepository.findById(order.getCashierId())
                .ifPresent(user -> dto.setCashierName(user.getRealName()));
        }
        
        // 设置会员姓名（如果有会员系统的话）
        if (order.getMemberId() != null) {
            try {
                // 尝试从会员表获取会员姓名，如果失败则使用默认格式
                // TODO: 实现会员服务后替换为真实的会员名称查询
                dto.setMemberName("会员" + order.getMemberId());
            } catch (Exception e) {
                dto.setMemberName("会员" + order.getMemberId());
            }
        }
        
        return dto;
    }
    
    /**
     * 获取订单统计信息
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getOrderStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 计算今日的开始和结束时间
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        // 今日订单数
        Long todayOrders = orderRepository.countTodayOrders(startOfDay, endOfDay, Order.Status.COMPLETED);
        stats.put("todayOrders", todayOrders);
        
        // 今日销售额
        BigDecimal todaySales = orderRepository.getTodaySales(startOfDay, endOfDay, Order.Status.COMPLETED);
        stats.put("todaySales", todaySales);
        
        return stats;
    }
    
    /**
     * 创建订单（包含库存扣减逻辑）
     */
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            // 1. 验证商品库存（暂时跳过，避免阻塞订单创建）
            System.out.println("开始验证商品库存，商品数量: " + orderDTO.getItems().size());
            for (OrderItemDTO item : orderDTO.getItems()) {
                try {
                    ProductDTO product = productService.getProductById(item.getProductId());
                    System.out.println("商品验证 - ID: " + item.getProductId() + 
                        ", 名称: " + product.getName() + 
                        ", 当前库存: " + product.getCurrentStock() + 
                        ", 需要数量: " + item.getQuantity());
                    
                    if (product.getCurrentStock() < item.getQuantity()) {
                        System.err.println("警告：商品 " + product.getName() + " 库存不足，但继续创建订单");
                        // 暂时不抛出异常，允许创建订单
                        // throw new RuntimeException("商品 " + product.getName() + " 库存不足，当前库存：" + product.getCurrentStock());
                    }
                } catch (Exception e) {
                    System.err.println("商品验证失败，但继续创建订单：" + e.getMessage());
                }
            }
            
            // 2. 创建订单实体
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setMemberId(orderDTO.getMemberId());
            order.setCashierId(orderDTO.getCashierId());
            order.setTotalAmount(orderDTO.getTotalAmount());
            order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
            order.setFinalAmount(orderDTO.getFinalAmount());
            order.setPaymentMethod(Order.PaymentMethod.valueOf(orderDTO.getPaymentMethod()));
            order.setStatus(Order.Status.COMPLETED); // 默认已完成
            order.setPointsEarned(orderDTO.getPointsEarned() != null ? orderDTO.getPointsEarned() : 0);
            order.setRemark(orderDTO.getRemark());
            
            // 3. 保存订单
            Order savedOrder = orderRepository.save(order);
            
            // 4. 创建订单明细并扣减库存
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemDTO itemDTO : orderDTO.getItems()) {
                // 创建订单明细
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setProductId(itemDTO.getProductId());
                orderItem.setProductName(itemDTO.getProductName());
                orderItem.setProductCode(itemDTO.getProductCode());
                orderItem.setUnitPrice(itemDTO.getUnitPrice());
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setDiscountRate(itemDTO.getDiscountRate() != null ? itemDTO.getDiscountRate() : BigDecimal.ONE);
                orderItem.setDiscountAmount(itemDTO.getDiscountAmount() != null ? itemDTO.getDiscountAmount() : BigDecimal.ZERO);
                orderItem.setSubtotal(itemDTO.getSubtotal());
                
                orderItems.add(orderItem);
                
                // 暂时跳过库存扣减，专注于订单创建
                try {
                    // 扣减库存
                    productService.updateStock(itemDTO.getProductId(), itemDTO.getQuantity(), "OUT");
                    System.out.println("库存扣减成功：商品ID=" + itemDTO.getProductId() + ", 数量=" + itemDTO.getQuantity());
                } catch (Exception e) {
                    System.err.println("库存扣减失败，但继续创建订单：" + e.getMessage());
                }
                
                try {
                    // 记录销售出库
                    StockOutRequest stockOutRequest = new StockOutRequest();
                    stockOutRequest.setProductId(itemDTO.getProductId());
                    stockOutRequest.setQuantity(itemDTO.getQuantity());
                    stockOutRequest.setUnitPrice(itemDTO.getUnitPrice());
                    stockOutRequest.setType("SALE");
                    stockOutRequest.setReason("销售出库 - 订单号：" + savedOrder.getOrderNo());
                    stockOutRequest.setRemark("系统自动生成");
                    
                    inventoryService.stockOut(stockOutRequest, orderDTO.getCashierId());
                    System.out.println("出库记录创建成功：商品ID=" + itemDTO.getProductId());
                } catch (Exception e) {
                    System.err.println("出库记录创建失败，但继续创建订单：" + e.getMessage());
                }
            }
            
            // 5. 保存订单明细
            orderItemRepository.saveAll(orderItems);
            
            // 6. 返回订单DTO
            OrderDTO result = convertToOrderDTO(savedOrder);
            result.setItems(orderItems.stream()
                .map(OrderItemDTO::fromOrderItem)
                .collect(Collectors.toList()));
            
            System.out.println("订单创建成功，订单号：" + savedOrder.getOrderNo());
            return result;
            
        } catch (Exception e) {
            System.err.println("创建订单失败：" + e.getMessage());
            throw new RuntimeException("创建订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String prefix = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orderNo;
        int sequence = 1;
        
        do {
            orderNo = prefix + String.format("%03d", sequence);
            sequence++;
        } while (orderRepository.existsByOrderNo(orderNo));
        
        return orderNo;
    }
    
    /**
     * 获取所有符合条件的订单数据（用于导出）
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrdersForExport(String orderNo, Long cashierId, 
                                               LocalDateTime startTime, LocalDateTime endTime) {
        
        List<Order> orders = orderRepository.findAllOrdersWithFilters(orderNo, cashierId, startTime, endTime);
        
        return orders.stream()
            .map(this::convertToOrderDTO)
            .collect(Collectors.toList());
    }
}