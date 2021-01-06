package net.stardust.blog.service;

import net.stardust.blog.pojo.Article;
import net.stardust.blog.response.ResponseResult;

public interface IArticleService {
    ResponseResult postArticle(Article article);
}
