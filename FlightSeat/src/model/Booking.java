package model;

import java.time.Instant;
import java.util.List;

public class Booking {

    private final String bookingId;
    private String userId;
    private String flightId;
    private List<String> seatIds;
    private final Instant bookedAt;
    private final String paymentRef;

    public Booking(String bookingId, String userId, String flightId, List<String> seatIds, Instant bookedAt,
            String paymentRef) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.flightId = flightId;
        this.seatIds = seatIds;
        this.bookedAt = bookedAt;
        this.paymentRef = paymentRef;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

}
