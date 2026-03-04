package model;

import java.time.LocalDate;

public class Flight {

    private final String flightId;
    private String from;
    private String to;
    private LocalDate date;

    public Flight(String flightId, String from, String to, LocalDate date) {
        this.flightId = flightId;
        this.from = from;
        this.to = to;
        this.date = date;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}