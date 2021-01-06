package net.stardust.blog.service.impl;

import net.stardust.blog.dao.LoopDao;
import net.stardust.blog.pojo.Looper;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.ILoopService;
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
public class LoopServiceImpl extends BaseService implements ILoopService {
    @Autowired
    private SnowFlakeIdWorker idWorker;

    @Autowired
    private LoopDao loopDao;
    @Override
    public ResponseResult addLoop(Looper looper) {

        //title、targetUrl,imageUrl不能为空
        String title = looper.getTitle();
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("标题不能为空");
        }
        String imageUrl = looper.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)){
            return ResponseResult.FAILED("图片不能为空");

        }
        String targetUrl = looper.getTargetUrl();
        if (TextUtils.isEmpty(targetUrl)){
            return ResponseResult.FAILED("跳转链接不能为空");
        }
        looper.setId(idWorker.nextId()+"");
        looper.setCreateTime(new Date());
        looper.setUpdateTime(new Date());
        loopDao.save(looper);
        return ResponseResult.SUCCESS("轮播图添加成功");

    }

    @Override
    public ResponseResult getLoop(String looperId) {
        Looper looperFromDb = loopDao.findOneById(looperId);
        if (looperFromDb == null) {
            return ResponseResult.FAILED("轮播图不存在");
        }
        return ResponseResult.SUCCESS("成功查询").setData(looperFromDb);
    }

    @Override
    public ResponseResult listLoops(int page, int size) {
        page=checkPage(page);
        size=checkSize(size);
        Sort sort=new Sort(Sort.Direction.DESC,"createTime");
        Pageable pageable= PageRequest.of(page-1,size);
        Page<Looper> all=loopDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取成功").setData(all);
    }

    @Override
    public ResponseResult updateLoop(String looperId, Looper looper) {
        Looper loopFromDb = loopDao.findOneById(looperId);
        if (loopFromDb==null){
            return ResponseResult.FAILED("轮播图不存在");
        }
        String title = looper.getTitle();
        if (!TextUtils.isEmpty(title)) {
            loopFromDb.setTitle(title);
        }
        String targetUrl = looper.getTargetUrl();
        if(!TextUtils.isEmpty(targetUrl)){
            loopFromDb.setTargetUrl(targetUrl);
        }
        String imageUrl = looper.getImageUrl();
        if(!TextUtils.isEmpty(imageUrl)){
            loopFromDb.setImageUrl(imageUrl);
        }
        loopFromDb.setOrder(looper.getOrder());
        loopFromDb.setUpdateTime(new Date());
        loopDao.save(loopFromDb);
        return ResponseResult.SUCCESS("轮播图更新成功");
    }

    @Override
    public ResponseResult deleteLoop(String looperId) {
        loopDao.deleteById(looperId);
        return ResponseResult.SUCCESS("删除成功");
    }
}
