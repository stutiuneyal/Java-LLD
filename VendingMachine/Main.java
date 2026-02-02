import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static Map<String, Slot> slots;
    private static List<String> admins;

    static {
        buildSlots();

        admins = Arrays.asList("Bob", "Charlie");
    }

    public static void main(String[] args) {

        VendingMachine vendingMachine = new VendingMachine(slots, 1000);


        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Name");
        String name = scanner.nextLine();

        System.out.println("Enter SlotId");
        String slotId = scanner.nextLine();

        System.out.println("Enter Qty: ");
        int qty = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter Amount: ");
        int amount = scanner.nextInt();

        Person person = null;
        if(admins.contains(name)){
            person = new Admin(name);
        }else{
            person = new User(name);
        }

        vendingMachine.purchase(person, slotId, qty, amount);

        if(person instanceof Admin){
            AdminService service = new AdminService();

            service.viewInventory(new ArrayList<>(vendingMachine.getSlots().values()));
        }

        scanner.close();


    }

    private static void buildSlots() {
        slots = new LinkedHashMap<>();

        slots.put("A1", new Slot("A1", "CHIPS", 20, 10, 10));
        slots.put("A2", new Slot("A2", "SODA", 40, 8, 10));
        slots.put("A3", new Slot("A3", "CHOCOLATE", 30, 6, 10));
        slots.put("A4", new Slot("A4", "WATER", 15, 12, 15));
        slots.put("A5", new Slot("A5", "JUICE", 35, 5, 10));

        slots.put("B1", new Slot("B1", "COFFEE", 25, 7, 10));
        slots.put("B2", new Slot("B2", "TEA", 20, 9, 10));
        slots.put("B3", new Slot("B3", "ENERGY_DRINK", 50, 4, 8));
        slots.put("B4", new Slot("B4", "BISCUITS", 10, 15, 20));
        slots.put("B5", new Slot("B5", "CANDY", 5, 20, 25));

        slots.put("C1", new Slot("C1", "SANDWICH", 60, 3, 5));
        slots.put("C2", new Slot("C2", "BURGER", 80, 2, 5));
        slots.put("C3", new Slot("C3", "NOODLES", 45, 6, 10));
        slots.put("C4", new Slot("C4", "PROTEIN_BAR", 55, 5, 8));
        slots.put("C5", new Slot("C5", "FRUIT_PACK", 30, 7, 10));
    }

}
