package com.skoow.quadlib.utilities.struct;

import com.skoow.quadlib.utilities.func.Prov;

public class RangeClamp {
    public Prov<Integer> min;
    public Prov<Integer> max;

    public int current;

    public RangeClamp(Prov<Integer> min, Prov<Integer> max) {
        this.min = min; this.max = max;
        current = min.get();
    }

    public int current() {
        return current;
    }
    public boolean peek() {
        return current+1 < max.get();
    }
    public boolean next() {
        if(!peek()) return false;
        current++;
        return true;
    }
    public boolean peekBack() {
        return current-1 >= min.get();
    };
    public boolean back() {
        if(!peekBack()) return false;
        current--;
        return true;
    }

    public boolean peekCurrent() {
        return current >= min.get() && current < max.get();
    }
}
