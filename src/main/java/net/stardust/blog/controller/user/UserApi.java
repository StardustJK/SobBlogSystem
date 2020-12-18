package net.stardust.blog.controller.user;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.response.ResponseState;
import net.stardust.blog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private IUserService userService;

    /**
     * 初始化管理员账号init-admin
     *
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody SobUser sobUser, HttpServletRequest request) {
        log.info("username==> " + sobUser.getUserName());
        log.info("password==> " + sobUser.getPassword());
        log.info("email==>" + sobUser.getEmail());
        return userService.initManagerAccount(sobUser, request);
    }

    /**
     * 注册
     *
     * @param sobUser
     * @return
     */

    @PostMapping
    public ResponseResult register(@RequestBody SobUser sobUser,
                                   @RequestParam("email_code") String emailCode,
                                   @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("captcha_key") String captchaKey,
                                   HttpServletRequest request) {
        log.info("emailCode:" + emailCode);
        return userService.register(sobUser, emailCode, captchaCode, captchaKey, request);

    }

    /**
     * 登录
     * * 需要提交的数据
     * * 1.用户的账号：昵称/邮箱（唯一）
     * * 2.用户密码
     * * 3.图灵验证码
     * * 4.图灵验证码的key
     *
     * @param captchaKey
     * @param captcha
     * @param sobUser
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/{captcha}/{captcha_key}")
    public ResponseResult logIn(@PathVariable("captcha_key") String captchaKey,
                                @PathVariable String captcha,
                                @RequestBody SobUser sobUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        return userService.logIn(captcha, captchaKey, sobUser, request, response);
    }


    /**
     * 获取图灵验证码
     * 有效时长 10分钟
     *
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("captcha_key") String captchaKey) throws IOException, FontFormatException {
        try {
            userService.createCaptcha(response, captchaKey);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 发送邮件
     * 使用场景：
     * 注册
     * 找回密码
     * 修改邮箱
     *
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request,
                                         @RequestParam("type") String type,
                                         @RequestParam("email") String emailAddress) {
        log.info(emailAddress);
        return userService.sendEmail(type, request, emailAddress);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password/{userId}")
    public ResponseResult updatePassword(@RequestBody SobUser sobUser, @PathVariable("userId") String userId) {
        return null;
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 修改用户信息
     * 可修改内容：头像，用户名（唯一），密码（单独修改），签名，email（唯一，单独修改）
     */
    @PutMapping("/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId,
                                         @RequestBody SobUser sobUser,
                                         HttpServletResponse response,
                                         HttpServletRequest request
    ) {
        return userService.updateUserInfo(userId, sobUser,response,request);
    }

    /**
     * 获取用户列表
     * 权限:管理员
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page") int page,
                                    @RequestParam("size") int size,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        return userService.listUsers(page,size,request,response);
    }

    /**
     * 删除用户
     * 需要管理员权限
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        //判断当前操作用户，根据用户角色判断是否可以删除
        //TODO 通过注解的方式控制权限
        return userService.deleteUserById(userId,request,response);

    }

    /**
     * 检查该Email是否已经注册
     *
     * @param email 邮箱地址
     * @return SUCCESS-》已经注册，FAIL->没有注册
     */
    @ApiResponses({
            @ApiResponse(code = 20000, message = "表示当前邮箱已经注册"),
            @ApiResponse(code = 40000, message = "表示当前邮箱未注册")
    })
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email") String email) {
        return userService.checkEmail(email);
    }

    /**
     * 检查该用户名是否已经注册
     *
     * @param userName 用户名
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 20000, message = "表示当前用户名已经注册"),
            @ApiResponse(code = 40000, message = "表示当前用户名未注册")
    })
    @GetMapping("/user_name")
    public ResponseResult checkUserName(@RequestParam("userName") String userName) {
        return userService.checkUserName(userName);
    }
}
