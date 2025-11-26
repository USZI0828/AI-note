package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmy.pojo.entity.Tag;
import com.zmy.pojo.form.Update.UpdateTagForm;
import com.zmy.pojo.query.TagQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    Page<Tag> listPage(Page<Tag> page, @Param("query") TagQuery query);

    @Select("select * from tag where tag_name = #{tagName}")
    Tag selectByName(String tagName);

    void updateInfo(UpdateTagForm updateForm);
}

