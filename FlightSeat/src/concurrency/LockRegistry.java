package concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
* Provides a per-flight ReadWrite Lock

* Why needed:
* - We want concurrent lock -> reads
* - We want exclusive lock -> write

*/
public class LockRegistry {

    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

    private ReentrantReadWriteLock lockFor(String flightId) {
        return locks.computeIfAbsent(flightId, k -> new ReentrantReadWriteLock(true)); // fair locking for predicatble
                                                                                       // scheduling
    }

    public ReentrantReadWriteLock.ReadLock readLock(String flightId) {
        return lockFor(flightId).readLock();
    }

    public ReentrantReadWriteLock.WriteLock writeLock(String flightId) {
        return lockFor(flightId).writeLock();
    }
}
