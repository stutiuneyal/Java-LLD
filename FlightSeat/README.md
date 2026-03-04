# In-Memory Flight Seat Booking System (LLD) — Uber SDE-1

## Problem
Design an in-memory backend system to allow users to:
- Search flights
- View seat maps
- Temporarily hold seats (with TTL/expiry)
- Confirm booking from a hold
- Release holds

The system must handle **high concurrency** such that:
- No seat is held or booked by two users simultaneously
- Expired holds automatically free seats
- Requests can be retried without creating duplicate holds/bookings (idempotency)

This is similar to an Uber-like matching problem:
- Multiple users compete for a limited resource (seats)
- The system must resolve conflicts safely under concurrent requests

---

## Functional Requirements

### 1. Search Flights
Input: origin, destination, date  
Output: list of flights

### 2. View Seat Map
For a flight, return seat layout + each seat status:
- AVAILABLE
- HELD (with expiry)
- BOOKED

### 3. Hold Seats (Temporary reservation)
User requests a hold for 1..N seats:
- If all seats are AVAILABLE, system creates a Hold and marks seats HELD
- Hold expires after a TTL (e.g., 5 minutes)
- If any seat is not AVAILABLE, the hold fails atomically (all-or-nothing)

### 4. Confirm Booking
User confirms a hold:
- Allowed only if hold is still valid
- Seats move from HELD -> BOOKED
- Confirmation is idempotent

### 5. Release Hold
User can release a hold before expiry:
- Seats move HELD -> AVAILABLE
- Release is idempotent

---

## Non-Functional Requirements

1. Concurrency correctness:
   - Under concurrent calls, the seat state machine must stay consistent.
2. Performance:
   - Seat map reads should allow concurrent access.
3. Idempotency:
   - Duplicate retries should return the same response as the first successful call.

---

## Seat State Machine
AVAILABLE -> HELD -> BOOKED  
HELD -> AVAILABLE (expiry/release)

BOOKED is terminal (for this design).

---

## Suggested APIs (LLD level)

GET  /flights?from=BLR&to=DEL&date=2026-03-12  
GET  /flights/{flightId}/seats  

POST /flights/{flightId}/holds
{
  "userId": "u1",
  "seatIds": ["12A","12B"],
  "idempotencyKey": "hold-uuid"
}

POST /holds/{holdId}/confirm
{
  "paymentRef": "dummy-pay-ref",
  "idempotencyKey": "confirm-uuid"
}

POST /holds/{holdId}/release
{
  "userId": "u1",
  "idempotencyKey": "release-uuid"
}