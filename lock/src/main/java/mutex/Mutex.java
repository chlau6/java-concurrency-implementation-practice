package mutex;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


class Mutex implements Lock {
    // Lock.class is user-facing
    // AbstractQueuedSynchronizer.class is lock-implementation-facing
    private static class Sync extends AbstractQueuedSynchronizer {
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        // acquire lock, 0 means no one hold lock, 1 means successfully get lock
        // setState use CAS operation to prevent race condition
        public boolean tryAcquire(int acquires) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }

            return false;
        }

        // release lock, set state to 0
        // only lock holder can release the lock, so no race condition occurs and no need to use CAS
        protected boolean tryRelease(int releases) {
            if (getState() == 0) throw new IllegalMonitorStateException();

            setExclusiveOwnerThread(null);
            setState(0);

            return true;
        }

        // condition queue
        Condition newCondition() {
            return new ConditionObject();
        }
    }

    // Sync class is a proxy to manage the lock implementation
    private final Sync sync = new Sync();

    public void lock() {
        sync.acquire(1);
    }

    // tryLock return lock acquisition result immediately, so it's non-blocking
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    // tryLock with timeout
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    public void unlock() {
        sync.release(1);
    }

    public Condition newCondition() {
        return sync.newCondition();
    }

    public boolean isLocked() {
        return sync.isHeldExclusively();
    }

    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }
}
