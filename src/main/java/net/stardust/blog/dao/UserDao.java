package net.stardust.blog.dao;

import net.stardust.blog.pojo.SobUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<SobUser,String>, JpaSpecificationExecutor<SobUser> {
}
