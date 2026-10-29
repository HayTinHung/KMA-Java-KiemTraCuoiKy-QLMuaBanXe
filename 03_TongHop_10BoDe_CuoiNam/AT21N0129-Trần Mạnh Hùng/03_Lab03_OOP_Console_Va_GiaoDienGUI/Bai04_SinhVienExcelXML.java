import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Lớp đại diện Sinh Viên Excel (MSSV, Tên SV, Điểm Rèn Luyện).
 */
class SinhVienExcel {
    private String mssv;
    private String tenSV;
    private double diemRL;

    public SinhVienExcel() {}

    public SinhVienExcel(String mssv, String tenSV, double diemRL) {
        this.mssv = mssv;
        this.tenSV = tenSV;
        this.diemRL = diemRL;
    }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }
    public String getTenSV() { return tenSV; }
    public void setTenSV(String tenSV) { this.tenSV = tenSV; }
    public double getDiemRL() { return diemRL; }
    public void setDiemRL(double diemRL) { this.diemRL = diemRL; }

    public String toXMLString() {
        return "  <SinhVien>\n" +
               "    <MSSV>" + escapeXml(mssv) + "</MSSV>\n" +
               "    <TenSV>" + escapeXml(tenSV) + "</TenSV>\n" +
               "    <DiemRL>" + diemRL + "</DiemRL>\n" +
               "  </SinhVien>";
    }

    public static String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }
}

/**
 * Giao diện Quản Lý Sinh Viên Excel (.xlsx) & Chuyển Đổi Sang XML (Buổi 1 - Bài 4 - Lab 03).
 * Sử dụng 100% thư viện chuẩn JDK (OpenXML .xlsx ZIP + DOM Parser) để chạy độc lập trên mọi máy.
 */
@SuppressWarnings("serial")
public class Bai04_SinhVienExcelXML extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JTextField txtMSSV, txtTenSV, txtDiemRL;
    private JButton btnThem, btnMoExcel, btnTaoExcelMau, btnChuyenXML, btnLuuXML, btnThoat;
    private JTable tableSinhVien;
    private DefaultTableModel tableModel;
    private JTextArea txtPreviewXML;

    private List<SinhVienExcel> danhSachSV;

    public Bai04_SinhVienExcelXML() {
        setTitle("Bài 4: Đọc Sinh Viên Từ Tập Tin Excel & Chuyển Sang Tập Tin XML");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        danhSachSV = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("NHẬP DỮ LIỆU TỪ EXCEL (.XLSX) VÀ CHUYỂN ĐỔI SANG XML", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 17));
        lblTitle.setForeground(new Color(153, 0, 153));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nhập Thông Tin Thủ Công"));

        formPanel.add(new JLabel("Mã Số Sinh Viên (MSSV):"));
        txtMSSV = new JTextField();
        formPanel.add(txtMSSV);

        formPanel.add(new JLabel("Tên Sinh Viên:"));
        txtTenSV = new JTextField();
        formPanel.add(txtTenSV);

        formPanel.add(new JLabel("Điểm Rèn Luyện (DiemRL):"));
        txtDiemRL = new JTextField();
        formPanel.add(txtDiemRL);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        String[] headers = {"MSSV", "Tên Sinh Viên", "Điểm Rèn Luyện"};
        tableModel = new DefaultTableModel(headers, 0);
        tableSinhVien = new JTable(tableModel);
        tableSinhVien.setRowHeight(22);

        JScrollPane scrollTable = new JScrollPane(tableSinhVien);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh Sách Sinh Viên (Từ Form / Excel)"));
        leftPanel.add(scrollTable, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Nội Dung Dữ Liệu Kiểu XML"));
        txtPreviewXML = new JTextArea();
        txtPreviewXML.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtPreviewXML.setEditable(false);
        JScrollPane scrollXML = new JScrollPane(txtPreviewXML);
        rightPanel.add(scrollXML, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(460);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnThem = new JButton("Thêm SV");
        btnMoExcel = new JButton("Mở Tệp Excel...");
        btnTaoExcelMau = new JButton("Tạo File Excel Mẫu");
        btnChuyenXML = new JButton("Chuyển Sang XML");
        btnLuuXML = new JButton("Lưu Tệp XML...");
        btnThoat = new JButton("Thoát");

        for (JButton b : new JButton[]{btnThem, btnMoExcel, btnTaoExcelMau, btnChuyenXML, btnLuuXML, btnThoat}) {
            b.addActionListener(this);
            buttonPanel.add(b);
        }
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void capNhatBang() {
        tableModel.setRowCount(0);
        for (SinhVienExcel sv : danhSachSV) {
            tableModel.addRow(new Object[]{sv.getMssv(), sv.getTenSV(), sv.getDiemRL()});
        }
    }

    public static String taoChuoiXML(List<SinhVienExcel> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<DanhSachSinhVien>\n");
        for (SinhVienExcel sv : list) {
            sb.append(sv.toXMLString()).append("\n");
        }
        sb.append("</DanhSachSinhVien>");
        return sb.toString();
    }

    /**
     * Ghi tệp Excel OpenXML chuẩn (.xlsx) sử dụng java.util.zip.ZipOutputStream thuần JDK.
     */
    public static void taoTepExcelMauStatic(File outFile) throws Exception {
        List<SinhVienExcel> sample = new ArrayList<>();
        sample.add(new SinhVienExcel("SV001", "Nguyễn Văn Hùng", 85));
        sample.add(new SinhVienExcel("SV002", "Phạm Thị Mai", 90));
        sample.add(new SinhVienExcel("SV003", "Lê Thanh Tùng", 78));

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outFile))) {
            putZipEntry(zos, "[Content_Types].xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                    + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                    + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                    + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                    + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                    + "</Types>");

            putZipEntry(zos, "_rels/.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                    + "</Relationships>");

            putZipEntry(zos, "xl/workbook.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                    + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                    + "<sheets><sheet name=\"SinhVien\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");

            putZipEntry(zos, "xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                    + "</Relationships>");

            StringBuilder sheet = new StringBuilder();
            sheet.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                 .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>")
                 .append("<row r=\"1\">")
                 .append("<c r=\"A1\" t=\"inlineStr\"><is><t>MSSV</t></is></c>")
                 .append("<c r=\"B1\" t=\"inlineStr\"><is><t>Tên Sinh Viên</t></is></c>")
                 .append("<c r=\"C1\" t=\"inlineStr\"><is><t>Điểm Rèn Luyện</t></is></c>")
                 .append("</row>");
            int rIdx = 2;
            for (SinhVienExcel sv : sample) {
                sheet.append("<row r=\"").append(rIdx).append("\">")
                     .append("<c r=\"A").append(rIdx).append("\" t=\"inlineStr\"><is><t>")
                     .append(SinhVienExcel.escapeXml(sv.getMssv())).append("</t></is></c>")
                     .append("<c r=\"B").append(rIdx).append("\" t=\"inlineStr\"><is><t>")
                     .append(SinhVienExcel.escapeXml(sv.getTenSV())).append("</t></is></c>")
                     .append("<c r=\"C").append(rIdx).append("\"><v>")
                     .append(sv.getDiemRL()).append("</v></c>")
                     .append("</row>");
                rIdx++;
            }
            sheet.append("</sheetData></worksheet>");
            putZipEntry(zos, "xl/worksheets/sheet1.xml", sheet.toString());
        }
    }

    private static void putZipEntry(ZipOutputStream zos, String name, String content) throws Exception {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * Đọc tệp Excel (.xlsx hoặc .csv) bằng bộ giải nén OpenXML thuần JDK.
     */
    public static List<SinhVienExcel> docTepExcelStatic(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".csv")) {
            return docTepCsv(file);
        }
        Map<String, byte[]> entries = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int n;
                while ((n = zis.read(buf)) > 0) {
                    bos.write(buf, 0, n);
                }
                entries.put(entry.getName(), bos.toByteArray());
            }
        }

        List<String> sharedStrings = new ArrayList<>();
        if (entries.containsKey("xl/sharedStrings.xml")) {
            Document docSS = parseXml(entries.get("xl/sharedStrings.xml"));
            NodeList siList = docSS.getElementsByTagName("si");
            for (int i = 0; i < siList.getLength(); i++) {
                sharedStrings.add(siList.item(i).getTextContent());
            }
        }

        byte[] sheetBytes = entries.get("xl/worksheets/sheet1.xml");
        if (sheetBytes == null) {
            throw new IllegalArgumentException("Tệp .xlsx không chứa xl/worksheets/sheet1.xml");
        }
        Document docSheet = parseXml(sheetBytes);
        NodeList rowNodes = docSheet.getElementsByTagName("row");

        List<SinhVienExcel> list = new ArrayList<>();
        for (int i = 0; i < rowNodes.getLength(); i++) {
            Element rowEl = (Element) rowNodes.item(i);
            if ("1".equals(rowEl.getAttribute("r")) || i == 0) continue;

            NodeList cNodes = rowEl.getElementsByTagName("c");
            String[] cols = new String[]{"", "", ""};
            for (int c = 0; c < cNodes.getLength() && c < 3; c++) {
                Element cellEl = (Element) cNodes.item(c);
                String t = cellEl.getAttribute("t");
                if ("inlineStr".equals(t)) {
                    cols[c] = cellEl.getTextContent().trim();
                } else if ("s".equals(t)) {
                    int idx = Integer.parseInt(cellEl.getTextContent().trim());
                    cols[c] = sharedStrings.get(idx).trim();
                } else {
                    cols[c] = cellEl.getTextContent().trim();
                }
            }
            if (!cols[0].isEmpty() && !cols[1].isEmpty()) {
                double diem = 0.0;
                try {
                    diem = Double.parseDouble(cols[2]);
                } catch (NumberFormatException ignored) {}
                list.add(new SinhVienExcel(cols[0], cols[1], diem));
            }
        }
        return list;
    }

    private static List<SinhVienExcel> docTepCsv(File file) throws Exception {
        List<SinhVienExcel> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    list.add(new SinhVienExcel(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim())));
                }
            }
        }
        return list;
    }

    private static Document parseXml(byte[] data) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new ByteArrayInputStream(data));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnThoat) {
            System.exit(0);
            return;
        }
        if (src == btnTaoExcelMau) {
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setSelectedFile(new File("danhsach_sinhvien_mau.xlsx"));
            chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    taoTepExcelMauStatic(chooser.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Đã tạo thành công tệp Excel mẫu!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tạo tệp Excel mẫu: " + ex.getMessage(), "Lỗi Ghi File", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }
        if (src == btnThem) {
            String mssv = txtMSSV.getText().trim();
            String ten = txtTenSV.getText().trim();
            String strDiem = txtDiemRL.getText().trim();
            if (mssv.isEmpty() || ten.isEmpty() || strDiem.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ MSSV, Tên SV và Điểm RL!", "Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double diem = Double.parseDouble(strDiem);
                danhSachSV.add(new SinhVienExcel(mssv, ten, diem));
                capNhatBang();
                txtPreviewXML.setText(taoChuoiXML(danhSachSV));
                txtMSSV.setText("");
                txtTenSV.setText("");
                txtDiemRL.setText("");
                txtMSSV.requestFocus();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm rèn luyện phải là một số thực hợp lệ!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        if (src == btnMoExcel) {
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setFileFilter(new FileNameExtensionFilter("Tệp Excel (*.xlsx, *.csv)", "xlsx", "csv"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    danhSachSV = docTepExcelStatic(chooser.getSelectedFile());
                    capNhatBang();
                    txtPreviewXML.setText(taoChuoiXML(danhSachSV));
                    JOptionPane.showMessageDialog(this, "Đã đọc thành công " + danhSachSV.size() + " sinh viên từ tệp Excel!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi đọc tệp Excel: " + ex.getMessage(), "Lỗi Đọc File", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }
        if (src == btnChuyenXML) {
            if (danhSachSV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Danh sách sinh viên đang rỗng!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            txtPreviewXML.setText(taoChuoiXML(danhSachSV));
            JOptionPane.showMessageDialog(this, "Đã chuyển đổi dữ liệu sinh viên sang dạng XML!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (src == btnLuuXML) {
            if (danhSachSV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu sinh viên để xuất XML!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setSelectedFile(new File("danhsach_sinhvien.xml"));
            chooser.setFileFilter(new FileNameExtensionFilter("Tệp XML (*.xml)", "xml"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File xmlFile = chooser.getSelectedFile();
                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), StandardCharsets.UTF_8))) {
                    bw.write(taoChuoiXML(danhSachSV));
                    JOptionPane.showMessageDialog(this, "Đã lưu thành công tập tin XML tại:\n" + xmlFile.getAbsolutePath(), "Lưu Tệp XML Thành Công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi ghi tệp XML: " + ex.getMessage(), "Lỗi Ghi File", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void runAutomatedTests() {
        System.out.println("=================================================");
        System.out.println("  KIỂM THỬ TỰ ĐỘNG BÀI 4: EXCEL (.XLSX) & XML");
        System.out.println("=================================================");
        File tempExcel = new File("temp_test_excel.xlsx");
        try {
            taoTepExcelMauStatic(tempExcel);
            boolean passExcelWrite = tempExcel.exists() && tempExcel.length() > 0;
            System.out.printf("[Test 1] Tạo tệp Excel (.xlsx) mẫu -> %s%n", passExcelWrite ? "ĐẠT (PASS)" : "THẤT BẠI");

            List<SinhVienExcel> listRead = docTepExcelStatic(tempExcel);
            boolean passExcelRead = listRead.size() == 3 && "SV001".equals(listRead.get(0).getMssv());
            System.out.printf("[Test 2] Đọc thành công %d sinh viên từ file Excel -> %s%n", listRead.size(), passExcelRead ? "ĐẠT (PASS)" : "THẤT BẠI");

            String xmlStr = taoChuoiXML(listRead);
            boolean passXmlConvert = xmlStr.contains("<DanhSachSinhVien>") && xmlStr.contains("<MSSV>SV001</MSSV>");
            System.out.printf("[Test 3] Chuyển đổi dữ liệu sang chuẩn XML -> %s%n", passXmlConvert ? "ĐẠT (PASS)" : "THẤT BẠI");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kiểm thử Bài 4", e);
        } finally {
            if (tempExcel.exists()) tempExcel.delete();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            runAutomatedTests();
            return;
        }
        SwingUtilities.invokeLater(() -> new Bai04_SinhVienExcelXML().setVisible(true));
    }
}
