package com.zmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmy.common.Result;
import com.zmy.pojo.entity.Task;
import com.zmy.pojo.form.Update.UpdateTaskForm;
import com.zmy.pojo.form.add.AddTaskForm;
import com.zmy.pojo.query.TaskQuery;

public interface TaskService extends IService<Task>{

    Result<?> getInfo(Long id);

    Result<?> listPage(TaskQuery query);

    Result<?> addOne(AddTaskForm addForm);

    Result<?> updateInfo(UpdateTaskForm updateForm);

    Result<?> delete(Long id);
}
