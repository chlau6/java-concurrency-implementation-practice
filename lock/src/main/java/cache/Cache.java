package cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache {
    private static Map<String, Object> map = new HashMap<>();
    private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static Lock r = rwl.readLock();
    private static Lock w = rwl.writeLock();

    public static Object get(String key) {
        r.lock();

        try {
            return map.get(key);
        } finally {
            r.unlock();
        }
    }

    public static Object put(String key, Object value) {
        w.lock();

        try {
            return map.put(key, value);
        } finally {
            w.unlock();
        }
    }

    public static void clear() {
        w.lock();

        try {
            map.clear();
        } finally {
            w.unlock();
        }
    }
}
