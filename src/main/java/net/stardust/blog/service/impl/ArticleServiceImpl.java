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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

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
        if (title.length() > Constants.Article.TITLE_MAX_LENGTH) {
            return ResponseResult.FAILED("文章标题不可以超过" + Constants.Article.TITLE_MAX_LENGTH + "个字符");
        }
        String content = article.getContent();
        if (TextUtils.isEmpty(content)) {
            return ResponseResult.FAILED("内容不能为空");

        }
        String type = article.getType();
        if (TextUtils.isEmpty(type)) {
            return ResponseResult.FAILED("类型不能为空");

        }
        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("类型格式不正确");
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
        }

        article.setId(idWorker.nextId()+"");
        article.setUserId(sobUser.getId());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        articleDao.save(article);
        return ResponseResult.SUCCESS("文章发表成功");
    }
}
