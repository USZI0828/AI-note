package com.zmy.controller;

import com.zmy.common.Result;
import com.zmy.service.ReviewScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviewSchedule")
@Tag(name = "复习计划控制器")
public class ReviewScheduleController {
    @Autowired
    private ReviewScheduleService reviewScheduleService;

    @Operation(summary = "更新复习计划（根据艾宾浩斯遗忘曲线）")
    @PutMapping("/update")
    public Result<?> updateReviewSchedule(@RequestParam("noteId") Integer noteId) {
        return reviewScheduleService.updateReviewSchedule(noteId);
    }
}

