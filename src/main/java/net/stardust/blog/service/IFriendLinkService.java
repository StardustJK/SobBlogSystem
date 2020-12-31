package net.stardust.blog.service;

import net.stardust.blog.pojo.Category;
import net.stardust.blog.pojo.FriendLink;
import net.stardust.blog.response.ResponseResult;

public interface IFriendLinkService {

    ResponseResult addFriendsLink(FriendLink friendLink);

    ResponseResult getFriendLink(String friendLinkId);

    ResponseResult listFriendLink(int page, int size);

    ResponseResult deleteFriendLink(String friendLinkId);

    ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLink);
}
