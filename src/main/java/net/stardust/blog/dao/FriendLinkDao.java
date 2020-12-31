package net.stardust.blog.dao;

import net.stardust.blog.pojo.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FriendLinkDao extends JpaSpecificationExecutor<FriendLink>, JpaRepository<FriendLink,String> {
    FriendLink findOneById(String id);

    @Modifying
    @Query(nativeQuery = true,value = "update `tb_friends` set `state`='0' where `id`=?")
    int deleteFriendLinkByUpdateStatus(String friendLinkId);
}
