package net.stardust.blog.service.impl;

import net.stardust.blog.dao.SettingsDao;
import net.stardust.blog.dao.UserDao;
import net.stardust.blog.pojo.Setting;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IUserService;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;


@Service
@Transactional
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
        Setting managerAccountState=settingsDao.findOneByKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if(managerAccountState!=null){
            return ResponseResult.FAILED("管理员账号已经初始化");
        }
        //检查数据
        if (TextUtils.isEmpty(sobUser.getUserName())){
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getPassword())){
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getEmail())){
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
        String password=sobUser.getPassword();
        String password_encode=bCryptPasswordEncoder.encode(password);
        sobUser.setPassword(password_encode);


        //保存至数据库
        userDao.save(sobUser);
        Setting setting=new Setting();
        setting.setKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        setting.setId(idWorker.nextId()+"");
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        setting.setValue("1");
        settingsDao.save(setting);
        return ResponseResult.SUCCESS("初始化成功");
    }
}
