
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Solution implements Q06WebpageVisitCounterInterface {

    private Helper06 helper;
    private AtomicIntegerArray counts;

    public Solution() {
    }

    @Override
    public void init(int totalPages, Helper06 helper) {
        this.helper = helper;
        this.counts = new AtomicIntegerArray(totalPages);

    }

    @Override
    public void incrementVisitCount(int pageIndex) {
        this.counts.incrementAndGet(pageIndex);
    }

    @Override
    public int getVisitCount(int pageIndex) {
        return this.counts.get(pageIndex);
    }

}
