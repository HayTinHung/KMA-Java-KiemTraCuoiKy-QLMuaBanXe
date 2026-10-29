import java.util.Objects;

/**
 * BÀI TẬP 1 - LỚP INVOICE (HÓA ĐƠN CỬA HÀNG PHẦN CỨNG MÁY TÍNH)
 * Lưu trữ thông tin:
 * - partNumber (String): Mã mặt hàng
 * - partDescription (String): Mô tả mặt hàng
 * - quantity (int): Số lượng mua (nếu không dương thì đặt thành 0)
 * - pricePerItem (double): Giá mỗi mặt hàng (nếu không dương thì đặt thành 0.0)
 */
public class Invoice implements Comparable<Invoice> {
    private String partNumber;
    private String partDescription;
    private int quantity;
    private double pricePerItem;

    public Invoice() {
        this("", "", 0, 0.0);
    }

    public Invoice(String partNumber, String partDescription, int quantity, double pricePerItem) {
        this.partNumber = partNumber == null ? "" : partNumber.trim();
        this.partDescription = partDescription == null ? "" : partDescription.trim();
        this.quantity = quantity > 0 ? quantity : 0;
        this.pricePerItem = pricePerItem > 0.0 ? pricePerItem : 0.0;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber == null ? "" : partNumber.trim();
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription == null ? "" : partDescription.trim();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity > 0 ? quantity : 0;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem > 0.0 ? pricePerItem : 0.0;
    }

    /**
     * Tính toán số tiền trên hóa đơn = số lượng * giá mỗi mặt hàng.
     */
    public double getInvoiceAmount() {
        return (double) quantity * pricePerItem;
    }

    @Override
    public int compareTo(Invoice other) {
        if (other == null) return 1;
        return this.partNumber.compareToIgnoreCase(other.partNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice)) return false;
        Invoice invoice = (Invoice) o;
        return partNumber.equalsIgnoreCase(invoice.partNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partNumber.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-30s | %8d | %14.2f | %16.2f",
                partNumber, partDescription, quantity, pricePerItem, getInvoiceAmount());
    }
}
