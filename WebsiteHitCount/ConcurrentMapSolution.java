import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapSolution implements Q06WebpageVisitCounterInterface {

    private Helper06 helper;
    private ConcurrentHashMap<Integer,Integer> countMap;

    public ConcurrentMapSolution() {
    }

    @Override
    public void init(int totalPages, Helper06 helper) {
        this.helper = helper;
        this.countMap = new ConcurrentHashMap<>();

    }

    @Override
    public void incrementVisitCount(int pageIndex) {
        this.countMap.merge(pageIndex, 1, Integer::sum);
    }

    @Override
    public int getVisitCount(int pageIndex) {
        return this.countMap.getOrDefault(pageIndex,0);
    }

}
