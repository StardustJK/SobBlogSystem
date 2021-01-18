package net.stardust.blog.dao;

import net.stardust.blog.pojo.Article;
import net.stardust.blog.pojo.ArticleNoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleNoContentDao extends JpaRepository<ArticleNoContent,String>, JpaSpecificationExecutor<ArticleNoContent> {
    Article findOneById(String articleId);
}
