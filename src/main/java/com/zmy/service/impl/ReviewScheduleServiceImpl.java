package com.zmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.exception.NoteException.NoteNoExistedException;
import com.zmy.mapper.NoteMapper;
import com.zmy.mapper.ReviewScheduleMapper;
import com.zmy.pojo.entity.Review_schedule;
import com.zmy.pojo.entity.Note;
import com.zmy.service.ReviewScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ReviewScheduleServiceImpl extends ServiceImpl<ReviewScheduleMapper, Review_schedule> implements ReviewScheduleService {

    @Autowired
    private ReviewScheduleMapper reviewScheduleMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    @Transactional
    public Result<?> updateReviewSchedule(Integer noteId) {
        // 检查笔记是否存在
        Note note = noteMapper.selectById(noteId);
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }

        // 查询当前笔记的复习计划
        LambdaQueryWrapper<Review_schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review_schedule::getNoteId, noteId);
        wrapper.orderByDesc(Review_schedule::getTimes);
        wrapper.last("limit 1");
        Review_schedule currentSchedule = reviewScheduleMapper.selectOne(wrapper);

        if (currentSchedule == null) {
            return Result.fail(400, "未找到复习计划", null);
        }

        // 如果times=5，状态已经是1，不再修改
        if (currentSchedule.getTimes() == 5 && currentSchedule.getStatus() == 1) {
            return Result.success("该笔记已完成所有复习");
        }

        // 删除其他与该笔记相关的数据
        reviewScheduleMapper.deleteByNoteId(noteId);

        // 计算下一次复习时间（根据艾宾浩斯遗忘曲线）
        int currentTimes = currentSchedule.getTimes();
        int nextTimes = currentTimes + 1;
        LocalDateTime nextReviewTime = calculateNextReviewTime(LocalDateTime.now(), nextTimes);

        // 创建新的复习计划
        Review_schedule newSchedule = new Review_schedule();
        newSchedule.setUserId(currentSchedule.getUserId());
        newSchedule.setNoteId(noteId);
        newSchedule.setReviewTime(nextReviewTime);
        newSchedule.setTimes(nextTimes);

        // 当times=5时，状态标记为1，表示复习完成，并更新计划完成时间
        if (nextTimes == 5) {
            newSchedule.setStatus(1);
            newSchedule.setFinishTime(LocalDateTime.now());
        } else {
            newSchedule.setStatus(0);
        }

        reviewScheduleMapper.insert(newSchedule);

        return Result.success("复习计划更新成功");
    }

    /**
     * 根据艾宾浩斯遗忘曲线计算下一次复习时间
     * times=1: 1天后
     * times=2: 2天后（总共3天）
     * times=3: 4天后（总共7天）
     * times=4: 7天后（总共14天）
     * times=5: 15天后（总共29天）
     */
    private LocalDateTime calculateNextReviewTime(LocalDateTime currentTime, int times) {
        int daysToAdd = 0;
        switch (times) {
            case 1:
                daysToAdd = 1;
                break;
            case 2:
                daysToAdd = 2;
                break;
            case 3:
                daysToAdd = 4;
                break;
            case 4:
                daysToAdd = 7;
                break;
            case 5:
                daysToAdd = 15;
                break;
            default:
                daysToAdd = 15; // 超过5次后，默认15天
        }
        return currentTime.plusDays(daysToAdd);
    }
}

