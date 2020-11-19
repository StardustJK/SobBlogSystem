package net.stardust.blog;

import net.stardust.blog.utils.RedisUtil;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Random;

@EnableSwagger2
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args){
        SpringApplication.run(BlogApplication.class,args);
    }

    @Bean
    public SnowFlakeIdWorker createIdWorker(){
        return new SnowFlakeIdWorker(0,0);
    }

    @Bean
    public BCryptPasswordEncoder createPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object>template=new RedisTemplate<>();
        //关联
        template.setConnectionFactory(factory);
        //设置key的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        //设置value的序列化器
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisUtil createRedisUtils(){
        return new RedisUtil();
    }

    @Bean
    public Random createRandom(){
        return new Random();
    }
}
