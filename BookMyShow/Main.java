public class Main {
    public static void main(String[] args) {
        Helper10 helper = new Helper10();
        Solution obj = new Solution();
        obj.init(helper);
        obj.addCinema(0,
                1, 4,
                5, 10);
        obj.addShow(1, 4,
                0, 1,
                1710516108725l, 1710523308725l);
        obj.addShow(2, 11,
                0, 3,
                1710516108725l, 1710523308725l);

        System.out.println(obj.listCinemas(0, 1));

        System.out.println(obj.listShows(4, 0));

        System.out.println(obj.listShows(11, 0));

        System.out.println(obj.getFreeSeatsCount(1));
        System.out.println(obj.bookTicket("tkt-1", 1, 4));

        System.out.println(obj.getFreeSeatsCount(1));

        System.out.println(obj.cancelTicket("tkt-1"));
        System.out.println(obj.getFreeSeatsCount(1));
    }
}
