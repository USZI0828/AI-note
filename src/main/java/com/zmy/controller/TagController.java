package com.zmy.controller;

import com.zmy.common.Result;
import com.zmy.pojo.form.Update.UpdateTagForm;
import com.zmy.pojo.form.add.AddTagForm;
import com.zmy.pojo.query.TagQuery;
import com.zmy.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
@Tag(name = "标签控制器")
public class TagController {
    @Autowired
    private TagService tagService;

    @Operation(summary = "获取标签信息")
    @GetMapping("/getInfo")
    public Result<?> getInfo(Integer id) {
        return tagService.getInfo(id);
    }

    @Operation(summary = "获取标签列表（分页）")
    @PostMapping("/list")
    public Result<?> list(@RequestBody TagQuery query) {
        return tagService.listPage(query);
    }

    @Operation(summary = "添加标签")
    @PostMapping("/add")
    public Result<?> add(@RequestBody AddTagForm addForm) {
        return tagService.addOne(addForm);
    }

    @Operation(summary = "更新标签信息")
    @PutMapping("/update")
    public Result<?> update(@RequestBody UpdateTagForm updateForm) {
        return tagService.updateInfo(updateForm);
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/delete")
    public Result<?> delete(Integer id) {
        return tagService.delete(id);
    }
}

