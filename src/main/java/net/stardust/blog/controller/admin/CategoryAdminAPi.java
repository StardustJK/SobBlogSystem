package net.stardust.blog.controller.admin;

import net.stardust.blog.pojo.Category;
import net.stardust.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/category")
public class CategoryAdminAPi {

    /**
     * 添加分类
     * @param category
     * @return
     */
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        return null;
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
     */
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory( @PathVariable("categoryId") String categoryId){
        return null;
    }

    /**
     * 查询分类列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult listCategory(@RequestParam("page") int page,@RequestParam("size") int size){
        return null;
    }



}
