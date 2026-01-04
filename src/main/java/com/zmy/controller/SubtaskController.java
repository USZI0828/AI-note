package com.zmy.controller;

import com.zmy.common.Result;
import com.zmy.pojo.form.Update.UpdateSubtaskForm;
import com.zmy.pojo.form.add.AddSubtaskForm;
import com.zmy.service.SubtaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subtask")
@Tag(name = "子任务控制器")
public class SubtaskController {
    @Autowired
    private SubtaskService subtaskService;

    @Operation(summary = "添加子任务（同时会在 task_sub 中插入关联）")
    @PostMapping("/add")
    public Result<?> add(@RequestBody AddSubtaskForm addForm) {
        return subtaskService.addOne(addForm);
    }

    @Operation(summary = "更新子任务")
    @PutMapping("/update")
    public Result<?> update(@RequestBody UpdateSubtaskForm updateForm) {
        return subtaskService.updateOne(updateForm);
    }

    @Operation(summary = "删除子任务（同时删除关联）")
    @DeleteMapping("/delete")
    public Result<?> delete(Long id) {
        return subtaskService.delete(id);
    }

    @Operation(summary = "获取子任务信息")
    @GetMapping("/getInfo")
    public Result<?> getInfo(Long id) {
        return subtaskService.getInfo(id);
    }

    @Operation(summary = "获取某主任务的子任务列表")
    @GetMapping("/listByTask")
    public Result<?> listByTask(Long taskId) {
        return subtaskService.listByTask(taskId);
    }

}


