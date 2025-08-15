package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/system")
public class SystemController {

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        data.put("status", "UP");
        data.put("now", Instant.now().toString());
        data.put("uptimeMs", runtimeMXBean.getUptime());
        return ApiResponse.success(data);
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> info() {
        Map<String, Object> info = new LinkedHashMap<>();

        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        info.put("appVersion", "v1.0.0");
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("jvmName", System.getProperty("java.vm.name"));
        info.put("osName", System.getProperty("os.name"));
        info.put("osArch", System.getProperty("os.arch"));
        info.put("processors", runtime.availableProcessors());
        info.put("systemLoadAvg", osBean.getSystemLoadAverage());
        info.put("pid", runtimeMXBean.getPid());
        info.put("jvmStartTime", runtimeMXBean.getStartTime());

        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("heapUsed", heap.getUsed());
        memory.put("heapCommitted", heap.getCommitted());
        memory.put("heapMax", heap.getMax());
        memory.put("nonHeapUsed", nonHeap.getUsed());
        memory.put("nonHeapCommitted", nonHeap.getCommitted());
        info.put("memory", memory);

        File[] roots = File.listRoots();
        Map<String, Object> disks = new LinkedHashMap<>();
        if (roots != null) {
            for (File root : roots) {
                Map<String, Object> d = new LinkedHashMap<>();
                try {
                    d.put("total", root.getTotalSpace());
                    d.put("free", root.getFreeSpace());
                    d.put("usable", root.getUsableSpace());
                } catch (Exception ignored) {}
                disks.put(root.getPath(), d);
            }
        }
        info.put("disks", disks);

        return ApiResponse.success(info);
    }

    @GetMapping("/metrics")
    public ApiResponse<Map<String, Object>> metrics() {
        Map<String, Object> m = new LinkedHashMap<>();
        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();

        m.put("cpuCores", runtime.availableProcessors());
        m.put("heapUsed", heap.getUsed());
        m.put("heapMax", heap.getMax());
        m.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(m);
    }
}
