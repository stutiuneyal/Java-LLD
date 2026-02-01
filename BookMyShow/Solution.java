import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Solution implements Q10MovieBookingInterface {

    private Helper10 helper;
    private Map<Integer, Cinema> cinemaMap;
    // CinemaId -> ShowId -> Show
    private Map<Integer, Map<Integer, Show>> showMap;
    private Map<String, Ticket> ticketMap;

    public Solution() {
    }

    @Override
    public void init(Helper10 helper) {
        this.helper = helper;
        this.cinemaMap = new HashMap<>();
        this.showMap = new HashMap<>();
        this.ticketMap = new HashMap<>();
    }

    @Override
    public void addCinema(int cinemaId, int cityId, int screenCount, int screenRow, int screenColumn) {
        this.cinemaMap.put(cinemaId, new Cinema(cinemaId, cityId, screenCount, screenRow, screenColumn));
    }

    @Override
    public void addShow(int showId, int movieId, int cinemaId, int screenIndex, long startTime, long endTime) {

        Cinema cinema = this.cinemaMap.getOrDefault(cinemaId, null);
        if (cinema == null) {
            return;
        }

        Map<Integer, Show> showDetails = this.showMap.computeIfAbsent(cinema.getCinemaId(), k -> new TreeMap<>());
        showDetails.put(showId, new Show(showId, movieId, cinemaId, screenIndex, startTime, endTime, cinema));
    }

    @Override
    public List<String> bookTicket(String ticketId, int showId, int ticketsCount) {

        // handle if ticket already present
        if (this.ticketMap.containsKey(ticketId)) {
            return this.ticketMap.get(ticketId).getSeatsBooked();
        }

        // find the show
        Show show = findShowById(showId);

        if (show == null) {
            return new ArrayList<>();
        }

        // handle free-seats validation
        if (show.getFreeSeatCount() < ticketsCount) {
            return new ArrayList<>();
        }

        // book the tickets

        /*
         * a. Iterate the seatMatrix and find the (i,j) with continuous available seats
         * -> book them
         * If Not found:
         * b. find first (i,j) that is available -> then from there start with random
         * allocation until all have been booked
         *
         * -1 -1 0 0 0 -1 0 0
         * -1 0 -1 0
         * -1 -1 0 0
         */

        show.setFreeSeatCount(show.getFreeSeatCount() - ticketsCount);

        List<String> seatsBooked = new ArrayList<>();
        int[][] seatMatrix = show.getSeatMatrix();

        boolean continuousSeatsFound = false;
        // int idx = -1, idy = -1;
        // for (int i = 0; i < seatMatrix.length; i++) {
        //     int count = 0;
        //     for (int j = 0; j < seatMatrix[i].length; j++) {
        //         if (seatMatrix[i][j] == 0) {
        //             if (idy == -1) {
        //                 idy = j;
        //             }
        //             count++;
        //         }
        //     }
        //     if (count >= ticketsCount) {
        //         idx = i;
        //         continuousSeatsFound = true;
        //         break;
        //     }

        //     idy = -1;
        // }

        int idx = -1, idy = -1;
        for(int i = 0; i < seatMatrix.length; i++){
            int consecutive = 0;
            for (int j = 0; j < seatMatrix[i].length; j++) {
                if(seatMatrix[i][j]==0){
                    consecutive++;
                    if(consecutive == ticketsCount){
                        idx=i;
                        idy = j-ticketsCount+1;
                        continuousSeatsFound = true;
                        break;
                    }
                }else{
                    consecutive=0;
                }
            }
            if(continuousSeatsFound){
                break;
            }
        }

        if (continuousSeatsFound) {

            for (int i = idy; i < seatMatrix[idx].length; i++) {
                if (ticketsCount <= 0) {
                    break;
                }
                ticketsCount--;
                String seat = idx + "-" + i;
                seatsBooked.add(seat);
                seatMatrix[idx][i] = -1;
            }

            show.setSeatMatrix(seatMatrix);
            this.ticketMap.put(ticketId, new Ticket(ticketId, showId, seatsBooked));

            return seatsBooked;
        }

        // If not continuous seats

        for (int i = 0; i < seatMatrix.length; i++) {
            for (int j = 0; j < seatMatrix[i].length; j++) {
                if (seatMatrix[i][j] == 0) {
                    String seat = i + "-" + j;
                    seatsBooked.add(seat);
                    ticketsCount--;
                    seatMatrix[i][j] = -1;
                }
                if (ticketsCount <= 0) {
                    break;
                }
            }
            if (ticketsCount <= 0) {
                break;
            }
        }

        show.setSeatMatrix(seatMatrix);
        this.ticketMap.put(ticketId, new Ticket(ticketId, showId, seatsBooked));

        return seatsBooked;

    }

    @Override
    public boolean cancelTicket(String ticketId) {
        /*
         * a. cancel seats
         * b. free the seats
         * c. update the seatMatrix
         */
        Ticket ticket = this.ticketMap.get(ticketId);
        if (ticket == null) {
            return false;
        }

        Show show = findShowById(ticket.getShowId());
        if (show == null) {
            return false;
        }

        int[][] seatMatrix = show.getSeatMatrix();

        for (String booked : ticket.getSeatsBooked()) {
            String[] splits = booked.split("-");
            int i = Integer.parseInt(splits[0]);
            int j = Integer.parseInt(splits[1]);
            seatMatrix[i][j] = 0;
        }

        show.setSeatMatrix(seatMatrix);
        show.setFreeSeatCount(show.getFreeSeatCount() + ticket.getSeatsBooked().size());
        this.ticketMap.remove(ticketId);

        return true;

    }

    @Override
    public int getFreeSeatsCount(int showId) {
        Show show = findShowById(showId);

        return show != null ? show.getFreeSeatCount() : 0;
    }

    @Override
    public List<Integer> listCinemas(int movieId, int cityId) {

        Set<Integer> cinemas = new TreeSet<>();

        for (Integer cinemaId : this.showMap.keySet()) {
            Cinema cinema = this.cinemaMap.get(cinemaId);
            if (cinema.getCityId() == cityId) {
                for (Integer showId : this.showMap.get(cinemaId).keySet()) {
                    Show show = this.showMap.get(cinemaId).get(showId);
                    if (show.getMovieId() == movieId) {
                        cinemas.add(cinemaId);
                    }
                }
            }
        }

        return new ArrayList<>(cinemas);
    }

    @Override
    public List<Integer> listShows(int movieId, int cinemaId) {

        Map<Integer, Show> showDetails = this.showMap.get(cinemaId);
        if (showDetails == null) {
            return new ArrayList<>();
        }

        /*
         * One Way
         */
        // List<Show> shows = new ArrayList<>();

        // for (Integer showId : showDetails.keySet()) {
        // if (showDetails.get(showId).getMovieId() == movieId) {
        // shows.add(showDetails.get(showId));
        // }
        // }

        // apply custom comparator
        // Collections.sort(shows, new ShowComparator());

        // List<Integer> result = new ArrayList<>();
        // for (Show show : shows) {
        // result.add(show.getShowId());
        // }

        // return result;

        /*
         * Second Way
         */
        return showDetails.values().stream()
                .filter(s -> s.getMovieId() == movieId)
                .sorted(
                        Comparator.comparingLong(Show::getStartTime).reversed()
                                .thenComparing(Show::getShowId))
                .map(Show::getShowId)
                .collect(Collectors.toList());
    }

    /*
     * Classes
     */

    class Cinema {
        private int cinemaId;
        private int cityId;
        private int screenCount;
        private int row;
        private int col;

        public Cinema() {
        }

        public Cinema(int cinemaId, int cityId, int screenCount, int row, int col) {
            this.cinemaId = cinemaId;
            this.cityId = cityId;
            this.screenCount = screenCount;
            this.row = row;
            this.col = col;
        }

        public Integer getCinemaId() {
            return cinemaId;
        }

        public int getCityId() {
            return cityId;
        }

        public int getScreenCount() {
            return screenCount;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

    }

    class Show {
        private int showId;
        private int movieId;
        private int cinemaId;
        private int screenIndex;
        private long startTime;
        private long endTime;
        private int[][] seatMatrix;
        private int freeSeatCount;

        public Show(int showId, int movieId, int cinemaId, int screenIndex, long startTime, long endTime,
                Cinema cinema) {
            this.showId = showId;
            this.movieId = movieId;
            this.cinemaId = cinemaId;
            this.screenIndex = screenIndex;
            this.startTime = startTime;
            this.endTime = endTime;
            this.seatMatrix = new int[cinema.getRow()][cinema.getCol()];
            this.freeSeatCount = cinema.getRow() * cinema.getCol();
        }

        public int getShowId() {
            return showId;
        }

        public int[][] getSeatMatrix() {
            return seatMatrix;
        }

        public void setSeatMatrix(int[][] seatMatrix) {
            this.seatMatrix = seatMatrix;
        }

        public void setFreeSeatCount(int freeSeatCount) {
            this.freeSeatCount = freeSeatCount;
        }

        public int getFreeSeatCount() {
            return freeSeatCount;
        }

        public int getMovieId() {
            return movieId;
        }

        public int getCinemaId() {
            return cinemaId;
        }

        public int getScreenIndex() {
            return screenIndex;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }

    }

    class Ticket {
        private String ticketId;
        private int showId;
        private List<String> seatsBooked;

        public Ticket(String ticketId, int showId, List<String> seatsBooked) {
            this.ticketId = ticketId;
            this.showId = showId;
            this.seatsBooked = seatsBooked;
        }

        public String getTicketId() {
            return ticketId;
        }

        public int getShowId() {
            return showId;
        }

        public List<String> getSeatsBooked() {
            return seatsBooked;
        }

    }

    class ShowComparator implements Comparator<Show> {

        @Override
        public int compare(Show s1, Show s2) {

            // long time = s2.getStartTime() - s1.getStartTime();

            // int id = s1.getShowId() - s2.getShowId();

            int time = Long.compare(s2.getStartTime(), s1.getStartTime());
            int id = Integer.compare(s1.getShowId(), s2.getShowId());

            return time == 0 ? id : time;
        }

    }

    private Show findShowById(int showId) {
        for (Integer cinemaId : this.showMap.keySet()) {
            Map<Integer, Show> map = this.showMap.get(cinemaId);
            if (map.containsKey(showId)) {
                return map.get(showId);
            }
        }

        return null;
    }

}