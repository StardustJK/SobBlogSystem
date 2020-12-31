package net.stardust.blog.controller.admin;

import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IImageService;
import net.stardust.blog.service.impl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        return null;
    }
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{imageId}")
    public ResponseResult updateImage(@PathVariable("imageId") String imageId){
        return null;
    }
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{imageId}")
    public ResponseResult getImage(@PathVariable("imageId") String imageId){
        return null;
    }
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listImage(@RequestParam("page") int page,@RequestParam("size") int size){
        return null;
    }
}
