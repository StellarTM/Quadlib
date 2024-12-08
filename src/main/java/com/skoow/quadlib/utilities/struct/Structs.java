package com.skoow.quadlib.utilities.struct;

public class Structs {
    public static  <T> T or(T a, T b) {
        if(a == null) return b;
        return a;
    }
}
