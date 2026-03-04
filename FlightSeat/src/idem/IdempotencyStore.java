package idem;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/*
* Key Pattern: include operation + userId + idemKey
* Value: the response object that we want to replay
*/
public class IdempotencyStore {

    private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

    public Optional<Object> get(String key) {
        return Optional.ofNullable(store.get(key));
    }

    public Object putIfAbsent(String key, Object value) {
        Object existing = store.putIfAbsent(key, value);
        return existing != null ? existing : value;
    }
}
