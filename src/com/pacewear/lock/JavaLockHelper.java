
package com.pacewear.lock;

public class JavaLockHelper {
    public static IJavaLock newLock() {
        IJavaLock lock = new SynchronLock();
        return lock;
    }
}
