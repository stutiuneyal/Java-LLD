package utils.coupons;

public enum CouponEnum {

    WELCOME(CouponType.FLAT, 80, null),
    FESTIVE(CouponType.PERCENTAGE, 10, 80d),
    VIP(CouponType.PERCENTAGE, 20, 150d),
    FIRSTBUY(CouponType.FLAT, 100, null),
    MEGA(CouponType.PERCENTAGE, 30, 300d),

    NEWUSER(CouponType.FLAT, 50, null),
    SUPERDEAL(CouponType.PERCENTAGE, 15, 120d),
    WEEKEND(CouponType.PERCENTAGE, 25, 200d),
    BULKBUY(CouponType.FLAT, 200, null),
    FLASH(CouponType.PERCENTAGE, 40, 250d),

    LOYALTY(CouponType.PERCENTAGE, 12, 100d),
    BIGSAVE(CouponType.FLAT, 150, null),
    SEASONAL(CouponType.PERCENTAGE, 18, 180d),
    HOLIDAY(CouponType.PERCENTAGE, 22, 220d),
    LIMITED(CouponType.FLAT, 300, null),

    PREMIUM(CouponType.PERCENTAGE, 35, 400d),
    APPONLY(CouponType.FLAT, 75, null),
    BANKOFFER(CouponType.PERCENTAGE, 8, 60d),
    CLEARANCE(CouponType.PERCENTAGE, 50, 500d),
    EXCLUSIVE(CouponType.FLAT, 250, null);

    private final CouponType type;
    private final double value;
    private final Double maxCap;

    CouponEnum(CouponType type, double value, Double maxCap) {
        this.type = type;
        this.value = value;
        this.maxCap = maxCap;
    }

    public double applyDiscount(double cartAmount) {

        switch (type) {
            case FLAT:
                double discountedAmount = cartAmount-this.value;
                if(discountedAmount<=0){
                    return cartAmount;
                }
                return discountedAmount;
            case PERCENTAGE:
                double discount = (this.value*cartAmount)/100d;
                discountedAmount = cartAmount - discount;
                if(discount>maxCap){
                    return cartAmount-maxCap;
                }
                return discountedAmount;
        }

        return 0d;

    }
}
