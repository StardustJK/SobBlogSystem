package net.stardust.blog.controller.admin;

import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/web_size_info")
public class WebSiteInfoApi {

    @GetMapping("/{title}")
    public ResponseResult getWebSiteTitle(){
        return null;
    }

    @PutMapping("/title")
    public ResponseResult UpWebSiteTitle(@RequestParam("title") String title){
        return null;
    }

    @GetMapping("/soe")
    public ResponseResult getSeoInfo(){
        return null;
    }
    @PutMapping("/soe")
    public ResponseResult getSeoInfo(@RequestParam("keywords") String keywords,@RequestParam("desciption") String description){
        return null;
    }

    @GetMapping("/view_count")
    public ResponseResult getWebsiteViewCount(){
        return null;
    }

}
