import java.math.BigInteger;
import java.util.Objects;

/**
 * BÀI TẬP 4 - LỚP RATIONAL (PHÂN SỐ TRONG TRƯỜNG SỐ HỮU TỶ Q)
 * Tử số (numerator) và mẫu số (denominator) là các số nguyên, mẫu số luôn khác 0.
 * Tự động rút gọn tối giản bằng thuật toán Euclid (GCD) và chuẩn hóa mẫu số dương.
 */
public class Rational implements Comparable<Rational> {
    public static final Rational ZERO = new Rational(0, 1);
    public static final Rational ONE = new Rational(1, 1);

    private final BigInteger numerator;
    private final BigInteger denominator;

    public Rational() {
        this(BigInteger.ZERO, BigInteger.ONE);
    }

    public Rational(long numerator) {
        this(BigInteger.valueOf(numerator), BigInteger.ONE);
    }

    public Rational(long numerator, long denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public Rational(BigInteger num, BigInteger den) {
        if (den == null || den.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Mẫu số của phân số không được bằng 0!");
        }
        if (num == null || num.equals(BigInteger.ZERO)) {
            this.numerator = BigInteger.ZERO;
            this.denominator = BigInteger.ONE;
            return;
        }
        if (den.compareTo(BigInteger.ZERO) < 0) {
            num = num.negate();
            den = den.negate();
        }
        BigInteger gcd = num.abs().gcd(den.abs());
        this.numerator = num.divide(gcd);
        this.denominator = den.divide(gcd);
    }

    /**
     * Phân tích chuỗi dạng "a/b" hoặc số nguyên "a" thành đối tượng Rational.
     */
    public static Rational parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            return ZERO;
        }
        String s = text.trim();
        if (s.contains("/")) {
            String[] parts = s.split("/");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Định dạng phân số không hợp lệ: " + text);
            }
            BigInteger n = new BigInteger(parts[0].trim());
            BigInteger d = new BigInteger(parts[1].trim());
            return new Rational(n, d);
        } else {
            return new Rational(new BigInteger(s), BigInteger.ONE);
        }
    }

    public long getNumerator() {
        return numerator.longValue();
    }

    public long getDenominator() {
        return denominator.longValue();
    }

    public BigInteger getBigNumerator() {
        return numerator;
    }

    public BigInteger getBigDenominator() {
        return denominator;
    }

    public boolean isZero() {
        return numerator.equals(BigInteger.ZERO);
    }

    public boolean isNegative() {
        return numerator.compareTo(BigInteger.ZERO) < 0;
    }

    public Rational abs() {
        return new Rational(numerator.abs(), denominator);
    }

    public Rational negate() {
        return new Rational(numerator.negate(), denominator);
    }

    public Rational add(Rational other) {
        BigInteger num = this.numerator.multiply(other.denominator)
                .add(other.numerator.multiply(this.denominator));
        BigInteger den = this.denominator.multiply(other.denominator);
        return new Rational(num, den);
    }

    public Rational subtract(Rational other) {
        BigInteger num = this.numerator.multiply(other.denominator)
                .subtract(other.numerator.multiply(this.denominator));
        BigInteger den = this.denominator.multiply(other.denominator);
        return new Rational(num, den);
    }

    public Rational multiply(Rational other) {
        return new Rational(
                this.numerator.multiply(other.numerator),
                this.denominator.multiply(other.denominator)
        );
    }

    public Rational divide(Rational other) {
        if (other == null || other.isZero()) {
            throw new ArithmeticException("Không thể chia cho phân số 0!");
        }
        return new Rational(
                this.numerator.multiply(other.denominator),
                this.denominator.multiply(other.numerator)
        );
    }

    public Rational pow(int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Số mũ của đa thức phải >= 0");
        }
        if (exponent == 0) return ONE;
        return new Rational(numerator.pow(exponent), denominator.pow(exponent));
    }

    public double toDouble() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    @Override
    public int compareTo(Rational other) {
        BigInteger left = this.numerator.multiply(other.denominator);
        BigInteger right = other.numerator.multiply(this.denominator);
        return left.compareTo(right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rational)) return false;
        Rational rational = (Rational) o;
        return numerator.equals(rational.numerator) && denominator.equals(rational.denominator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        if (denominator.equals(BigInteger.ONE)) {
            return numerator.toString();
        }
        return numerator.toString() + "/" + denominator.toString();
    }
}
