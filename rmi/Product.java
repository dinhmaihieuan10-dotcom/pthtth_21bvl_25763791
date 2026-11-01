package rmi;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;           // Mã sản phẩm (ví dụ: SP01)
    private String name;         // Tên sản phẩm
    private double startPrice;   // Giá khởi điểm
    private double highestBid;   // Giá thầu cao nhất hiện tại
    private String highestBidder;// Tên người đặt giá cao nhất

    public Product(String id, String name, double startPrice) {
        this.id = id;
        this.name = name;
        this.startPrice = startPrice;
        this.highestBid = startPrice;
        this.highestBidder = "Chưa có";
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getStartPrice() { return startPrice; }
    public double getHighestBid() { return highestBid; }
    public void setHighestBid(double highestBid) { this.highestBid = highestBid; }
    public String getHighestBidder() { return highestBidder; }
    public void setHighestBidder(String highestBidder) { this.highestBidder = highestBidder; }

    @Override
    public String toString() {
        return String.format("Mã SP: %-6s | Tên SP: %-20s | Giá khởi điểm: %,.0f VNĐ | Giá cao nhất: %,.0f VNĐ (Bởi: %s)",
                id, name, startPrice, highestBid, highestBidder);
    }
}