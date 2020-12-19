package net.stardust.blog.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.dao.CommentDao;
import net.stardust.blog.dao.LabelDao;
import net.stardust.blog.pojo.Comment;
import net.stardust.blog.pojo.Labels;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.impl.UserServiceImpl;
import net.stardust.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        String captha = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + "123456");
        log.info("captha==>" + captha);
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
        if (size < 1) {
            size = Constants.Page.MIN_SIZE;
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Labels> result = labelDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取成功").setData(result);

    }

    @GetMapping("/label/search")
    public ResponseResult fuzzySearch(@RequestParam("keyword") String keyword, @RequestParam("count") int count) {
        List<Labels> all = labelDao.findAll(new Specification<Labels>() {
            @Override
            public Predicate toPredicate(Root<Labels> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate namePre = criteriaBuilder.like(root.get("name").as(String.class), "%" + keyword + "%");
                Predicate countPre = criteriaBuilder.equal(root.get("name").as(Integer.class), count);
                Predicate and = criteriaBuilder.and(namePre, countPre);
                return and;
            }
        });
        if (all.size() == 0) {
            return ResponseResult.FAILED("结果为空");
        }
        return ResponseResult.SUCCESS("查询成功").setData(all);
    }

    @Autowired
    private RedisUtil redisUtil;


    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        // specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        specCaptcha.setFont(Captcha.FONT_1);
        // 设置类型，纯数字、纯字母、字母数字混合
        //specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = specCaptcha.text().toLowerCase();
        log.info("captcha content == > " + content);
        // 验证码存入session
//        request.getSession().setAttribute("captcha", content);
        //验证码存入redis
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + "123456", content, 60 * 10);
        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }


    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserServiceImpl userService;
    @PostMapping("/comment")
    public ResponseResult testComment(@RequestBody Comment comment) {
        String content = comment.getContent();
        log.info("comment content == >" + content);
        ServletRequestAttributes requestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=requestAttributes.getRequest();
        String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
        if (tokenKey == null) {
            return ResponseResult.FAILED("账号未登录");
        }
        String token=(String)redisUtil.get(Constants.User.KEY_TOKEN+tokenKey);
        if(token==null){
            //过期了，查refreshToken
            //如果refreshToken也不存在，告诉用户未登录

        }
        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null) {
            return ResponseResult.FAILED("账号未登录");
        }
        comment.setUserId(sobUser.getId());
        comment.setUserAvatar(sobUser.getAvatar());
        comment.setUserName(sobUser.getUserName());
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setId(idWorker.nextId()+"");
        commentDao.save(comment);
        return ResponseResult.SUCCESS("评论成功");



    }
}