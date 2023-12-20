package com.netty.customer.configs;


import com.netty.customer.storage.BaseBroadcast;
import com.netty.customer.storage.BaseFile;
import com.netty.customer.storage.BaseIndex;
import com.netty.customer.storage.BaseMemory;
import com.sun.management.OperatingSystemMXBean;
import org.ehcache.CacheManager;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 功能描述：持久化配置
 * 作者：唐泽齐
 */
@Configuration
public class EhcacheConfig {

    @Bean
    public CacheManager makeCache() throws IOException {
        getMemInfo();
        getDiskInfo();
        getCahceInfo();
        File file = new File("tmpdir");
        System.out.println("初始化存储文件 ："+file.getCanonicalPath());
        File base = new File((file.getCanonicalPath().split(":"))[0] + ":\\");
        ResourcePools resourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(100, EntryUnit.ENTRIES)
                .disk(base.getTotalSpace() / 1024 / 1024 <= 0 ? 100: base.getTotalSpace() / 1024 / 1024 , MemoryUnit.MB, true)
                .build();
        CacheManager cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(file))
                .withCache("ChatGPT", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ConcurrentLinkedDeque.class, resourcePools))
                .withCache("User", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, BaseMemory.class, resourcePools))
                .withCache("File", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, BaseFile.class, resourcePools))
                .withCache("UserFiles", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, BaseIndex.class, resourcePools))
                .withCache("ModelFiles", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, BaseIndex.class, resourcePools))
                .withCache("Broadcast", CacheConfigurationBuilder.newCacheConfigurationBuilder( String.class, BaseBroadcast.class, resourcePools))
                .build();
        cacheManager.init();
        return cacheManager;
    }


    public static void main(String[] args) throws IOException {
        File file = new File("tmpdir");
        File file1 = new File((file.getCanonicalPath().split(":"))[0] + ":\\");
        System.out.print("空闲未使用 = " + file1.getFreeSpace() / 1024 / 1024 + "M" + "    ");// 空闲空间
        System.out.print("已经使用 = " + file1.getUsableSpace() / 1024 / 1024 + "M" + "    ");// 可用空间
        System.out.print("总容量 = " + file1.getTotalSpace() / 1024 / 1024 + "M" + "    ");// 总空间
        System.out.println();
    }

    public static void getDiskInfo() {
        File[] disks = File.listRoots();
        for (File file : disks) {
            System.out.print(file.getPath() + "    ");
            System.out.print("空闲未使用 = " + file.getFreeSpace() / 1024 / 1024 + "M" + "    ");// 空闲空间
            System.out.print("已经使用 = " + file.getUsableSpace() / 1024 / 1024 + "M" + "    ");// 可用空间
            System.out.print("总容量 = " + file.getTotalSpace() / 1024 / 1024 + "M" + "    ");// 总空间
            System.out.println();
        }
    }

    public static void getMemInfo() {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        System.out.print("内存    ");
        System.out.print("空闲未使用 = " + mem.getFreePhysicalMemorySize() / 1024 / 1024 + "M" + "    ");
        System.out.print("总容量 = " + mem.getTotalPhysicalMemorySize() / 1024 / 1024 + "M" + "    ");
        System.out.println();
    }

    public static void getCahceInfo() {
        File file = new File("tmpdir");
        System.out.print("持久池  ");
        System.out.print("空闲未使用 = " + file.getFreeSpace() / 1024 / 1024 + "M" + "    ");// 空闲空间
        System.out.print("已经使用 = " + file.getUsableSpace() / 1024 / 1024 + "M" + "    ");// 可用空间
        System.out.print("总容量 = " + file.getTotalSpace() / 1024 / 1024 + "M" + "    ");// 总空间
        System.out.println();
    }

}