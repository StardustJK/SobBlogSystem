package net.stardust.blog.service;

import net.stardust.blog.pojo.SobUser;
import net.stardust.blog.response.ResponseResult;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request);
}
