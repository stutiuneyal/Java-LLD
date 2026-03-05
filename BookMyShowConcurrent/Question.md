# LLD: BookMyShow-like Seat Booking with Concurrency (In-Memory)

Design a simplified online ticket booking system similar to BookMyShow.

## Core Domain
- Movies
- Theatres (Screens)
- Shows (a movie playing on a screen at a fixed time)
- Seats (belong to a screen; each show has its own seat availability state)
- Users

## Functional Requirements
1. Search / browse
   - List movies (optional)
   - List shows for a movie in a city/date (simplified is fine)
2. Seat map
   - Fetch seat map for a show (AVAILABLE / HELD / BOOKED)
3. Hold seats (temporary lock)
   - A user can select seats and place a HOLD for X minutes (e.g., 2 minutes)
   - Held seats should not be holdable by other users during the hold window
4. Release hold
   - User can release the hold (or it expires automatically)
5. Confirm booking
   - User confirms booking for a hold (payment reference string is enough)
   - Seats become BOOKED

## Non-Functional / Concurrency Requirements
- The system is in-memory and must handle concurrent requests.
- Prevent double booking:
  - Two users attempting the same seat concurrently must never both succeed.
- Reads are frequent (seat map views), writes are less frequent (hold/confirm).
  - Use a per-show locking strategy (avoid global lock).
- Holds expire:
  - After expiry, seats revert back to AVAILABLE automatically.
- Idempotency:
  - HOLD / RELEASE / CONFIRM must be idempotent based on an idempotency key:
    - If the same request is retried, return the same result without duplicating effects.

## Constraints (Reasonable for LLD)
- One show can have up to ~500 seats.
- Multiple shows can be active concurrently.
- Assume single process (no distributed locking needed).
- You may use Java standard library concurrency primitives.

## APIs (Suggested)
- GET  /shows/{showId}/seats
- POST /shows/{showId}/holds      { userId, seatIds, idemKey }
- POST /holds/{holdId}/release    { userId, idemKey }
- POST /holds/{holdId}/confirm    { userId, paymentRef, idemKey }

## Expected Output from Candidate
- Clean entities + services + repositories
- Locking plan (and why)
- Expiry mechanism for holds
- Idempotency store design
- Mermaid diagrams for class + key flows
- Code that is easy to test locally