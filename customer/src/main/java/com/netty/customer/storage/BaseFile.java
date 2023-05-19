package com.netty.customer.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.netty.customer.storage.FileStorage.delFile;

/**
 * 功能描述：文件桶
 * 作者：唐泽齐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseFile implements Serializable {
    private static final long serialVersionUID = -5613789959948993601L;
    //文件名
    private String name;
    //文件类型
    private String type;
    //归属用户
    private Long userId;
    //场景
    private String model;
    //标识ID
    private String index;
    //文件大小
    private Long size;
    //存储时间
    private Long time;

    public BaseFile(Long userId, String model, MultipartFile file) {
        this.name = file.getOriginalFilename();
        this.type = file.getContentType();
        this.userId = userId;
        this.model = model;
        this.index = makeIndex(userId);
        this.size = file.getSize();
        this.time = System.currentTimeMillis();
    }

    private synchronized String makeIndex(Long userId) {
        return userId + "-" + System.nanoTime();
    }

    public void sFile(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get("tmpdir/temp"));
            Files.copy(file.getInputStream(), Paths.get(path()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable e) {
            delFile(index);
        }
    }

    private String path() {
        return String.format("tmpdir/temp/%s.sl", index);
    }

    @SneakyThrows
    public void dFile() {
        Files.delete(Paths.get(path()));
    }

    @SneakyThrows
    public String gFilePath() {
        return path();
    }
}
