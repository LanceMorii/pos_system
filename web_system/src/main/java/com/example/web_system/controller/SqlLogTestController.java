package com.example.web_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sql-log")
@CrossOrigin(origins = "*")
public class SqlLogTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testSqlLog() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 首先检查表是否存在
            String checkTableQuery = "SHOW TABLES LIKE 'products'";
            List<Map<String, Object>> tableExists = jdbcTemplate.queryForList(checkTableQuery);
            
            if (tableExists.isEmpty()) {
                result.put("success", false);
                result.put("message", "products表不存在，请先初始化数据库");
                return ResponseEntity.ok(result);
            }
            
            // 执行一些SQL查询来生成日志
            String countQuery = "SELECT COUNT(*) as total FROM products";
            Integer totalProducts = jdbcTemplate.queryForObject(countQuery, Integer.class);
            
            if (totalProducts == 0) {
                result.put("success", false);
                result.put("message", "products表为空，请先初始化数据。可以访问 /api/data-init/all 来初始化数据");
                result.put("data", Map.of(
                    "totalProducts", 0,
                    "totalCategories", 0,
                    "sampleProducts", List.of()
                ));
                return ResponseEntity.ok(result);
            }
            
            String categoryQuery = "SELECT COUNT(DISTINCT category_id) as categories FROM products";
            Integer totalCategories = jdbcTemplate.queryForObject(categoryQuery, Integer.class);
            
            // 查询一些产品信息 - 修正字段名
            String productQuery = "SELECT id, name, sale_price as price FROM products LIMIT 5";
            List<Map<String, Object>> products = jdbcTemplate.queryForList(productQuery);
            
            result.put("success", true);
            result.put("message", "SQL日志测试完成");
            result.put("data", Map.of(
                "totalProducts", totalProducts,
                "totalCategories", totalCategories,
                "sampleProducts", products
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "SQL执行失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> checkDatabaseStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查数据库连接
            String connectionQuery = "SELECT 1";
            jdbcTemplate.queryForObject(connectionQuery, Integer.class);
            
            // 检查所有表
            String tablesQuery = "SHOW TABLES";
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(tablesQuery);
            
            // 检查各表的数据量
            Map<String, Object> tableCounts = new HashMap<>();
            for (Map<String, Object> table : tables) {
                String tableName = (String) table.values().iterator().next();
                try {
                    String countQuery = "SELECT COUNT(*) FROM " + tableName;
                    Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
                    tableCounts.put(tableName, count);
                } catch (Exception e) {
                    tableCounts.put(tableName, "查询失败: " + e.getMessage());
                }
            }
            
            result.put("success", true);
            result.put("message", "数据库状态检查完成");
            result.put("data", Map.of(
                "connectionStatus", "正常",
                "totalTables", tables.size(),
                "tableList", tables.stream().map(t -> t.values().iterator().next()).toList(),
                "tableCounts", tableCounts
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "数据库状态检查失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取SQL日志统计信息
            Integer totalCount = 0;
            Integer fastCount = 0;
            Integer mediumCount = 0;
            Integer slowCount = 0;
            
            try {
                // 总记录数
                totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sql_logs", Integer.class);
                
                // 快速查询 (<500ms)
                fastCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sql_logs WHERE execution_time < 500", Integer.class);
                
                // 中等查询 (500-1000ms)
                mediumCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sql_logs WHERE execution_time >= 500 AND execution_time < 1000", Integer.class);
                
                // 慢查询 (>=1000ms)
                slowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sql_logs WHERE execution_time >= 1000", Integer.class);
                
            } catch (Exception e) {
                System.out.println("查询sql_logs表失败: " + e.getMessage());
            }
            
            result.put("success", true);
            result.put("data", Map.of(
                "totalCount", totalCount,
                "fastCount", fastCount,
                "mediumCount", mediumCount,
                "slowCount", slowCount,
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getSqlLogs() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从sql_logs表获取真实的SQL日志数据
            String logsQuery = "SELECT * FROM sql_logs ORDER BY created_at DESC LIMIT 50";
            List<Map<String, Object>> logs = jdbcTemplate.queryForList(logsQuery);
            
            // 转换数据格式以匹配前端期望
            List<Map<String, Object>> formattedLogs = logs.stream().map(log -> {
                Map<String, Object> formattedLog = new HashMap<>();
                formattedLog.put("id", log.get("id"));
                formattedLog.put("sqlType", log.get("sql_type"));
                formattedLog.put("sqlStatement", log.get("sql_statement"));
                formattedLog.put("tableName", log.get("table_name"));
                formattedLog.put("executionTime", log.get("execution_time"));
                formattedLog.put("rowsAffected", log.get("rows_affected"));
                formattedLog.put("success", log.get("success"));
                formattedLog.put("errorMessage", log.get("error_message"));
                formattedLog.put("createdAt", log.get("created_at"));
                formattedLog.put("parameters", log.get("parameters"));
                return formattedLog;
            }).toList();
            
            result.put("success", true);
            result.put("data", Map.of(
                "logs", formattedLogs,
                "totalCount", formattedLogs.size(),
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取SQL日志失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }
}