package com.netty.customer.storage;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**
 * 功能描述：文件缓存
 * 作者：唐泽齐
 */
@Component
public class FileStorage {

    private static volatile Cache<String, BaseFile> files;
    private static volatile Cache<String, BaseIndex> userFiles;
    private static volatile Cache<String, BaseIndex> modelFiles;

    @Resource
    public void makeCahe(CacheManager cacheManager) {
        files = cacheManager.getCache("File", String.class, BaseFile.class);
        userFiles = cacheManager.getCache("UserFiles", String.class, BaseIndex.class);
        modelFiles = cacheManager.getCache("ModelFiles", String.class, BaseIndex.class);
    }

    public static BaseFile putFile(Long userId, String model, MultipartFile file) {
        BaseFile baseFile = new BaseFile(userId, model, file);
        files.put(baseFile.getIndex(), baseFile);
        BaseIndex uf = new BaseIndex();
        if (userFiles.containsKey(userId + "")) uf = userFiles.get(userId + "");
        uf.add(baseFile.getIndex());
        userFiles.put(userId + "", uf);
        BaseIndex mf = new BaseIndex();
        if (modelFiles.containsKey(model)) mf = modelFiles.get(model);
        mf.add(baseFile.getIndex());
        modelFiles.put(model, mf);
        baseFile.sFile(file);
        return baseFile;
    }

    public static BaseIndex getIndexs(Long userId) {
        if (!userFiles.containsKey(userId + "")) {
            return new BaseIndex();
        }
        return userFiles.get(userId + "");
    }

    public static BaseIndex getIndexs(String model) {
        if (!modelFiles.containsKey(model)) {
            return new BaseIndex();
        }
        return modelFiles.get(model);
    }

    public static BaseFile getFile(String index) {
        return files.get(index);
    }

    public static boolean delFile(String index) {
        if (files.containsKey(index)) {
            BaseFile baseFile = files.get(index);
            files.remove(index);
            BaseIndex userIndex = userFiles.get(baseFile.getUserId() + "");
            userIndex.del(index);
            userFiles.put(baseFile.getUserId() + "", userIndex);
            BaseIndex modelIndex = modelFiles.get(baseFile.getModel());
            modelIndex.del(index);
            modelFiles.put(baseFile.getModel(), modelIndex);
            baseFile.dFile();
        }
        return true;
    }

}
