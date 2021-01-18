package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.Article;
import net.stardust.blog.pojo.Looper;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Action;

@RestController
@RequestMapping("/admin/article")
public class ArticleAdminApi {

    @Autowired
    private IArticleService articleService;

    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article){
        return articleService.postArticle(article);
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

    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticles(@PathVariable("page") int page,@PathVariable("size") int size,
                                       @RequestParam(value = "keyword",required = false)String keyword,
                                       @RequestParam(value = "categoryId",required = false)String categoryId,
                                       @RequestParam(value = "state",required = false)String state){
        return articleService.listArticles(page,size,keyword,categoryId,state);
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
