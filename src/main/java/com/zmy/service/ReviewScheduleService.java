package com.zmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmy.common.Result;
import com.zmy.pojo.entity.Review_schedule;

public interface ReviewScheduleService extends IService<Review_schedule> {
    Result<?> updateReviewSchedule(Integer noteId);
}

