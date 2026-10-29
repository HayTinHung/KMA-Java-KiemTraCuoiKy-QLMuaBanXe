import java.math.BigDecimal;
import java.util.List;
import ontap2.HangHoa;
import ontap2.HangHoaRepository;
import ontap2.LoaiHang;

/** Kiểm tra tổng và trung bình khi hai hàng thuộc hai loại có tỷ lệ khác nhau. */
public final class RepositoryMixedRatesTest {
    private static final int FIRST_CODE = 98991;
    private static final int SECOND_CODE = 98992;

    public static void main(String[] args) throws Exception {
        HangHoaRepository repository = HangHoaRepository.fromEnvironment();
        List<LoaiHang> types = repository.listLoaiHang();
        if (types.size() < 2 || !repository.listHangHoa().isEmpty()) {
            throw new AssertionError("Cần CSDL mẫu có ít nhất hai loại và chưa có hàng hóa.");
        }

        LoaiHang first = types.get(0);
        LoaiHang second = types.get(1);
        if (first.getTyLe() == second.getTyLe()) {
            throw new AssertionError("Hai loại mẫu cần có tỷ lệ khác nhau.");
        }

        try {
            repository.themHangHoa(new HangHoa(FIRST_CODE, "Kiểm thử tỷ lệ thứ nhất",
                    first.getMaLoai(), "Mới"));
            repository.themHangHoa(new HangHoa(SECOND_CODE, "Kiểm thử tỷ lệ thứ hai",
                    second.getMaLoai(), "Mới"));

            HangHoaRepository.ThongKe result = repository.getThongKe();
            BigDecimal expectedTotal = BigDecimal.valueOf(2000L + 10L * first.getTyLe()
                    + 10L * second.getTyLe()).setScale(2);
            BigDecimal expectedAverage = expectedTotal.divide(BigDecimal.valueOf(2L));
            if (result.getSoHangHoa() != 2
                    || result.getTongGiaTri().compareTo(expectedTotal) != 0
                    || result.getTrungBinh().compareTo(expectedAverage) != 0) {
                throw new AssertionError("Thống kê sai: total=" + result.getTongGiaTri()
                        + ", average=" + result.getTrungBinh());
            }
            System.out.println("PASS mixed rates: total=" + result.getTongGiaTri()
                    + ", average=" + result.getTrungBinh());
        } finally {
            try { repository.xoaHangHoa(FIRST_CODE); } catch (Exception ignored) { }
            try { repository.xoaHangHoa(SECOND_CODE); } catch (Exception ignored) { }
        }
    }
}
