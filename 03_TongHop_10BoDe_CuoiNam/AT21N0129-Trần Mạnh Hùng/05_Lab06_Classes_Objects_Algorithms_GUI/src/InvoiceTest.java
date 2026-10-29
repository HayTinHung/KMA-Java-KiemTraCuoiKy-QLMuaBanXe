import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * BÀI TẬP 1 - LỚP INVOICETEST
 * Quản lý danh sách Invoice (ít nhất 10 phần tử, không trùng mã mặt hàng),
 * hỗ trợ 5 thuật toán sắp xếp (Bubble, Interchange, Insertion, Selection, Quick Sort)
 * theo Mã mặt hàng hoặc Số lượng mua; 3 thuật toán tìm kiếm (Linear, Binary, Library)
 * và xóa theo Mã mặt hàng.
 */
public class InvoiceTest {

    public static final Comparator<Invoice> BY_PART_NUMBER =
            (a, b) -> a.getPartNumber().compareToIgnoreCase(b.getPartNumber());

    public static final Comparator<Invoice> BY_QUANTITY =
            Comparator.comparingInt(Invoice::getQuantity)
                    .thenComparing((a, b) -> a.getPartNumber().compareToIgnoreCase(b.getPartNumber()));

    /**
     * Khởi tạo bộ dữ liệu giả (mocked data) chuẩn gồm 10 hóa đơn phần cứng không trùng mã.
     */
    public static List<Invoice> createMockInvoices() {
        List<Invoice> list = new ArrayList<>();
        list.add(new Invoice("PART008", "SSD Samsung 990 Pro 1TB NVMe", 15, 2850000.0));
        list.add(new Invoice("PART002", "RAM Corsair Vengeance 32GB DDR5", 8, 3200000.0));
        list.add(new Invoice("PART010", "CPU Intel Core i7-14700K", 5, 9800000.0));
        list.add(new Invoice("PART004", "Mainboard ASUS ROG Strix Z790", 4, 8500000.0));
        list.add(new Invoice("PART001", "VGA NVIDIA RTX 4070 Super 12GB", 3, 16500000.0));
        list.add(new Invoice("PART009", "Nguồn Corsair RM850e 850W 80+ Gold", 12, 2950000.0));
        list.add(new Invoice("PART005", "Tản nhiệt nước NZXT Kraken 360", 6, 4500000.0));
        list.add(new Invoice("PART003", "Màn hình LG UltraGear 27 inch 2K", 10, 6900000.0));
        list.add(new Invoice("PART007", "Bàn phím cơ Keychron Q1 Pro", 20, 4100000.0));
        list.add(new Invoice("PART006", "Chuột Logitech G Pro X Superlight 2", 25, 3390000.0));
        return list;
    }

    /**
     * Kiểm tra mã mặt hàng đã tồn tại trong danh sách hay chưa.
     */
    public static boolean existsPartNumber(List<Invoice> list, String partNumber) {
        if (partNumber == null) return false;
        for (Invoice inv : list) {
            if (inv.getPartNumber().equalsIgnoreCase(partNumber.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Thêm một Invoice mới vào danh sách (từ chối nếu trùng mã mặt hàng hoặc mã rỗng).
     */
    public static boolean addInvoice(List<Invoice> list, Invoice invoice) {
        if (invoice == null || invoice.getPartNumber().isEmpty()) {
            return false;
        }
        if (existsPartNumber(list, invoice.getPartNumber())) {
            return false;
        }
        list.add(invoice);
        return true;
    }

    // =========================================================================
    // 5 THUẬT TOÁN SẮP XẾP (BUBBLE, INTERCHANGE, INSERTION, SELECTION, QUICK)
    // =========================================================================

    /** 1. Sắp xếp nổi bọt (Bubble Sort) */
    public static void bubbleSort(List<Invoice> list, Comparator<Invoice> cmp) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (cmp.compare(list.get(j), list.get(j + 1)) > 0) {
                    Collections.swap(list, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    /** 2. Sắp xếp đổi chỗ trực tiếp (Interchange Sort) */
    public static void interchangeSort(List<Invoice> list, Comparator<Invoice> cmp) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (cmp.compare(list.get(i), list.get(j)) > 0) {
                    Collections.swap(list, i, j);
                }
            }
        }
    }

    /** 3. Sắp xếp chèn (Insertion Sort) */
    public static void insertionSort(List<Invoice> list, Comparator<Invoice> cmp) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            Invoice key = list.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    /** 4. Sắp xếp chọn lựa (Selection Sort) */
    public static void selectionSort(List<Invoice> list, Comparator<Invoice> cmp) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (cmp.compare(list.get(j), list.get(minIdx)) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                Collections.swap(list, i, minIdx);
            }
        }
    }

    /** 5. Sắp xếp phân hoạch (Quick Sort) */
    public static void quickSort(List<Invoice> list, Comparator<Invoice> cmp) {
        if (list == null || list.size() <= 1) return;
        quickSortRecursive(list, 0, list.size() - 1, cmp);
    }

    private static void quickSortRecursive(List<Invoice> list, int low, int high, Comparator<Invoice> cmp) {
        if (low < high) {
            int pi = partition(list, low, high, cmp);
            quickSortRecursive(list, low, pi - 1, cmp);
            quickSortRecursive(list, pi + 1, high, cmp);
        }
    }

    private static int partition(List<Invoice> list, int low, int high, Comparator<Invoice> cmp) {
        Invoice pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (cmp.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    // =========================================================================
    // 3 PHƯƠNG THỨC TÌM KIẾM THEO MÃ MẶT HÀNG
    // =========================================================================

    /** 1. Tìm kiếm tuyến tính (Linear Search) */
    public static Invoice linearSearch(List<Invoice> list, String partNumber) {
        if (partNumber == null) return null;
        String target = partNumber.trim();
        for (Invoice inv : list) {
            if (inv.getPartNumber().equalsIgnoreCase(target)) {
                return inv;
            }
        }
        return null;
    }

    /** 2. Tìm kiếm nhị phân tự cài đặt (Binary Search) */
    public static Invoice binarySearch(List<Invoice> list, String partNumber) {
        if (partNumber == null || list.isEmpty()) return null;
        List<Invoice> sorted = new ArrayList<>(list);
        quickSort(sorted, BY_PART_NUMBER);
        String target = partNumber.trim();
        int left = 0;
        int right = sorted.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = sorted.get(mid).getPartNumber().compareToIgnoreCase(target);
            if (cmp == 0) {
                return sorted.get(mid);
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    /** 3. Tìm kiếm bằng hàm thư viện sẵn có (Collections.binarySearch) */
    public static Invoice librarySearch(List<Invoice> list, String partNumber) {
        if (partNumber == null || list.isEmpty()) return null;
        List<Invoice> sorted = new ArrayList<>(list);
        sorted.sort(BY_PART_NUMBER);
        Invoice key = new Invoice(partNumber.trim(), "", 0, 0.0);
        int index = Collections.binarySearch(sorted, key, BY_PART_NUMBER);
        return index >= 0 ? sorted.get(index) : null;
    }

    // =========================================================================
    // XÓA HÓA ĐƠN THEO MÃ MẶT HÀNG
    // =========================================================================

    public static boolean deleteByPartNumber(List<Invoice> list, String partNumber) {
        if (partNumber == null) return false;
        String target = partNumber.trim();
        return list.removeIf(inv -> inv.getPartNumber().equalsIgnoreCase(target));
    }

    // =========================================================================
    // HIỂN THỊ DANH SÁCH & MENU ĐIỀU KHIỂN
    // =========================================================================

    public static void printInvoiceList(List<Invoice> list) {
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-30s | %8s | %14s | %16s%n",
                "Mã MH", "Mô tả mặt hàng", "Số lượng", "Đơn giá (VNĐ)", "Thành tiền (VNĐ)");
        System.out.println("--------------------------------------------------------------------------------------------");
        if (list.isEmpty()) {
            System.out.println("  [Danh sách hóa đơn đang rỗng]");
        } else {
            double total = 0;
            for (Invoice inv : list) {
                System.out.println(inv);
                total += inv.getInvoiceAmount();
            }
            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.printf("Tổng số mặt hàng: %d | Tổng cộng tiền hóa đơn: %,.2f VNĐ%n", list.size(), total);
        }
        System.out.println("--------------------------------------------------------------------------------------------");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");
        List<Invoice> invoices = createMockInvoices();

        while (true) {
            System.out.println("\n================ MENU QUẢN LÝ HÓA ĐƠN (INVOICE TEST) ================");
            System.out.println("1. Khởi tạo lại 10 Invoice mẫu (Mocked Data) hoặc Nhập thêm Invoice");
            System.out.println("2. Xuất toàn bộ danh sách Invoice hoặc 1 Invoice theo vị trí");
            System.out.println("3. Sắp xếp danh sách Invoice (Bubble/Interchange/Insertion/Selection/Quick)");
            System.out.println("4. Tìm kiếm Invoice theo Mã mặt hàng (Linear / Binary / Library)");
            System.out.println("5. Xóa một Invoice theo Mã mặt hàng");
            System.out.println("0. Thoát chương trình");
            System.out.print("Chọn chức năng (0-5): ");

            String line = sc.nextLine().trim();
            if (line.equals("0")) break;

            switch (line) {
                case "1":
                    System.out.print("Chọn (1: Nạp lại 10 mẫu chuẩn | 2: Nhập thêm thủ công): ");
                    String sub1 = sc.nextLine().trim();
                    if (sub1.equals("1")) {
                        invoices = createMockInvoices();
                        System.out.println("Đã nạp thành công 10 Invoice mẫu không trùng mã!");
                    } else {
                        while (true) {
                            System.out.print("Nhập Mã mặt hàng: ");
                            String code = sc.nextLine().trim();
                            if (code.isEmpty()) {
                                System.out.println("Mã mặt hàng không được để trống!");
                                continue;
                            }
                            if (existsPartNumber(invoices, code)) {
                                System.out.println("Lỗi: Mã mặt hàng '" + code + "' đã tồn tại!");
                                continue;
                            }
                            System.out.print("Nhập Mô tả mặt hàng: ");
                            String desc = sc.nextLine().trim();
                            System.out.print("Nhập Số lượng mua: ");
                            int qty = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Nhập Đơn giá: ");
                            double price = Double.parseDouble(sc.nextLine().trim());

                            addInvoice(invoices, new Invoice(code, desc, qty, price));
                            System.out.println("Đã thêm thành công! Tổng số hiện tại: " + invoices.size());
                            if (invoices.size() >= 10) {
                                System.out.print("Tiếp tục nhập thêm? (y/n): ");
                                if (!sc.nextLine().trim().equalsIgnoreCase("y")) break;
                            } else {
                                System.out.println("Cần tối thiểu 10 Invoice (hiện có " + invoices.size() + ").");
                            }
                        }
                    }
                    break;

                case "2":
                    printInvoiceList(invoices);
                    break;

                case "3":
                    System.out.println("Chọn tiêu chí: 1. Theo Mã mặt hàng | 2. Theo Số lượng mua");
                    String crit = sc.nextLine().trim();
                    Comparator<Invoice> cmp = crit.equals("2") ? BY_QUANTITY : BY_PART_NUMBER;
                    System.out.println("Chọn thuật toán: 1.Bubble | 2.Interchange | 3.Insertion | 4.Selection | 5.QuickSort");
                    String alg = sc.nextLine().trim();
                    switch (alg) {
                        case "1": bubbleSort(invoices, cmp); break;
                        case "2": interchangeSort(invoices, cmp); break;
                        case "3": insertionSort(invoices, cmp); break;
                        case "4": selectionSort(invoices, cmp); break;
                        default: quickSort(invoices, cmp); break;
                    }
                    System.out.println("Đã sắp xếp danh sách thành công!");
                    printInvoiceList(invoices);
                    break;

                case "4":
                    System.out.print("Nhập Mã mặt hàng cần tìm: ");
                    String searchCode = sc.nextLine().trim();
                    System.out.print("Chọn kiểu tìm (1.Linear | 2.Binary | 3.Library): ");
                    String sType = sc.nextLine().trim();
                    Invoice found;
                    if (sType.equals("2")) found = binarySearch(invoices, searchCode);
                    else if (sType.equals("3")) found = librarySearch(invoices, searchCode);
                    else found = linearSearch(invoices, searchCode);

                    if (found != null) {
                        System.out.println("Tìm thấy hóa đơn:");
                        System.out.println(found);
                    } else {
                        System.out.println("Không tìm thấy hóa đơn có mã: " + searchCode);
                    }
                    break;

                case "5":
                    System.out.print("Nhập Mã mặt hàng cần xóa: ");
                    String delCode = sc.nextLine().trim();
                    if (deleteByPartNumber(invoices, delCode)) {
                        System.out.println("Đã xóa thành công hóa đơn có mã: " + delCode);
                    } else {
                        System.out.println("Không tìm thấy mã mặt hàng để xóa!");
                    }
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }
}
