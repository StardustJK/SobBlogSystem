package net.stardust.blog.service;

import net.stardust.blog.pojo.Article;
import net.stardust.blog.response.ResponseResult;

public interface IArticleService {
    ResponseResult postArticle(Article article);

    ResponseResult listArticles(int page, int size, String keyword, String categoryId, String state);

    ResponseResult getArticleById(String articleId);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult deleteArticle(String articleId);
}
