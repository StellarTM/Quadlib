package com.skoow.quadlib;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Log {
    public static void info(String s,Object...objs) {
        getLogger().info(String.format(s,objs));
    }
    public static void debug(String s,Object...objs) {
        getLogger().debug(String.format(s,objs));
    }
    public static void error(String s,Object...objs) {
        getLogger().error(String.format(s,objs));
    }
    public static void warn(String s,Object...objs) {
        getLogger().warn(String.format(s,objs));
    }

    public static Logger getLogger() {
        return LogUtils.getLogger();
    }
}
