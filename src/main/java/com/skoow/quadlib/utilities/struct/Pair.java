package com.skoow.quadlib.utilities.struct;

import com.skoow.quadlib.utilities.func.Cons2;

import java.io.Serializable;

public class Pair<A,B> implements Serializable {

    private A first;
    private B second;

    public Pair() {
        first = null; second = null;
    }

    public Pair(A a, B b) {
        first = a; second = b;
    }

    public Pair<A,B> then(Cons2<A,B> cons) {
        cons.get(first,second);
        return this;
    }

    public A first() {
        return first;
    }
    public B second() {
        return second;
    }
    public void first(A a) {
        first = a;
    }
    public void second(B b) {
        second = b;
    }
}
