package com.zmy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.exception.SubjectException.SubjectExistedException;
import com.zmy.exception.SubjectException.SubjectNoExistedException;
import com.zmy.mapper.SubjectMapper;
import com.zmy.pojo.entity.Subject;
import com.zmy.pojo.form.Update.UpdateSubjectForm;
import com.zmy.pojo.form.add.AddSubjectForm;
import com.zmy.pojo.query.SubjectQuery;
import com.zmy.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Autowired
    private SubjectMapper subjectMapper;

    @Override
    public Result<?> getInfo(Long id) {
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new SubjectNoExistedException();
        }
        return Result.success(subject);
    }

    @Override
    public Result<?> listPage(SubjectQuery query) {
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<Subject> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        subjectMapper.listPage(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    public Result<?> addOne(AddSubjectForm addForm) {
        Subject subject = subjectMapper.selectByName(addForm.getSubjectName());
        if (subject != null) {
            throw new SubjectExistedException();
        }
        Subject newSubject = new Subject(null,
                addForm.getSubjectName(),
                addForm.getDescription()
                );
        subjectMapper.insert(newSubject);
        return Result.success("科目添加成功");
    }

    @Override
    public Result<?> updateInfo(UpdateSubjectForm updateForm) {
        Subject subject = subjectMapper.selectById(updateForm.getSubjectId());
        if (subject == null) {
            throw new SubjectNoExistedException();
        }
        subjectMapper.updateInfo(updateForm);
        return Result.success("科目更新成功");
    }

    @Override
    public Result<?> delete(Long id) {
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new SubjectNoExistedException();
        }
        subjectMapper.deleteById(id);
        return Result.success("科目删除成功");
    }
}