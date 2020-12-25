package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.Comment;
import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {

    @PutMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId){
        return null;
    }

    @GetMapping("/list")
    public ResponseResult listComments(@RequestParam("page") int page,@RequestParam("size") int size){
        return null;
    }

    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId") String commentId){
        return null;
    }
}
