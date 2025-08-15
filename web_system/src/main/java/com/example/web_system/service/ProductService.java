package com.example.web_system.service;

import com.example.web_system.dto.ProductDTO;
import com.example.web_system.dto.ProductRequest;
import com.example.web_system.entity.Category;
import com.example.web_system.entity.Product;
import com.example.web_system.repository.CategoryRepository;
import com.example.web_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * 查询商品列表
     */
    public List<ProductDTO> getProducts(String name, Long categoryId, String status) {
        Product.Status statusEnum = null;
        if (StringUtils.hasText(status)) {
            try {
                statusEnum = Product.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效状态
            }
        }
        
        List<Product> products = productRepository.findProductsWithFilters(name, categoryId, statusEnum);
        
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
     * 根据ID获取商品详情
     */
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        ProductDTO dto = ProductDTO.fromProduct(product);
        // 设置分类名称
        if (product.getCategoryId() != null) {
            categoryRepository.findById(product.getCategoryId())
                .ifPresent(category -> dto.setCategoryName(category.getName()));
        }
        
        return dto;
    }
    
    /**
     * 创建商品
     */
    public ProductDTO createProduct(ProductRequest request) {
        // 验证商品编码是否已存在
        if (productRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("商品编码已存在");
        }
        
        // 验证条形码是否已存在（如果提供了条形码）
        if (StringUtils.hasText(request.getBarcode()) && 
            productRepository.existsByBarcode(request.getBarcode())) {
            throw new RuntimeException("条形码已存在");
        }
        
        // 验证分类是否存在
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new RuntimeException("商品分类不存在"));
        
        // 创建商品实体
        Product product = new Product();
        updateProductFromRequest(product, request);
        
        // 保存商品
        Product savedProduct = productRepository.save(product);
        
        // 返回DTO
        ProductDTO dto = ProductDTO.fromProduct(savedProduct);
        dto.setCategoryName(category.getName());
        
        return dto;
    }
    
    /**
     * 更新商品
     */
    public ProductDTO updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证商品编码是否已被其他商品使用
        if (!existingProduct.getCode().equals(request.getCode()) && 
            productRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("商品编码已存在");
        }
        
        // 验证条形码是否已被其他商品使用
        if (StringUtils.hasText(request.getBarcode()) && 
            !request.getBarcode().equals(existingProduct.getBarcode()) &&
            productRepository.existsByBarcode(request.getBarcode())) {
            throw new RuntimeException("条形码已存在");
        }
        
        // 验证分类是否存在
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new RuntimeException("商品分类不存在"));
        
        // 更新商品信息
        updateProductFromRequest(existingProduct, request);
        
        // 保存更新
        Product updatedProduct = productRepository.save(existingProduct);
        
        // 返回DTO
        ProductDTO dto = ProductDTO.fromProduct(updatedProduct);
        dto.setCategoryName(category.getName());
        
        return dto;
    }
    
    /**
     * 删除商品
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 检查商品是否可以删除（例如是否有相关的订单记录）
        // 这里可以添加业务逻辑检查
        
        productRepository.delete(product);
    }
    
    /**
     * 批量删除商品
     */
    public void deleteProducts(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new RuntimeException("部分商品不存在");
        }
        
        productRepository.deleteAll(products);
    }
    
    /**
     * 根据条形码查找商品
     */
    public ProductDTO getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        ProductDTO dto = ProductDTO.fromProduct(product);
        // 设置分类名称
        if (product.getCategoryId() != null) {
            categoryRepository.findById(product.getCategoryId())
                .ifPresent(category -> dto.setCategoryName(category.getName()));
        }
        
        return dto;
    }
    
    /**
     * 获取库存不足的商品列表
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
     * 更新商品库存
     */
    public void updateStock(Long productId, Integer quantity, String operation) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if ("IN".equals(operation)) {
            product.setCurrentStock(product.getCurrentStock() + quantity);
        } else if ("OUT".equals(operation)) {
            if (product.getCurrentStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            product.setCurrentStock(product.getCurrentStock() - quantity);
        } else {
            throw new RuntimeException("无效的库存操作类型");
        }
        
        productRepository.save(product);
    }
    
    /**
     * 获取商品总数
     */
    public long count() {
        return productRepository.count();
    }
    
    /**
     * 获取商品总数（别名方法）
     */
    public long getTotalCount() {
        return productRepository.count();
    }
    
    /**
     * 从请求对象更新商品实体
     */
    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setCategoryId(request.getCategoryId());
        product.setSupplierId(request.getSupplierId());
        product.setBarcode(request.getBarcode());
        product.setUnit(request.getUnit());
        product.setSpecification(request.getSpecification());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setMemberPrice(request.getMemberPrice());
        product.setMinStock(request.getMinStock());
        product.setMaxStock(request.getMaxStock());
        product.setCurrentStock(request.getCurrentStock());
        product.setStatus(Product.Status.valueOf(request.getStatus()));
        product.setImageUrl(request.getImageUrl());
        product.setDescription(request.getDescription());
    }
}