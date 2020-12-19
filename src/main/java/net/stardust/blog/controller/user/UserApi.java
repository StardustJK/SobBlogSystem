package net.stardust.blog.controller.user;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
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
     * /@return
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

    @PostMapping("/register")
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
    @PostMapping("/login/{captcha}/{captcha_key}")
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
     * 修改密码和找回密码
     * 普通做法：通过旧密码对比来更新密码
     * 找回/修改密码：发送验证码到邮箱/手机，判断验证码是否正确。
     * 1.用户填写邮箱
     * 2.用户获取验证码 type=forget
     * 3.填写验证码
     * 4.填写新密码
     * 5.提交数据
     * <p>
     * 数据包括
     * 1.邮箱和新密码
     * 2.验证码
     */
    @PutMapping("/password/{verifyCode}")
    public ResponseResult updatePassword(@RequestBody SobUser sobUser, @PathVariable("verifyCode") String verifyCode) {
        return userService.updatePassword(sobUser, verifyCode);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 修改用户信息
     * 可修改内容：头像，用户名（唯一），密码（单独修改），签名，email（唯一，单独修改）
     */
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId,
                                         @RequestBody SobUser sobUser) {
        return userService.updateUserInfo(userId, sobUser);
    }

    /**
     * 获取用户列表
     * 权限:管理员
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page") int page,
                                    @RequestParam("size") int size
                                    ) {
        return userService.listUsers(page, size);
    }

    /**
     * 删除用户
     * 需要管理员权限
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId) {
        //判断当前操作用户，根据用户角色判断是否可以删除
        return userService.deleteUserById(userId);

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

    /**
     * 1.必须登录
     * 2.新的邮箱没有注册过
     * <p>
     * 用户步骤
     * 1.已经登录
     * 2.输入新的邮箱
     * 3.获取验证码type=update
     * 4.输入验证码
     * 5.提交数据
     * <p>
     * 数据：
     * 1.新的邮箱地址
     * 2.验证码
     * 3.其他信息从token获取
     *
     * @return
     */
    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("email") String email,
                                      @RequestParam("verify_code") String verifyCode) {
        return userService.updateEmail(email, verifyCode);
    }

    /**
     * 退出登录
     * 1.拿到tokenKey
     * 2.删除redis对应的token
     * 3.删除mysql中的refreshToken
     * 4.删除cookie里的tokenKey
     * @return
     */
    @GetMapping("/logout")
    public ResponseResult logout(){
        return userService.logout();
    }
}
