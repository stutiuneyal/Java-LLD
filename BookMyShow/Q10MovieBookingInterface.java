import java.util.List;

public interface Q10MovieBookingInterface {

    void init(Helper10 helper);

    void addCinema(int cinemaId, int cityId,
            int screenCount, int screenRow, int screenColumn);

    void addShow(int showId, int movieId, int cinemaId,
            int screenIndex, long startTime, long endTime);

    List<String> bookTicket(String ticketId,
            int showId, int ticketsCount);

    boolean cancelTicket(String ticketId);

    int getFreeSeatsCount(int showId);

    // returns cinemaId's of all cinemas which are running a show for given movie
    // cinemaId's are ordered in ascending order
    List<Integer> listCinemas(int movieId, int cityId);

    // returns all showId's of all shows displaying the movie in given cinema
    // showId's are ordered in ascending order
    List<Integer> listShows(int movieId, int cinemaId);
}