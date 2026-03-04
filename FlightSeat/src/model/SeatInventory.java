package model;

import java.time.Instant;

/*
One seat on one flight
This is ashared mutable entity -> must be protected by locks
*/
public class SeatInventory {

    private final String seatId;
    private SeatStatus status;
    private String holdId; // which hold owns it
    private Instant holdExpiresAt;

    public SeatInventory(String seatId) {
        this.seatId = seatId;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getSeatId() {
        return seatId;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public String getHoldId() {
        return holdId;
    }

    public boolean isHeldAndExpired(Instant now) {
        return status == SeatStatus.HELD && holdExpiresAt != null && holdExpiresAt.isBefore(now);
    }

    public void markAvailable() {
        this.status = SeatStatus.AVAILABLE;
        this.holdId = null;
        this.holdExpiresAt = null;
    }

    public void markHeld(String holdId, Instant expiresAt) {
        this.status = SeatStatus.HELD;
        this.holdId = holdId;
        this.holdExpiresAt = expiresAt;
    }

    public void markBooked() {
        this.status = SeatStatus.BOOKED;
        this.holdId = null;
        this.holdExpiresAt = null;
    }

}
