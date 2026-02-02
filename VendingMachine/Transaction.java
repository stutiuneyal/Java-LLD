import java.util.UUID;

public class Transaction {

    private String transactionId;
    private String userName;
    private long timestamp;
    private String product;
    private int qty;
    private int amountInserted;
    private int amountReceived;

    public Transaction(String userName, String product, int qty,
            int amountInserted, int amountReceived) {
        this.transactionId = UUID.randomUUID().toString();
        this.userName = userName;
        this.timestamp = System.currentTimeMillis();
        this.product = product;
        this.qty = qty;
        this.amountInserted = amountInserted;
        this.amountReceived = amountReceived;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getAmountInserted() {
        return amountInserted;
    }

    public void setAmountInserted(int amountInserted) {
        this.amountInserted = amountInserted;
    }

    public int getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(int amountReceived) {
        this.amountReceived = amountReceived;
    }

}
