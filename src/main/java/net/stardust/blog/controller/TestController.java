package net.stardust.blog.controller;

import net.stardust.blog.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "hello world!";
    }

    @GetMapping("/testJson")
    public User testJson(){
        User user=new User(11,"male");
        return user;
    }


}