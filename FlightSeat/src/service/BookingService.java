package service;

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
import model.Booking;
import model.SeatHold;
import model.SeatInventory;
import model.SeatStatus;
import repo.SeatRepo;
import util.IdUtil;
import util.TimeUtil;

public class BookingService {

    private final SeatRepo seatRepo;
    private final LockRegistry registry;
    private final SeatHoldService seatHoldService;
    private final IdempotencyStore store;

    private final ConcurrentHashMap<String, Booking> bookingsById = new ConcurrentHashMap<>();

    public BookingService(SeatRepo seatRepo, LockRegistry registry, SeatHoldService seatHoldService,
            IdempotencyStore store) {
        this.seatRepo = seatRepo;
        this.registry = registry;
        this.seatHoldService = seatHoldService;
        this.store = store;
    }

    /* Idempotency key should be per operation -> to avoid any collisions */
    private String createIdemKey(String operation, String userId, String idemKey) {
        return operation + "|" + userId + "|" + idemKey;
    }

    public Booking confirmHold(String holdId, String userId, String paymentRef, String idempotencyKey) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("idempotencyKey is Required");
        }
        if (paymentRef == null || paymentRef.isBlank()) {
            throw new BadRequestException("paymentRef is Required");
        }

        Optional<Object> cached = store.get(createIdemKey("CONFIRM", userId, idempotencyKey));
        if (cached.isPresent()) {
            return (Booking) cached.get();
        }

        SeatHold hold = seatHoldService.getHold(holdId);
        if (hold == null) {
            throw new NotFoundException("Hold not found: " + holdId);
        }
        if (!hold.getUserId().equals(userId)) {
            throw new UnauthorizedException("Hold doesnot belong to the currently loggedIn user");
        }

        ReentrantReadWriteLock.WriteLock lock = registry.writeLock(hold.getFlightId());
        lock.lock();

        try {
            cached = store.get(createIdemKey("CONFIRM", userId, idempotencyKey));
            if (cached.isPresent()) {
                return (Booking) cached.get();
            }

            Instant now = TimeUtil.now();
            seatRepo.expireHoldsIfNeeded(hold.getFlightId(), now);

            if (hold.isExpired(now)) {
                throw new ConflictException("Hold expired");
            }

            List<SeatInventory> seats = seatRepo.getSeatsMutable(hold.getFlightId(), hold.getSeatIds());

            for (SeatInventory s : seats) {
                if (s.getStatus() != SeatStatus.HELD && !holdId.equals(hold.getHoldId())) {
                    throw new ConflictException("Seat no longer held by this hold: " + s.getSeatId());
                }
            }

            for (SeatInventory s : seats) {
                s.markBooked();
            }

            Booking booking = new Booking(IdUtil.newId("booking"), userId, hold.getFlightId(), hold.getSeatIds(), now,
                    paymentRef);

            bookingsById.put(booking.getBookingId(), booking);

            Object stored = store.putIfAbsent(createIdemKey("CONFIRM", userId, idempotencyKey), booking);
            return (Booking) stored;

        } finally {
            lock.unlock();
        }
    }

    public Booking getBooking(String bookingId) {
        Booking booking = bookingsById.get(bookingId);
        if (booking == null) {
            throw new NotFoundException("Booking not found: " + bookingId);
        }
        return booking;
    }

}
