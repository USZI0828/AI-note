package com.zmy.pojo.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseQuery implements Serializable {
    //当前页码
    private Integer currentPage = 1;
    //每页显示条数
    private Integer pageSize = 10;

    public Integer getCurrentPage() {
        return (currentPage == null || currentPage <= 0) ? 1 : currentPage;
    }

    public Integer getPageSize() {
        return (pageSize == null || pageSize <= 0) ? 10 : pageSize;
    }
}
