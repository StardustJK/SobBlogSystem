import org.springframework.util.DigestUtils;

public class TestCreateJwtMd5Value {
    public static void main(String[] args) {
        String jwtKeyMd5 = DigestUtils.md5DigestAsHex("sob_blog_system_-=".getBytes());
        System.out.println(jwtKeyMd5);

    }
}
