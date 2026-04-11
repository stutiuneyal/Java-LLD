package main.java.rate_limiting.redis;

import java.util.Map;
import java.util.Set;

public interface RedisClient {

    long increment(String key);

    void expire(String key, long ttlSeconds);

    Long getLong(String key);

    void setLong(String key, long value, long ttlSeconds);

    void zAdd(String key, long score, String member);

    void zRemoveRangeByScore(String key, long minScoreInclusive, long maxScoreInclusive);

    long zCard(String key);

    Set<String> zRange(String key);

    void hSet(String key, String field, String value);

    String hGet(String key, String field);

    Map<String, String> hGetAll(String key);

    void delete(String key);
}
