package net.stardust.blog.service;

import net.stardust.blog.response.ResponseResult;

public interface IWebSiteInfoService {
    ResponseResult getWebSiteTitle();

    ResponseResult putWebSiteTitle(String title);

    ResponseResult getSeoInfo();

    ResponseResult putSeoInfo(String keywords, String description);

    ResponseResult getWebSiteViewCount();
}
