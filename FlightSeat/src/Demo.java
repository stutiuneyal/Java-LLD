import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import concurrency.LockRegistry;
import idem.IdempotencyStore;
import model.Booking;
import model.Flight;
import model.SeatHold;
import repo.FlightRepo;
import repo.SeatRepo;
import service.BookingService;
import service.FlightService;
import service.SeatHoldService;

public class Demo {

    public static void main(String[] args) throws InterruptedException {

        FlightRepo flightRepo = new FlightRepo();
        SeatRepo seatRepo = new SeatRepo();
        LockRegistry locks = new LockRegistry();
        IdempotencyStore idem = new IdempotencyStore();

        String flightId = "F100";
        flightRepo.addFlight(new Flight(flightId, "BLR", "DEL", LocalDate.now()));

        // create seat map
        seatRepo.initIfMissing(flightId, List.of("12A", "12B", "12C", "13A", "13B"));

        FlightService flightService = new FlightService(flightRepo, seatRepo, locks);
        SeatHoldService holdService = new SeatHoldService(seatRepo, flightRepo, locks, idem, Duration.ofSeconds(30l));
        BookingService bookingService = new BookingService(seatRepo, locks, holdService, idem);

        System.out.println("Initial seat map: " + flightService.getSeatMap(flightId).keySet());

        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    start.await();
                    String user = "u" + idx;
                    // All race for 12A
                    SeatHold hold = holdService.createHold(flightId, user, List.of("12A"), "hold-" + idx);
                    System.out.println("SUCCESS user=" + user + " holdId=" + hold.getHoldId());

                    // First successful holder confirms quickly (optional)
                    if (idx == 0) {
                        Booking booking = bookingService.confirmHold(hold.getHoldId(), user, "pay-demo", "confirm-" + idx);
                        System.out.println("BOOKED user=" + user + " bookingId=" + booking.getBookingId());
                    }
                } catch (Exception e) {
                    System.out.println(
                            "FAIL user=u" + idx + " reason=" + e.getClass().getSimpleName() + " " + e.getMessage());
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        pool.shutdown();

        System.out.println("Final seat status for 12A = " + flightService.getSeatMap(flightId).get("12A").getStatus());

    }
}
