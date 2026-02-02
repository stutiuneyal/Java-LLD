public class Slot {

    private String slotId;
    private String product;
    private int price;
    private int quantity;
    private int maxCapacity;

    public Slot(String slotId, String product, int price, int quantity, int maxCapacity) {
        this.slotId = slotId;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.maxCapacity = maxCapacity;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        return "Slot [slotId=" + slotId + ", product=" + product + ", price=" + price + ", quantity=" + quantity
                + ", maxCapacity=" + maxCapacity + "]";
    }

}
