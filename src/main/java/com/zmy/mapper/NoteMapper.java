package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmy.pojo.entity.Note;
import com.zmy.pojo.form.Update.UpdateNoteForm;
import com.zmy.pojo.query.NoteQuery;
import com.zmy.pojo.vo.NoteVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    Page<NoteVo> listPage(Page<NoteVo> page, @Param("query") NoteQuery query);

    void updateInfo(UpdateNoteForm updateForm);

    List<NoteVo> listReviewNotesInWeek(@Param("userId") Integer userId,
                                       @Param("startTime") java.time.LocalDateTime startTime,
                                       @Param("endTime") java.time.LocalDateTime endTime);

    NoteVo selectVoById(@Param("noteId") Integer noteId);
}

