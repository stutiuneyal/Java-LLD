package Actors.Catalog;

public class Variants {

    private String code;
    private int availableQuantity;
    private boolean isActive;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Variants [code=" + code + ", availableQuantity=" + availableQuantity + ", isActive=" + isActive + "]";
    }

}
