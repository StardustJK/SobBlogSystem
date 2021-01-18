package net.stardust.blog.dao;

import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.pojo.SobUserNoPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserNoPasswordDao extends JpaRepository<SobUserNoPassword, String>, JpaSpecificationExecutor<SobUserNoPassword> {


}
