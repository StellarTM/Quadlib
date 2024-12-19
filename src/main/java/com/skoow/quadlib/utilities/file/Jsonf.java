package com.skoow.quadlib.utilities.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skoow.quadlib.utilities.func.Cons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;

import static com.skoow.quadlib.utilities.file.Files.*;
import static com.skoow.quadlib.utilities.file.TypeAdapters.*;

public class Jsonf {
    private static final GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setLenient().setPrettyPrinting()
            .registerTypeAdapter(ItemStack.class,           itemStackDeserializer())
            .registerTypeAdapter(ResourceLocation.class,    resourceLocationDeserializer());
    public static Gson gson = gsonBuilder.create();

    public static void updateGson(Cons<GsonBuilder> builder) {
        builder.get(gsonBuilder);
        gson = gsonBuilder.create();
    }

    public static <T> T jsonToJava(File f, Class<T> classOf) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        try {
            f.createNewFile();
            InputStreamReader reader = input(f);
            T data = gson.fromJson(reader,classOf);
            if(data == null) {
                data = classOf.getDeclaredConstructor().newInstance();
                javaToJson(f,data);
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return classOf.getDeclaredConstructor().newInstance();
        }
    }

    public static void javaToJson(File f, Object data) {
        try {
            f.createNewFile();
            OutputStreamWriter writer = output(f);
            gson.toJson(data,writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
