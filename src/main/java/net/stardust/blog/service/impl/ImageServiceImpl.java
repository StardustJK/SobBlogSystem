package net.stardust.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IImageService;
import net.stardust.blog.utils.TextUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;

@Slf4j
@Service
@Transactional
public class ImageServiceImpl implements IImageService {

    public static final String imagePath="D:\\AProjects\\images";

    @Override
    public ResponseResult uploadImage(MultipartFile file) {
        if (file == null) {
            return ResponseResult.FAILED("图片不可以为空");
        }
        //只支持png,jpg,gif
        String contentType = file.getContentType();
        if (TextUtils.isEmpty(contentType)) {
            return ResponseResult.FAILED("文件格式错误");
        }

        if (!"image/png".equals(contentType) &&
                !"image/gif".equals(contentType) &&
                !"image/jpg".equals(contentType)
        ) {
            return ResponseResult.FAILED("不支持此图片类型");
        }
        log.info("contentType==>" +contentType);
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename==>  "+originalFilename);
        File targetFile=new File(imagePath+File.separator+originalFilename);
        log.info("targetFile == >"+targetFile);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED("图片上传失败");
        }
        return ResponseResult.SUCCESS("图片上传成功");
    }

    @Override
    public void viewImage(HttpServletResponse response, String imageId) throws IOException {
        File file=new File(imagePath+File.separator+"Snipaste_2020-12-31_13-25-11.png");
        OutputStream writer=null;
        FileInputStream fos=null;
        try {
            response.setContentType("image/png");
            writer= response.getOutputStream();
            //读取
            fos=new FileInputStream(file);
            byte[] buff=new byte[1024];
            int len;
            while ((len=fos.read(buff))!=-1){
                writer.write(buff,0,len);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (writer != null) {
                writer.close();
            }
            if (fos!=null){
                fos.close();
            }
        }
    }
}
