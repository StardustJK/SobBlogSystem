package net.stardust.blog.dao;

import net.stardust.blog.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CategoryDao extends JpaRepository<Category,String>, JpaSpecificationExecutor<Category> {
    Category findOneById(String id);

    @Modifying
    @Query(nativeQuery = true,value = "update `tb_categories` set `status`='0' where `id`=?")
    int deleteCategoryByUpdateStatus(String categoryId);
}
