package net.stardust.blog.dao;

import net.stardust.blog.pojo.Comment;
import net.stardust.blog.pojo.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RefreshTokenDao extends JpaRepository<RefreshToken,String>, JpaSpecificationExecutor<RefreshToken> {
    RefreshToken findOneByTokenKey(String tokenKey);
    int deleteAllByUserId(String userId);
}

