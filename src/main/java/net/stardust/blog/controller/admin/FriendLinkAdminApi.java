package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.FriendLink;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.impl.FriendLinkServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkAdminApi {

    @Autowired
    private FriendLinkServiceImpl friendLinkService;

    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addFriendsLink(@RequestBody FriendLink friendLink){
        return friendLinkService.addFriendsLink(friendLink);
    }

    @DeleteMapping("/{friendLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLinkId") String friendLinkId){
        return friendLinkService.deleteFriendLink(friendLinkId);
    }
    @PutMapping("/{friendLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendLinkId") String friendLinkId,@RequestBody FriendLink friendLink){
        return friendLinkService.updateFriendLink(friendLinkId,friendLink);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/{friendLinkId}")
    public ResponseResult getFriendLink(@PathVariable("friendLinkId") String friendLinkId){
        return friendLinkService.getFriendLink(friendLinkId);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listFriendLink(@PathVariable("page") int page,@PathVariable("size") int size){
        return friendLinkService.listFriendLink(page,size);
    }
}
