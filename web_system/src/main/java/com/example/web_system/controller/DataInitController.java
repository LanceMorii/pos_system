package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.entity.Category;
import com.example.web_system.entity.Product;
import com.example.web_system.repository.CategoryRepository;
import com.example.web_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据初始化控制器
 * 用于创建测试数据
 */
@RestController
@RequestMapping("/data-init")
@CrossOrigin(origins = "*")
public class DataInitController {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;

    /**
     * 初始化商品分类数据
     */
    @PostMapping("/categories")
    public ApiResponse<?> initCategories() {
        try {
            // 检查是否已有数据
            if (categoryRepository.count() > 0) {
                return ApiResponse.success("分类数据已存在，无需重复初始化", null);
            }

            List<Category> categories = new ArrayList<>();
            
            // 创建主分类
            Category beverages = new Category();
            beverages.setName("饮料");
            beverages.setLevel(1);
            beverages.setSortOrder(1);
            beverages.setStatus(Category.Status.ACTIVE);
            beverages.setDescription("各类饮品");
            categories.add(beverages);

            Category food = new Category();
            food.setName("食品");
            food.setLevel(1);
            food.setSortOrder(2);
            food.setStatus(Category.Status.ACTIVE);
            food.setDescription("各类食品");
            categories.add(food);

            Category daily = new Category();
            daily.setName("日用品");
            daily.setLevel(1);
            daily.setSortOrder(3);
            daily.setStatus(Category.Status.ACTIVE);
            daily.setDescription("日常用品");
            categories.add(daily);

            Category snacks = new Category();
            snacks.setName("零食");
            snacks.setLevel(1);
            snacks.setSortOrder(4);
            snacks.setStatus(Category.Status.ACTIVE);
            snacks.setDescription("休闲零食");
            categories.add(snacks);

            Category fresh = new Category();
            fresh.setName("生鲜");
            fresh.setLevel(1);
            fresh.setSortOrder(5);
            fresh.setStatus(Category.Status.ACTIVE);
            fresh.setDescription("新鲜食材");
            categories.add(fresh);

            categoryRepository.saveAll(categories);
            
            return ApiResponse.success("分类数据初始化成功", categories.size() + "个分类");
        } catch (Exception e) {
            return ApiResponse.error("分类数据初始化失败: " + e.getMessage());
        }
    }

    /**
     * 初始化商品数据
     */
    @PostMapping("/products")
    public ApiResponse<?> initProducts() {
        try {
            // 检查是否已有数据
            if (productRepository.count() > 0) {
                return ApiResponse.success("商品数据已存在，无需重复初始化", null);
            }

            // 先确保有分类数据
            if (categoryRepository.count() == 0) {
                initCategories();
            }

            List<Category> categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                return ApiResponse.error("请先初始化分类数据");
            }

            List<Product> products = new ArrayList<>();
            
            // 获取分类ID
            Long beverageId = categories.stream()
                .filter(c -> "饮料".equals(c.getName()))
                .findFirst().map(Category::getId).orElse(1L);
            
            Long foodId = categories.stream()
                .filter(c -> "食品".equals(c.getName()))
                .findFirst().map(Category::getId).orElse(2L);
            
            Long dailyId = categories.stream()
                .filter(c -> "日用品".equals(c.getName()))
                .findFirst().map(Category::getId).orElse(3L);
            
            Long snackId = categories.stream()
                .filter(c -> "零食".equals(c.getName()))
                .findFirst().map(Category::getId).orElse(4L);
            
            Long freshId = categories.stream()
                .filter(c -> "生鲜".equals(c.getName()))
                .findFirst().map(Category::getId).orElse(5L);

            // 饮料类商品
            products.add(createProduct("P001", "可口可乐 330ml", beverageId, "6901234567890", 
                new BigDecimal("2.50"), new BigDecimal("3.50"), new BigDecimal("3.20"), 200));
            
            products.add(createProduct("P002", "百事可乐 330ml", beverageId, "6901234567891", 
                new BigDecimal("2.40"), new BigDecimal("3.40"), new BigDecimal("3.10"), 180));
            
            products.add(createProduct("P003", "矿泉水 550ml", beverageId, "6901234567892", 
                new BigDecimal("1.20"), new BigDecimal("2.00"), new BigDecimal("1.80"), 500));
            
            products.add(createProduct("P004", "统一绿茶 500ml", beverageId, "6901234567893", 
                new BigDecimal("2.80"), new BigDecimal("4.00"), new BigDecimal("3.70"), 150));

            // 食品类商品
            products.add(createProduct("P005", "蒙牛纯牛奶 250ml", foodId, "6901234567894", 
                new BigDecimal("3.20"), new BigDecimal("4.50"), new BigDecimal("4.20"), 120));
            
            products.add(createProduct("P006", "康师傅红烧牛肉面", foodId, "6901234567895", 
                new BigDecimal("3.80"), new BigDecimal("5.50"), new BigDecimal("5.20"), 80));
            
            products.add(createProduct("P007", "白象方便面", foodId, "6901234567896", 
                new BigDecimal("3.50"), new BigDecimal("5.00"), new BigDecimal("4.80"), 60));

            // 零食类商品
            products.add(createProduct("P008", "薯片 大包装", snackId, "6901234567897", 
                new BigDecimal("4.50"), new BigDecimal("6.80"), new BigDecimal("6.50"), 100));
            
            products.add(createProduct("P009", "奥利奥饼干", snackId, "6901234567898", 
                new BigDecimal("5.20"), new BigDecimal("7.50"), new BigDecimal("7.20"), 90));
            
            products.add(createProduct("P010", "德芙巧克力", snackId, "6901234567899", 
                new BigDecimal("8.50"), new BigDecimal("12.00"), new BigDecimal("11.50"), 70));

            // 生鲜类商品
            products.add(createProduct("P011", "苹果", freshId, "6901234567900", 
                new BigDecimal("8.00"), new BigDecimal("12.00"), new BigDecimal("11.50"), 50));
            
            products.add(createProduct("P012", "香蕉", freshId, "6901234567901", 
                new BigDecimal("6.50"), new BigDecimal("9.80"), new BigDecimal("9.50"), 60));
            
            products.add(createProduct("P013", "白菜", freshId, "6901234567902", 
                new BigDecimal("2.00"), new BigDecimal("3.50"), new BigDecimal("3.20"), 80));

            // 日用品类商品
            products.add(createProduct("P014", "牙膏", dailyId, "6901234567903", 
                new BigDecimal("8.50"), new BigDecimal("12.50"), new BigDecimal("12.00"), 40));
            
            products.add(createProduct("P015", "洗发水", dailyId, "6901234567904", 
                new BigDecimal("15.00"), new BigDecimal("22.00"), new BigDecimal("21.00"), 30));
            
            products.add(createProduct("P016", "抽纸", dailyId, "6901234567905", 
                new BigDecimal("6.00"), new BigDecimal("9.00"), new BigDecimal("8.50"), 100));

            productRepository.saveAll(products);
            
            return ApiResponse.success("商品数据初始化成功", products.size() + "个商品");
        } catch (Exception e) {
            return ApiResponse.error("商品数据初始化失败: " + e.getMessage());
        }
    }

    /**
     * 一键初始化所有数据
     */
    @PostMapping("/all")
    public ApiResponse<?> initAllData() {
        try {
            // 初始化分类
            ApiResponse<?> categoryResult = initCategories();
            
            // 初始化商品
            ApiResponse<?> productResult = initProducts();
            
            return ApiResponse.success("数据初始化完成", 
                "分类: " + categoryResult.getMessage() + ", 商品: " + productResult.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("数据初始化失败: " + e.getMessage());
        }
    }

    /**
     * 清空所有数据
     */
    @DeleteMapping("/clear")
    public ApiResponse<?> clearAllData() {
        try {
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            return ApiResponse.success("数据清空成功", null);
        } catch (Exception e) {
            return ApiResponse.error("数据清空失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据统计
     */
    @GetMapping("/stats")
    public ApiResponse<?> getDataStats() {
        try {
            long categoryCount = categoryRepository.count();
            long productCount = productRepository.count();
            
            return ApiResponse.success("数据统计", 
                "分类数量: " + categoryCount + ", 商品数量: " + productCount);
        } catch (Exception e) {
            return ApiResponse.error("获取数据统计失败: " + e.getMessage());
        }
    }

    /**
     * 创建商品对象
     */
    private Product createProduct(String code, String name, Long categoryId, String barcode,
                                BigDecimal purchasePrice, BigDecimal salePrice, BigDecimal memberPrice,
                                int currentStock) {
        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setBarcode(barcode);
        product.setUnit("个");
        product.setPurchasePrice(purchasePrice);
        product.setSalePrice(salePrice);
        product.setMemberPrice(memberPrice);
        product.setMinStock(10);
        product.setMaxStock(1000);
        product.setCurrentStock(currentStock);
        product.setStatus(Product.Status.ACTIVE);
        product.setDescription("测试商品");
        return product;
    }
}