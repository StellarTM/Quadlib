package com.skoow.quadlib.utilities.math;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.io.Serializable;

public class Vec3 implements Serializable {
    public float x,y,z;
    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vec3(double x, double y, double z) {
        this((float) x,(float) y,(float) z);
    }
    public Vec3 lerp(Vec3 next, float p) {
        set(Mathf.lerp(x,next.x,p),Mathf.lerp(y,next.y,p),Mathf.lerp(z,next.z,p));
        return this;
    }
    public Vec3 lerp(Vec3 next, float p, Interp interp) {
        set(interp.apply(p,x,next.x),interp.apply(p,y,next.y),interp.apply(p,z,next.z));
        return this;
    }

    public Vec3 constraintMin(Vec3 to, float constraint) {
        return constraintMin(to,constraint,1f);
    }
    public Vec3 constraintMin(Vec3 to, float constraint,float speed) {
        return constraint(to,constraint,true,false,speed);
    }
    public Vec3 constraintMax(Vec3 to, float constraint) {
        return constraintMax(to,constraint,1f);
    }
    public Vec3 constraintMax(Vec3 to, float constraint,float speed) {
        return constraint(to,constraint,false,true,speed);
    }
    public Vec3 constraint(Vec3 to, float constraint,boolean min,boolean max,float speed) {
        Vec3 vec = to.cpy().sub(this);
        float len = vec.len();
        if((max && len > constraint) || (min && len < constraint)) {
            float step = (len-constraint)*speed;
            Vec3 norVec = vec.cpy().nor().scale(step);
            add(norVec);
        }
        return this;
    }
    /**
     * Normalizes this vector.
     * @return this vector after normalization
     */
    public Vec3 nor() {
        return scale(1 / len());
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x",x);
        tag.putFloat("y",y);
        tag.putFloat("z",z);
        return tag;
    }
    public static Vec3 from(CompoundTag tag) {
        return new Vec3(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z")
        );
    }
    public static Vec3 from(Entity entity) {
        return new Vec3(
                (float) entity.getX(), (float) entity.getY(), (float) entity.getZ()
        );
    }

    public static Vec3 zero() {
        return new Vec3(0,0,0);
    }

    public float x() {
        return x;
    }

    public Vec3 x(float x) {
        this.x = x;
        return this;
    }

    public float y() {
        return y;
    }

    public Vec3 y(float y) {
        this.y = y;
        return this;
    }

    public float z() {
        return z;
    }

    public Vec3 cpy() {
        return new Vec3(x,y,z);
    }

    public Vec3 trns(float x, float y, float z, float angle) {
        set(x,y,z);
        rotate(angle);
        return this;
    };

    public Vec3 set(float x, float y, float z) {
        x(x); y(y); z(z);
        return this;
    }

    public Vec3 rotate(float angle) {
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);

        float xNew = (float) (x * cosTheta + z * sinTheta);
        float yNew = y;
        float zNew = (float) (-x * sinTheta + z * cosTheta);

        set(xNew,yNew,zNew);

        return this;
    }
    public Vec3 rotate(float angleX,float angleY,float angleZ) {
        double cosX = Math.cos(angleX), sinX = Math.sin(angleX);
        double cosY = Math.cos(angleY), sinY = Math.sin(angleY);
        double cosZ = Math.cos(angleZ), sinZ = Math.sin(angleZ);

        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;

        double x2 = x * cosY + z1 * sinY;
        double z2 = -x * sinY + z1 * cosY;

        double x3 = x2 * cosZ - y1 * sinZ;
        double y3 = x2 * sinZ + y1 * cosZ;

        set((float) x3, (float) y3, (float) z2);

        return this;
    }
    public Vec3 rotate(Vec3 angle) {
        return rotate(angle.x,angle.y,angle.z);
    }

    public Vec3 z(float z) {
        this.z = z;
        return this;
    }

    public Vec3 add(float x, float y, float z) {
        return set(x()+x,y()+y,z()+z);
    }
    public Vec3 add(double x, double y, double z) {
        return add((float)x,(float)y,(float)z);
    }
    public Vec3 add(Vec3 vec) {
        return add(vec.x,vec.y,vec.z);
    }

    public Vec3 sub(float x, float y, float z) {
        return add(-x,-y,-z);
    }
    public Vec3 sub(Vec3 vec) {
        return sub(vec.x,vec.y,vec.z);
    }

    public Vec3 scale(float x, float y, float z) {
        return set(x()*x,y()*y,z()*z);
    }
    public Vec3 scale(float v) {
        return scale(v,v,v);
    }
    public Vec3 scale(Vec3 vec) {
        return scale(vec.x,vec.y,vec.z);
    }

    public float len() {
        return (float) Math.sqrt(x*x+y*y+z*z);
    }
}
