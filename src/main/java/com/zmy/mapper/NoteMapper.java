package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmy.pojo.entity.Note;
import com.zmy.pojo.form.Update.UpdateNoteForm;
import com.zmy.pojo.query.NoteQuery;
import com.zmy.pojo.vo.NoteVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    Page<NoteVo> listPage(Page<NoteVo> page, @Param("query") NoteQuery query);

    void updateInfo(UpdateNoteForm updateForm);

    List<NoteVo> listReviewNotesInWeek(@Param("userId") Integer userId,
                                       @Param("startTime") java.time.LocalDateTime startTime,
                                       @Param("endTime") java.time.LocalDateTime endTime);

    NoteVo selectVoById(@Param("noteId") Integer noteId);
    
    /**
     * 在相同科目中使用全文检索按相关度返回前 N 篇笔记
     */
    List<NoteVo> searchTopBySubjectAndKeywords(@Param("subjectId") Integer subjectId,
                                              @Param("keywords") String keywords,
                                              @Param("limit") Integer limit);
    
    /**
     * 根据科目获取该科目下所有未删除的笔记（供内存计算相关度使用）
     */
    List<NoteVo> selectBySubject(@Param("subjectId") Integer subjectId);
    
    /**
     * 返回某用户所有笔记的总学习时长（秒或分钟，取决于存储单位）
     */
    @Select("SELECT IFNULL(SUM(duration),0) FROM note WHERE user_id = #{userId} AND delete_flag = 0")
    Float selectTotalDurationByUserId(@Param("userId") Integer userId);
}

