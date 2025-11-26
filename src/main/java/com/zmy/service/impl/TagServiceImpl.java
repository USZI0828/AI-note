package com.zmy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.exception.TagException.TagExistedException;
import com.zmy.exception.TagException.TagNoExistedException;
import com.zmy.mapper.TagMapper;
import com.zmy.pojo.entity.Tag;
import com.zmy.pojo.form.Update.UpdateTagForm;
import com.zmy.pojo.form.add.AddTagForm;
import com.zmy.pojo.query.TagQuery;
import com.zmy.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public Result<?> getInfo(Integer id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new TagNoExistedException();
        }
        return Result.success(tag);
    }

    @Override
    public Result<?> listPage(TagQuery query) {
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<Tag> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        tagMapper.listPage(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    public Result<?> addOne(AddTagForm addForm) {
        Tag tag = tagMapper.selectByName(addForm.getTagName());
        if (tag != null) {
            throw new TagExistedException();
        }
        Tag newTag = new Tag();
        newTag.setTagName(addForm.getTagName());
        tagMapper.insert(newTag);
        return Result.success("标签添加成功");
    }

    @Override
    public Result<?> updateInfo(UpdateTagForm updateForm) {
        Tag tag = tagMapper.selectById(updateForm.getTagId());
        if (tag == null) {
            throw new TagNoExistedException();
        }
        tagMapper.updateInfo(updateForm);
        return Result.success("标签更新成功");
    }

    @Override
    public Result<?> delete(Integer id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new TagNoExistedException();
        }
        tagMapper.deleteById(id);
        return Result.success("标签删除成功");
    }
}

