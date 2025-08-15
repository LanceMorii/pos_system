package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.entity.Order;
import com.example.web_system.entity.OrderItem;
import com.example.web_system.entity.Product;
import com.example.web_system.repository.OrderItemRepository;
import com.example.web_system.repository.OrderRepository;
import com.example.web_system.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/cashier")
@CrossOrigin(origins = "*")
public class CashierController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CashierController(ProductRepository productRepository,
                             OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "OK");
        data.put("timestamp", System.currentTimeMillis());
        data.put("message", "Cashier service is running");
        return ApiResponse.success(data);
    }

    @GetMapping("/products")
    public ApiResponse<List<Product>> listProducts(@RequestParam(value = "name", required = false) String name) {
        List<Product> list = productRepository.findProductsWithFilters(
                name, null, Product.Status.ACTIVE
        );
        return ApiResponse.success(list);
    }

    public record CheckoutItem(Long productId, Integer qty, BigDecimal price) {}
    public record CheckoutRequest(List<CheckoutItem> items, String paymentMethod, String remark, String customerName) {}

    @PostMapping("/checkout")
    @Transactional
    public ApiResponse<Map<String, Object>> checkout(@RequestBody CheckoutRequest req) {
        if (req.items() == null || req.items().isEmpty()) {
            return ApiResponse.error("购物车为空");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(genOrderNo());
        order.setCashierId(0L); // 无登录上下文时占位
        order.setCashierName("SYSTEM");
        // 会员/顾客名称
        String customerName = req.customerName();
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = "客户无会员信息";
        }
        order.setCustomerName(customerName);
        order.setStatus(Order.Status.COMPLETED);

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        List<OrderItem> toSaveItems = new ArrayList<>();

        for (CheckoutItem ci : req.items()) {
            Product p = productRepository.findById(ci.productId())
                    .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + ci.productId()));
            if (p.getCurrentStock() < ci.qty()) {
                throw new IllegalArgumentException("库存不足: " + p.getName());
            }

            BigDecimal unit = ci.price() != null ? ci.price() : (p.getSalePrice() == null ? BigDecimal.ZERO : p.getSalePrice());
            BigDecimal subtotal = unit.multiply(BigDecimal.valueOf(ci.qty()));
            total = total.add(subtotal);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(p.getId());
            item.setProductName(p.getName());
            item.setProductCode(p.getCode());
            item.setUnitPrice(unit);
            item.setQuantity(ci.qty());
            item.setSubtotal(subtotal);
            toSaveItems.add(item);

            // 扣减库存
            p.setCurrentStock(p.getCurrentStock() - ci.qty());
            productRepository.save(p);
        }

        order.setTotalAmount(total);
        order.setDiscountAmount(discount);
        order.setFinalAmount(total.subtract(discount));

        // 支付方式
        Order.PaymentMethod pm = Order.PaymentMethod.CASH;
        try {
            if (req.paymentMethod() != null) {
                pm = Order.PaymentMethod.valueOf(req.paymentMethod());
            }
        } catch (IllegalArgumentException ignored) {}
        order.setPaymentMethod(pm);
        order.setRemark(req.remark());

        // 保存订单与明细
        Order saved = orderRepository.save(order);
        for (OrderItem it : toSaveItems) {
            it.setOrder(saved);
        }
        orderItemRepository.saveAll(toSaveItems);

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", saved.getId());
        data.put("orderNo", saved.getOrderNo());
        data.put("finalAmount", saved.getFinalAmount());
        data.put("items", toSaveItems.size());
        return ApiResponse.success("结算成功", data);
    }

    private String genOrderNo() {
        return "POS" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + (int)(Math.random() * 900 + 100);
    }
}
