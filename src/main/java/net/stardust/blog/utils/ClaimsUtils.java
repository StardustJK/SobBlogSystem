package net.stardust.blog.utils;

import io.jsonwebtoken.Claims;
import net.stardust.blog.pojo.SobUser;

import java.util.HashMap;
import java.util.Map;

public class ClaimsUtils {

    public static final String ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String ROLES = "roles";
    public static final String AVATAR = "avatar";
    public static final String EMAIL = "email";
    public static final String SIGN = "sign";

    public static Map<String, Object> sobUser2Claims(SobUser sobUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID, sobUser.getId());
        claims.put(USER_NAME, sobUser.getUserName());
        claims.put(ROLES, sobUser.getRoles());
        claims.put(AVATAR, sobUser.getAvatar());
        claims.put(EMAIL, sobUser.getEmail());
        claims.put(SIGN, sobUser.getSign());
        return claims;
    }

    public static SobUser claims2SobUser(Claims claims) {
        SobUser sobUser = new SobUser();
        String id = (String) claims.get(ID);
        sobUser.setId(id);
        String userName = (String) claims.get(USER_NAME);
        sobUser.setUserName(userName);
        String roles = (String) claims.get(ROLES);
        sobUser.setRoles(roles);
        String avatar = (String) claims.get(AVATAR);
        sobUser.setAvatar(avatar);
        String email = (String) claims.get(EMAIL);
        sobUser.setEmail(email);
        String sign = (String) claims.get(SIGN);
        sobUser.setSign(sign);
        return sobUser;
    }
}
