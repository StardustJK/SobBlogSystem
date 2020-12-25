package net.stardust.blog.service.impl;

import net.stardust.blog.dao.CategoryDao;
import net.stardust.blog.pojo.Category;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.ICategoryService;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private SnowFlakeIdWorker idWorker;

    @Autowired
    private CategoryDao categoryDao;
    @Override
    public ResponseResult addCategory(Category category) {
        //必填数据：分类名称，pinyin,顺序，描述
        if(TextUtils.isEmpty(category.getName())){
            return ResponseResult.FAILED("分类名称不可以为空");
        }
        if(TextUtils.isEmpty(category.getPinyin())){
            return ResponseResult.FAILED("分类拼音不可以为空");
        }
        if(TextUtils.isEmpty(category.getDescription())){
            return ResponseResult.FAILED("分类描述不可以为空");
        }

        category.setId(idWorker.nextId()+"");
        category.setStatus("1");
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());

        categoryDao.save(category);
        return ResponseResult.SUCCESS("添加分类成功");
    }
}
