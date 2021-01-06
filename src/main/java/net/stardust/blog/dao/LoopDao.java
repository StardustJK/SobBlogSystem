package net.stardust.blog.dao;

import net.stardust.blog.pojo.Looper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoopDao extends JpaSpecificationExecutor<Looper>, JpaRepository<Looper,String> {
    Looper findOneById(String looperId);
}
