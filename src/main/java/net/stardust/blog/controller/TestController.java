package net.stardust.blog.controller;

import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TestController {


    @GetMapping("/hello")
    public ResponseResult hello(){
        log.info("hello world");
        return ResponseResult.SUCCESS("登陆成功");
    }




}