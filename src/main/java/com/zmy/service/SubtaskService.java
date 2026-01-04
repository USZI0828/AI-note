package com.zmy.service;

import com.zmy.common.Result;
import com.zmy.pojo.form.Update.UpdateSubtaskForm;
import com.zmy.pojo.form.add.AddSubtaskForm;

public interface SubtaskService {
    Result<?> addOne(AddSubtaskForm addForm);
    Result<?> updateOne(UpdateSubtaskForm updateForm);
    Result<?> delete(Long id);
    Result<?> getInfo(Long id);
    Result<?> listByTask(Long taskId);
}


