package com.github.bootbox.util.page;

public interface PageQuery {
    default Integer getPageNum() {
        return 1;
    }

    default Integer getPageSize() {
        return 10;
    }
}
