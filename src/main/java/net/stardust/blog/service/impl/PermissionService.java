package net.stardust.blog.service.impl;

import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.service.IUserService;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.CookieUtils;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("permission")
public class PermissionService {
    @Autowired
    private IUserService userService;
    /**
     * 判断是不是管理员
     * @return
     */
    public boolean admin(){
        ServletRequestAttributes requestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=requestAttributes.getRequest();
        String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
        //没有登录，不用往下了
        if (TextUtils.isEmpty(tokenKey)) {
            return false;
        }
        SobUser sobUser=userService.checkSobUser();
        if (sobUser == null) {
            return false;
        }
        if (sobUser.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return true;
        }
        return false;
    }
}
