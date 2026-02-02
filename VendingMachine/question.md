# LLD Problem — Vending Machine (SDE-1)

## Context
You are asked to design an **in-memory Vending Machine system**. The machine stores items in fixed slots, accepts money, allows users to select items, returns change (if possible), and maintains inventory.

This is a **Low-Level Design** question: focus on **classes, responsibilities, interfaces, flows, and edge cases**.

---

## What you need to build

### Core capabilities
1. The vending machine has **multiple slots**.
2. Each slot can hold **one product type** (e.g., CHIPS) and has:
   - `slotId` (like "A1", "B2")
   - `product`
   - `price`
   - `quantity`
   - `maxCapacity`
3. Users can:
   - Insert money (coins/notes).
   - Select a slot.
   - Either receive the product + change, or get an error and/or refund.
4. Admin can:
   - Refill slots.
   - Change product/price for a slot.
   - View inventory.

---

## Functional Requirements:

**Ask Interviewer**

- Store the transaction history/details: UserName, time, product, qty, amount_inserted, amount_received


## Classes and their fields:

- Person{Abstract Class}: id,name
  - User
  - Admin -> refill(), update(), view()


- Slot: slotId, product, price, qty, maxCap

- Transaction: transactionId, UserName, time, product, qty, amount_inserted, amount_received

- Vending Machine: Map(slots), Map(transactions), int totalMoney;



---

## Non-Functional Requirements:

**Infer from Above**

- For now we are taking amount as a whole and no notes remuneration
- refillSlots -> refill to maxCap

---

## Constraints (Assume)
- Single machine.
- In-memory only (no DB).
- Products and denominations are limited and known.
- Dispensing can be simulated by returning an object/response.