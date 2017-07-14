
package com.pacewear.lock;

public class SynchronLock implements IJavaLock {
    private final Object sLockObj = new Object();

    @Override
    public void lock(long ms) {
        synchronized (sLockObj) {
            try {
                if (ms <= 0) {
                    sLockObj.wait();
                } else {
                    sLockObj.wait(ms);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void unlock() {
        synchronized (sLockObj) {
            sLockObj.notify();
        }
    }

}
