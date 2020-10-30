package net.stardust.blog.controller.user;

import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    /**
     * 初始化管理员账号init-admin
     *
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody SobUser sobUser) {
        log.info("username==> " + sobUser.getUserName());
        log.info("password==> " + sobUser.getPassword());
        log.info("email==>" + sobUser.getEmail());
        return ResponseResult.SUCCESS();
    }

    /**
     * 注册
     *
     * @param sobUser
     * @return
     */

    @PostMapping
    public ResponseResult register(@RequestBody SobUser sobUser) {
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
     *
     * @return
     */
    @GetMapping("/captcha")
    public ResponseResult getCaptcha() {
        return null;
    }

    /**
     * 发送邮件
     *
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("email") String emailAddress) {
        log.info(emailAddress);
        return ResponseResult.SUCCESS();
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
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId,@RequestBody SobUser sobUser) {
        return null;
    }
}
