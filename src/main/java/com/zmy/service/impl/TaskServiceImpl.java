package com.zmy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.exception.TaskException.TaskExistedException;
import com.zmy.exception.TaskException.TaskNoExistedException;
import com.zmy.mapper.TaskMapper;
import com.zmy.pojo.entity.Task;
import com.zmy.pojo.form.Update.UpdateTaskForm;
import com.zmy.pojo.form.add.AddTaskForm;
import com.zmy.pojo.query.TaskDeadlineQuery;
import com.zmy.pojo.query.TaskQuery;
import com.zmy.pojo.vo.TaskTodoVo;
import com.zmy.pojo.vo.TaskVo;
import com.zmy.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Result<?> getInfo(Long id) {
        TaskVo taskVo = taskMapper.getInfoById(id);
        if (taskVo == null) {
            throw new TaskNoExistedException();
        }
        return Result.success(taskVo);
    }

    @Override
    public Result<?> listPage(TaskQuery query) {
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<TaskVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        taskMapper.listPage(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    public Result<?> addOne(AddTaskForm addForm) {
        Task task = taskMapper.selectByName(addForm.getTaskName());
        if (task != null) {
            throw new TaskExistedException();
        }
        Task newTask = new Task(null,
                addForm.getUserId(),
                addForm.getSubjectId(),
                addForm.getTagId(),
                addForm.getTaskName(),
                addForm.getDeadline(),
                addForm.getDescription(),
                addForm.getPriority(),
                null,
                null,
                LocalDateTime.now(),
                null);
        taskMapper.insert(newTask);
        return Result.success("任务添加成功");
    }

    @Override
    public Result<?> updateInfo(UpdateTaskForm updateForm) {
        Task task = taskMapper.selectById(updateForm.getTaskId());
        if (task == null) {
            throw new TaskNoExistedException();
        }
        taskMapper.updateInfo(updateForm);
        return Result.success("任务更新成功");
    }

    @Override
    public Result<?> delete(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new TaskNoExistedException();
        }
        taskMapper.deleteById(id);
        return Result.success("任务删除成功");
    }

    @Override
    public Result<?> listPageOfTodo(TaskDeadlineQuery query) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now;
        LocalDateTime endTime = now.plusDays(1);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<TaskTodoVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        taskMapper.listPageOfTodo(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    public Result<?> listPageOfDeadline(TaskDeadlineQuery query) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = now.plusDays(3);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<TaskTodoVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        taskMapper.listPageOfDeadline(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }
}
