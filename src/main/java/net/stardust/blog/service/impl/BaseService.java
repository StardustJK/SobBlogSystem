package net.stardust.blog.service.impl;

import net.stardust.blog.utils.Constants;

public class BaseService {
    int checkPage(int page){
        if (page < Constants.Page.DEFAULT_PAGE) {
            page = 1;
        }
        return page;
    }
    int checkSize(int size){
        if (size < Constants.Page.MIN_SIZE) {
            size = Constants.Page.MIN_SIZE;
        }
        return size;
    }
}
