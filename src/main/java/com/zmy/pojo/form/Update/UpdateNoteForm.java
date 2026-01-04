package com.zmy.pojo.form.Update;

import lombok.Data;

@Data
public class UpdateNoteForm {
    private Integer noteId;
    private Integer subjectId;
    private String tagId;
    private String title;
    private String content;
}

