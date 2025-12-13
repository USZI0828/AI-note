package com.zmy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.exception.NoteException.NoteNoExistedException;
import com.zmy.mapper.NoteMapper;
import com.zmy.mapper.ReviewScheduleMapper;
import com.zmy.pojo.entity.Review_schedule;
import com.zmy.pojo.entity.Note;
import com.zmy.pojo.form.Update.UpdateNoteForm;
import com.zmy.pojo.form.add.AddNoteForm;
import com.zmy.pojo.query.NoteQuery;
import com.zmy.pojo.vo.NoteVo;
import com.zmy.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Slf4j
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private ReviewScheduleMapper reviewScheduleMapper;

    @Override
    public Result<?> getInfo(Integer id) {
        Note note = noteMapper.selectById(id);
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }
        return Result.success(note);
    }

    @Override
    public Result<?> listPage(NoteQuery query) {
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<NoteVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        noteMapper.listPage(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result<?> addOne(AddNoteForm addForm) {
        Note newNote = new Note();
        newNote.setUserId(addForm.getUserId());
        newNote.setSubjectId(addForm.getSubjectId());
        newNote.setTagId(addForm.getTagId());
        newNote.setTitle(addForm.getTitle());
        newNote.setContent(addForm.getContent());
        newNote.setCreateTime(LocalDateTime.now());
        newNote.setUpdateTime(LocalDateTime.now());
        newNote.setDeleteFlag(0);
        noteMapper.insert(newNote);

        // 创建笔记时，向review_schedule表中插入一条数据
        // 状态标记为0，times标记为1，时间为当前时间+1天（第一次复习时间）
        Review_schedule reviewSchedule = new Review_schedule();
        reviewSchedule.setUserId(addForm.getUserId());
        reviewSchedule.setNoteId(newNote.getNoteId());
        reviewSchedule.setReviewTime(LocalDateTime.now().plusDays(1)); // 第一次复习：1天后
        reviewSchedule.setStatus(0);
        reviewSchedule.setTimes(1);
        reviewScheduleMapper.insert(reviewSchedule);

        return Result.success("笔记添加成功");
    }

    @Override
    public Result<?> updateInfo(UpdateNoteForm updateForm) {
        Note note = noteMapper.selectById(updateForm.getNoteId());
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }
        noteMapper.updateInfo(updateForm);
        return Result.success("笔记更新成功");
    }

    @Override
    public Result<?> delete(Integer id) {
        Note note = noteMapper.selectById(id);
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }
        // 逻辑删除
        note.setDeleteFlag(1);
        note.setUpdateTime(LocalDateTime.now());
        noteMapper.updateById(note);
        return Result.success("笔记删除成功");
    }

    @Override
    public Result<?> listReviewNotesInWeek(Integer userId) {
        // 计算时间范围：当前时间到未来一周
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusDays(7);
        
        log.info("查询近一周需要复习的笔记，userId={}, startTime={}, endTime={}", userId, now, endTime);
        
        List<NoteVo> notes = noteMapper.listReviewNotesInWeek(userId, now, endTime);
        return Result.success(notes);
    }
}

