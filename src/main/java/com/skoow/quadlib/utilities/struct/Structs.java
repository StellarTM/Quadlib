package com.skoow.quadlib.utilities.struct;

import com.skoow.quadlib.utilities.func.Cons;
import com.skoow.quadlib.utilities.func.Func;
import com.skoow.quadlib.utilities.func.Prov;

public class Structs {

    public static <A> Prov<A> nil(){
        return () -> null;
    }

    public static  <T> T or(T a, T b) {
        if(a == null) return b;
        return a;
    }
    public static String or(String a, String b) {
        if(a == null) return b;
        if(a.isBlank()) return b;
        return a;
    }
    public static <T,A> A safeGet(T a, Func<T,A> cb) {
        return safeGet(a,cb,nil());
    }
    public static <T,A> A safeGet(T a, Func<T,A> cb, Prov<A> def) {
        if(a != null) return cb.get(a);
        return def.get();
    }
    public static <T> void safeRun(T obj, Cons<T> cb) {
        if(obj != null) cb.get(obj);
    }
    public static <T> void safeRun(T obj, Runnable cb) {
        if(obj != null) cb.run();
    }
}
