package com.example.web_system.service;

import com.example.web_system.dto.CategoryDTO;
import com.example.web_system.entity.Category;
import com.example.web_system.repository.CategoryRepository;
import com.example.web_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 获取所有活跃分类
     */
    public List<CategoryDTO> getAllActiveCategories() {
        List<Category> categories = categoryRepository.findAllActiveOrderBySortOrder();
        return categories.stream()
            .map(CategoryDTO::fromCategory)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取所有分类（包括活跃和非活跃）
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    /**
     * 根据ID获取分类
     */
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
        return CategoryDTO.fromCategory(category);
    }
    
    /**
     * 根据ID获取分类实体
     */
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
            .orElse(null);
    }
    
    /**
     * 创建分类
     */
    public CategoryDTO createCategory(String name, String description) {
        // 检查分类名称是否已存在
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("分类名称已存在");
        }
        
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setLevel(1); // 默认为一级分类
        category.setStatus(Category.Status.ACTIVE);
        
        // 设置排序顺序为最大值+1
        List<Category> existingCategories = categoryRepository.findByLevel(1);
        int maxSortOrder = existingCategories.stream()
            .mapToInt(Category::getSortOrder)
            .max()
            .orElse(0);
        category.setSortOrder(maxSortOrder + 1);
        
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.fromCategory(savedCategory);
    }
    
    /**
     * 更新分类
     */
    public CategoryDTO updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 检查分类名称是否已被其他分类使用
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new RuntimeException("分类名称已存在");
        }
        
        category.setName(name);
        category.setDescription(description);
        
        Category updatedCategory = categoryRepository.save(category);
        return CategoryDTO.fromCategory(updatedCategory);
    }
    
    /**
     * 删除分类
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 检查分类下是否有商品
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException("该分类下还有商品，不能删除");
        }
        
        // 检查是否有子分类
        List<Category> subCategories = categoryRepository.findByParentId(id);
        if (!subCategories.isEmpty()) {
            throw new RuntimeException("该分类下还有子分类，不能删除");
        }
        
        categoryRepository.delete(category);
    }
    
    /**
     * 获取根分类列表
     */
    public List<CategoryDTO> getRootCategories() {
        List<Category> categories = categoryRepository.findByParentIdIsNull();
        return categories.stream()
            .filter(category -> category.getStatus() == Category.Status.ACTIVE)
            .map(CategoryDTO::fromCategory)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据父分类ID获取子分类
     */
    public List<CategoryDTO> getSubCategories(Long parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return categories.stream()
            .filter(category -> category.getStatus() == Category.Status.ACTIVE)
            .map(CategoryDTO::fromCategory)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取分类总数
     */
    public long count() {
        return categoryRepository.count();
    }
    
    /**
     * 获取分类总数（别名方法）
     */
    public long getTotalCount() {
        return categoryRepository.count();
    }
}