public interface Q06WebpageVisitCounterInterface {
    void init(int totalPages, Helper06 helper);

    void incrementVisitCount(int pageIndex);

    int getVisitCount(int pageIndex);
}
