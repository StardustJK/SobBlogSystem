package net.stardust.blog.utils;

public interface Constants {


    interface User {
        String ROLE_ADMIN = "role_admin";
        String ROLE_NORMAL = "role_normal";

        String DEFAULT_AVATAR = "https://cdn.sunofbeaches.com/images/default_avatar.png";
        String DEFAULT_STATE = "1";
        String KEY_CAPTCHA_CONTENT = "key_captcha_content_";
        String KEY_EMAIL_CODE_CONTENT = "key_email_code_content_";
        String KEY_EMAIL_SEND_IP = "key_email_send_ip_";
        String KEY_EMAIL_SEND_ADDRESS = "key_email_send_address_";
        String KEY_TOKEN = "key_token_";
        String COOKIE_TOKEN_KEY = "sob_blog_token";

    }

    interface Settings {
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
    }

    interface Page {
        int DEFAULT_PAGE = 1;
        int MIN_SIZE = 10;
    }

    /**
     * 单位为秒
     */
    interface TimeValueInSecond {
        int MIN = 60;
        int HOUR = 60 * MIN;
        int DAY = 24 * HOUR;
        int WEEK = 7 * DAY;
        int MONTH = DAY * 30;

        int HOUR_2 = 2 * HOUR;
    }

    //单位是ms
    interface TimeValueInMillions {
        long MIN = 60 * 1000;
        long HOUR = 60 * MIN;
        long DAY = 24 * HOUR;
        long WEEK = 7 * DAY;
        long MONTH = DAY * 30;

        long HOUR_2 = 2 * HOUR;
    }

    interface ImageType{
        String PREFIX="image/";
        String TYPE_JPG="jpg";
        String TYPE_PNG="png";
        String TYPE_GIF="gif";
        String TYPE_JPG_WITH_PREFIX=PREFIX+"jpg";
        String TYPE_PNG_WITH_PREFIX=PREFIX+"png";
        String TYPE_GIF_WITH_PREFIX =PREFIX+"gif";
    }
}