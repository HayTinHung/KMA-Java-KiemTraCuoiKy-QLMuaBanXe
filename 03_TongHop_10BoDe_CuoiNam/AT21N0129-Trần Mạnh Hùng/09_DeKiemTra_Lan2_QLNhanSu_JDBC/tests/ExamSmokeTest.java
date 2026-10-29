import de2.Cap;
import de2.NhanSu;
import de2.NhanSuRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Kiểm tra nhanh CRUD, khóa ngoại và công thức bảo hiểm của đề. */
public final class ExamSmokeTest {
    public static void main(String[] args) throws Exception {
        NhanSuRepository repo = NhanSuRepository.fromEnvironment();
        List<Cap> caps = repo.listCap();
        if (caps.isEmpty()) throw new AssertionError("Database phải có ít nhất một cấp mẫu.");
        int capId = caps.get(0).getMaCap();
        int employeeId = 98765;
        try {
            try { repo.xoaNhanSu(employeeId); } catch (Exception ignored) { }
            NhanSu first = new NhanSu(employeeId, "Smoke Test", 1000, capId, caps.get(0).getTen(), caps.get(0).getBaoHiem());
            repo.themNhanSu(first);
            require(repo.listNhanSu().size() >= 1, "insert/list");
            BigDecimal insurance = NhanSuRepository.tienBaoHiem(first);
            BigDecimal expectedInsurance = BigDecimal.valueOf(1100L * caps.get(0).getBaoHiem())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (insurance.compareTo(expectedInsurance) != 0) {
                throw new AssertionError("insurance formula: " + insurance);
            }
            repo.suaNhanSu(new NhanSu(employeeId, "Smoke Updated", 1200, capId, caps.get(0).getTen(), caps.get(0).getBaoHiem()));
            boolean updated = false;
            for (NhanSu row : repo.listNhanSu()) if (row.getMaNhanSu() == employeeId && "Smoke Updated".equals(row.getTenNhanSu())) updated = true;
            require(updated, "update");
        } finally {
            try { repo.xoaNhanSu(employeeId); } catch (Exception ignored) { }
        }
        System.out.println("EXAM SMOKE PASS");
    }

    private static void require(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
    }
}
