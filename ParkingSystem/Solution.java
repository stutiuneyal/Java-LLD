import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution implements Q07ParkingLotInterface {

    private Helper07 helper;
    Integer[][][] parking;
    private Map<String, Vehicle> vehicleMap; // store spotId -> vehicle
    private Map<String, String> queryMap; // store (ticketId or vehicleNumber) -> spotId
    private Map<Integer, List<ParkingDetails>> floorMap;// store -> spots parked on a particular floor
    private Set<String> occupiedSpots;

    public Solution() {
    }

    @Override
    public void init(Helper07 helper, Integer[][][] parking) {
        this.helper = helper;
        helper.println("Initializing the Parking System");

        this.parking = parking;
        this.vehicleMap = new LinkedHashMap<>();
        this.queryMap = new LinkedHashMap<>();
        this.floorMap = new LinkedHashMap<>();
        this.occupiedSpots = new LinkedHashSet<>();
    }

    @Override
    public String park(int vehicleType, String vehicleNumber, String ticketId, int parkingStrategy) {
        Vehicle vehicle = new Vehicle(vehicleType, vehicleNumber, ticketId);

        String spotId = "";

        int floors = this.parking.length;

        if (parkingStrategy == 0) {

            for (int f = 0; f < floors; f++) {

                Integer[][] parkingSpots = this.parking[f];
                boolean spotFound = false;
                for (int i = 0; i < parkingSpots.length; i++) {
                    for (int j = 0; j < parkingSpots[i].length; j++) {
                        if (vehicle.getVehicleType() == parkingSpots[i][j] && !isSpotOccupied(f, i, j)) {
                            spotId = f + "-" + i + "-" + j;
                            spotFound = true;
                            break;
                        }
                    }
                    if (spotFound) {
                        break;
                    }
                }

                if (spotFound) {
                    break;
                }

            }

            if (spotId.isEmpty()) {
                return "";
            }

            this.vehicleMap.put(spotId, vehicle);
            this.queryMap.put(vehicle.getVehicleNumber(), spotId);
            this.queryMap.put(vehicle.getTicketId(), spotId);
            this.occupiedSpots.add(spotId);

            String[] splits = spotId.split("-");
            if (!this.floorMap.containsKey(Integer.parseInt(splits[0]))) {
                List<ParkingDetails> list = new ArrayList<>();
                list.add(new ParkingDetails(vehicle.getVehicleType(),
                        new Pair(Integer.parseInt(splits[1]), Integer.parseInt(splits[2]))));
                this.floorMap.put(Integer.parseInt(splits[0]), list);
            } else {
                List<ParkingDetails> list = this.floorMap.get(Integer.parseInt(splits[0]));
                list.add(new ParkingDetails(vehicle.getVehicleType(),
                        new Pair(Integer.parseInt(splits[1]), Integer.parseInt(splits[2]))));
                this.floorMap.put(Integer.parseInt(splits[0]), list);
            }

            return spotId;
        }

        // parkingStrategy == 1

        // find the floor with max number of spots for the vehicleType
        int reqFloor = 0, max = 0;

        for (int f = 0; f < floors; f++) {
            Integer[][] parkingSpots = this.parking[f];

            int freeSpotCount = 0;
            for (int i = 0; i < parkingSpots.length; i++) {
                for (int j = 0; j < parkingSpots[i].length; j++) {
                    if (parkingSpots[i][j] == vehicle.getVehicleType() && !isSpotOccupied(f, i, j)) {
                        freeSpotCount++;
                    }
                }
            }

            if (freeSpotCount > max) {
                max = freeSpotCount;
                reqFloor = f;
            }
        }

        Integer[][] parkingSpots = this.parking[reqFloor];
        boolean spotFound = false;
        for (int i = 0; i < parkingSpots.length; i++) {
            for (int j = 0; j < parkingSpots[i].length; j++) {
                if (parkingSpots[i][j] == vehicle.getVehicleType() && !isSpotOccupied(reqFloor, i, j)) {
                    spotId = reqFloor + "-" + i + "-" + j;
                    spotFound = true;
                    break;
                }
            }
            if (spotFound) {
                break;
            }
        }

        if (spotId.isEmpty()) {
            return "";
        }

        this.vehicleMap.put(spotId, vehicle);
        this.queryMap.put(vehicle.getTicketId(), spotId);
        this.queryMap.put(vehicle.getVehicleNumber(), spotId);
        this.occupiedSpots.add(spotId);

        String[] splits = spotId.split("-");
        if (!this.floorMap.containsKey(Integer.parseInt(splits[0]))) {
            List<ParkingDetails> list = new ArrayList<>();
            list.add(new ParkingDetails(vehicle.getVehicleType(),
                    new Pair(Integer.parseInt(splits[1]), Integer.parseInt(splits[2]))));
            this.floorMap.put(Integer.parseInt(splits[0]), list);
        } else {
            List<ParkingDetails> list = this.floorMap.get(Integer.parseInt(splits[0]));
            list.add(new ParkingDetails(vehicle.getVehicleType(),
                    new Pair(Integer.parseInt(splits[1]), Integer.parseInt(splits[2]))));
            this.floorMap.put(Integer.parseInt(splits[0]), list);
        }

        return spotId;

    }

    @Override
    public boolean removeVehicle(String spotId) {
        // lookup vehicleMap

        boolean vehicleRemoved = false;
        Vehicle vehicle = this.vehicleMap.get(spotId);
        if (vehicle != null) {
            this.vehicleMap.remove(spotId);
            vehicleRemoved = true;
        }
        this.occupiedSpots.remove(spotId);

        // lookup queryMap
        this.queryMap.remove(vehicle.getVehicleNumber());
        this.queryMap.remove(vehicle.getTicketId());

        // remove the occupied spot from the floorMap
        String[] splits = spotId.split("-");
        int floor = Integer.parseInt(splits[0]);
        List<ParkingDetails> parkingDetail = this.floorMap.get(floor);
        if (parkingDetail != null) {
            Iterator<ParkingDetails> pit = parkingDetail.listIterator();
            while (pit.hasNext()) {
                ParkingDetails pd = pit.next();
                if (pd.getPair().i == Integer.parseInt(splits[1]) && pd.getPair().j == Integer.parseInt(splits[2])) {
                    pit.remove();
                    break;
                }
            }
        }

        if (!this.queryMap.values().contains(spotId) && vehicleRemoved) {
            return true;
        }

        return false;
    }

    @Override
    public String searchVehicle(String query) {
        return this.queryMap.getOrDefault(query, "");
    }

    @Override
    public int getFreeSpotsCount(int floor, int vehicleType) {

        List<ParkingDetails> parkingDetails = this.floorMap.get(floor);

        int spotsParked = 0;
        int freeSpots = 0;

        if (parkingDetails != null) {
            for (ParkingDetails parkingDetail : parkingDetails) {
                if (parkingDetail.getVehicleType() == vehicleType) {
                    spotsParked++;
                }
            }
        }

        Integer[][] parkingSpots = this.parking[floor];
        for (int i = 0; i < parkingSpots.length; i++) {
            for (int j = 0; j < parkingSpots[i].length; j++) {
                if (parkingSpots[i][j] == vehicleType) {
                    freeSpots++;
                }
            }
        }

        return freeSpots - spotsParked;
    }

    private boolean isSpotOccupied(int floor, int i, int j) {
        return occupiedSpots.contains(floor + "-" + i + "-" + j);
    }

    // Inner Vehicle Class
    class Vehicle {
        private int vehicleType;
        private String vehicleNumber;
        private String ticketId;

        public Vehicle(int vehicleType, String vehicleNumber, String ticketId) {
            this.vehicleType = vehicleType;
            this.vehicleNumber = vehicleNumber;
            this.ticketId = ticketId;
        }

        public int getVehicleType() {
            return vehicleType;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public String getTicketId() {
            return ticketId;
        }

    }

    class ParkingDetails {
        int vehicleType;
        Pair pair;

        ParkingDetails(int vehicleType, Pair pair) {
            this.vehicleType = vehicleType;
            this.pair = pair;
        }

        public int getVehicleType() {
            return vehicleType;
        }

        public Pair getPair() {
            return pair;
        }

    }

    class Pair {
        int i;
        int j;

        Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

}