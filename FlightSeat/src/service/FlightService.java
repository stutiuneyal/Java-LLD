package service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import concurrency.LockRegistry;
import exception.NotFoundException;
import model.Flight;
import model.SeatInventory;
import repo.FlightRepo;
import repo.SeatRepo;

/*
 * Read-Focused operations
 * Use read lock to allow concurrent seat-map viewers
*/

public class FlightService {

    private final FlightRepo flightRepo;
    private final SeatRepo seatRepo;
    private final LockRegistry registry;

    public FlightService(FlightRepo flightRepo, SeatRepo seatRepo, LockRegistry registry) {
        this.flightRepo = flightRepo;
        this.seatRepo = seatRepo;
        this.registry = registry;
    }

    public List<Flight> searchFlights(String from, String to, LocalDate date) {
        return flightRepo.search(from, to, date);
    }

    public Map<String, SeatInventory> getSeatMap(String flightId) {
        flightRepo.getById(flightId).orElseThrow(() -> new NotFoundException("Flight Not Found: " + flightId));

        ReentrantReadWriteLock.ReadLock readLock = registry.readLock(flightId);
        readLock.lock();

        try {
            return seatRepo.getSeatMapSnapshot(flightId);
        } finally {
            readLock.unlock();
        }
    }
}
