package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.Looper;
import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/loop")
public class LooperApi {

    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper looper){
        return null;
    }

    @DeleteMapping("/{looperId}")
    public ResponseResult deleteLoop(@PathVariable("looperId") String looperId){
        return null;
    }
    @PutMapping("/{looperId}")
    public ResponseResult updateLoop(@PathVariable("looperId") String looperId){
        return null;
    }
    @GetMapping("/{looperId}")
    public ResponseResult getLoop(@PathVariable("looperId") String looperId){
        return null;
    }

    @GetMapping("/list")
    public ResponseResult listLoops(@RequestParam("page") int page,@RequestParam("size") int size){
        return null;
    }
}
