package net.stardust.blog;

import net.stardust.blog.utils.SnowFlakeIdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
}
