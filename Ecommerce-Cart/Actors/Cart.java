package Actors;

import java.util.ArrayList;
import java.util.List;

import utils.PlatformUtilEnum;

public class Cart {

    private List<CartProduct> products;
    private double cartTotal;
    private double cartAmountAfterDeductions;
    private List<PlatformUtilEnum> platformCosts; // Coupon, Tax, Delivery, Platform Fees
    private String coupon;

    public Cart(){
        this.products = new ArrayList<>();
        this.platformCosts = new ArrayList<>();
        this.coupon = "";
        this.cartTotal = 0d;
        this.cartAmountAfterDeductions = 0d;
    }

    public List<CartProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CartProduct> products) {
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

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

}
