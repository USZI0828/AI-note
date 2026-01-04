package com.zmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.mapper.SubtaskMapper;
import com.zmy.mapper.TaskSubMapper;
import com.zmy.pojo.entity.Subtask;
import com.zmy.pojo.entity.Task_sub;
import com.zmy.pojo.form.Update.UpdateSubtaskForm;
import com.zmy.pojo.form.add.AddSubtaskForm;
import com.zmy.service.SubtaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SubtaskServiceImpl extends ServiceImpl<SubtaskMapper, Subtask> implements SubtaskService {

    @Autowired
    private SubtaskMapper subtaskMapper;

    @Autowired
    private TaskSubMapper taskSubMapper;

    @Override
    public Result<?> addOne(AddSubtaskForm addForm) {
        // 检查主任务已有子任务数量，最多 5 个
        Long exist = taskSubMapper.selectCount(new QueryWrapper<Task_sub>().eq("task_id", addForm.getTaskId()));
        if (exist != null && exist >= 5) {
            return Result.fail(null, "一个主任务最多有5个子任务", null);
        }

        Subtask subtask = new Subtask();
        subtask.setTaskId(addForm.getTaskId());
        subtask.setTaskName(addForm.getTaskName());
        subtask.setDescription(addForm.getDescription());
        subtask.setStatus("未完成");
        subtask.setCreateTime(LocalDateTime.now());
        // 子任务通过 task_sub 表与主任务关联，无需在 subtask 表中写入主任务 id

        subtaskMapper.insert(subtask);

        Task_sub mapping = new Task_sub();
        // subtask_id 在插入后从实体中获取
        mapping.setSubtaskId(subtask.getSubtaskId() != null ? subtask.getSubtaskId().intValue() : null);
        mapping.setTaskId(addForm.getTaskId() != null ? addForm.getTaskId().intValue() : null);
        taskSubMapper.insert(mapping);

        return Result.success("子任务添加成功");
    }

    @Override
    public Result<?> updateOne(UpdateSubtaskForm updateForm) {
        Subtask subtask = subtaskMapper.selectById(updateForm.getSubtaskId());
        if (subtask == null) {
            return Result.fail(null, "子任务不存在", null);
        }
        if (updateForm.getTaskName() != null) {
            subtask.setTaskName(updateForm.getTaskName());
        }
        if (updateForm.getDescription() != null) {
            subtask.setDescription(updateForm.getDescription());
        }
        if (updateForm.getStatus() != null) {
            subtask.setStatus(updateForm.getStatus());
        }
        if (updateForm.getFinishTime() != null) {
            subtask.setFinishTime(updateForm.getFinishTime());
        }
        subtaskMapper.updateById(subtask);
        return Result.success("子任务更新成功");
    }

    @Override
    public Result<?> delete(Long id) {
        Subtask subtask = subtaskMapper.selectById(id);
        if (subtask == null) {
            return Result.fail(null, "子任务不存在", null);
        }
        // 删除映射关系
        taskSubMapper.delete(new QueryWrapper<Task_sub>().eq("subtask_id", id));
        subtaskMapper.deleteById(id);
        return Result.success("子任务删除成功");
    }

    @Override
    public Result<?> getInfo(Long id) {
        Subtask subtask = subtaskMapper.selectById(id);
        if (subtask == null) {
            return Result.fail(null, "子任务不存在", null);
        }
        return Result.success(subtask);
    }

    @Override
    public Result<?> listByTask(Long taskId) {
        List<Task_sub> mappings = taskSubMapper.selectList(new QueryWrapper<Task_sub>().eq("task_id", taskId));
        if (mappings == null || mappings.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        List<Integer> ids = new ArrayList<>();
        for (Task_sub ts : mappings) {
            if (ts.getSubtaskId() != null) {
                ids.add(ts.getSubtaskId());
            }
        }
        if (ids.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        List<Subtask> subtasks = subtaskMapper.selectList(new QueryWrapper<Subtask>().in("subtask_id", ids));
        return Result.success(subtasks);
    }
}
