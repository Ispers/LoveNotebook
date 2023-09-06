package com.example.lovenotebook_back.controller;

import com.example.lovenotebook_back.common.Constants;
import com.example.lovenotebook_back.utils.SFTPUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * 向SFTP服务器上传文件控制器
 *
 * @author sun0316
 * @date 2023/5/17 13:42
 */
@Controller
@RequestMapping("/api/upload")
@Api(tags = "文件上传接口", description = "请求接口必须携带token")
public class UploadController {
    @PostMapping("/file")
    @ApiImplicitParam(name = "file", value = "文件", dataType = "MultipartFile", required = true)
    @ApiOperation(value = "向SFTP服务器上传文件", notes = "向SFTP服务器上传文件")
    public void uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {
        // 用文件输出流返回前端
        PrintWriter printWriter = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        SFTPUtils sftps = null;

        if (file.isEmpty()) {
            printWriter.write("未接收到指定参数");
            printWriter.flush();
        } else {
            // 获取文件名
            String fileNames = file.getOriginalFilename();
            int split = fileNames.lastIndexOf(".");

            // 获取上传文件的后缀
            String extName = fileNames.substring(split + 1, fileNames.length());
            // 申明UUID
            String uuid = UUID.randomUUID().toString().replace("-", "");

            // 组成新的名称
            String newName = uuid + "." + extName;

            try {
                sftps = new SFTPUtils();
                //连接服务器
                sftps.connect(Constants.IMAGE_UPLOAD_HOST, Constants.IMAGE_UPLOAD_PORT, Constants.IMAGE_UPLOAD_USERNAME, Constants.IMAGE_UPLOAD_PASSWORD);
                //上传到服务器的位置
                sftps.uploadFileInputStream(file, Constants.IMAGE_UPLOAD_PATH, newName);
            } catch (Exception e) {
                printWriter.write("文件上传失败");
                e.printStackTrace();
            } finally {
                sftps.disconnect();
                printWriter.flush();
            }

            printWriter.write(newName);
            printWriter.flush();
        }
    }
}