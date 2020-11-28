package net.stardust.blog.dao;

import net.stardust.blog.pojo.SobUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserDao extends JpaRepository<SobUser,String>, JpaSpecificationExecutor<SobUser> {
    /**
     * 根据用户名查找
     * @param userName
     * @return
     */
    SobUser findOneByUserName(String userName);

    /**
     * 通过邮箱查找
     * @param email
     * @return
     */
    SobUser findOneByEmail(String email);


}
