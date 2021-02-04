package net.stardust.blog.service;

import net.stardust.blog.pojo.Category;
import net.stardust.blog.response.ResponseResult;



public interface ICategoryService {
    ResponseResult addCategory(Category category);

    ResponseResult getCategory(String categoryId);

    ResponseResult listCategory();

    ResponseResult updateCategory(String categoryId,Category category);

    ResponseResult deleteCategory(String categoryId);
}
