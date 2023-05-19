package com.netty.customer.controller;

import cn.hutool.core.collection.ListUtil;
import com.netty.customer.storage.BaseFile;
import com.netty.customer.storage.BaseIndex;
import com.netty.customer.storage.BaseMemory;
import com.netty.customer.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.netty.customer.storage.FileStorage.*;

/**
 * 功能描述：文件管理
 * 作者：唐泽齐
 */
@RestController
@RequestMapping("file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    /**
     * 单文件上传
     * @param request
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public BaseFile handleFileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws Exception {
        BaseMemory user = UserStorage.get(request);
        if (ObjectUtils.isEmpty(user)) throw new Exception("请登录");
        return putFile(user.getBs().getUserId(), "file", file);
    }

    /**
     * 单文件下载
     * @param request
     * @param index
     * @param response
     * @throws Exception
     */
    @GetMapping("/download/{index}")
    public void handleFileDownload(HttpServletRequest request,@PathVariable("index") String index, HttpServletResponse response) throws Exception {
        BaseFile baseFile = getFile(index);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + baseFile.getName() + "\"");
        // 将文件复制到响应输出流中
        Files.copy(Paths.get(baseFile.gFilePath()), response.getOutputStream());
    }

    /**
     * 单文件删除
     * @param request
     * @param index
     * @param response
     * @throws Exception
     */
    @GetMapping("/del/{index}")
    public void handleFileDel(HttpServletRequest request,@PathVariable("index") String index, HttpServletResponse response) throws Exception {
        delFile(index);
    }


    /**
     * 文件展示（返回文件字节流）
     * @param request
     * @param index
     * @return
     */
    @GetMapping("/show/{index}")
    public void show(HttpServletRequest request, @PathVariable("index") String index,HttpServletResponse response) throws Exception {
        BaseMemory user = UserStorage.get(request);
        if (ObjectUtils.isEmpty(user)) throw new Exception("请登录");
        BaseFile baseFile = getFile(index);
        String[] split = baseFile.getName().split("\\.");
        response.setContentType(split[split.length-1]);
        response.getOutputStream().write(Files.readAllBytes(Paths.get(baseFile.gFilePath())));
    }

    /**
     * 获取文件列表
     * @param request
     * @return
     */
    @GetMapping("/list")
    public Flux<BaseFile> list(HttpServletRequest request) throws Exception {
        BaseMemory user = UserStorage.get(request);
        if (ObjectUtils.isEmpty(user)) throw new Exception("请登录");
        BaseIndex uindexs = getIndexs(user.getBs().getUserId());
        BaseIndex mindexs = getIndexs("file");
        ArrayList<String> strings = new ArrayList<>();
        strings.addAll(mindexs.getIndex());
        strings.retainAll(uindexs.getIndex());
        ArrayList<String> finalStrings1 = strings;
        finalStrings1.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2)>0 ? -1 : 1;
            }
        });
        return Flux.range(0,strings.size()).map(i ->getFile(finalStrings1.get(i)));
    }
}
