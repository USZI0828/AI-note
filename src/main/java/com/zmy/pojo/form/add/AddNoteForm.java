package com.zmy.pojo.form.add;

import lombok.Data;

@Data
public class AddNoteForm {
    private Integer userId;
    private Integer subjectId;
    private String tagId;
    private String title;
    private String content;
}

