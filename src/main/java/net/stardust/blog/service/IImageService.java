package net.stardust.blog.service;

import net.stardust.blog.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile file);
}
