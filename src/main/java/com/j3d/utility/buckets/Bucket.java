package com.j3d.utility.buckets;


/**
 * A utility class that acts as a container for a set of `Class` objects.
 * It provides a method to check if a given object is an instance of the
 * classes held within the bucket.
 * @see Buckets
 * @author Lehlogonolo Poole
 */
public class Bucket {
    private Class<?>[] cls;
    public Bucket(Class<?> ...classes) {
        cls = classes;
    }

    public boolean shouldHold(Object t) {
        for (Class<?> c : cls) {
            if (c.isInstance(t)) return true;
        }
        return false;
    }
}