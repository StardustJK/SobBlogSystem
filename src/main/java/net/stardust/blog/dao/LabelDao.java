package net.stardust.blog.dao;

import net.stardust.blog.pojo.Labels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LabelDao extends JpaRepository<Labels,String>, JpaSpecificationExecutor<Labels> {

    Labels findOneByName(String name);
}
