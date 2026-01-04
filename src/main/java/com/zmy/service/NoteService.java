package com.zmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmy.common.Result;
import com.zmy.pojo.entity.Note;
import com.zmy.pojo.form.Update.UpdateNoteForm;
import com.zmy.pojo.form.add.AddNoteForm;
import com.zmy.pojo.query.NoteQuery;

public interface NoteService extends IService<Note> {

    Result<?> getInfo(Integer id);

    Result<?> listPage(NoteQuery query);

    Result<?> addOne(AddNoteForm addForm);

    Result<?> updateInfo(UpdateNoteForm updateForm);

    Result<?> delete(Integer id);

    Result<?> listReviewNotesInWeek(Integer userId);

    Result<?> summarizeNote(Integer noteId);

    // 将传入的学习时长加到笔记已有学习时长中
    Result<?> addStudyDuration(Integer noteId, Float durationToAdd);
    
    // 获取某用户的笔记总学习时长
    Result<?> getTotalStudyDuration(Integer userId);
}

