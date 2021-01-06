package net.stardust.blog.controller.admin;

import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IWebSiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/web_size_info")
public class WebSiteInfoAdminApi {

    @Autowired
    private IWebSiteInfoService webSiteInfoService;

    @PreAuthorize("@permission.admin()")
    @GetMapping("/title")
    public ResponseResult getWebSiteTitle(){
        return webSiteInfoService.getWebSiteTitle();
    }

    @PreAuthorize("@permission.admin()")
    @PutMapping("/title")
    public ResponseResult upWebSiteTitle(@RequestParam("title") String title){
        return webSiteInfoService.putWebSiteTitle(title);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/soe")
    public ResponseResult getSeoInfo(){
        return webSiteInfoService.getSeoInfo();
    }
    @PreAuthorize("@permission.admin()")
    @PutMapping("/soe")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords,@RequestParam("description") String description){
        return webSiteInfoService.putSeoInfo(keywords,description);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/view_count")
    public ResponseResult getWebSiteViewCount(){
        return webSiteInfoService.getWebSiteViewCount();
    }

}
