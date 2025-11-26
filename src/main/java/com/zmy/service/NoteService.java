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
}

