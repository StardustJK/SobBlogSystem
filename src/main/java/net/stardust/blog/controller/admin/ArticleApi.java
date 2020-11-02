package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.Article;
import net.stardust.blog.pojo.Looper;
import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/article")
public class ArticleApi {
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article){
        return null;
    }

    @DeleteMapping("/{articleId}")
    public ResponseResult deleteArticle(@PathVariable("articleId") String articleId){
        return null;
    }
    @PutMapping("/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId") String articleId){
        return null;
    }
    @GetMapping("/{articleId}")
    public ResponseResult getArticle(@PathVariable("articleId") String articleId){
        return null;
    }

    @GetMapping("/list")
    public ResponseResult listArticles(@RequestParam("page") int page,@RequestParam("size") int size){
        return null;
    }

    @PutMapping("/state/{articleId}/{state}")
    public ResponseResult updateArticleStatus(@PathVariable("articleId") String articleId,@PathVariable("state") String state){
        return null;
    }
    @PutMapping("/state/{articleId}")
    public ResponseResult updateArticleStatus(@PathVariable("articleId") String articleId){
        return null;
    }
}
