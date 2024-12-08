package com.skoow.quadlib.common.entity;

import com.skoow.quadlib.utilities.func.Cons;
import com.skoow.quadlib.utilities.func.Cons2;
import com.skoow.quadlib.utilities.func.Func;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class QuadEntityVar<T> {
    public Cons<SynchedEntityData> define;
    public Cons2<SynchedEntityData,T> set;
    public Func<SynchedEntityData,T> get;

    public EntityDataAccessor<T> accessor;

    public Class<T> type;

    public String name;

    public QuadEntityVar(String name, Class<? extends LivingEntity> entity, T def) {
        type = (Class<T>) def.getClass();
        accessor = SynchedEntityData.defineId(entity, getSerializer());
        define = data -> data.define(accessor,def);
        set = (data,v) -> data.set(accessor,v);
        get = (data) -> data.get(accessor);
        this.name = name;
    }

    public EntityDataSerializer<T> getSerializer() {
        for (Field field : EntityDataSerializers.class.getFields()) {
            if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
                Type actualType = parameterizedType.getActualTypeArguments()[0]; // первый generic аргумент
                if (actualType.equals(type)) { // сравнение с твоим типом
                    try {
                        return (EntityDataSerializer<T>) field.get(null); // достаём значение из static поля
                    } catch (IllegalAccessException e) {
                        e.printStackTrace(); // или хотя бы логнуть чот
                    }
                }
            }
        }
        return null;
    };
}
