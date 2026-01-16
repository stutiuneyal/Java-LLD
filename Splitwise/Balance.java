public class Balance {

    private User paidBy;
    private Double totalBillAmount; // total Balance
    private User paidFor;
    private Double userBalance; // per User Balance

    public Balance(User paidBy, Double totalBillAmount, User paidFor, Double userBalance) {
        this.paidBy = paidBy;
        this.totalBillAmount = totalBillAmount;
        this.paidFor = paidFor;
        this.userBalance = userBalance;
    }


    public User getPaidBy() {
        return paidBy;
    }


    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }


    public Double getTotalBillAmount() {
        return totalBillAmount;
    }


    public void setTotalBillAmount(Double totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }


    public User getPaidFor() {
        return paidFor;
    }


    public void setPaidFor(User paidFor) {
        this.paidFor = paidFor;
    }


    public Double getUserBalance() {
        return userBalance;
    }


    public void setUserBalance(Double userBalance) {
        this.userBalance = userBalance;
    }


    @Override
    public String toString() {
        return "Balance [paidBy=" + paidBy.getName() + ", totalBillAmount=" + totalBillAmount + ", paidFor=" + paidFor.getName()
                + ", userBalance=" + userBalance + "]";
    }

    

}
