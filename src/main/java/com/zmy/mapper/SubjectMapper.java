package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmy.pojo.entity.Subject;
import com.zmy.pojo.form.Update.UpdateSubjectForm;
import com.zmy.pojo.query.SubjectQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SubjectMapper extends BaseMapper<Subject> {
    Page<Subject> listPage(Page<Subject> page, @Param("query") SubjectQuery query);

    @Select("select * from subject where subject_name = #{subjectName}")
    Subject selectByName(String subjectName);

    void updateInfo(UpdateSubjectForm updateForm);
}