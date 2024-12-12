package com.skoow.quadlib.utilities.file;

import com.skoow.quadlib.utilities.func.Cons2;
import com.skoow.quadlib.utilities.struct.Pair;
import com.skoow.quadlib.utilities.struct.Seq;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Files {
    public static <T> T parse(Class<T> clazz, String body, String ext) {
        return switch (ext) {
            case "json" -> Jsonf.gson.fromJson(body, clazz);
            case "yaml" -> Yamlf.yamlToJava(body, clazz);
            default -> null;
        };
    }
    public static void filesDeep(File dir, Cons2<Pair<File,String>,Pair<String,String>> consumer) {
        Seq<File> seq = filesDeep(dir);
        FilePredicate predicate = FilePredicate.ofStatic(dir);
        seq.each(f -> consumer.get(new Pair<>(f,read(f)),idExt(predicate,f)));
    }
    public static void filesDeepf(File dir, Cons2<File,Pair<String,String>> consumer) {
        Seq<File> seq = filesDeep(dir);
        FilePredicate predicate = FilePredicate.ofStatic(dir);
        seq.each(f -> consumer.get(f,idExt(predicate,f)));
    }
    public static InputStreamReader input(File f) throws FileNotFoundException
    { return new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8); }
    public static OutputStreamWriter output(File f) throws FileNotFoundException
    { return new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8); }
    public static FileInputStream inputStream(File f) throws FileNotFoundException
    { return new FileInputStream(f); }
    public static FileOutputStream outputStream(File f) throws FileNotFoundException
    { return new FileOutputStream(f); }
    public static String id(FilePredicate rootPredicate, File file) {
        String path = file.getAbsolutePath();
        String id = path
                .substring(rootPredicate.get().getAbsolutePath().length()+1)
                .replace("\\","/");
        id = removeExtension(id);
        return id;
    };
    public static Pair<String,String> idExt(FilePredicate rootPredicate, File file) {
        return new Pair<>(id(rootPredicate,file), ext(file.getName()));
    };
    public static String removeExtension(String name) {
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex != -1) name = name.substring(0, dotIndex);
        return name;
    }
    public static String ext(String name) {
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex != -1) return name.substring(dotIndex + 1);
        return "";
    }

    public static Seq<File> filesDeep(File dir) {
        File[] files = dir.listFiles();
        Seq<File> fileList = Seq.with();
        for (File file : files) {
            if(file.isDirectory()) {
                fileList.addAll(filesDeep(file));
                continue;
            }
            fileList.add(file);
        }
        return fileList;
    }

    public static Seq<File> dirs(File dir) {
        File[] dirs = dir.listFiles(File::isDirectory);
        Seq<File> dirList = Seq.with();
        for (File file : dirs) {
            dirList.add(file);
        }
        return dirList;
    }

    public static String read(File f) {
        try {
            InputStreamReader reader = input(f);
            int t;
            String readResult = "";
            while ((t = reader.read()) != -1) {
                readResult = readResult + (char) t;
            }
            return readResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] readBytes(File f) {
        try {
            InputStream stream = inputStream(f);
            byte[] bytes = stream.readAllBytes();
            stream.close();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(File f, String str) {
        try {
            OutputStreamWriter writer = output(f);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
