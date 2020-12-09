package net.stardust.blog.controller.user;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IUserService;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.RedisUtil;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
                                   @RequestParam("captcha_code")String captchaCode,
                                   @RequestParam("captcha_key") String captchaKey,
                                   HttpServletRequest request) {
        log.info("emailCode:"+emailCode);
        return userService.register(sobUser,emailCode,captchaCode,captchaKey,request);

    }

    /**
     * 登录
     * 需要提交的数据
     * 1.用户的账号：昵称/邮箱（唯一）
     * 2.用户密码
     * 3.图灵验证码
     * 4.图灵验证码的key
     * @param captcha_key 图灵验证码的 key
     * @param captcha 图灵验证码
     * @param sobUser 封装了账号和密码
     * @return
     */
    @PostMapping("/{captcha}")
    public ResponseResult signIn(@PathVariable("captcha_key")String captchaKey,
                                 @PathVariable String captcha,
                                 @RequestBody SobUser sobUser,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        return userService.signIn(captcha,captchaKey,sobUser,request,response);
    }



    /**
     * 获取图灵验证码
     * 有效时长 10分钟
     *
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("captcha_key") String captchaKey) throws IOException, FontFormatException {
        try{
            userService.createCaptcha(response,captchaKey);
        }catch (Exception e){
            log.error(e.toString());
        }
    }

    /**
     * 发送邮件
     * 使用场景：
     *  注册
     *  找回密码
     *  修改邮箱
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request,
                                         @RequestParam("type") String type,
                                         @RequestParam("email") String emailAddress) {
        log.info(emailAddress);
        return userService.sendEmail(type,request,emailAddress);
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
        return null;
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId, @RequestBody SobUser sobUser) {
        return null;
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId) {
        return null;
    }


}
