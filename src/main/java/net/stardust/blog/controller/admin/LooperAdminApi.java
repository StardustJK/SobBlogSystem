package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.Looper;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.ILoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/loop")
public class LooperAdminApi {
    @Autowired
    private ILoopService loopService;

    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper looper){
        return loopService.addLoop(looper);
    }
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{looperId}")
    public ResponseResult deleteLoop(@PathVariable("looperId") String looperId){
        return loopService.deleteLoop(looperId);
    }
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{looperId}")
    public ResponseResult updateLoop(@PathVariable("looperId") String looperId,@RequestBody Looper looper){
        return loopService.updateLoop(looperId,looper);
    }
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{looperId}")
    public ResponseResult getLoop(@PathVariable("looperId") String looperId){
        return loopService.getLoop(looperId);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listLoops(@PathVariable("page") int page,@PathVariable("size") int size){
        return loopService.listLoops(page,size);
    }
}
