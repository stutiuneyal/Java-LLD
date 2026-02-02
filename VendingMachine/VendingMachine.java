import java.util.HashMap;
import java.util.Map;

public class VendingMachine {

    private Map<String, Slot> slots;
    private Map<String, Transaction> transactionHistory;
    private int totalAmount;

    public VendingMachine(Map<String, Slot> slots, int totalAmount) {
        this.slots = slots;
        this.transactionHistory = new HashMap<>();
        this.totalAmount = totalAmount;
    }

    public void purchase(Person user, String slotId, int qty, int amount) {

        Slot slot = this.slots.get(slotId);
        if (slot == null) {
            System.out.println("Invalid Slot");
            return;
        }

        if (slot.getQuantity() == 0 || slot.getQuantity() < qty) {
            System.out.println("Insufficient Quantity for: " + slot.getProduct());
            return;
        }

        int totalPrice = slot.getPrice() * qty;
        if (totalPrice > amount) {
            System.out.println("Please enter sufficient amount: " + totalPrice);
            return;
        }

        int balance = amount - totalPrice;
        if (balance > totalAmount) {
            System.out.println("Vending machine not having required change, refund initiated: " + amount);
            return;
        }

        // Update the slot
        slot.setQuantity(slot.getQuantity() - qty);
        this.totalAmount = this.totalAmount + amount - balance;

        // Create a transaction
        createTransaction(user.getName(), slot.getProduct(), qty, amount, balance);

        System.out.println("Vending " + qty + " " + slot.getProduct() + ". Balance: " + balance+" . Vending Balance: "+this.totalAmount);

    }

    private void createTransaction(String userName, String product, int qty, int amountInserted, int amountReceived) {

        Transaction transaction = new Transaction(userName, product, qty, amountInserted, amountReceived);

        this.transactionHistory.put(transaction.getTransactionId(), transaction);

    }

    public Map<String, Slot> getSlots() {
        return slots;
    }

}
