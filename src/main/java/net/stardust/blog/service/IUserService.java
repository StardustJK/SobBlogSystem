package net.stardust.blog.service;

import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request);
    void createCaptcha(HttpServletResponse response,String captchaKey) throws Exception;

    ResponseResult sendEmail(String type,HttpServletRequest request, String emailAddress);

    ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request);

    ResponseResult logIn(String captcha, String captchaKey, SobUser sobUser, HttpServletRequest request, HttpServletResponse response);
    SobUser checkSobUser(HttpServletRequest request,HttpServletResponse response);

    ResponseResult getUserInfo(String userId);

    ResponseResult checkEmail(String email);

    ResponseResult checkUserName(String userName);

    ResponseResult updateUserInfo(String userId, SobUser sobUser, HttpServletResponse response, HttpServletRequest request);

    ResponseResult deleteUserById(String userId, HttpServletRequest request, HttpServletResponse response);

}
