package net.stardust.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IImageService;
import net.stardust.blog.utils.TextUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

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
        String name = file.getName();
        String originalFilename = file.getOriginalFilename();
        log.info("name==>  "+name);
        log.info("originalFilename==>  "+originalFilename);
        File targetFile=new File(imagePath+File.separator+originalFilename);
        log.info("originalFilename == >"+originalFilename);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED("图片上传失败");
        }
        return ResponseResult.SUCCESS("图片上传成功");
    }
}
