package com.github.bootbox.util.page;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {
    private final List<T> list;
    private final int hasMore;
    private Integer total;

    public Page(List<T> list, int hasMore) {
        this.hasMore = hasMore;
        this.list = list;
    }

    public int getHasMore() {
        return hasMore;
    }

    public List<T> getList() {
        return list;
    }


    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
