public interface Q07ParkingLotInterface {

    void init(Helper07 helper, Integer[][][] parking);

    String park(int vehicleType, String vehicleNumber, String ticketId, int parkingStrategy);

    boolean removeVehicle(String spotId);

    String searchVehicle(String query);

    int getFreeSpotsCount(int floor, int vehicleType);
}
