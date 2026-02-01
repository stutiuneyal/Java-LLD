import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RateLimiter {

    private Map<String, Resource> resourceMap;
    private Map<String,Map<Integer, Integer>> fixedCountMap;
    private Map<String, Deque<Integer>> windowMap;

    public RateLimiter() {
        this.resourceMap = new LinkedHashMap<>();
        this.fixedCountMap = new LinkedHashMap<>();
        this.windowMap = new LinkedHashMap<>();
    }

    public void addResource(String resourceId, String strategy, String limits) {
        this.resourceMap.put(resourceId, new Resource(strategy, limits));
        this.fixedCountMap.remove(resourceId);
        this.windowMap.remove(resourceId);
    }

    public boolean isAllowed(String resourceId, int timestamp) {

        Resource resource = this.resourceMap.get(resourceId);
        if (resource == null) {
            return false;
        }

        switch (resource.getStrategy()) {
            case FIXED:
                /*
                 * timePeriod -> 4
                 * timestamp -> 3 : 1, 5: 2, 9: 3
                 */
                Map<Integer, Integer> resourceCounts = fixedCountMap.computeIfAbsent(resourceId, k -> new LinkedHashMap<>());
                int window = (timestamp / resource.getTimePeriod()) + 1;

                int hitCount = resourceCounts.getOrDefault(window, 0) + 1;
                if (hitCount > resource.getMaxRequestsCount()) {
                    return false;
                }
                if (!resourceCounts.containsKey(window)) {
                    resourceCounts.put(window, 1);
                } else {
                    resourceCounts.put(window, resourceCounts.get(window) + 1);
                }
                this.fixedCountMap.put(resourceId, resourceCounts);
                return true;

            case SLIDING_WINDOW:
                /*
                 * timePeriod -> 3
                 *
                 * (2,3)
                 * timestamp:
                 * 4 -> [2,3,4] -> 1
                 * 6 -> [4,5,6] -> 2
                 * 7 -> [5,6,7] -> 2
                 */
                Deque<Integer> q = this.windowMap.computeIfAbsent(resourceId, k -> new ArrayDeque<>());

                int start = timestamp - resource.getTimePeriod() + 1;
                if(start<0){
                    start = 0;
                }

                // evict the old hits
                while(!q.isEmpty() && q.peekFirst()<start){
                    q.pollFirst();
                }

                if(q.size() >= resource.getMaxRequestsCount()){
                    return false;
                }

                q.addLast(timestamp);

                return true;

            default:
                return false;
        }

    }

    private class Resource {
        private Strategy strategy;
        private int maxRequestsCount;
        private int timePeriod;
        private String limits;

        public Resource(String strategy, String limits) {
            this.strategy = Strategy.getStrategy(strategy);
            this.limits = limits;

            String[] splits = this.limits.trim().split(",");
            this.maxRequestsCount = Integer.parseInt(splits[0]);
            this.timePeriod = Integer.parseInt(splits[1]);
        }

        public Strategy getStrategy() {
            return strategy;
        }

        public int getMaxRequestsCount() {
            return maxRequestsCount;
        }

        public int getTimePeriod() {
            return timePeriod;
        }

    }

    // Just for Information -> hashCode and equals
    class Window {
        public int start;
        public int end;

        public Window(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Window window = (Window) o;

            return start == window.start && end == window.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }

    private enum Strategy {

        FIXED("fixed-window-counter"),
        SLIDING_WINDOW("sliding-window-counter");

        private String value;

        private Strategy(String value) {
            this.value = value;
        }

        public static Strategy getStrategy(String value) {
            if (value.equals(FIXED.getValue())) {
                return FIXED;
            }
            return SLIDING_WINDOW;
        }

        public String getValue() {
            return this.value;
        }
    }
}