import java.util.List;

public class AdminService {

    public void refillSlots(List<String> slotIds) {

    }

    public void updateProductOrPrice(String slotId, String product, int price) {

    }

    public void viewInventory(List<Slot> slots) {
        System.out.println("----- Inventory ----- ");
        for(Slot slot : slots){
            System.out.println(slot);
        }
    }

}
