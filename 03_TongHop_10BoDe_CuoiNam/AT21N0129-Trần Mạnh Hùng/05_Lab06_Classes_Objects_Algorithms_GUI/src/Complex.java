/**
 * BÀI TẬP 3 - LỚP COMPLEX (SỐ PHỨC a + b*i)
 * Hỗ trợ:
 * - Cộng 2 số phức: (a + c) + (b + d)*i
 * - Trừ 2 số phức: (a - c) + (b - d)*i (trừ toán hạng bên phải khỏi toán hạng bên trái)
 * - Xuất số phức dạng: (realPart, imaginaryPart) và dạng đại số a + bi
 */
public class Complex {
    private double realPart;
    private double imaginaryPart;

    public Complex() {
        this(0.0, 0.0);
    }

    public Complex(double realPart, double imaginaryPart) {
        this.realPart = realPart;
        this.imaginaryPart = imaginaryPart;
    }

    public double getRealPart() {
        return realPart;
    }

    public void setRealPart(double realPart) {
        this.realPart = realPart;
    }

    public double getImaginaryPart() {
        return imaginaryPart;
    }

    public void setImaginaryPart(double imaginaryPart) {
        this.imaginaryPart = imaginaryPart;
    }

    /**
     * Cộng 2 số phức: this + right
     */
    public Complex add(Complex right) {
        if (right == null) return new Complex(this.realPart, this.imaginaryPart);
        return new Complex(
                this.realPart + right.realPart,
                this.imaginaryPart + right.imaginaryPart
        );
    }

    /**
     * Trừ 2 số phức: this - right
     * Phần thực của toán hạng bên phải bị trừ đi từ phần thực của toán hạng bên trái,
     * và phần ảo của toán hạng bên phải bị trừ đi từ phần ảo của toán hạng bên trái.
     */
    public Complex subtract(Complex right) {
        if (right == null) return new Complex(this.realPart, this.imaginaryPart);
        return new Complex(
                this.realPart - right.realPart,
                this.imaginaryPart - right.imaginaryPart
        );
    }

    /**
     * Trả về chuỗi định dạng chuẩn theo yêu cầu đề bài: (realPart, imaginaryPart)
     */
    public String toTupleString() {
        return String.format("(%.2f, %.2f)", realPart, imaginaryPart);
    }

    /**
     * Biểu diễn dạng đại số a + b*i
     */
    public String toAlgebraicString() {
        if (imaginaryPart >= 0) {
            return String.format("%.2f + %.2fi", realPart, imaginaryPart);
        } else {
            return String.format("%.2f - %.2fi", realPart, Math.abs(imaginaryPart));
        }
    }

    @Override
    public String toString() {
        return toTupleString();
    }
}
