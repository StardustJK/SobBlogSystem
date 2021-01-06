package net.stardust.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.stardust.blog.dao.ImageDao;
import net.stardust.blog.pojo.Category;
import net.stardust.blog.pojo.Image;
import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;
import net.stardust.blog.service.IImageService;
import net.stardust.blog.service.IUserService;
import net.stardust.blog.utils.Constants;
import net.stardust.blog.utils.SnowFlakeIdWorker;
import net.stardust.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class ImageServiceImpl extends BaseService implements IImageService {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

    @Value("${sob.blog.image.save-path}")
    public String imagePath;

    @Value("${sob.blog.image.max-size}")
    public long maxSize;

    @Autowired
    private SnowFlakeIdWorker idWorker;

    @Autowired
    private IUserService userService;

    @Autowired
    private ImageDao imageDao;
    /**
     * 保存数据到数据库：ID/存储路径/Url/原名称/创建日期/更新日期/状态/用户id/
     *
     * @param file
     * @return
     */
    @Override
    public ResponseResult uploadImage(MultipartFile file) {
        if (file == null) {
            return ResponseResult.FAILED("图片不可以为空");
        }
        //只支持png,jpg,gif
        String contentType = file.getContentType();
        if (TextUtils.isEmpty(contentType)) {
            return ResponseResult.FAILED("文件格式错误");
        }
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename==>  " + originalFilename);

        String type = getType(contentType, originalFilename);
        if (type == null) {
            return ResponseResult.FAILED("不支持此图片类型");

        }
        log.info("contentType==>" + contentType);

        //限制文件大小
        long size = file.getSize();
        log.info("maxSize = = > " + maxSize + "   size===>" + size);
        if (size > maxSize) {
            return ResponseResult.FAILED("图片最大仅支持" + (maxSize / 1024 / 1024) + "Mb");
        }
        //命名规则:配置目录/日期/类型/ID.类型
        long currentMillions = System.currentTimeMillis();
        String currentDay = simpleDateFormat.format(currentMillions);
        log.info("currentDay == > " + currentDay);
        String parentPath = imagePath + File.separator + currentDay + File.separator + type;
        File parentPathFile = new File(parentPath);
        //判断父文件夹是否存在
        if (!parentPathFile.exists()) {
            parentPathFile.mkdirs();
        }
        String targetName = idWorker.nextId() + "";
        String targetPath = parentPath + File.separator + targetName + "." + type;
        File targetFile = new File(targetPath);
        log.info("targetFile == >" + targetFile);

        try {
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            //保存文件
            file.transferTo(targetFile);
            //返回结果：图片的名称和访问路径
            Map<String,String> result=new HashMap<>();
            String resultPath = currentMillions + "_" +targetName+"."+type;
            result.put("id",resultPath);
            result.put("name",originalFilename);

            Image image=new Image();
            image.setContentType(contentType);
            image.setId(targetName);
            image.setCreateTime(new Date());
            image.setUpdateTime(new Date());
            image.setPath(targetFile.getPath());
            image.setName(originalFilename);
            image.setUrl(resultPath);
            image.setState("1");
            SobUser sobUser = userService.checkSobUser();
            String userId = sobUser.getId();
            image.setUserId(userId);
            imageDao.save(image);


            return ResponseResult.SUCCESS("图片上传成功").setData(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseResult.FAILED("图片上传失败");

    }

    private String getType(String contentType, String originalFilename) {
        String type = null;
        if (Constants.ImageType.TYPE_PNG_WITH_PREFIX.equals(contentType)
                && originalFilename.endsWith(Constants.ImageType.TYPE_PNG)) {
            type = Constants.ImageType.TYPE_PNG;
        } else if (Constants.ImageType.TYPE_GIF_WITH_PREFIX.equals(contentType)
                && originalFilename.endsWith(Constants.ImageType.TYPE_GIF)) {
            type = Constants.ImageType.TYPE_GIF;
        } else if (Constants.ImageType.TYPE_JPG_WITH_PREFIX.equals(contentType)
                && originalFilename.endsWith(Constants.ImageType.TYPE_JPG)) {
            type = Constants.ImageType.TYPE_JPG;
        }
        return type;
    }

    @Override
    public void viewImage(HttpServletResponse response, String imageId) throws IOException {
        //日期的时间戳_.ID.类型；
        String[] paths = imageId.split("_");
        String dayValue=paths[0];
        String format=simpleDateFormat.format(Long.parseLong(dayValue));

        String name=paths[1];
        String type=name.substring(name.length()-3);

        String targetPath=imagePath+File.separator+format+File.separator+type+File.separator+name;
        log.info("get target path == > "+targetPath);
        File file = new File(targetPath);
        OutputStream writer = null;
        FileInputStream fos = null;
        try {
            response.setContentType("image/png");
            writer = response.getOutputStream();
            //读取
            fos = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = fos.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    @Override
    public ResponseResult listImage(int page, int size) {
        //分页查询
        page=checkPage(page);
        //限制size,每页不少于10
        size=checkSize(size);
        SobUser sobUser = userService.checkSobUser();
        if (sobUser==null){
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //根据注册日期降序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Image> all = imageDao.findAll(new Specification<Image>() {
            @Override
            public Predicate toPredicate(Root<Image> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate userIdPre=criteriaBuilder.equal(root.get("userId").as(String.class),sobUser.getId());
                Predicate statePre = criteriaBuilder.equal(root.get("state").as(String.class), "1");
                return criteriaBuilder.and(userIdPre,statePre);
            }
        },pageable);
        return ResponseResult.SUCCESS("成功获取图片列表").setData(all);
    }

    @Override
    public ResponseResult deleteImage(String imageId) {
        int result = imageDao.deleteImageByUpdateState(imageId);
        if(result==0){
            return ResponseResult.FAILED("删除图片失败,该图片不存在");
        }
        return ResponseResult.SUCCESS("删除图片成功");
    }

}
