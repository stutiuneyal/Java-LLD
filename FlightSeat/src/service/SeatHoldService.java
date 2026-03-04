package service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import concurrency.LockRegistry;
import exception.BadRequestException;
import exception.ConflictException;
import exception.NotFoundException;
import exception.UnauthorizedException;
import idem.IdempotencyStore;
import model.SeatHold;
import model.SeatInventory;
import repo.FlightRepo;
import repo.SeatRepo;
import util.IdUtil;
import util.TimeUtil;

public class SeatHoldService {

    private final SeatRepo seatRepo;
    private final FlightRepo flightRepo;
    private final LockRegistry registry;
    private final IdempotencyStore store;

    private final ConcurrentHashMap<String, SeatHold> holdsById = new ConcurrentHashMap<>();

    private final Duration holdTtl;

    public SeatHoldService(SeatRepo seatRepo, FlightRepo flightRepo, LockRegistry registry, IdempotencyStore store,
            Duration holdTtl) {
        this.seatRepo = seatRepo;
        this.flightRepo = flightRepo;
        this.registry = registry;
        this.store = store;
        this.holdTtl = holdTtl;
    }

    /* Idempotency key should be per operation -> to avoid any collisions */
    private String createIdemKey(String operation, String userId, String idemKey) {
        return operation + "|" + userId + "|" + idemKey;
    }

    public SeatHold createHold(String flightId, String userId, List<String> seatIds, String idempotencyKey) {

        if (seatIds == null || seatIds.isEmpty()) {
            throw new BadRequestException("seatIds is Required");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("idempotencyKey is Required");
        }

        flightRepo.getById(flightId).orElseThrow(() -> new NotFoundException("Flight Not Found: " + flightId));

        // Return the same response for retries
        Optional<Object> cached = store.get(createIdemKey("HOLD", userId, idempotencyKey));

        if (cached.isPresent()) {
            return (SeatHold) cached.get();
        }

        // Create new hold
        ReentrantReadWriteLock.WriteLock lock = registry.writeLock(flightId);
        lock.lock();

        String holdId = "";

        try {
            // Double check idempotency inside the -> avoid race condition
            cached = store.get(createIdemKey("HOLD", userId, idempotencyKey));
            if (cached.isPresent()) {
                return (SeatHold) cached.get();
            }

            Instant now = Instant.now();
            seatRepo.expireHoldsIfNeeded(flightId, now);

            // ensure deterministic behaviour
            seatIds = seatIds.stream().map(String::trim).map(String::toUpperCase).sorted().toList();

            List<SeatInventory> seats = seatRepo.getSeatsMutable(flightId, seatIds);

            if (!seatRepo.isAllAvailable(seats)) {
                throw new ConflictException("One or more seats are not available");
            }

            // all the seats are available
            holdId = IdUtil.newId("hold");
            Instant expiresAt = now.plus(holdTtl);

            for (SeatInventory s : seats) {
                s.markHeld(holdId, expiresAt);
            }

            // create a SeatHold object
            SeatHold hold = new SeatHold(holdId, flightId, userId, seatIds, now, expiresAt);
            holdsById.put(holdId, hold);

            // store this in the idemptency store
            Object stored = store.putIfAbsent(createIdemKey("HOLD", userId, idempotencyKey), hold);
            return (SeatHold) stored;

        } finally {
            releaseHold(holdId, userId, idempotencyKey);
            lock.unlock();
        }
    }

    public String releaseHold(String holdId, String userId, String idempotencyKey) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("idempotencyKey is Required");
        }

        Optional<Object> cached = store.get(createIdemKey("RELEASE", userId, idempotencyKey));
        if (cached.isPresent()) {
            return (String) cached.get();
        }

        SeatHold hold = holdsById.get(holdId);
        if (hold == null) {
            throw new NotFoundException("Hold not found: " + holdId);
        }
        if (!hold.getUserId().equals(userId)) {
            throw new UnauthorizedException("Hold doesnot belong to the currently loggedIn user");
        }

        ReentrantReadWriteLock.WriteLock lock = registry.writeLock(hold.getFlightId());
        lock.lock();

        try {
            cached = store.get(createIdemKey("RELEASE", userId, idempotencyKey));
            if (cached.isPresent()) {
                return (String) cached.get();
            }

            Instant now = TimeUtil.now();
            seatRepo.expireHoldsIfNeeded(hold.getFlightId(), now);

            // If already expired, threat it as a successful RELEASE -> idempotent-friendly
            if (hold.isExpired(now)) {
                String res = "ALREADY_EXPIRED";
                Object released = store.putIfAbsent(createIdemKey("RELEASE", userId, idempotencyKey), res);
                return (String) released;
            }

            List<SeatInventory> seats = seatRepo.getSeatsMutable(hold.getFlightId(), hold.getSeatIds());
            for (SeatInventory s : seats) {
                // Only release the seats owned by this user -> extra safety
                if (holdId.equals(s.getHoldId())) {
                    s.markAvailable();
                }
            }

            String res = "ALREADY_EXPIRED";
            Object released = store.putIfAbsent(createIdemKey("RELEASE", userId, idempotencyKey), res);
            return (String) released;
        } finally {
            lock.unlock();
        }
    }

    public SeatHold getHold(String holdId) {
        SeatHold hold = holdsById.get(holdId);
        if (hold == null) {
            throw new NotFoundException("Hold not found: " + holdId);
        }
        return hold;
    }

}
