import net.stardust.blog.utils.EmailSender;

import javax.mail.MessagingException;

public class EmailSenderTest {
    public static void main(String [] args) throws MessagingException {
        EmailSender.subject("test email sender")
                .from("sobBlogSystem")
                .text("验证码为：test")
                .to("1115159016@qq.com")
                .send();

    }


}
