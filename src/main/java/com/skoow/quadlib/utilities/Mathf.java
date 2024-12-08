package com.skoow.quadlib.utilities;

public class Mathf {
    /** Distance between two 3D points*/
    public static double dst(double x, double y, double z, double x2, double y2, double z2) {
        double dx = x-x2;
        double dy = y-y2;
        double dz = z-z2;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    /**Lerps from one point to another with specified progress*/
    public static float lerp(float from, float to, float dst) {
        return (to-from)*clamp(dst)+from;
    }
    /** Lerps from one point to another with specified progress without clamping*/
    public static float lerpf(float from, float to, float dst) {
        return (to-from)*dst+from;
    }

    /** Clamps to [min,max]. */
    public static int clamp(int value, int min, int max){
        return Math.max(Math.min(value, max), min);
    }
    /** Clamps to [min,max]. */
    public static long clamp(long value, long min, long max){
        return Math.max(Math.min(value, max), min);
    }
    /** Clamps to [min,max]. */
    public static float clamp(float value, float min, float max){
        return Math.max(Math.min(value, max), min);
    }
    /** Clamps to [min,max]. */
    public static double clamp(double value, double min, double max){
        return Math.max(Math.min(value, max), min);
    }
    /** Clamps to [0, 1]. */
    public static float clamp(float value){
        return clamp(value, 0f, 1f);
    }
}
