package com.zmy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zmy.common.Result;
import com.zmy.pojo.form.LoginForm;
import com.zmy.pojo.form.RegisterForm;
import com.zmy.pojo.form.Update.UpdateTaskForm;
import com.zmy.pojo.form.Update.UpdateUserForm;
import com.zmy.pojo.form.add.AddTaskForm;
import com.zmy.pojo.query.TaskQuery;
import com.zmy.service.TaskService;
import com.zmy.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
@Tag(name = "任务控制器")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = "获取任务信息")
    @GetMapping("/getInfo")
    public Result<?> getInfo(Long id) {
        return taskService.getInfo(id);
    }

    @Operation(summary = "获取任务列表")
    @PostMapping("/list")
    public Result<?> list(@RequestBody TaskQuery query) {
        return taskService.listPage(query);
    }

    @Operation(summary = "添加任务")
    @PostMapping("/add")
    public Result<?> add(@RequestBody AddTaskForm addForm) {
        return taskService.addOne(addForm);
    }

    @Operation(summary = "更新任务信息")
    @PutMapping("/update")
    public Result<?> update(@RequestBody UpdateTaskForm updateForm) {
        return taskService.updateInfo(updateForm);
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/delete")
    public Result<?> delete(Long id) {
        return taskService.delete(id);
    }


}
