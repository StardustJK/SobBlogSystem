package net.stardust.blog.dao;

import net.stardust.blog.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleDao extends JpaRepository<Article, String>, JpaSpecificationExecutor<Article> {
    Article findOneById(String articleId);

    int deleteAllById(String articleId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_article` set `state`='0' where `id`=?")
    int deleteArticleByState(String articleId);

}
