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
    public ResponseResult register(@RequestBody SobUser sobUser) {
        //1.检查当前用户名是否被注册

        //2.检查邮箱格式是否正确

        //3.检查该邮箱是否被注册

        //4.检查邮箱验证码是否正确

        //5.检查图灵验证码是否正确

        //6 对密码进行加密
        //7 补全数据
        //8 存数据
        //9 返回结果
        return null;
    }

    /**
     * 登陆
     */

    @PostMapping("/{captcha}")
    public ResponseResult login(@PathVariable String captcha, @RequestBody SobUser sobUser) {
        return null;
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
     *
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request,@RequestParam("email") String emailAddress) {
        log.info(emailAddress);
        return userService.sendEmail(request,emailAddress);
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
