package com.github.bootbox.starter.app.web.api;

import com.github.bootbox.util.page.Page;

import java.util.HashMap;
import java.util.Map;

public class Result<T> {
    private int code;
    private String message;

    private Object data;

    public Result() {
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <R> Result<R> success() {
        Result<R> r = new Result<>(ResultCode.OK, "");
        return r;
    }

    public static <R> Result<R> success(Map data) {
        Result<R> r = new Result<>(ResultCode.OK, "");
        r.data = data;
        return r;
    }

    public static <R> Result<R> success(R list) {
        Result<R> r = new Result<>(ResultCode.OK, "");
        r.putToMap("list", list);
        return r;
    }

    public static <R> Result<R> success(R list, String desc) {
        Result<R> r = new Result<>(ResultCode.OK, desc);
        r.putToMap("list", list);
        return r;
    }

    public static <R> Result<R> error(int code, String m) {
        Result<R> r = new Result<>(code, m);
        return r;
    }

    public static <F, T> Result<T> error(Result<F> s) {
        Result<T> r = new Result<>(s.getCode(), s.getMessage());
        return r;
    }


    public boolean isSuccess() {
        return code == ResultCode.OK;
    }

    public Result putToMap(String key, Object value) {
        if (null == data) this.data = new HashMap();
        if (data instanceof Map) {
            Map map = (Map) data;
            map.put(key, value);
        } else {
            throw new RuntimeException("Object type error");
        }
        return this;
    }

    public Result putToAll(Map m) {
        if (null == data) this.data = new HashMap();
        if (m != null) {
            if (data instanceof Map) {
                Map map = (Map) data;
                map.putAll(m);
            } else {
                throw new RuntimeException("Object type error");
            }
        }
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public static <R> Result<R> successWithDesc(String desc) {
        Result<R> r = new Result<>(ResultCode.OK, desc);
        return r;
    }

    public Object getData() {
        return data;
    }

    public static <R> Result<R> success(Page<R> page) {
        Result<R> r = new Result<>(ResultCode.OK, "");
        r.putToMap("list", page.getList());
        r.putToMap("hasMore", page.getHasMore());
        return r;
    }

    public static <R> Result<R> data(Object object) {
        Result<R> r = new Result<>(ResultCode.OK, "");
        r.setData(object);
        return r;
    }

    public static <T> Result paginate(Page<T> page) {
        return Result.success(page.getList())
                .putToMap("hasMore", page.getHasMore())
                .putToMap("total", page.getTotal());
    }
}
