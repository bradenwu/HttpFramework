
package com.pacewear.lock;

public interface IJavaLock {
    public void lock(long ms);

    public void unlock();
}
