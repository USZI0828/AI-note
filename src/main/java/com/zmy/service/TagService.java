package com.zmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmy.common.Result;
import com.zmy.pojo.entity.Tag;
import com.zmy.pojo.form.Update.UpdateTagForm;
import com.zmy.pojo.form.add.AddTagForm;
import com.zmy.pojo.query.TagQuery;

public interface TagService extends IService<Tag> {

    Result<?> getInfo(Integer id);

    Result<?> listPage(TagQuery query);

    Result<?> addOne(AddTagForm addForm);

    Result<?> updateInfo(UpdateTagForm updateForm);

    Result<?> delete(Integer id);
}

