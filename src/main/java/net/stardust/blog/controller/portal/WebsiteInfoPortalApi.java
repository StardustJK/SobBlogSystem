package net.stardust.blog.controller.portal;

import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/web_site_info")
public class WebsiteInfoPortalApi {

    @GetMapping("/categories")
    public ResponseResult getCategories(){
        return null;
    }

    @GetMapping("/title")
    public ResponseResult getTitle(){
        return null;
    }

    @GetMapping("/view_count")
    public ResponseResult getViewCount(){
        return null;
    }

    @GetMapping("/seo")
    public ResponseResult getSeoInfo(){
        return null;
    }

    @GetMapping("/loop")
    public ResponseResult getLoop(){
        return null;
    }

    @GetMapping("friend_link")
    public ResponseResult getFriendLink(){
        return null;
    }

}
