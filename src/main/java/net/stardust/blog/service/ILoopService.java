package net.stardust.blog.service;

import net.stardust.blog.pojo.Looper;
import net.stardust.blog.response.ResponseResult;

public interface ILoopService {
    ResponseResult addLoop(Looper looper);

    ResponseResult getLoop(String looperId);

    ResponseResult listLoops(int page, int size);

    ResponseResult updateLoop(String looperId, Looper looper);

    ResponseResult deleteLoop(String looperId);
}
