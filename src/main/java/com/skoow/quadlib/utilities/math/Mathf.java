package com.skoow.quadlib.utilities.math;

public class Mathf {
    public static final int[] signs = {-1, 1};
    public static final int[] zeroOne = {0, 1};
    public static final boolean[] booleans = {true, false};
    public static final float PI = NumberConst.pi, pi = PI, halfPi = PI/2;
    public static final float PI2 = NumberConst.tau;
    public static final float E = NumberConst.e;
    public static final float radiansToDegrees = 1f / NumberConst.rad;
    public static final float radDeg = radiansToDegrees;
    /** multiply by this to convert from degrees to radians */
    public static final float degreesToRadians = NumberConst.rad;
    public static final float degRad = degreesToRadians;
    private static final int sinBits = 14; // 16KB. Adjust for accuracy.
    private static final int sinMask = ~(-1 << sinBits);
    private static final int sinCount = sinMask + 1;
    private static final float[] sinTable = new float[sinCount];
    private static final float radFull = PI * 2;
    private static final float degFull = 360;
    private static final float radToIndex = sinCount / radFull;
    private static final float degToIndex = sinCount / degFull;
    static{
        for(int i = 0; i < sinCount; i++)
            sinTable[i] = (float)Math.sin((i + 0.5f) / sinCount * radFull);
        for(int i = 0; i < 360; i += 90)
            sinTable[(int)(i * degToIndex) & sinMask] = (float)Math.sin(i * degreesToRadians);

        sinTable[0] = 0f;
        sinTable[(int)(90 * degToIndex) & sinMask] = 1f;
        sinTable[(int)(180 * degToIndex) & sinMask] = 0f;
        sinTable[(int)(270 * degToIndex) & sinMask] = -1f;
    }
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
    /** Clamps floating point to specified points */
    public static float toFixed(float val, int points) {
        int pow = powTen(points);
        return (float) Math.round(val * pow) / pow;
    }
    /** 10^pow */
    public static int powTen(int pow) {
        return (int) Math.pow(10,pow);
    }
    /** Returns the sine in radians from a lookup table. */
    public static float sin(float radians){
        return sinTable[(int)(radians * radToIndex) & sinMask];
    }

    /** Returns the cosine in radians from a lookup table. */
    public static float cos(float radians){
        return sinTable[(int)((radians + PI / 2) * radToIndex) & sinMask];
    }
    /** Returns: the input 0-1 value scaled to 0-1-0. */
    public static float slope(float fin){
        return 1f - Math.abs(fin - 0.5f) * 2f;
    }
}
