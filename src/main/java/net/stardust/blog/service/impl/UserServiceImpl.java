package net.stardust.blog.service.impl;

import com.google.gson.Gson;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.dao.RefreshTokenDao;
import net.stardust.blog.dao.SettingsDao;
import net.stardust.blog.dao.UserDao;
import net.stardust.blog.pojo.RefreshToken;
import net.stardust.blog.pojo.Setting;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.response.ResponseState;
import net.stardust.blog.service.IUserService;
import net.stardust.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.security.krb5.internal.crypto.CksumType;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    private String userName;

    @Autowired
    private Gson gson;

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
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + key, content, Constants.TimeValue.MIN * 10);
        // 输出图片流
        specCaptcha.out(response.getOutputStream());

    }

    @Autowired
    TaskService taskService;

    /**
     * 发送邮箱的验证码
     *
     * @param request
     * @param emailAddress
     * @return
     */
    @Override
    public ResponseResult sendEmail(String type, HttpServletRequest request, String emailAddress) {
        if (emailAddress == null) {
            return ResponseResult.FAILED("邮箱地址不可以为空");
        }
        //根据类型查询邮箱是否存在
        if (type.equals("register") || type.equals("update")) {
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail != null) {
                return ResponseResult.FAILED("该邮箱已注册");
            }
        } else if (type.equals("forget")) {
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail == null) {
                return ResponseResult.FAILED("该邮箱未注册");
            }
        }
        //1 防止暴力发送（不断发送）。同一个邮箱间隔要超过30s，同一个IP最多发10次（短信5次）
        String remoteAddr = request.getRemoteAddr();
        log.info("sendEmail == > ip == >" + remoteAddr);
        if (remoteAddr != null) {
            remoteAddr = remoteAddr.replaceAll(":", "-");
        }
        Integer ipSendTime = (Integer) redisUtil.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
        log.info("ipsendtime==>" + ipSendTime);
        if (ipSendTime != null && ipSendTime > 10) {
            return ResponseResult.FAILED("发送验证码过于频繁1");
        }
        Object hasEmailSend = redisUtil.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if (hasEmailSend != null) {
            return ResponseResult.FAILED("发送验证码过于频繁2");
        }

        //2 检查邮箱地址是否正确
        boolean isEmailFormatOk = TextUtils.isEmailAddressValid(emailAddress);
        if (!isEmailFormatOk) {
            return ResponseResult.FAILED("邮箱地址不正确");
        }


        //3 发送验证码6位数100000-999999
        int code = random.nextInt(999999);
        if (code < 100000) {
            code += 100000;
        }
        log.info("sendEmail code == > " + code);
        try {
            taskService.sendEmailVerifyCode(code + "", emailAddress);
        } catch (MessagingException e) {
            return ResponseResult.FAILED("验证码发送失败，请稍后重试");
        }

        //4 做记录 发送记录和code
        if (ipSendTime == null) {
            ipSendTime = 0;
        }
        ipSendTime++;
        //一小时有效期
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr, ipSendTime + "", 60 * 60);
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", 30);
        //保存code
        redisUtil.set(Constants.User.KEY_EMAIL_CODE_CONTENT + emailAddress, code + "", 60 * 10);

        return ResponseResult.SUCCESS("验证码发送成功");
    }

    @Override
    public ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request) {
        //1.检查当前用户名是否被注册
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("用户名不可以为空");
        }
        SobUser userByName = userDao.findOneByUserName(userName);
        if (userByName != null) {
            return ResponseResult.FAILED("该用户名已注册");
        }
        //2.检查邮箱格式是否正确
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱地址不可以为空");
        }
        if (!TextUtils.isEmailAddressValid(email)) {
            return ResponseResult.FAILED("邮箱格式不正确");
        }

        //3.检查该邮箱是否被注册
        SobUser userByEmail = userDao.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("该邮箱已被注册");
        }
        //4.检查邮箱验证码是否正确
        String emailVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(emailVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码已过期");
        }
        if (!emailVerifyCode.equals(emailCode)) {
            return ResponseResult.FAILED("邮箱验证码不正确");
        } else {
            //正确，删掉redis里面的内容
            redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        }
        //5.检查图灵验证码是否正确
        String captchaVerifyCode = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (TextUtils.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("人类验证码过期");
        }
        if (!captchaVerifyCode.equals(captchaCode)) {
            return ResponseResult.FAILED("人类验证码不正确");
        } else {
            redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        }
        //6 对密码进行加密
        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不能为空");
        }
        sobUser.setPassword(bCryptPasswordEncoder.encode(sobUser.getPassword()));
        //7 补全数据
        String ipAddress = request.getRemoteAddr();
        sobUser.setRegIp(ipAddress);
        sobUser.setLoginIp(ipAddress);
        sobUser.setUpdateTime(new Date());
        sobUser.setCreateTime(new Date());
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setRoles(Constants.User.ROLE_NORMAL);
        sobUser.setId(idWorker.nextId() + "");
        sobUser.setState("1");
        //8 存数据
        userDao.save(sobUser);
        //9 返回结果
        return ResponseResult.GET(ResponseState.JOIN_IN_SUCCESS);
    }

    @Autowired
    private RefreshTokenDao refreshTokenDao;

    /**
     * 登录的实现 sign-in
     *
     * @param captcha
     * @param captchaKey
     * @param sobUser
     * @param request
     * @param response
     * @return
     */
    @Override
    public ResponseResult logIn(String captcha, String captchaKey, SobUser sobUser, HttpServletRequest request, HttpServletResponse response) {

        String captchaValue = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        //todo 取到的captchaValue为空呢？
        if (!captcha.equals(captchaValue)) {
            return ResponseResult.FAILED("人类验证码不正确");
        }

        //可能是邮箱或者用户名
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("帐号不可以为空");
        }

        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }

        SobUser userFromDb = userDao.findOneByUserName(userName);
        if (userFromDb == null) {
            userFromDb = userDao.findOneByEmail(userName);
        }
        if (userFromDb == null) {
            return ResponseResult.FAILED("用户名或者密码错误");
        }
        //用户存在
        //对比密码
        boolean matches = bCryptPasswordEncoder.matches(password, userFromDb.getPassword());
        if (!matches) {
            return ResponseResult.FAILED("用户名或者密码错误");
        }
        //密码正确
        //判断用户状态，如果是非正常的，则返回结果
        if (!"1".equals(userFromDb.getState())) {
            return ResponseResult.ACCOUNT_DENIED();
        }
        createToken(response, userFromDb);
        return ResponseResult.SUCCESS("登录成功");
    }

    private String createToken(HttpServletResponse response, SobUser userFromDb) {
        refreshTokenDao.deleteAllByUserId(userFromDb.getId());
        Map<String, Object> claims = ClaimsUtils.sobUser2Claims(userFromDb);
        //默认2小时有效
        String token = JwtUtil.createToken(claims);
        //返回token的md5，token保存在redis
        //如果前端访问的时候携带token的md5key,从redis获取即可
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        //保存token到redis,有效期2h
        redisUtil.set(Constants.User.KEY_TOKEN + tokenKey, token, Constants.TimeValue.HOUR * 2);

        //把tokenKey写到cookies
        //从request动态获取
        CookieUtils.setUpCookie(response, Constants.User.COOKIE_TOKEN_KEY, tokenKey);
        //生成RefreshToken
        String refreshTokenValue = JwtUtil.createRefreshToken(userFromDb.getId(), Constants.TimeValue.MONTH * 1000);
        //保存到数据库
        //refreshToken,tokenKey,用户ID，创建时间，更新时间
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(idWorker.nextId() + "");
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userFromDb.getId());
        refreshToken.setTokenKey(tokenKey);
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenDao.save(refreshToken);
        return tokenKey;
    }

    /**
     * 通过携带的tokenkey检查用户是否有登录，如果登录了就返回用户信息
     *
     * @return
     */
    @Override
    public SobUser checkSobUser() {

        //拿到token_key
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        log.info("tokenKey==> " + tokenKey);
        SobUser sobUser = parseByTokenKey(tokenKey);
        if (sobUser == null) {
            //解析出错->过期了
            //1.去mysql查refreshtoken
            RefreshToken refreshToken = refreshTokenDao.findOneByTokenKey(tokenKey);
            //2.不存在，则没登陆
            if (refreshToken == null) {
                log.info("refreshToken为空");
                return null;
            }
            //3.存在，解析refreshtoken，
            try {
                JwtUtil.parseJWT(refreshToken.getRefreshToken());
                //有效，创建新的token和refreshToken
                String userId = refreshToken.getUserId();
                SobUser userFromDb = userDao.findOneById(userId);
                String newTokenKey = createToken(getResponse(), userFromDb);
                log.info("创建了新的refreshToken和Token");
                return parseByTokenKey(newTokenKey);
            } catch (Exception e1) {
                //4.refreshtoken过期了，提示用户登录
                log.info("refreshToken过期");
                return null;

            }
        }

        return sobUser;
    }

    @Override
    public ResponseResult getUserInfo(String userId) {
        //从数据库获取
        SobUser userFromDb = userDao.findOneById(userId);
        if (userFromDb == null) {
            return ResponseResult.FAILED("用户不存在");
        }
        //克隆，清除敏感信息(一定要克隆！不然就修改数据库里面的内容)
        String userJson = gson.toJson(userFromDb);
        SobUser newUser = gson.fromJson(userJson, SobUser.class);
        newUser.setPassword("");
        newUser.setEmail("");
        newUser.setRegIp("");
        newUser.setLoginIp("");
        return ResponseResult.SUCCESS("获取成功").setData(newUser);


    }

    @Override
    public ResponseResult checkEmail(String email) {
        SobUser user = userDao.findOneByEmail(email);
        return user == null ? ResponseResult.FAILED("该邮箱未注册") : ResponseResult.SUCCESS("该邮箱已经注册");
    }

    @Override
    public ResponseResult checkUserName(String userName) {
        SobUser user = userDao.findOneByUserName(userName);
        return user == null ? ResponseResult.FAILED("该用户名未注册") : ResponseResult.SUCCESS("该用户名已经注册");
    }

    @Override
    public ResponseResult updateUserInfo(String userId, SobUser sobUser) {
        //检查是否已经登录
        SobUser userFromTokenKey = checkSobUser();
        if (userFromTokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        SobUser userFromDb = userDao.findOneById(userFromTokenKey.getId());
        //判断用户id是否一致
        if (!userFromDb.getId().equals(userId)) {
            return ResponseResult.FAILED("无权限修改");
        }
        //用户名
        if (!TextUtils.isEmpty(sobUser.getUserName())) {
            SobUser oneByUserName = userDao.findOneByUserName(sobUser.getUserName());
            if (oneByUserName != null) {
                return ResponseResult.FAILED("该用户名已注册");
            }
            userFromDb.setUserName(sobUser.getUserName());
        }
        //头像
        if (!TextUtils.isEmpty(sobUser.getAvatar())) {
            userFromDb.setAvatar(sobUser.getAvatar());
        }
        //签名，可空
        userFromDb.setSign(sobUser.getSign());
        userDao.save(userFromDb);
        //更新redis里面的token（token里面的用户名是旧的）
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        redisUtil.del(Constants.User.KEY_TOKEN + tokenKey);
        return ResponseResult.SUCCESS("用户信息修改成功");

    }

    /**
     * 删除用户，不是真的删除，是修改了状态，需要管理员权限
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult deleteUserById(String userId) {
        int result = userDao.deleteUserByState(userId);
        if (result > 0) {
            return ResponseResult.SUCCESS("删除成功");
        } else {
            return ResponseResult.FAILED("用户不存在");
        }

    }

    @Override
    public ResponseResult listUsers(int page, int size) {

        //分页查询
        if (page < Constants.Page.DEFAULT_PAGE) {
            page = 1;
        }
        //限制size,每页不少于10
        if (size < Constants.Page.MIN_SIZE) {
            size = Constants.Page.MIN_SIZE;
        }
        //根据注册日期降序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<SobUser> all = userDao.listAllUserNoPassword(pageable);
        return ResponseResult.SUCCESS("成功获取用户列表").setData(all);

    }

    @Override
    public ResponseResult updatePassword(SobUser sobUser, String verifyCode) {
        //检查邮箱是否填写
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱不可以为空");
        }
        //根据在redis验证
        String redisVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (redisVerifyCode == null || !redisVerifyCode.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码错误");
        }
        redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        int result = userDao.updatePasswordByEmail(bCryptPasswordEncoder.encode(sobUser.getPassword()), email);
        return result > 0 ? ResponseResult.SUCCESS("修改密码成功") : ResponseResult.FAILED("修改密码失败");

    }

    @Override
    public ResponseResult updateEmail(String email, String verifyCode) {
        //1.确保已经登录
        SobUser sobUser = checkSobUser();
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //2.对比验证码
        String redisVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(redisVerifyCode)) {
            return ResponseResult.FAILED("验证码过期");
        }
        if (!redisVerifyCode.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码错误");
        }
        //3.修改邮箱
        int result = userDao.updateEmailById(email, sobUser.getId());

        return result > 0 ? ResponseResult.SUCCESS("邮箱修改成功") : ResponseResult.FAILED("邮箱修改失败");
    }
    private HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }
    private HttpServletResponse getResponse(){
        ServletRequestAttributes requestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }
    @Override
    public ResponseResult logout() {
        //拿tokenKey
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        if (TextUtils.isEmpty(tokenKey)) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //删除redis里的token
        redisUtil.del(Constants.User.KEY_TOKEN+tokenKey);
        //删除mysql的refreshToken
        refreshTokenDao.deleteAllByTokenKey(tokenKey);
        //删除cookie里面的tokenKey
        CookieUtils.deleteCookie(getResponse(),Constants.User.COOKIE_TOKEN_KEY);
        return ResponseResult.SUCCESS("成功退出登录");
    }

    private SobUser parseByTokenKey(String tokenKey) {
        String token = (String) redisUtil.get(Constants.User.KEY_TOKEN + tokenKey);
        if (token != null) {
            try {
                Claims claims = JwtUtil.parseJWT(token);
                Date expiration = claims.getExpiration();
                return ClaimsUtils.claims2SobUser(claims);
            } catch (Exception e) {
                log.info("parseByTokenKey过期了");
                return null;
            }

        }
        return null;
    }

}
