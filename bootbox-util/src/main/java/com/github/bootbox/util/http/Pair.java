package com.github.bootbox.util.http;

/**
 * Created by jinsong on 2014/6/18.
 */
public class Pair<L, R> {
    private L l;
    private R r;

    public Pair() {
    }

    public Pair(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public L getL() {
        return l;
    }

    public void setL(L l) {
        this.l = l;
    }

    public R getR() {
        return r;
    }

    public void setR(R r) {
        this.r = r;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "l=" + l +
                ", r=" + r +
                '}';
    }
}
