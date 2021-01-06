package net.stardust.blog.service.impl;

import io.netty.util.Constant;
import net.stardust.blog.dao.SettingDao;
import net.stardust.blog.pojo.Setting;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IWebSiteInfoService;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class WebSiteInfoServiceImpl extends BaseService implements IWebSiteInfoService {

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private SnowFlakeIdWorker idWorker;

    @Override
    public ResponseResult getWebSiteTitle() {
        Setting title = settingDao.findOneByKey(Constants.Settings.WEB_SITE_TITLE);
        return ResponseResult.SUCCESS("获取网站title成功").setData(title);
    }

    @Override
    public ResponseResult putWebSiteTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("网站标题不能为空");
        }
        Setting titleFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SITE_TITLE);
        if (titleFromDb == null) {
            titleFromDb = new Setting();
            titleFromDb.setId(idWorker.nextId() + "");
            titleFromDb.setCreateTime(new Date());
            titleFromDb.setKey(Constants.Settings.WEB_SITE_TITLE);
        }
        titleFromDb.setValue(title);
        titleFromDb.setUpdateTime(new Date());
        settingDao.save(titleFromDb);
        return ResponseResult.SUCCESS("网站title更新成功");
    }

    @Override
    public ResponseResult getSeoInfo() {
        Setting description = settingDao.findOneByKey(Constants.Settings.WEB_SITE_DESCRIPTION);
        Setting keywords = settingDao.findOneByKey(Constants.Settings.WEB_SITE_KEYWORDS);
        Map<String, String> result = new HashMap<>();
        result.put("description",description.getValue());
        result.put("keywords", keywords.getValue());
        return ResponseResult.SUCCESS("获取seo信息成功").setData(result);
    }

    @Override
    public ResponseResult putSeoInfo(String keywords, String description) {
        if (TextUtils.isEmpty(keywords)){
            return ResponseResult.FAILED("关键字不能为空");
        }
        if (TextUtils.isEmpty(description)){
            return ResponseResult.FAILED("描述不能为空");
        }
        Setting descriptionFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SITE_DESCRIPTION);
        if (descriptionFromDb == null) {
            descriptionFromDb=new Setting();
            descriptionFromDb.setId(idWorker.nextId()+"");
            descriptionFromDb.setCreateTime(new Date());
            descriptionFromDb.setKey(Constants.Settings.WEB_SITE_DESCRIPTION);
        }
        descriptionFromDb.setValue(description);
        descriptionFromDb.setUpdateTime(new Date());

        settingDao.save(descriptionFromDb);
        Setting keywordsFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SITE_KEYWORDS);
        if (keywordsFromDb == null) {
            keywordsFromDb=new Setting();
            keywordsFromDb.setId(idWorker.nextId()+"");
            keywordsFromDb.setCreateTime(new Date());
            keywordsFromDb.setKey(Constants.Settings.WEB_SITE_KEYWORDS);
        }
        keywordsFromDb.setValue(keywords);
        keywordsFromDb.setUpdateTime(new Date());

        settingDao.save(keywordsFromDb);
        return ResponseResult.SUCCESS("更新seo信息成功");
    }


    @Override
    public ResponseResult getWebSiteViewCount() {
        Setting viewCountFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SITE_VIEW_COUNT);
        if (viewCountFromDb == null) {
            viewCountFromDb=new Setting();
            viewCountFromDb.setId(idWorker.nextId()+"");
            viewCountFromDb.setCreateTime(new Date());
            viewCountFromDb.setValue("1");
            viewCountFromDb.setKey(Constants.Settings.WEB_SITE_VIEW_COUNT);
            viewCountFromDb.setUpdateTime(new Date());
            settingDao.save(viewCountFromDb);
        }
        Map<String,Integer >result=new HashMap<>();
        result.put(viewCountFromDb.getKey(),Integer.parseInt(viewCountFromDb.getValue()));
        return ResponseResult.SUCCESS("获取网站浏览量成功").setData(result);
    }
}
