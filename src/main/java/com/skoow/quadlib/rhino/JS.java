package com.skoow.quadlib.rhino;

import com.skoow.quadlib.utilities.struct.Pair;
import com.skoow.quadlib.utilities.struct.Seq;
import com.skoow.rhino.Context;
import com.skoow.rhino.ScriptableObject;
import com.skoow.rhino.TopLevel;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class JS {
    public static Pair<Context, ScriptableObject> begin() {
        Context context = Context.enter();
        ScriptableObject scope = new TopLevel();
        return new Pair<>(context,scope);
    };
    public static void run(Context context, ScriptableObject scope, String body, String filename) {
        context.evaluateString(scope,body,filename,0,null);
    }
    public static void addToScope(ScriptableObject scope, Context ctx, Object object, boolean fields, boolean methods) {
        if(fields)
            for (Field field : object.getClass().getFields()) {
                if(!field.isAnnotationPresent(Scope.class)) continue;
                try {
                    ScriptableObject.putConstProperty(
                            scope,field.getName(),field.get(object),ctx
                    );
                } catch (IllegalAccessException e) {

                }
            }
        if(methods) {
            HashMap<String, Seq<Method>> mthds = new HashMap<>();
            for (Method method : object.getClass().getMethods()) {
                if (!method.isAnnotationPresent(Scope.class)) continue;
                mthds.putIfAbsent(method.getName(),Seq.with());
                mthds.get(method.getName()).add(method);
            }
            mthds.forEach((k,v) -> {
                /*ScriptableObject.putConstProperty(
                        scope,k,fn
                );*/
            });
        }
    }
}
