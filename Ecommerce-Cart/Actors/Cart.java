package Actors;

import java.util.List;


import Actors.Catalog.Product;
import utils.PlatformUtilEnum;

public class Cart {

    private List<Product> products;
    private double cartTotal;
    private double cartAmountAfterDeductions;
    private List<PlatformUtilEnum> platformCosts; // Coupon, Tax, Delivery, Platform Fees

    // TODO: add constructor

    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    public double getCartTotal() {
        return cartTotal;
    }
    public void setCartTotal(double cartTotal) {
        this.cartTotal = cartTotal;
    }
    public double getCartAmountAfterDeductions() {
        return cartAmountAfterDeductions;
    }
    public void setCartAmountAfterDeductions(double cartAmountAfterDeductions) {
        this.cartAmountAfterDeductions = cartAmountAfterDeductions;
    }
    public List<PlatformUtilEnum> getPlatformCosts() {
        return platformCosts;
    }
    public void setPlatformCosts(List<PlatformUtilEnum> platformCosts) {
        this.platformCosts = platformCosts;
    }

    

}
