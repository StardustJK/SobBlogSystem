package net.stardust.blog.service.impl;

import com.sun.org.apache.bcel.internal.generic.ARETURN;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.dao.SettingsDao;
import net.stardust.blog.dao.UserDao;
import net.stardust.blog.pojo.Setting;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IUserService;
import net.stardust.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.Random;


@Service
@Transactional
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private SnowFlakeIdWorker idWorker;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SettingsDao settingsDao;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request) {
        //检测是否初始化
        Setting managerAccountState = settingsDao.findOneByKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化");
        }
        //检查数据
        if (TextUtils.isEmpty(sobUser.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getEmail())) {
            return ResponseResult.FAILED("邮箱不能为空");
        }
        //补充数据
        sobUser.setId(String.valueOf(idWorker.nextId()));
        sobUser.setRoles(Constants.User.ROLE_ADMIN);
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setState(Constants.User.DEFAULT_STATE);
        sobUser.setRegIp(request.getRemoteAddr());
        sobUser.setLoginIp(request.getRemoteAddr());
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());

        //加密密码
        String password = sobUser.getPassword();
        String password_encode = bCryptPasswordEncoder.encode(password);
        sobUser.setPassword(password_encode);


        //保存至数据库
        userDao.save(sobUser);
        Setting setting = new Setting();
        setting.setKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        setting.setId(idWorker.nextId() + "");
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        setting.setValue("1");
        settingsDao.save(setting);
        return ResponseResult.SUCCESS("初始化成功");
    }

    public static final int[] captcha_font_types = {Captcha.FONT_1,
            Captcha.FONT_2,
            Captcha.FONT_3,
            Captcha.FONT_4,
            Captcha.FONT_5,
            Captcha.FONT_6,
            Captcha.FONT_7,
            Captcha.FONT_8,
            Captcha.FONT_9,

    };

    @Autowired
    Random random;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void createCaptcha(HttpServletResponse response, String captchaKey) throws IOException, FontFormatException {
        if (TextUtils.isEmpty(captchaKey) || captchaKey.length() < 13) {
            return;
        }
        Long key = 0l;
        try {
            key = Long.parseLong(captchaKey);
        } catch (Exception e) {
            return;
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(200, 60, 5);
        // 设置字体
        int index = random.nextInt(captcha_font_types.length);
        log.info("captcha font index : " + index);
        specCaptcha.setFont(captcha_font_types[index]);
        // 设置类型
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = specCaptcha.text().toLowerCase();
        log.info("captcha content == > " + content);
        //验证码存入redis
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + key, content, 60 * 10);
        // 输出图片流
        specCaptcha.out(response.getOutputStream());

    }

    /**
     * 发送邮箱的验证码
     *
     * @param request
     * @param emailAddress
     * @return
     */
    @Override
    public ResponseResult sendEmail(HttpServletRequest request, String emailAddress) {
        //1 防止暴力发送（不断发送）。同一个邮箱间隔要超过30s，同一个IP最多发10次（短信5次）
        String remoteAddr = request.getRemoteAddr();
        log.info("sendEmail == > ip == >" + remoteAddr);
        Integer ipSendTime = (Integer) redisUtil.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
        if (ipSendTime != null || ipSendTime > 10) {
            return ResponseResult.FAILED("发送验证码过于频繁");
        }
        Integer addressSendTime = (Integer) redisUtil.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if (addressSendTime != null || addressSendTime > 10) {
            return ResponseResult.FAILED("发送验证码过于频繁");
        }

        //2 检查邮箱地址是否正确
        boolean isEmailFormatOk=TextUtils.isEmailAddressValid(emailAddress);
        if(!isEmailFormatOk){
            return ResponseResult.FAILED("邮箱地址不正确");
        }


        //3 发送验证码6位数100000-999999
        int code=random.nextInt(999999);
        if(code<100000){
            code+=100000;
        }
        log.info("sendEmail code == > "+code);
        try {
            EmailSender.sendRegisterVerifyCode(code+"",emailAddress);
        } catch (MessagingException e) {
            return ResponseResult.FAILED("验证码发送失败，请稍后重试");
        }

        //4 做记录 发送记录和code
        if(ipSendTime==null){
            ipSendTime=0;
        }
        ipSendTime++;
        //一小时有效期
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_IP+remoteAddr,ipSendTime,60*60);
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_ADDRESS+emailAddress,"send",30);
        //保存code


        return null;
    }
}
