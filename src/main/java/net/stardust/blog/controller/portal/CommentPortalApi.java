package net.stardust.blog.controller.portal;

import net.stardust.blog.pojo.Comment;
import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("portal/comment")
public class CommentPortalApi {
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment){
        return null;
    }

    @PutMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId){
        return null;
    }
    @GetMapping("/{commentId}")
    public ResponseResult getComment(@PathVariable("commentId") String commentId){
        return null;
    }

    @GetMapping("/list")
    public ResponseResult listComments(@RequestParam("page") int page,@RequestParam("size") int size){
        return null;
    }
}
