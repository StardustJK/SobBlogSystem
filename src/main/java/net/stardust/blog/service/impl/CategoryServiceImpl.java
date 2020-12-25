package net.stardust.blog.service.impl;

import net.stardust.blog.dao.CategoryDao;
import net.stardust.blog.pojo.Category;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.ICategoryService;
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

    @Override
    public ResponseResult getCategory(String categoryId) {

        Category category = categoryDao.findOneById(categoryId);
        if (category == null) {
            return ResponseResult.FAILED("分类不存在");
        }
        return ResponseResult.SUCCESS("获取分类成功").setData(category);
    }

    @Override
    public ResponseResult listCategory(int page, int size) {
        //分页查询
        if (page < Constants.Page.DEFAULT_PAGE) {
            page = 1;
        }
        //限制size,每页不少于10
        if (size < Constants.Page.MIN_SIZE) {
            size = Constants.Page.MIN_SIZE;
        }
        //根据注册日期降序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime","order");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Category> all = categoryDao.findAll(pageable);
        return ResponseResult.SUCCESS("成功获取分类列表").setData(all);

    }
}
