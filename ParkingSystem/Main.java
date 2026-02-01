public class Main {

    public static void main(String[] args) {

        Integer[][][] parking = {
                {
                        { 4, 4, 2, 0 },
                        { 2, 4, 2, 2 },
                        { 0, 2, 0, 2 },
                        { 4, 4, 4, 2 }
                }
        };

        Solution solution = new Solution();
        Helper07 helper = new Helper07();

        // 1. init
        solution.init(helper, parking);

        // 2. park(vehicleType=4, vehicleNumber='V-1', ticketId='T-2', parkingStrategy=0)
        String spotId = solution.park(4, "V-1", "T-2", 0);
        System.out.println("2) " + spotId); // expected: 0-0-0

        // 3. getFreeSpotsCount(floor=0, vehicleType=4)
        System.out.println("3) " + solution.getFreeSpotsCount(0, 4)); // expected: 5

        // 4. park(vehicleType=4, vehicleNumber='V-3', ticketId='T-4', parkingStrategy=0)
        spotId = solution.park(4, "V-3", "T-4", 0);
        System.out.println("4) " + spotId); // expected: 0-0-1

        // 5. getFreeSpotsCount(floor=0, vehicleType=4)
        System.out.println("5) " + solution.getFreeSpotsCount(0, 4)); // expected: 4

        // 6. park(vehicleType=4, vehicleNumber='V-5', ticketId='T-6', parkingStrategy=1)
        spotId = solution.park(4, "V-5", "T-6", 1);
        System.out.println("6) " + spotId); // expected: 0-1-1

        // 7. getFreeSpotsCount(floor=0, vehicleType=4)
        System.out.println("7) " + solution.getFreeSpotsCount(0, 4)); // expected: 3

        // 8. park(vehicleType=2, vehicleNumber='V-7', ticketId='T-8', parkingStrategy=1)
        spotId = solution.park(2, "V-7", "T-8", 1);
        System.out.println("8) " + spotId); // expected: 0-0-2

        // 9. getFreeSpotsCount(floor=0, vehicleType=2)
        System.out.println("9) " + solution.getFreeSpotsCount(0, 2)); // expected: 6

        // 10. removeVehicle(spotId=0-0-0)
        System.out.println("10) " + solution.removeVehicle("0-0-0")); // expected: true

        // ❌ FAILING CALL
        // getFreeSpotsCount(floor=0, vehicleType=4)
        System.out.println("11) " + solution.getFreeSpotsCount(0, 4));
        // ❌ your code returns 6
        // ✅ correct answer is 4
    }
}
