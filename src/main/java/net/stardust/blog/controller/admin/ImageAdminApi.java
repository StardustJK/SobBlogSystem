package net.stardust.blog.controller.admin;

import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IImageService;
import net.stardust.blog.service.impl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {

    @Autowired
    private ImageServiceImpl imageService;


    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult uploadImage(@RequestParam("file")MultipartFile file){
        return imageService.uploadImage(file);
    }
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId){
        return imageService.deleteImage(imageId);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/{imageId}")
    public void getImage(HttpServletResponse response, @PathVariable("imageId") String imageId){
        try {
            imageService.viewImage(response,imageId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listImage(@PathVariable("page") int page,@PathVariable("size") int size){
        return imageService.listImage(page,size);
    }
}
