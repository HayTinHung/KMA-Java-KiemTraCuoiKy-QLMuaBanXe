import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * BÀI TẬP 2 - LỚP EMPLOYEETEST
 * Quản lý danh sách Employee (ít nhất 10 phần tử, không trùng mã nhân viên),
 * hỗ trợ 5 thuật toán sắp xếp (Bubble, Interchange, Insertion, Selection, Quick Sort)
 * theo Mã nhân viên hoặc Tên nhân viên; 3 thuật toán tìm kiếm (Linear, Binary, Library)
 * và xóa theo Mã nhân viên.
 */
public class EmployeeTest {

    public static final Comparator<Employee> BY_EMPLOYEE_ID =
            (a, b) -> a.getEmployeeId().compareToIgnoreCase(b.getEmployeeId());

    public static final Comparator<Employee> BY_FIRST_NAME =
            (a, b) -> {
                int res = a.getFirstName().compareToIgnoreCase(b.getFirstName());
                if (res != 0) return res;
                return a.getEmployeeId().compareToIgnoreCase(b.getEmployeeId());
            };

    /**
     * Tạo danh sách dữ liệu giả (mocked data) gồm 10 Employee không trùng mã.
     */
    public static List<Employee> createMockEmployees() {
        List<Employee> list = new ArrayList<>();
        list.add(new Employee("EMP009", "Mạnh Hùng", "Trần", 25000000.0, new HireDate(15, 3, 2022)));
        list.add(new Employee("EMP003", "Văn An", "Nguyễn", 18000000.0, new HireDate(10, 6, 2021)));
        list.add(new Employee("EMP007", "Thị Bích", "Lê", 21500000.0, new HireDate(20, 1, 2023)));
        list.add(new Employee("EMP001", "Hoàng Nam", "Phạm", 30000000.0, new HireDate(5, 9, 2020)));
        list.add(new Employee("EMP010", "Minh Tuấn", "Võ", 19500000.0, new HireDate(12, 11, 2022)));
        list.add(new Employee("EMP004", "Ngọc Lan", "Đặng", 22000000.0, new HireDate(1, 4, 2023)));
        list.add(new Employee("EMP008", "Quốc Bảo", "Hoàng", 27500000.0, new HireDate(18, 7, 2021)));
        list.add(new Employee("EMP002", "Thanh Hà", "Bùi", 16500000.0, new HireDate(25, 2, 2024)));
        list.add(new Employee("EMP006", "Đức Thịnh", "Đỗ", 24000000.0, new HireDate(30, 8, 2022)));
        list.add(new Employee("EMP005", "Kim Ngân", "Ngô", 20500000.0, new HireDate(14, 12, 2023)));
        return list;
    }

    public static boolean existsEmployeeId(List<Employee> list, String employeeId) {
        if (employeeId == null) return false;
        for (Employee emp : list) {
            if (emp.getEmployeeId().equalsIgnoreCase(employeeId.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean addEmployee(List<Employee> list, Employee emp) {
        if (emp == null || emp.getEmployeeId().isEmpty()) {
            return false;
        }
        if (existsEmployeeId(list, emp.getEmployeeId())) {
            return false;
        }
        list.add(emp);
        return true;
    }

    // =========================================================================
    // 5 THUẬT TOÁN SẮP XẾP (BUBBLE, INTERCHANGE, INSERTION, SELECTION, QUICK)
    // =========================================================================

    /** 1. Sắp xếp nổi bọt (Bubble Sort) */
    public static void bubbleSort(List<Employee> list, Comparator<Employee> cmp) {
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
    public static void interchangeSort(List<Employee> list, Comparator<Employee> cmp) {
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
    public static void insertionSort(List<Employee> list, Comparator<Employee> cmp) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            Employee key = list.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    /** 4. Sắp xếp chọn lựa (Selection Sort) */
    public static void selectionSort(List<Employee> list, Comparator<Employee> cmp) {
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
    public static void quickSort(List<Employee> list, Comparator<Employee> cmp) {
        if (list == null || list.size() <= 1) return;
        quickSortRecursive(list, 0, list.size() - 1, cmp);
    }

    private static void quickSortRecursive(List<Employee> list, int low, int high, Comparator<Employee> cmp) {
        if (low < high) {
            int pi = partition(list, low, high, cmp);
            quickSortRecursive(list, low, pi - 1, cmp);
            quickSortRecursive(list, pi + 1, high, cmp);
        }
    }

    private static int partition(List<Employee> list, int low, int high, Comparator<Employee> cmp) {
        Employee pivot = list.get(high);
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
    // 3 PHƯƠNG THỨC TÌM KIẾM THEO MÃ NHÂN VIÊN
    // =========================================================================

    /** 1. Tìm kiếm tuyến tính (Linear Search) */
    public static Employee linearSearch(List<Employee> list, String employeeId) {
        if (employeeId == null) return null;
        String target = employeeId.trim();
        for (Employee emp : list) {
            if (emp.getEmployeeId().equalsIgnoreCase(target)) {
                return emp;
            }
        }
        return null;
    }

    /** 2. Tìm kiếm nhị phân tự cài đặt (Binary Search) */
    public static Employee binarySearch(List<Employee> list, String employeeId) {
        if (employeeId == null || list.isEmpty()) return null;
        List<Employee> sorted = new ArrayList<>(list);
        quickSort(sorted, BY_EMPLOYEE_ID);
        String target = employeeId.trim();
        int left = 0;
        int right = sorted.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = sorted.get(mid).getEmployeeId().compareToIgnoreCase(target);
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
    public static Employee librarySearch(List<Employee> list, String employeeId) {
        if (employeeId == null || list.isEmpty()) return null;
        List<Employee> sorted = new ArrayList<>(list);
        sorted.sort(BY_EMPLOYEE_ID);
        Employee key = new Employee(employeeId.trim(), "", "", 0.0, new HireDate(1, 1, 2024));
        int index = Collections.binarySearch(sorted, key, BY_EMPLOYEE_ID);
        return index >= 0 ? sorted.get(index) : null;
    }

    // =========================================================================
    // XÓA NHÂN VIÊN THEO MÃ NHÂN VIÊN
    // =========================================================================

    public static boolean deleteByEmployeeId(List<Employee> list, String employeeId) {
        if (employeeId == null) return false;
        String target = employeeId.trim();
        return list.removeIf(emp -> emp.getEmployeeId().equalsIgnoreCase(target));
    }

    // =========================================================================
    // HIỂN THỊ DANH SÁCH & MENU ĐIỀU KHIỂN
    // =========================================================================

    public static void printEmployeeList(List<Employee> list) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-12s | %-20s | %14s | %-12s%n",
                "Mã NV", "Họ", "Tên & Tên đệm", "Lương tháng", "Ngày thuê");
        System.out.println("--------------------------------------------------------------------------------");
        if (list.isEmpty()) {
            System.out.println("  [Danh sách nhân viên đang rỗng]");
        } else {
            for (Employee emp : list) {
                System.out.println(emp);
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("Tổng số nhân viên: %d%n", list.size());
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");
        List<Employee> employees = createMockEmployees();

        while (true) {
            System.out.println("\n================ MENU QUẢN LÝ NHÂN VIÊN (EMPLOYEE TEST) ================");
            System.out.println("1. Khởi tạo lại 10 Employee mẫu (Mocked Data) hoặc Nhập thêm Employee");
            System.out.println("2. Xuất toàn bộ danh sách Employee");
            System.out.println("3. Sắp xếp danh sách Employee (Bubble/Interchange/Insertion/Selection/Quick)");
            System.out.println("4. Tìm kiếm Employee theo Mã nhân viên (Linear / Binary / Library)");
            System.out.println("5. Xóa một Employee theo Mã nhân viên");
            System.out.println("0. Thoát chương trình");
            System.out.print("Chọn chức năng (0-5): ");

            String line = sc.nextLine().trim();
            if (line.equals("0")) break;

            switch (line) {
                case "1":
                    System.out.print("Chọn (1: Nạp lại 10 mẫu chuẩn | 2: Nhập thêm thủ công): ");
                    String sub = sc.nextLine().trim();
                    if (sub.equals("1")) {
                        employees = createMockEmployees();
                        System.out.println("Đã nạp 10 Employee mẫu không trùng mã!");
                    } else {
                        System.out.print("Nhập Mã nhân viên: ");
                        String id = sc.nextLine().trim();
                        if (existsEmployeeId(employees, id)) {
                            System.out.println("Lỗi: Mã nhân viên đã tồn tại!");
                            break;
                        }
                        System.out.print("Nhập Họ: ");
                        String last = sc.nextLine().trim();
                        System.out.print("Nhập Tên & Tên đệm: ");
                        String first = sc.nextLine().trim();
                        System.out.print("Nhập Lương tháng: ");
                        double sal = Double.parseDouble(sc.nextLine().trim());
                        System.out.print("Nhập Ngày, Tháng, Năm thuê (cách nhau dấu cách): ");
                        String[] dParts = sc.nextLine().trim().split("\\s+");
                        HireDate hd = new HireDate(
                                Integer.parseInt(dParts[0]),
                                Integer.parseInt(dParts[1]),
                                Integer.parseInt(dParts[2]));
                        addEmployee(employees, new Employee(id, first, last, sal, hd));
                        System.out.println("Đã thêm nhân viên thành công!");
                    }
                    break;

                case "2":
                    printEmployeeList(employees);
                    break;

                case "3":
                    System.out.println("Chọn tiêu chí: 1. Theo Mã nhân viên | 2. Theo Tên nhân viên");
                    String crit = sc.nextLine().trim();
                    Comparator<Employee> cmp = crit.equals("2") ? BY_FIRST_NAME : BY_EMPLOYEE_ID;
                    System.out.println("Chọn thuật toán: 1.Bubble | 2.Interchange | 3.Insertion | 4.Selection | 5.QuickSort");
                    String alg = sc.nextLine().trim();
                    switch (alg) {
                        case "1": bubbleSort(employees, cmp); break;
                        case "2": interchangeSort(employees, cmp); break;
                        case "3": insertionSort(employees, cmp); break;
                        case "4": selectionSort(employees, cmp); break;
                        default: quickSort(employees, cmp); break;
                    }
                    System.out.println("Đã sắp xếp danh sách nhân viên!");
                    printEmployeeList(employees);
                    break;

                case "4":
                    System.out.print("Nhập Mã nhân viên cần tìm: ");
                    String code = sc.nextLine().trim();
                    System.out.print("Chọn kiểu tìm (1.Linear | 2.Binary | 3.Library): ");
                    String sType = sc.nextLine().trim();
                    Employee found;
                    if (sType.equals("2")) found = binarySearch(employees, code);
                    else if (sType.equals("3")) found = librarySearch(employees, code);
                    else found = linearSearch(employees, code);

                    if (found != null) {
                        System.out.println("Tìm thấy nhân viên:");
                        System.out.println(found);
                    } else {
                        System.out.println("Không tìm thấy nhân viên có mã: " + code);
                    }
                    break;

                case "5":
                    System.out.print("Nhập Mã nhân viên cần xóa: ");
                    String delId = sc.nextLine().trim();
                    if (deleteByEmployeeId(employees, delId)) {
                        System.out.println("Đã xóa nhân viên có mã: " + delId);
                    } else {
                        System.out.println("Không tìm thấy nhân viên để xóa!");
                    }
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }
}
