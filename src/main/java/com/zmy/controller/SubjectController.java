package com.zmy.controller;

import com.zmy.common.Result;
import com.zmy.pojo.form.Update.UpdateSubjectForm;
import com.zmy.pojo.form.add.AddSubjectForm;
import com.zmy.pojo.query.SubjectQuery;
import com.zmy.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subject")
@Tag(name = "科目控制器")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @Operation(summary = "获取科目信息")
    @GetMapping("/getInfo")
    public Result<?> getInfo(Long id) {
        return subjectService.getInfo(id);
    }

    @Operation(summary = "获取科目列表")
    @PostMapping("/list")
    public Result<?> list(@RequestBody SubjectQuery query) {
        return subjectService.listPage(query);
    }

    @Operation(summary = "添加科目")
    @PostMapping("/add")
    public Result<?> add(@RequestBody AddSubjectForm addForm) {
        return subjectService.addOne(addForm);
    }

    @Operation(summary = "更新科目信息")
    @PutMapping("/update")
    public Result<?> update(@RequestBody UpdateSubjectForm updateForm) {
        return subjectService.updateInfo(updateForm);
    }

    @Operation(summary = "删除科目")
    @DeleteMapping("/delete")
    public Result<?> delete(Long id) {
        return subjectService.delete(id);
    }


}
