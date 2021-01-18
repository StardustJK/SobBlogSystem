package net.stardust.blog.service.impl;

import net.stardust.blog.dao.ArticleDao;
import net.stardust.blog.pojo.Article;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IArticleService;
import net.stardust.blog.service.IUserService;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl extends BaseService implements IArticleService {

    @Autowired
    private SnowFlakeIdWorker idWorker;
    @Autowired
    private IUserService userService;

    @Autowired
    private ArticleDao articleDao;

    @Override
    public ResponseResult postArticle(Article article) {


        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //title,categoryId,Content,summary,label
        String title = article.getTitle();
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("标题不能为空");
        }

        String state = article.getState();
        if (!Constants.Article.STATE_PUBLISH.equals(state) && !Constants.Article.STATE_DRAFT.equals(state)) {
            return ResponseResult.FAILED("不支持此操作");
        }
        String type = article.getType();
        if (TextUtils.isEmpty(type)) {
            return ResponseResult.FAILED("类型不能为空");
        }
        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("类型格式不正确");
        }
        //以下是发布的检查，草稿不需要检查
        if (Constants.Article.STATE_PUBLISH.equals(state)) {
            if (title.length() > Constants.Article.TITLE_MAX_LENGTH) {
                return ResponseResult.FAILED("文章标题不可以超过" + Constants.Article.TITLE_MAX_LENGTH + "个字符");
            }
            String content = article.getContent();
            if (TextUtils.isEmpty(content)) {
                return ResponseResult.FAILED("内容不能为空");

            }


            String summary = article.getSummary();
            if (TextUtils.isEmpty(summary)) {
                return ResponseResult.FAILED("摘要不能为空");
            }
            if (summary.length() > Constants.Article.SUMMARY_MAX_LENGTH) {
                return ResponseResult.FAILED("摘要不可以超过" + Constants.Article.SUMMARY_MAX_LENGTH + "个字符");
            }
            String labels = article.getLabels();
            if (TextUtils.isEmpty(labels)) {
                return ResponseResult.FAILED("标签不能为空");
            }
            String categoryId = article.getCategoryId();
            if (TextUtils.isEmpty(categoryId)) {
                return ResponseResult.FAILED("分类不能为空");

            }
        }
        String articleId = article.getId();
        if (TextUtils.isEmpty(articleId)) {
            //新内容，数据库没有
            article.setId(idWorker.nextId() + "");
            article.setCreateTime(new Date());

        } else {
            //更新内容
            Article articleFromDb = articleDao.findOneById(articleId);
            if (articleFromDb.getState().equals(Constants.Article.STATE_PUBLISH) &&
                    Constants.Article.STATE_DRAFT.equals(state)) {
                //已经发布了，只能更新，不能保存草稿
                return ResponseResult.FAILED("已经发布的文章不支持保存为草稿");
            }
        }
        article.setUserId(sobUser.getId());
        article.setUpdateTime(new Date());
        articleDao.save(article);


        return ResponseResult.SUCCESS(Constants.Article.STATE_DRAFT.equals(state) ? "草稿保存成功" : "文章发表成功").setData(article.getId());
    }

    /**
     *
     * @param page
     * @param size
     * @param keyword
     * @param categoryId
     * @param state 状态：已经删除、草稿、已经发布的、置顶的
     * @return
     */
    @Override
    public ResponseResult listArticles(int page, int size, String keyword, String categoryId, String state) {
        page=checkPage(page);
        size=checkSize(size);

        Sort sort=new Sort(Sort.Direction.DESC,"createTime");
        Pageable pageable= PageRequest.of(page-1,size,sort);
        Page<Article> all=articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates=new ArrayList<>();
                //判断是否传了参数
                if(!TextUtils.isEmpty(state)){
                    Predicate statePre=criteriaBuilder.equal(root.get("state").as(String.class),state);
                    predicates.add(statePre);
                }
                if(!TextUtils.isEmpty(categoryId)){
                    Predicate categoryIdPre=criteriaBuilder.equal(root.get("categoryId").as(String.class),categoryId);
                    predicates.add(categoryIdPre);
                }
                if(!TextUtils.isEmpty(keyword)){
                    Predicate keywordPre=criteriaBuilder.like(root.get("title").as(String.class),"%"+keyword+"%");
                    predicates.add(keywordPre);
                }
                Predicate []preArray=new Predicate[predicates.size()];
                predicates.toArray(preArray);
                return criteriaBuilder.and(preArray);
            }
        },pageable);
        return ResponseResult.SUCCESS("获取文章列表成功").setData(all);
    }
}