package com.zmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmy.common.Result;
import com.zmy.pojo.entity.Subject;
import com.zmy.pojo.form.Update.UpdateSubjectForm;
import com.zmy.pojo.form.add.AddSubjectForm;
import com.zmy.pojo.query.SubjectQuery;

public interface SubjectService extends IService<Subject>{

    Result<?> getInfo(Long id);

    Result<?> listPage(SubjectQuery query);

    Result<?> addOne(AddSubjectForm addForm);

    Result<?> updateInfo(UpdateSubjectForm updateForm);

    Result<?> delete(Long id);
}