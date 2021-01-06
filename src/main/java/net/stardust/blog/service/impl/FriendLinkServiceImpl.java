package net.stardust.blog.service.impl;

import net.stardust.blog.dao.FriendLinkDao;
import net.stardust.blog.pojo.Category;
import net.stardust.blog.pojo.FriendLink;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IFriendLinkService;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class FriendLinkServiceImpl extends BaseService implements IFriendLinkService {

    @Autowired
    private SnowFlakeIdWorker idWorker;
    @Autowired
    private FriendLinkDao friendLinkDao;

    @Override
    public ResponseResult addFriendsLink(FriendLink friendLink) {
        String url = friendLink.getUrl();
        if (TextUtils.isEmpty(url)) {
            return ResponseResult.FAILED("链接不可以为空");
        }
        String logo = friendLink.getLogo();
        if (TextUtils.isEmpty(logo)) {
            return ResponseResult.FAILED("logo不可以为空");
        }
        String name = friendLink.getName();
        if (TextUtils.isEmpty(name)) {
            return ResponseResult.FAILED("友情链接名称不可以为空");
        }

        friendLink.setId(idWorker.nextId() + "");
        friendLink.setCreateTime(new Date());
        friendLink.setUpdateTime(new Date());

        friendLinkDao.save(friendLink);
        return ResponseResult.SUCCESS("友情链接创建成功");


    }

    @Override
    public ResponseResult getFriendLink(String friendLinkId) {
        FriendLink friendLink = friendLinkDao.findOneById(friendLinkId);
        if (friendLink == null) {
            return ResponseResult.FAILED("友情链接不存在");
        }
        return ResponseResult.SUCCESS("获取友情链接成功").setData(friendLink);

    }

    @Override
    public ResponseResult listFriendLink(int page, int size) {
        //分页查询
        page=checkPage(page);
        //限制size,每页不少于10
        size=checkSize(size);
        //根据注册日期降序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "order");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<FriendLink> all = friendLinkDao.findAll(pageable);
        return ResponseResult.SUCCESS("成功获取分类列表").setData(all);

    }

    @Override
    public ResponseResult deleteFriendLink(String friendLinkId) {
        int result = friendLinkDao.deleteFriendLinkByUpdateStatus(friendLinkId);
        if (result == 0) {
            return ResponseResult.FAILED("删除友情链接失败,该链接不存在");
        }
        return ResponseResult.SUCCESS("删除友情链接成功");

    }

    @Override
    public ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLink) {
        FriendLink friendLinkFromDb = friendLinkDao.findOneById(friendLinkId);
        if (friendLinkFromDb == null) {
            return ResponseResult.FAILED("友情链接不存在");
        }
        String logo = friendLink.getLogo();
        if (!TextUtils.isEmpty(logo)) {
            friendLinkFromDb.setLogo(logo);
        }
        String name = friendLink.getName();
        if (!TextUtils.isEmpty(name)) {
            friendLinkFromDb.setName(name);
        }
        String url = friendLink.getUrl();
        if (!TextUtils.isEmpty(url)) {
            friendLinkFromDb.setUrl(url);
        }
        friendLinkFromDb.setOrder(friendLink.getOrder());
        friendLinkFromDb.setUpdateTime(new Date());
        friendLinkDao.save(friendLinkFromDb);
        return ResponseResult.SUCCESS("友情链接更新成功");
    }
}
