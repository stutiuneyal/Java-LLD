package Actors.Catalog;

import java.util.List;

public class Product {

    private String id;
    private String name;
    private String category;
    private double basePrice;
    private int totalQuantity;
    private Boolean isActive = true;
    private int maxPerOrder = 5;
    private int minOrderQty = 2;
    private Boolean isReturnable = true;
    private Constraints constraints;
    private List<Variants> variants;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public boolean isActive() {
        return isActive != null ? isActive : true;
    }

    public void setActive(Boolean isActive) {
        this.isActive = (isActive == null) ? true : isActive;
    }

    public int getMaxPerOrder() {
        return maxPerOrder;
    }

    public void setMaxPerOrder(int maxPerOrder) {
        this.maxPerOrder = maxPerOrder == 0 ? 5 : maxPerOrder;
    }

    public int getMinOrderQty() {
        return minOrderQty == 0 ? 1 : minOrderQty;
    }

    public void setMinOrderQty(int minOrderQty) {
        this.minOrderQty = minOrderQty;
    }

    public boolean isReturnable() {
        return isReturnable != null ? isReturnable : true;
    }

    public void setReturnable(Boolean isReturnable) {
        this.isReturnable = (isReturnable == null) ? true : isReturnable;
    }

    public Constraints getConstraints() {
        if (constraints != null) {
            if (constraints.getMinOrderQuantity() == 0) {
                constraints.setMinOrderQuantity(getMinOrderQty());
            }
            if (constraints.getAvailableQuantity() == 0) {
                constraints.setAvailableQuantity(getTotalQuantity());
            }
        }
        return constraints;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    public List<Variants> getVariants() {
        return variants;
    }

    public void setVariants(List<Variants> variants) {
        // assert validateVariants();
        this.variants = variants;
    }

    private boolean validateVariants() {
        int total = 0;
        for (Variants variants : getVariants()) {
            total += variants.getAvailableQuantity();
        }

        return getTotalQuantity() == total;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", category=" + category + ", basePrice=" + basePrice
                + ", totalQuantity=" + totalQuantity + ", isActive=" + isActive + ", maxPerOrder=" + maxPerOrder
                + ", minOrderQty=" + minOrderQty + ", isReturnable=" + isReturnable + ", constraints=" + constraints
                + ", variants=" + variants + "]";
    }

}
