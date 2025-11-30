package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmy.pojo.entity.Review_schedule;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewScheduleMapper extends BaseMapper<Review_schedule> {
    @Delete("delete from review_schedule where note_id = #{noteId}")
    void deleteByNoteId(@Param("noteId") Integer noteId);
}

