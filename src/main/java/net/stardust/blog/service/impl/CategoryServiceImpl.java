package net.stardust.blog.service.impl;

import net.bytebuddy.asm.Advice;
import net.stardust.blog.dao.CategoryDao;
import net.stardust.blog.pojo.Category;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.ICategoryService;
import net.stardust.blog.service.IUserService;
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
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl extends BaseService implements ICategoryService {

    @Autowired
    private SnowFlakeIdWorker idWorker;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private IUserService userService;

    @Override
    public ResponseResult addCategory(Category category) {
        //必填数据：分类名称，pinyin,顺序，描述
        if (TextUtils.isEmpty(category.getName())) {
            return ResponseResult.FAILED("分类名称不可以为空");
        }
        if (TextUtils.isEmpty(category.getPinyin())) {
            return ResponseResult.FAILED("分类拼音不可以为空");
        }
        if (TextUtils.isEmpty(category.getDescription())) {
            return ResponseResult.FAILED("分类描述不可以为空");
        }

        category.setId(idWorker.nextId() + "");
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
    public ResponseResult listCategory() {

        //根据注册日期降序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "order");
        //普通用户只能拿到正常分类
        SobUser sobUser = userService.checkSobUser();
        List<Category> categories;
        if (sobUser == null || !Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())) {
            categories = categoryDao.listCategoryByState("1");
        }
        //管理员用户可以拿到所有的分类
        else {
            categories=categoryDao.findAll(sort);
        }
        return ResponseResult.SUCCESS("成功获取分类列表").setData(categories);

    }

    @Override
    public ResponseResult updateCategory(String categoryId, Category category) {

        Category categoryFromDb = categoryDao.findOneById(categoryId);
        if (categoryFromDb == null) {
            return ResponseResult.FAILED("分类不存在");
        }
        //判断内容进行修改
        String name = category.getName();
        if (!TextUtils.isEmpty(name)) {
            categoryFromDb.setName(name);
        }
        String pinyin = category.getPinyin();
        if (!TextUtils.isEmpty(pinyin)) {
            categoryFromDb.setName(pinyin);
        }
        String description = category.getDescription();
        if (!TextUtils.isEmpty(description)) {
            categoryFromDb.setDescription(description);
        }
        categoryFromDb.setOrder(category.getOrder());
        categoryFromDb.setUpdateTime(new Date());
        categoryDao.save(categoryFromDb);
        return ResponseResult.SUCCESS("更新分类成功");


    }

    @Override
    public ResponseResult deleteCategory(String categoryId) {
        int result = categoryDao.deleteCategoryByUpdateStatus(categoryId);
        if (result == 0) {
            return ResponseResult.FAILED("删除分类失败,该分类不存在");
        }
        return ResponseResult.SUCCESS("删除分类成功");
    }
}
