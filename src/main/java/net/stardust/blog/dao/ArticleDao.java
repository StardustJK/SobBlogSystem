package net.stardust.blog.dao;

import net.stardust.blog.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleDao extends JpaRepository<Article,String>, JpaSpecificationExecutor<Article> {
}
