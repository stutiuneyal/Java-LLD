package repo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.SeatInventory;
import model.SeatStatus;

/*
 * in-memory seat storage: flightId -> (seatId -> SeatInventory)
*/
public class SeatRepo {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, SeatInventory>> seats = new ConcurrentHashMap<>();

    /* Initialize a seat map if missing */
    public void initIfMissing(String flightId, List<String> seatIds) {
        seats.computeIfAbsent(flightId, f -> new ConcurrentHashMap<>());
        ConcurrentHashMap<String, SeatInventory> m = seats.get(flightId);
        for (String seatId : seatIds) {
            m.computeIfAbsent(seatId, SeatInventory::new);
        }
    }

    public Map<String, SeatInventory> getSeatMapSnapshot(String flightId) {
        ConcurrentHashMap<String, SeatInventory> m = seats.getOrDefault(flightId, new ConcurrentHashMap<>());
        return new LinkedHashMap<>(m);
    }

    public List<SeatInventory> getSeatsMutable(String flightId, List<String> seatIds) {
        ConcurrentHashMap<String, SeatInventory> m = seats.get(flightId);
        List<SeatInventory> out = new ArrayList<>();
        for (String seatId : seatIds) {
            SeatInventory seat = m.get(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat not found: " + seatId);
            }
            out.add(seat);
        }

        return out;
    }

    /* Lazy Expiry */
    public void expireHoldsIfNeeded(String flightId, Instant now) {
        ConcurrentHashMap<String, SeatInventory> m = seats.getOrDefault(flightId, new ConcurrentHashMap<>());
        for (SeatInventory seat : m.values()) {
            if (seat.isHeldAndExpired(now)) {
                seat.markAvailable();
            }
        }
    }

    /* Whether all the required seats are Available or not */
    public boolean isAllAvailable(List<SeatInventory> seats) {
        for (SeatInventory s : seats) {
            if (s.getStatus() != SeatStatus.AVAILABLE) {
                return false;
            }
        }
        return true;
    }
}
