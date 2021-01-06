package net.stardust.blog.controller.admin;

import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IWebSiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/web_size_info")
public class WebSiteInfoAdminApi {

    @Autowired
    private IWebSiteInfoService webSiteInfoService;

    @GetMapping("/title")
    public ResponseResult getWebSiteTitle(){
        return webSiteInfoService.getWebSiteTitle();
    }

    @PutMapping("/title")
    public ResponseResult upWebSiteTitle(@RequestParam("title") String title){
        return webSiteInfoService.putWebSiteTitle(title);
    }

    @GetMapping("/soe")
    public ResponseResult getSeoInfo(){
        return webSiteInfoService.getSeoInfo();
    }
    @PutMapping("/soe")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords,@RequestParam("description") String description){
        return webSiteInfoService.putSeoInfo(keywords,description);
    }

    @GetMapping("/view_count")
    public ResponseResult getWebSiteViewCount(){
        return webSiteInfoService.getWebSiteViewCount();
    }

}
