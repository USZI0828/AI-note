package com.zmy.controller;

import com.zmy.common.Result;
import com.zmy.pojo.form.Update.UpdateNoteForm;
import com.zmy.pojo.form.add.AddNoteForm;
import com.zmy.pojo.query.NoteQuery;
import com.zmy.service.NotePdfService;
import com.zmy.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/note")
@Tag(name = "笔记控制器")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @Autowired
    private NotePdfService notePdfService;

    @Operation(summary = "获取笔记信息")
    @GetMapping("/getInfo")
    public Result<?> getInfo(Integer id) {
        return noteService.getInfo(id);
    }

    @Operation(summary = "获取笔记列表（分页，支持按创建时间、标签、科目查询）")
    @PostMapping("/list")
    public Result<?> list(@RequestBody NoteQuery query) {
        return noteService.listPage(query);
    }

    @Operation(summary = "添加笔记")
    @PostMapping("/add")
    public Result<?> add(@RequestBody AddNoteForm addForm) {
        return noteService.addOne(addForm);
    }

    @Operation(summary = "更新笔记信息")
    @PutMapping("/update")
    public Result<?> update(@RequestBody UpdateNoteForm updateForm) {
        return noteService.updateInfo(updateForm);
    }

    @Operation(summary = "删除笔记")
    @DeleteMapping("/delete")
    public Result<?> delete(Integer id) {
        return noteService.delete(id);
    }

    @Operation(summary = "查询近一周需要复习的笔记")
    @GetMapping("/reviewList")
    public Result<?> reviewList(@RequestParam("userId") Integer userId) {
        return noteService.listReviewNotesInWeek(userId);
    }

    @Operation(summary = "导出笔记为PDF")
    @GetMapping("/exportPdf")
    public void exportPdf(@RequestParam("noteId") Integer noteId, HttpServletResponse response) throws IOException {
        notePdfService.exportNoteToPdf(noteId, response);
    }
}

