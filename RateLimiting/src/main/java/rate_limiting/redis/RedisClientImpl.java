package main.java.rate_limiting.redis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class RedisClientImpl implements RedisClient {

    private static class ExpiringValue {
        Object value;
        long expiryTimeMillis; // 0 means no expiry

        ExpiringValue(Object value, long expiryTimeMillis) {
            this.value = value;
            this.expiryTimeMillis = expiryTimeMillis;
        }
    }

    private final Map<String, ExpiringValue> store = new ConcurrentHashMap<>();

    private void cleanupIfExpired(String key) {
        ExpiringValue value = store.get(key);
        if (value != null && value.expiryTimeMillis > 0 && System.currentTimeMillis() > value.expiryTimeMillis) {
            store.remove(key);
        }
    }

    @Override
    public synchronized long increment(String key) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current == null) {
            AtomicLong counter = new AtomicLong(1);
            store.put(key, new ExpiringValue(counter, 0));
            return 1;
        }
        AtomicLong counter = (AtomicLong) current.value;
        return counter.incrementAndGet();
    }

    @Override
    public synchronized void expire(String key, long ttlSeconds) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current != null) {
            current.expiryTimeMillis = System.currentTimeMillis() + ttlSeconds * 1000;
        }
    }

    @Override
    public synchronized Long getLong(String key) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current == null) {
            return null;
        }
        return ((AtomicLong) current.value).get();
    }

    @Override
    public synchronized void setLong(String key, long value, long ttlSeconds) {
        long expiry = ttlSeconds > 0 ? System.currentTimeMillis() + ttlSeconds * 1000 : 0;
        store.put(key, new ExpiringValue(new AtomicLong(value), expiry));
    }

    @Override
    public synchronized void zAdd(String key, long score, String member) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        ConcurrentSkipListMap<Long, List<String>> zset;

        if (current == null) {
            zset = new ConcurrentSkipListMap<>();
            store.put(key, new ExpiringValue(zset, 0));
        } else {
            zset = (ConcurrentSkipListMap<Long, List<String>>) current.value;
        }

        zset.computeIfAbsent(score, k -> new ArrayList<>()).add(member);
    }

    @Override
    public synchronized void zRemoveRangeByScore(String key, long minScoreInclusive, long maxScoreInclusive) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current == null) return;

        ConcurrentSkipListMap<Long, List<String>> zset = (ConcurrentSkipListMap<Long, List<String>>) current.value;
        NavigableMap<Long, List<String>> sub = zset.subMap(minScoreInclusive, true, maxScoreInclusive, true);
        Set<Long> keysToRemove = new HashSet<>(sub.keySet());
        for (Long k : keysToRemove) {
            zset.remove(k);
        }
    }

    @Override
    public synchronized long zCard(String key) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current == null) return 0;

        ConcurrentSkipListMap<Long, List<String>> zset = (ConcurrentSkipListMap<Long, List<String>>) current.value;
        long count = 0;
        for (List<String> list : zset.values()) {
            count += list.size();
        }
        return count;
    }

    @Override
    public synchronized Set<String> zRange(String key) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current == null) return Collections.emptySet();

        ConcurrentSkipListMap<Long, List<String>> zset = (ConcurrentSkipListMap<Long, List<String>>) current.value;
        Set<String> result = new LinkedHashSet<>();
        for (List<String> list : zset.values()) {
            result.addAll(list);
        }
        return result;
    }

    @Override
    public synchronized void hSet(String key, String field, String value) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        Map<String, String> hash;
        if (current == null) {
            hash = new ConcurrentHashMap<>();
            store.put(key, new ExpiringValue(hash, 0));
        } else {
            hash = (Map<String, String>) current.value;
        }
        hash.put(field, value);
    }

    @Override
    public synchronized String hGet(String key, String field) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current == null) return null;
        Map<String, String> hash = (Map<String, String>) current.value;
        return hash.get(field);
    }

    @Override
    public synchronized Map<String, String> hGetAll(String key) {
        cleanupIfExpired(key);
        ExpiringValue current = store.get(key);
        if (current == null) return Collections.emptyMap();
        return new HashMap<>((Map<String, String>) current.value);
    }

    @Override
    public synchronized void delete(String key) {
        store.remove(key);
    }
}
