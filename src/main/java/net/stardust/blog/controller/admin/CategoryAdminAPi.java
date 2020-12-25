package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.Category;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class CategoryAdminAPi {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    /**
     * 删除分类
     * @param categoryId
     * @return
     */
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId){
        return null;
    }

    /**
     * 修改分类
     * @param categoryId
     * @return
     */
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory( @PathVariable("categoryId") String categoryId){
        return null;
    }
    /**
     * 查询分类
     * 修改的时候
     * 填充弹窗
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory( @PathVariable("categoryId") String categoryId){
        return categoryService.getCategory(categoryId);
    }

    /**
     * 查询分类列表
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listCategory(@PathVariable("page") int page,@PathVariable("size") int size){
        return categoryService.listCategory(page,size);
    }



}
