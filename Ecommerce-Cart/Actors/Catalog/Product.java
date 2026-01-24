package Actors.Catalog;

import java.util.List;

public class Product {

    private String id;
    private String name;
    private String category;
    private double basePrice;
    private int totalQuantity;
    private boolean isActive;
    private int maxPerOrder;
    private int minOrderQty;
    private boolean isReturnable;
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
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getMaxPerOrder() {
        return maxPerOrder;
    }

    public void setMaxPerOrder(int maxPerOrder) {
        this.maxPerOrder = maxPerOrder;
    }

    public int getMinOrderQty() {
        return minOrderQty;
    }

    public void setMinOrderQty(int minOrderQty) {
        this.minOrderQty = minOrderQty;
    }

    public boolean isReturnable() {
        return isReturnable;
    }

    public void setReturnable(boolean isReturnable) {
        this.isReturnable = isReturnable;
    }

    public Constraints getConstraints() {
        return constraints;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    public List<Variants> getVariants() {
        return variants;
    }

    public void setVariants(List<Variants> variants) {
        this.variants = variants;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", category=" + category + ", basePrice=" + basePrice
                + ", totalQuantity=" + totalQuantity + ", isActive=" + isActive + ", maxPerOrder=" + maxPerOrder
                + ", minOrderQty=" + minOrderQty + ", isReturnable=" + isReturnable + ", constraints=" + constraints
                + ", variants=" + variants + "]";
    }

}
