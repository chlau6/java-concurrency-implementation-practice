package aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// Twinslock can be held / shared between two threads at the same time
public class TwinsLock implements Lock {
    // valid state is 0, 1, 2
    // 0: no one is holding the lock
    // 1: one seat left for threads
    // 2: two seats left for threads
    private final Sync sync = new Sync(2);

    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("count must large than zero.");
            }

            setState(count);
        }

        public int tryAcquireShared(int reduceCount) {
            for (;;) {
                int current = getState();
                int newCount = current - reduceCount;

                // return the state to tryAcquire
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount;
                }
            }
        }

        // Unlike Mutex, release lock require CAS operation
        // Two threads can release the lock at the same time, and it may cause race condition
        public boolean tryReleaseShared(int returnCount) {
            for (;;) {
                int current = getState();
                int newCount = current + returnCount;

                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }
    }

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquireShared(1) > 0;
    }

    // TODO: implement
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    // TODO: implement
    @Override
    public Condition newCondition() {
        return null;
    }

    // TODO: implement
    @Override
    public void lockInterruptibly() throws InterruptedException {

    }
}
