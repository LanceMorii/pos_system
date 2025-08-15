package com.example.web_system.service;

import com.example.web_system.dto.OrderDTO;
import com.example.web_system.dto.OrderItemDTO;
import com.example.web_system.entity.Order;
import com.example.web_system.entity.OrderItem;
import com.example.web_system.entity.Product;
import com.example.web_system.repository.OrderRepository;
import com.example.web_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            System.out.println("=== OrderService.createOrder 开始执行 ===");
            System.out.println("接收到的OrderDTO: " + orderDTO);
            
            // 验证订单数据
            if (orderDTO == null) {
                throw new RuntimeException("订单数据不能为空");
            }
            
            if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
                throw new RuntimeException("订单商品不能为空");
            }
            
            System.out.println("订单数据验证通过，开始创建订单实体...");
            
            // 创建订单实体
            Order order = new Order();
            String orderNo = generateOrderNo();
            System.out.println("生成的订单号: " + orderNo);
            
            order.setOrderNo(orderNo);
            order.setCustomerName(orderDTO.getCustomerName() != null ? orderDTO.getCustomerName() : "");
            order.setCashierName(orderDTO.getCashierName() != null ? orderDTO.getCashierName() : "收银员");
            order.setMemberId(orderDTO.getMemberId()); // 可以为null
            order.setTotalAmount(orderDTO.getTotalAmount());
            order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
            order.setFinalAmount(orderDTO.getFinalAmount());
            order.setCashierId(orderDTO.getCashierId() != null ? orderDTO.getCashierId() : 1L); // 设置默认收银员ID
            
            // 转换支付方式字符串为枚举
            if (orderDTO.getPaymentMethod() != null) {
                try {
                    order.setPaymentMethod(Order.PaymentMethod.valueOf(orderDTO.getPaymentMethod()));
                } catch (IllegalArgumentException e) {
                    order.setPaymentMethod(Order.PaymentMethod.CASH); // 默认现金
                }
            } else {
                order.setPaymentMethod(Order.PaymentMethod.CASH);
            }
            
            // 转换状态字符串为枚举
            if (orderDTO.getStatus() != null) {
                try {
                    order.setStatus(Order.Status.valueOf(orderDTO.getStatus()));
                } catch (IllegalArgumentException e) {
                    order.setStatus(Order.Status.COMPLETED); // 默认已完成
                }
            } else {
                order.setStatus(Order.Status.COMPLETED);
            }
            
            order.setRemark(orderDTO.getRemark());
            order.setCashierId(orderDTO.getCashierId());
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            
            System.out.println("创建的Order实体: " + order);
            
            // 保存订单
            Order savedOrder = orderRepository.save(order);
            System.out.println("保存后的Order: " + savedOrder);
            
            // 创建订单明细
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemDTO itemDTO : orderDTO.getItems()) {
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
                orderItem.setCreatedAt(LocalDateTime.now());
                
                orderItems.add(orderItem);
                
                // 更新商品库存
                Optional<Product> productOpt = productRepository.findById(itemDTO.getProductId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    int newStock = product.getCurrentStock() - itemDTO.getQuantity();
                    if (newStock < 0) {
                        throw new RuntimeException("商品 " + product.getName() + " 库存不足");
                    }
                    product.setCurrentStock(newStock);
                    productRepository.save(product);
                    System.out.println("更新商品库存: " + product.getName() + " 剩余: " + newStock);
                }
            }
            
            savedOrder.setItems(orderItems);
            
            // 如果是会员订单，计算并更新积分
            if (savedOrder.getMemberId() != null) {
                try {
                    int earnedPoints = calculatePoints(savedOrder.getFinalAmount());
                    savedOrder.setPointsEarned(earnedPoints);
                    
                    // 更新会员积分（这里需要调用MemberService，暂时跳过具体实现）
                    System.out.println("会员订单，应获得积分: " + earnedPoints);
                    
                    // 重新保存订单以更新积分信息
                    savedOrder = orderRepository.save(savedOrder);
                } catch (Exception e) {
                    System.err.println("计算积分失败: " + e.getMessage());
                    // 积分计算失败不影响订单创建
                }
            }
            
            // 转换为DTO返回
            OrderDTO result = convertToDTO(savedOrder);
            System.out.println("OrderService.createOrder 执行完成，返回: " + result);
            return result;
            
        } catch (Exception e) {
            System.err.println("OrderService.createOrder 异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售订单列表（支持搜索和筛选）
     */
    public Page<OrderDTO> getSalesOrders(String keyword, String status, 
                                       LocalDateTime startDate, LocalDateTime endDate, 
                                       Pageable pageable) {
        
        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索（订单号或客户姓名）
            if (keyword != null && !keyword.trim().isEmpty()) {
                Predicate orderNoPredicate = criteriaBuilder.like(
                    root.get("orderNo"), "%" + keyword + "%");
                Predicate customerNamePredicate = criteriaBuilder.like(
                    root.get("customerName"), "%" + keyword + "%");
                predicates.add(criteriaBuilder.or(orderNoPredicate, customerNamePredicate));
            }
            
            // 状态筛选
            if (status != null && !status.trim().isEmpty()) {
                try {
                    Order.Status statusEnum = Order.Status.valueOf(status);
                    predicates.add(criteriaBuilder.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的状态值
                }
            }
            
            // 日期范围筛选
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        return orderPage.map(this::convertToDTO);
    }

    /**
     * 根据ID获取订单详情
     */
    public OrderDTO getOrderById(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            return convertToDTO(orderOpt.get());
        } else {
            throw new RuntimeException("订单不存在");
        }
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus() != Order.Status.COMPLETED) {
                throw new RuntimeException("只能取消已完成的订单");
            }
            
            order.setStatus(Order.Status.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            // 恢复库存
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Optional<Product> productOpt = productRepository.findById(item.getProductId());
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        product.setCurrentStock(product.getCurrentStock() + item.getQuantity());
                        productRepository.save(product);
                    }
                }
            }
        } else {
            throw new RuntimeException("订单不存在");
        }
    }

    /**
     * 获取订单列表（支持搜索和筛选）
     */
    public List<OrderDTO> getOrderList(String keyword, String status, 
                                     LocalDateTime startTime, LocalDateTime endTime) {
        
        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索（订单号或客户姓名）
            if (keyword != null && !keyword.trim().isEmpty()) {
                Predicate orderNoPredicate = criteriaBuilder.like(root.get("orderNo"), "%" + keyword + "%");
                Predicate customerNamePredicate = criteriaBuilder.like(root.get("customerName"), "%" + keyword + "%");
                predicates.add(criteriaBuilder.or(orderNoPredicate, customerNamePredicate));
            }
            
            // 状态筛选
            if (status != null && !status.trim().isEmpty()) {
                try {
                    Order.Status orderStatus = Order.Status.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), orderStatus));
                } catch (IllegalArgumentException e) {
                    // 忽略无效状态
                }
            }
            
            // 时间范围筛选
            if (startTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTime));
            }
            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTime));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        List<Order> orders = orderRepository.findAll(spec);
        return orders.stream().map(this::convertToDTO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取订单列表（支持搜索和筛选，带分页）
     */
    public Page<OrderDTO> getOrderList(String orderNo, String cashier, 
                                     LocalDateTime startTime, LocalDateTime endTime, 
                                     Pageable pageable) {
        
        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 订单号搜索
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + orderNo + "%"));
            }
            
            // 收银员搜索
            if (cashier != null && !cashier.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cashierName"), "%" + cashier + "%"));
            }
            
            // 时间范围筛选
            if (startTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTime));
            }
            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTime));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        return orderPage.map(this::convertToDTO);
    }

    /**
     * 根据订单号获取订单详情
     */
    public OrderDTO getOrderByOrderNo(String orderNo) {
        Optional<Order> orderOpt = orderRepository.findByOrderNo(orderNo);
        if (orderOpt.isPresent()) {
            return convertToDTO(orderOpt.get());
        } else {
            throw new RuntimeException("订单不存在");
        }
    }

    /**
     * 更新订单
     */
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            
            // 更新基本信息
            if (orderDTO.getCustomerName() != null) {
                order.setCustomerName(orderDTO.getCustomerName());
            }
            if (orderDTO.getRemark() != null) {
                order.setRemark(orderDTO.getRemark());
            }
            if (orderDTO.getStatus() != null) {
                try {
                    order.setStatus(Order.Status.valueOf(orderDTO.getStatus()));
                } catch (IllegalArgumentException e) {
                    // 忽略无效状态
                }
            }
            
            order.setUpdatedAt(LocalDateTime.now());
            Order savedOrder = orderRepository.save(order);
            return convertToDTO(savedOrder);
        } else {
            throw new RuntimeException("订单不存在");
        }
    }

    /**
     * 订单退款
     */
    @Transactional
    public OrderDTO refundOrder(Long id, Map<String, Object> request) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            
            if (order.getStatus() != Order.Status.COMPLETED) {
                throw new RuntimeException("只能退款已完成的订单");
            }
            
            // 设置为退款状态
            order.setStatus(Order.Status.REFUNDED);
            order.setUpdatedAt(LocalDateTime.now());
            
            // 恢复库存
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Optional<Product> productOpt = productRepository.findById(item.getProductId());
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        product.setCurrentStock(product.getCurrentStock() + item.getQuantity());
                        productRepository.save(product);
                    }
                }
            }
            
            Order savedOrder = orderRepository.save(order);
            return convertToDTO(savedOrder);
        } else {
            throw new RuntimeException("订单不存在");
        }
    }

    /**
     * 删除订单（仅允许删除已取消或已退款）
     */
    @Transactional
    public void deleteOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("订单不存在");
        }

        Order order = orderOpt.get();
        Order.Status status = order.getStatus();
        if (!(status == Order.Status.CANCELLED || status == Order.Status.REFUNDED)) {
            throw new RuntimeException("仅允许删除已取消或已退款的订单");
        }

        // 由于在实体上对 items 配置了 CascadeType.ALL，删除订单会级联删除明细
        orderRepository.delete(order);
    }

    /**
     * 获取订单统计信息
     */
    public Map<String, Object> getOrderStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        Map<String, Object> stats = new HashMap<>();
        
        // 今日订单统计
        Long todayOrderCount = orderRepository.countTodayOrders(startOfDay, endOfDay, Order.Status.COMPLETED);
        BigDecimal todaySales = orderRepository.getTodaySales(startOfDay, endOfDay, Order.Status.COMPLETED);
        
        stats.put("todayOrderCount", todayOrderCount);
        stats.put("todaySales", todaySales);
        
        // 总订单统计
        long totalOrders = orderRepository.count();
        stats.put("totalOrders", totalOrders);
        
        return stats;
    }

    /**
     * 导出订单数据
     */
    public void exportOrderData(String orderNo, String cashier, 
                              LocalDateTime startTime, LocalDateTime endTime,
                              jakarta.servlet.ServletOutputStream outputStream) {
        try {
            // 构建查询条件
            Specification<Order> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                if (orderNo != null && !orderNo.trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("orderNo"), "%" + orderNo + "%"));
                }
                
                if (cashier != null && !cashier.trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("cashierName"), "%" + cashier + "%"));
                }
                
                if (startTime != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTime));
                }
                if (endTime != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTime));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            
            List<Order> orders = orderRepository.findAll(spec);
            
            // 写入CSV头部
            String header = "订单号,客户姓名,收银员,总金额,折扣金额,实付金额,支付方式,状态,创建时间\n";
            outputStream.write(header.getBytes("UTF-8"));
            
            // 写入数据
            for (Order order : orders) {
                StringBuilder line = new StringBuilder();
                line.append(order.getOrderNo()).append(",");
                line.append(order.getCustomerName() != null ? order.getCustomerName() : "").append(",");
                line.append(order.getCashierName() != null ? order.getCashierName() : "").append(",");
                line.append(order.getTotalAmount()).append(",");
                line.append(order.getDiscountAmount()).append(",");
                line.append(order.getFinalAmount()).append(",");
                line.append(order.getPaymentMethod().name()).append(",");
                line.append(order.getStatus().name()).append(",");
                line.append(order.getCreatedAt().toString()).append("\n");
                
                outputStream.write(line.toString().getBytes("UTF-8"));
            }
            
            outputStream.flush();
            
        } catch (Exception e) {
            throw new RuntimeException("导出数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售统计
     */
    public Map<String, Object> getSalesStats(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只统计已完成的订单
            predicates.add(criteriaBuilder.equal(root.get("status"), Order.Status.COMPLETED));
            
            // 日期范围
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        List<Order> orders = orderRepository.findAll(spec);
        
        // 计算统计数据
        BigDecimal totalSales = orders.stream()
            .map(Order::getFinalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalOrders = orders.size();
        
        int totalItems = orders.stream()
            .mapToInt(order -> order.getItems() != null ? order.getItems().size() : 0)
            .sum();
        
        BigDecimal avgOrderAmount = totalOrders > 0 ? 
            totalSales.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        stats.put("totalSales", totalSales);
        stats.put("totalOrders", totalOrders);
        stats.put("totalItems", totalItems);
        stats.put("avgOrderAmount", avgOrderAmount);
        
        return stats;
    }
    
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }
    
    /**
     * 计算订单积分
     * 规则：每消费1元获得1积分
     */
    private int calculatePoints(BigDecimal finalAmount) {
        if (finalAmount == null) {
            return 0;
        }
        // 每消费1元获得1积分，向下取整
        return finalAmount.intValue();
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setCustomerName(order.getCustomerName());
        dto.setCashierName(order.getCashierName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setPaymentMethod(order.getPaymentMethod().name());
        dto.setStatus(order.getStatus().name());
        dto.setRemark(order.getRemark());
        dto.setCashierId(order.getCashierId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        if (order.getItems() != null) {
            List<OrderItemDTO> itemDTOs = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setProductId(item.getProductId());
                itemDTO.setProductName(item.getProductName());
                itemDTO.setProductCode(item.getProductCode());
                itemDTO.setUnitPrice(item.getUnitPrice());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setDiscountRate(item.getDiscountRate());
                itemDTO.setDiscountAmount(item.getDiscountAmount());
                itemDTO.setSubtotal(item.getSubtotal());
                itemDTOs.add(itemDTO);
            }
            dto.setItems(itemDTOs);
            dto.setItemCount(itemDTOs.size());
        }
        
        return dto;
    }
}