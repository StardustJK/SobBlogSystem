package net.stardust.blog.dao;

import net.stardust.blog.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryDao extends JpaRepository<Category,String>, JpaSpecificationExecutor<Category> {
}
