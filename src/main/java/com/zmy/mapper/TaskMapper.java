package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmy.pojo.entity.Task;
import com.zmy.pojo.form.Update.UpdateTaskForm;
import com.zmy.pojo.query.TaskQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    Page<Task> listPage(Page<Task> page, @Param("query") TaskQuery query);

    @Select("select * from task where task_name = #{taskName}")
    Task selectByName(String taskName);

    void updateInfo(UpdateTaskForm updateForm);
}
