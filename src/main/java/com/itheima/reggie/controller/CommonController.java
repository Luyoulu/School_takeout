package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info(file.toString());
        String fileName = UUID.randomUUID().toString()+substring;
        File file1 = new File(basePath);
        if (!file1.exists()){
            log.info("目录不存在");
            file1.mkdir();
        }
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);

    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response)  {
        //输入流读取文件，输出流回写到浏览器展示
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            ServletOutputStream fileOutputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length=fileInputStream.read(bytes))!=-1){
                fileOutputStream.write(bytes,0,length);
                fileOutputStream.flush();
            }
            fileOutputStream.close();
            fileInputStream.close();
            fileInputStream.read(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
