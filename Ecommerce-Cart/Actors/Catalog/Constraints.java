package Actors.Catalog;

public class Constraints {

    private String type;
    private int minOrderQuantity;
    private boolean requiresSpecialHandling;
    private boolean shippingRequired;
    private String note;
    private String expiryDate; // yyyy-MM-dd
    private int availableQuantity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(int minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public boolean isRequiresSpecialHandling() {
        return requiresSpecialHandling;
    }

    public void setRequiresSpecialHandling(boolean requiresSpecialHandling) {
        this.requiresSpecialHandling = requiresSpecialHandling;
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString() {
        return "Constraints [type=" + type + ", minOrderQuantity=" + minOrderQuantity + ", requiresSpecialHandling="
                + requiresSpecialHandling + ", shippingRequired=" + shippingRequired + ", note=" + note
                + ", expiryDate=" + expiryDate + ", availableQuantity=" + availableQuantity + "]";
    }

}
