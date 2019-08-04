package com.github.bootbox.util.page;

import java.util.List;

public interface Queryer<T> {
    List<T> query(int pageStart, int pageLimit);
}

