import java.util.Objects;

/**
 * BÀI TẬP 2 - LỚP EMPLOYEE (NHÂN VIÊN)
 * Gồm các thông tin:
 * - employeeId (String): Mã nhân viên
 * - firstName (String): Tên và tên đệm
 * - lastName (String): Họ
 * - monthlySalary (double): Lương tháng (không được phép < 0, nếu < 0 thì đặt về 0.0)
 * - hireDate (HireDate): Ngày thuê (cấu trúc ngày, tháng, năm)
 */
public class Employee implements Comparable<Employee> {
    private String employeeId;
    private String firstName;
    private String lastName;
    private double monthlySalary;
    private HireDate hireDate;

    public Employee() {
        this("", "", "", 0.0, new HireDate(1, 1, 2024));
    }

    public Employee(String employeeId, String firstName, String lastName, double monthlySalary, HireDate hireDate) {
        this.employeeId = employeeId == null ? "" : employeeId.trim();
        this.firstName = firstName == null ? "" : firstName.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
        this.monthlySalary = monthlySalary >= 0.0 ? monthlySalary : 0.0;
        this.hireDate = hireDate != null ? hireDate : new HireDate(1, 1, 2024);
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId == null ? "" : employeeId.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName == null ? "" : firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName == null ? "" : lastName.trim();
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(double monthlySalary) {
        this.monthlySalary = monthlySalary >= 0.0 ? monthlySalary : 0.0;
    }

    public HireDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(HireDate hireDate) {
        this.hireDate = hireDate != null ? hireDate : new HireDate(1, 1, 2024);
    }

    public String getFullName() {
        return (lastName + " " + firstName).trim();
    }

    public double getYearlySalary() {
        return monthlySalary * 12.0;
    }

    @Override
    public int compareTo(Employee other) {
        if (other == null) return 1;
        return this.employeeId.compareToIgnoreCase(other.employeeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return employeeId.equalsIgnoreCase(employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-12s | %-20s | %14.2f | %-12s",
                employeeId, lastName, firstName, monthlySalary, hireDate);
    }
}
