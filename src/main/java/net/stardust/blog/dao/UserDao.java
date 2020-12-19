package net.stardust.blog.dao;

import net.stardust.blog.pojo.SobUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<SobUser, String>, JpaSpecificationExecutor<SobUser> {
    /**
     * 根据用户名查找
     *
     * @param userName
     * @return
     */
    SobUser findOneByUserName(String userName);

    /**
     * 通过邮箱查找
     *
     * @param email
     * @return
     */
    SobUser findOneByEmail(String email);

    /**
     * 通过userId查找
     *
     * @param userId
     * @return
     */
    SobUser findOneById(String userId);

    /**
     * 通过修改用户的状态来删除用户
     *
     * @param userId
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET `state` = '0' WHERE `id` = ?")
    int deleteUserByState(String userId);

    @Query(value = "select new SobUser(u.id,u.userName,u.roles,u.avatar,u.email,u.sign,u.state,u.regIp,u.loginIp,u.createTime,u.updateTime) from SobUser as u")
    Page<SobUser> listAllUserNoPassword(Pageable pageable);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET `password` = ?1 WHERE `email` = ?2")
    int updatePasswordByEmail(String encode, String email);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE `tb_user` SET `email` = ?1 WHERE `id` = ?2")
    int updateEmailById(String email, String id);
}
