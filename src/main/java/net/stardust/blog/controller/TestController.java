package net.stardust.blog.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.dao.LabelDao;
import net.stardust.blog.pojo.Labels;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private LabelDao labelDao;
    @Autowired
    private SnowFlakeIdWorker idWorker;

    @GetMapping("/hello")
    public ResponseResult hello() {
        log.info("hello world");
        return ResponseResult.SUCCESS("登陆成功");
    }

    @PostMapping("/label")
    public ResponseResult addLabel(@RequestBody Labels labels) {
        labels.setId(idWorker.nextId() + "");
        labels.setCreateTime(new Date());
        labels.setUpdateTime(new Date());
        labelDao.save(labels);
        return ResponseResult.SUCCESS();
    }

    @GetMapping("/label/list/{page}/{size}")
    public ResponseResult listLabels(@PathVariable("page") int page, @PathVariable("size") int size) {
        if (page < 1) {
            page = 1;
        }
        if(size<1){
            size= Constants.DEFAULT_SIZE;
        }
        Sort sort=new Sort(Sort.Direction.DESC,"createTime");
        Pageable pageable = PageRequest.of(page - 1, size,sort);
        Page<Labels> result=labelDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取成功").setData(result);

    }
    @GetMapping("/label/search")
    public ResponseResult fuzzySearch(@RequestParam("keyword") String keyword , @RequestParam("count") int count){
        List<Labels> all=labelDao.findAll(new Specification<Labels>() {
            @Override
            public Predicate toPredicate(Root<Labels> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate namePre=criteriaBuilder.like(root.get("name").as(String.class),"%"+keyword+"%");
                Predicate countPre=criteriaBuilder.equal(root.get("name").as(Integer.class),count);
                Predicate and=criteriaBuilder.and(namePre,countPre);
                return and;
            }
        });
        if(all.size()==0){
            return ResponseResult.FAILED("结果为空");
        }
        return ResponseResult.SUCCESS("查询成功").setData(all);
    }


}