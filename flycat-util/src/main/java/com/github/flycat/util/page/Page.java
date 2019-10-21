/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.util.page;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {
    private final List<T> list;
    private Integer hasNext;
    private Long total;
    private Integer hasPrevious;
    private Integer current;
    private Integer totalPages;

    private Integer startPage;
    private Integer endPage;


    public Page(List<T> list) {
        this.list = list;
    }

    public int getHasNext() {
        return hasNext;
    }

    public int getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(int hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public void setHasNext(Integer hasNext) {
        this.hasNext = hasNext;
    }

    public void setHasPrevious(Integer hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public List<T> getList() {
        return list;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
        trySet();
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    private void trySet() {
        if (current != null) {
            hasPrevious = current > 1 ? 1 : 0;
            final int pageInterval = 4;
            startPage = (current - pageInterval) > 0 ? current - pageInterval : 1;
            if (totalPages != null) {
                hasNext = current < totalPages ? 1 : 0;
                endPage = (current + pageInterval) > totalPages ? totalPages : (current + pageInterval);
            }
        }
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        trySet();
    }


    public Integer getStartPage() {
        return startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

    public Integer getEndPage() {
        return endPage;
    }

    public void setEndPage(Integer endPage) {
        this.endPage = endPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
