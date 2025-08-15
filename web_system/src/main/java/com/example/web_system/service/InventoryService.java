package com.example.web_system.service;

import com.example.web_system.dto.*;
import com.example.web_system.entity.*;
import com.example.web_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private StockInRecordRepository stockInRecordRepository;
    
    @Autowired
    private StockOutRecordRepository stockOutRecordRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取库存列表（带搜索和筛选）
     */
    public Page<ProductDTO> getInventoryList(String name, String stockStatus, Pageable pageable) {
        Product.Status status = Product.Status.ACTIVE; // 只显示活跃商品
        
        Page<Product> productPage = productRepository.findProductsWithFilters(name, null, status, pageable);
        
        return productPage.map(product -> {
            ProductDTO dto = ProductDTO.fromProduct(product);
            // 设置分类名称
            if (product.getCategoryId() != null) {
                categoryRepository.findById(product.getCategoryId())
                    .ifPresent(category -> dto.setCategoryName(category.getName()));
            }
            return dto;
        });
    }
    
    /**
     * 获取库存预警商品列表
     */
    public List<ProductDTO> getLowStockProducts() {
        List<Product> products = productRepository.findLowStockProducts();
        
        return products.stream().map(product -> {
            ProductDTO dto = ProductDTO.fromProduct(product);
            // 设置分类名称
            if (product.getCategoryId() != null) {
                categoryRepository.findById(product.getCategoryId())
                    .ifPresent(category -> dto.setCategoryName(category.getName()));
            }
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 商品入库
     */
    public StockRecordDTO stockIn(StockInRequest request, Long operatorId) {
        // 验证商品是否存在
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 记录入库前库存
        int beforeStock = product.getCurrentStock();
        int afterStock = beforeStock + request.getQuantity();
        
        // 生成入库单号
        String recordNo = generateStockInRecordNo();
        
        // 计算总金额
        BigDecimal totalAmount = request.getUnitPrice().multiply(new BigDecimal(request.getQuantity()));
        
        // 创建入库记录
        StockInRecord record = new StockInRecord();
        record.setRecordNo(recordNo);
        record.setProductId(request.getProductId());
        record.setSupplierId(request.getSupplierId());
        record.setQuantity(request.getQuantity());
        record.setUnitPrice(request.getUnitPrice());
        record.setTotalAmount(totalAmount);
        record.setType(StockInRecord.Type.valueOf(request.getType()));
        record.setOperatorId(operatorId);
        record.setRemark(request.getRemark());
        
        // 保存入库记录
        StockInRecord savedRecord = stockInRecordRepository.save(record);
        
        // 更新商品库存
        product.setCurrentStock(afterStock);
        productRepository.save(product);
        
        // 返回记录DTO，包含入库前后库存信息
        return convertToStockRecordDTO(savedRecord, product, "IN", beforeStock, afterStock);
    }
    
    /**
     * 商品出库
     */
    public StockRecordDTO stockOut(StockOutRequest request, Long operatorId) {
        // 验证商品是否存在
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 检查库存是否足够
        if (product.getCurrentStock() < request.getQuantity()) {
            throw new RuntimeException("库存不足，当前库存：" + product.getCurrentStock());
        }
        
        // 记录出库前库存
        int beforeStock = product.getCurrentStock();
        int afterStock = beforeStock - request.getQuantity();
        
        // 生成出库单号
        String recordNo = generateStockOutRecordNo();
        
        // 计算总金额
        BigDecimal totalAmount = request.getUnitPrice().multiply(new BigDecimal(request.getQuantity()));
        
        // 创建出库记录
        StockOutRecord record = new StockOutRecord();
        record.setRecordNo(recordNo);
        record.setProductId(request.getProductId());
        record.setQuantity(request.getQuantity());
        record.setUnitPrice(request.getUnitPrice());
        record.setTotalAmount(totalAmount);
        record.setType(StockOutRecord.Type.valueOf(request.getType()));
        record.setReason(request.getReason());
        record.setOperatorId(operatorId);
        record.setRemark(request.getRemark());
        
        // 保存出库记录
        StockOutRecord savedRecord = stockOutRecordRepository.save(record);
        
        // 更新商品库存
        product.setCurrentStock(afterStock);
        productRepository.save(product);
        
        // 返回记录DTO，包含出库前后库存信息
        return convertToStockRecordDTO(savedRecord, product, "OUT", beforeStock, afterStock);
    }
    
    /**
     * 获取商品库存记录
     */
    public Page<StockRecordDTO> getStockRecords(Long productId, String type, 
                                               LocalDateTime startTime, LocalDateTime endTime, 
                                               Pageable pageable) {
        List<StockRecordDTO> allRecords = new ArrayList<>();
        
        // 获取商品当前信息
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 获取入库记录
        if (type == null || "IN".equals(type)) {
            List<StockInRecord> inRecords;
            if (startTime != null && endTime != null) {
                inRecords = stockInRecordRepository.findByProductIdAndCreatedAtBetween(
                    productId, startTime, endTime, Pageable.unpaged()).getContent();
            } else {
                inRecords = stockInRecordRepository.findByProductId(productId);
            }
            
            for (StockInRecord record : inRecords) {
                allRecords.add(convertToStockRecordDTO(record, product, "IN"));
            }
        }
        
        // 获取出库记录
        if (type == null || "OUT".equals(type)) {
            List<StockOutRecord> outRecords;
            if (startTime != null && endTime != null) {
                outRecords = stockOutRecordRepository.findByProductIdAndCreatedAtBetween(
                    productId, startTime, endTime, Pageable.unpaged()).getContent();
            } else {
                outRecords = stockOutRecordRepository.findByProductId(productId);
            }
            
            for (StockOutRecord record : outRecords) {
                allRecords.add(convertToStockRecordDTO(record, product, "OUT"));
            }
        }
        
        // 按时间排序（最新的在前）
        allRecords.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // 计算历史库存前后数量
        calculateHistoricalStock(allRecords, product.getCurrentStock());
        
        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allRecords.size());
        List<StockRecordDTO> pageContent = allRecords.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, allRecords.size());
    }
    
    /**
     * 计算历史库存前后数量
     */
    private void calculateHistoricalStock(List<StockRecordDTO> records, int currentStock) {
        if (records.isEmpty()) {
            return;
        }
        
        // 从当前库存开始，逆向计算每个记录的库存前后数量
        int runningStock = currentStock;
        
        for (StockRecordDTO record : records) {
            if ("IN".equals(record.getType())) {
                // 入库记录：当前库存是变动后，减去入库数量得到变动前
                record.setAfterStock(runningStock);
                record.setBeforeStock(runningStock - record.getQuantity());
                runningStock = record.getBeforeStock();
            } else if ("OUT".equals(record.getType())) {
                // 出库记录：当前库存是变动后，加上出库数量得到变动前
                record.setAfterStock(runningStock);
                record.setBeforeStock(runningStock + record.getQuantity());
                runningStock = record.getBeforeStock();
            }
        }
    }
    
    /**
     * 生成入库单号
     */
    private String generateStockInRecordNo() {
        String prefix = "IN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String recordNo;
        int sequence = 1;
        
        do {
            recordNo = prefix + String.format("%03d", sequence);
            sequence++;
        } while (stockInRecordRepository.existsByRecordNo(recordNo));
        
        return recordNo;
    }
    
    /**
     * 生成出库单号
     */
    private String generateStockOutRecordNo() {
        String prefix = "OUT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String recordNo;
        int sequence = 1;
        
        do {
            recordNo = prefix + String.format("%03d", sequence);
            sequence++;
        } while (stockOutRecordRepository.existsByRecordNo(recordNo));
        
        return recordNo;
    }
    
    /**
     * 转换入库记录为DTO
     */
    private StockRecordDTO convertToStockRecordDTO(StockInRecord record, Product product, String type) {
        StockRecordDTO dto = new StockRecordDTO();
        dto.setId(record.getId());
        dto.setRecordNo(record.getRecordNo());
        dto.setProductId(record.getProductId());
        dto.setProductName(product.getName());
        dto.setProductCode(product.getCode());
        dto.setQuantity(record.getQuantity());
        dto.setUnitPrice(record.getUnitPrice());
        dto.setTotalAmount(record.getTotalAmount());
        dto.setType(type);
        dto.setSubType(getStockInTypeText(record.getType()));
        dto.setOperatorId(record.getOperatorId());
        dto.setRemark(record.getRemark());
        dto.setCreatedAt(record.getCreatedAt());
        
        // 设置操作员姓名
        userRepository.findById(record.getOperatorId())
            .ifPresent(user -> dto.setOperatorName(user.getRealName()));
        
        return dto;
    }
    
    /**
     * 转换入库记录为DTO（带库存前后信息）
     */
    private StockRecordDTO convertToStockRecordDTO(StockInRecord record, Product product, String type, Integer beforeStock, Integer afterStock) {
        StockRecordDTO dto = convertToStockRecordDTO(record, product, type);
        dto.setBeforeStock(beforeStock);
        dto.setAfterStock(afterStock);
        return dto;
    }
    
    /**
     * 转换出库记录为DTO
     */
    private StockRecordDTO convertToStockRecordDTO(StockOutRecord record, Product product, String type) {
        StockRecordDTO dto = new StockRecordDTO();
        dto.setId(record.getId());
        dto.setRecordNo(record.getRecordNo());
        dto.setProductId(record.getProductId());
        dto.setProductName(product.getName());
        dto.setProductCode(product.getCode());
        dto.setQuantity(record.getQuantity());
        dto.setUnitPrice(record.getUnitPrice());
        dto.setTotalAmount(record.getTotalAmount());
        dto.setType(type);
        dto.setSubType(getStockOutTypeText(record.getType()));
        dto.setReason(record.getReason());
        dto.setOperatorId(record.getOperatorId());
        dto.setRemark(record.getRemark());
        dto.setCreatedAt(record.getCreatedAt());
        
        // 设置操作员姓名
        userRepository.findById(record.getOperatorId())
            .ifPresent(user -> dto.setOperatorName(user.getRealName()));
        
        return dto;
    }
    
    /**
     * 转换出库记录为DTO（带库存前后信息）
     */
    private StockRecordDTO convertToStockRecordDTO(StockOutRecord record, Product product, String type, Integer beforeStock, Integer afterStock) {
        StockRecordDTO dto = convertToStockRecordDTO(record, product, type);
        dto.setBeforeStock(beforeStock);
        dto.setAfterStock(afterStock);
        return dto;
    }
    
    /**
     * 获取入库类型文本
     */
    private String getStockInTypeText(StockInRecord.Type type) {
        switch (type) {
            case PURCHASE: return "采购入库";
            case RETURN: return "退货入库";
            case TRANSFER: return "调拨入库";
            case ADJUSTMENT: return "盘点调整";
            default: return "未知类型";
        }
    }
    
    /**
     * 获取出库类型文本
     */
    private String getStockOutTypeText(StockOutRecord.Type type) {
        switch (type) {
            case SALE: return "销售出库";
            case LOSS: return "损耗出库";
            case TRANSFER: return "调拨出库";
            case ADJUSTMENT: return "盘点调整";
            default: return "未知类型";
        }
    }
    
    /**
     * 库存盘点
     */
    public void inventoryCheck(List<Map<String, Object>> checkItems, Long operatorId, String remark) {
        for (Map<String, Object> item : checkItems) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer actualStock = Integer.valueOf(item.get("actualStock").toString());
            
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
            
            int difference = actualStock - product.getCurrentStock();
            
            if (difference != 0) {
                // 生成盘点调整记录
                if (difference > 0) {
                    // 盘盈，生成入库记录
                    String recordNo = generateStockInRecordNo();
                    StockInRecord record = new StockInRecord();
                    record.setRecordNo(recordNo);
                    record.setProductId(productId);
                    record.setQuantity(difference);
                    record.setUnitPrice(product.getPurchasePrice());
                    record.setTotalAmount(product.getPurchasePrice().multiply(new BigDecimal(difference)));
                    record.setType(StockInRecord.Type.ADJUSTMENT);
                    record.setOperatorId(operatorId);
                    record.setRemark("库存盘点调整 - 盘盈: " + remark);
                    stockInRecordRepository.save(record);
                } else {
                    // 盘亏，生成出库记录
                    String recordNo = generateStockOutRecordNo();
                    StockOutRecord record = new StockOutRecord();
                    record.setRecordNo(recordNo);
                    record.setProductId(productId);
                    record.setQuantity(-difference);
                    record.setUnitPrice(product.getPurchasePrice());
                    record.setTotalAmount(product.getPurchasePrice().multiply(new BigDecimal(-difference)));
                    record.setType(StockOutRecord.Type.ADJUSTMENT);
                    record.setReason("库存盘点调整 - 盘亏");
                    record.setOperatorId(operatorId);
                    record.setRemark("库存盘点调整 - 盘亏: " + remark);
                    stockOutRecordRepository.save(record);
                }
                
                // 更新商品库存
                product.setCurrentStock(actualStock);
                productRepository.save(product);
            }
        }
    }
    
    /**
     * 获取库存统计报表
     */
    public Map<String, Object> getInventoryStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 如果没有指定日期范围，默认查询最近30天
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        // 总商品数量
        long totalProducts = productRepository.countByStatus(Product.Status.ACTIVE);
        statistics.put("totalProducts", totalProducts);
        
        // 库存总价值
        List<Product> allProducts = productRepository.findByStatus(Product.Status.ACTIVE);
        BigDecimal totalStockValue = allProducts.stream()
            .map(p -> p.getPurchasePrice().multiply(new BigDecimal(p.getCurrentStock())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalStockValue", totalStockValue);
        
        // 预警商品数量
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        statistics.put("lowStockCount", lowStockProducts.size());
        
        // 缺货商品数量
        long outOfStockCount = productRepository.countByCurrentStockAndStatus(0, Product.Status.ACTIVE);
        statistics.put("outOfStockCount", outOfStockCount);
        
        // 入库统计
        List<StockInRecord> inRecords = stockInRecordRepository.findByCreatedAtBetween(startDate, endDate);
        int totalInQuantity = inRecords.stream().mapToInt(StockInRecord::getQuantity).sum();
        BigDecimal totalInAmount = inRecords.stream()
            .map(StockInRecord::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalInQuantity", totalInQuantity);
        statistics.put("totalInAmount", totalInAmount);
        
        // 出库统计
        List<StockOutRecord> outRecords = stockOutRecordRepository.findByCreatedAtBetween(startDate, endDate);
        int totalOutQuantity = outRecords.stream().mapToInt(StockOutRecord::getQuantity).sum();
        BigDecimal totalOutAmount = outRecords.stream()
            .map(StockOutRecord::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalOutQuantity", totalOutQuantity);
        statistics.put("totalOutAmount", totalOutAmount);
        
        // 按分类统计库存
        Map<String, Object> categoryStats = new HashMap<>();
        List<Category> categories = categoryRepository.findByStatus(Category.Status.ACTIVE);
        for (Category category : categories) {
            List<Product> categoryProducts = productRepository.findByCategoryIdAndStatus(category.getId(), Product.Status.ACTIVE);
            int categoryStockCount = categoryProducts.stream().mapToInt(Product::getCurrentStock).sum();
            BigDecimal categoryStockValue = categoryProducts.stream()
                .map(p -> p.getPurchasePrice().multiply(new BigDecimal(p.getCurrentStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> catStat = new HashMap<>();
            catStat.put("productCount", categoryProducts.size());
            catStat.put("stockCount", categoryStockCount);
            catStat.put("stockValue", categoryStockValue);
            categoryStats.put(category.getName(), catStat);
        }
        statistics.put("categoryStats", categoryStats);
        
        return statistics;
    }
    
    /**
     * 导出库存报表
     */
    public String exportInventoryReport(String name, String stockStatus) {
        // 这里简化实现，实际项目中应该生成Excel文件
        List<Product> products;
        
        if (name != null && !name.trim().isEmpty()) {
            products = productRepository.findByNameContainingAndStatus(name, Product.Status.ACTIVE);
        } else {
            products = productRepository.findByStatus(Product.Status.ACTIVE);
        }
        
        // 根据库存状态筛选
        if ("warning".equals(stockStatus)) {
            products = products.stream()
                .filter(p -> p.getCurrentStock() <= p.getMinStock())
                .collect(Collectors.toList());
        } else if ("out".equals(stockStatus)) {
            products = products.stream()
                .filter(p -> p.getCurrentStock() == 0)
                .collect(Collectors.toList());
        }
        
        // 生成文件路径（实际项目中应该生成真实的Excel文件）
        String fileName = "inventory_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        String filePath = "/exports/" + fileName;
        
        // 这里应该实现真实的Excel导出逻辑
        // 暂时返回文件路径
        return filePath;
    }
    
    /**
     * 设置库存预警阈值
     */
    public void setAlertThreshold(Long productId, Integer minStock, Integer maxStock) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (minStock < 0 || maxStock < 0) {
            throw new RuntimeException("库存阈值不能为负数");
        }
        
        if (minStock >= maxStock) {
            throw new RuntimeException("最低库存不能大于等于最高库存");
        }
        
        product.setMinStock(minStock);
        product.setMaxStock(maxStock);
        productRepository.save(product);
    }
}