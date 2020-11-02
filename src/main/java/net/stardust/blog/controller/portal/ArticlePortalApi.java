package net.stardust.blog.controller.portal;

import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page") int page,@PathVariable("size") int size){
        return null;
    }

    @GetMapping("/list/{categoryId}/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("categoryId") String categoryId,
                                      @PathVariable("page") int page,
                                      @PathVariable("size") int size){
        return null;
    }

    @GetMapping("/{articleId}")
    public ResponseResult getArticleDetail(@PathVariable("articleId") String articleId){
        return null;
    }

    @GetMapping("/recommend/{articleId}")
    public ResponseResult getRecommendArticles(@PathVariable("articleId") String articleId){
        return null;
    }

}
