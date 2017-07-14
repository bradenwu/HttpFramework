
package com.pacewear.lock;

import java.util.concurrent.Semaphore;

public class SemophoneLock implements IJavaLock {
    private Semaphore mSemaphore = null;

    public SemophoneLock() {
        mSemaphore = new Semaphore(0);
    }

    @Override
    public void lock(long ms) {
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void unlock() {
        mSemaphore.release();
    }

}
