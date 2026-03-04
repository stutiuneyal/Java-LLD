package model;

import java.time.Instant;
import java.util.List;

public class SeatHold {

    private final String holdId;
    private String flightId;
    private String userId;
    private List<String> seatIds;
    private final Instant createdAt;
    private final Instant expiresAt;

    public SeatHold(String holdId, String flightId, String userId, List<String> seatIds, Instant createdAt,
            Instant expiresAt) {
        this.holdId = holdId;
        this.flightId = flightId;
        this.userId = userId;
        this.seatIds = seatIds;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getHoldId() {
        return holdId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired(Instant now){
        return expiresAt.isBefore(now);
    }

}
