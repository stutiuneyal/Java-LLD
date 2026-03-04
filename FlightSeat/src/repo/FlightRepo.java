package repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import model.Flight;

/*
* repository for flights
* Thread-safe storage
*/
public class FlightRepo {

    private final ConcurrentHashMap<String,Flight> flightById = new ConcurrentHashMap<>();

    public void addFlight(Flight flight){
        flightById.put(flight.getFlightId(), flight);
    }

    public Optional<Flight> getById(String flightId){
        return Optional.ofNullable(flightById.get(flightId));
    }

    public List<Flight> search(String from, String to, LocalDate date){
        return flightById.values().stream()
        .filter(f -> f.getFrom().equalsIgnoreCase(from))
        .filter(f -> f.getTo().equalsIgnoreCase(to))
        .filter(f -> f.getDate().equals(date))
        .collect(Collectors.toList());
    }
}
