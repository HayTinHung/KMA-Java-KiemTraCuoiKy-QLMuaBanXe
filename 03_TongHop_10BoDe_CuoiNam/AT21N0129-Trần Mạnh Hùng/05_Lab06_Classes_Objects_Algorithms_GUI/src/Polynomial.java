import java.util.Arrays;

/**
 * BÀI TẬP 4 - LỚP POLYNOMIAL (ĐA THỨC TRONG TRƯỜNG SỐ HỮU TỶ Q)
 * Biểu diễn đa thức f(x) = sum_{i=0..n} a_i * x^i với bậc tự nhiên n (0 <= n <= 100),
 * trong đó hệ số a_i và biến x là các phân số hữu tỷ (Rational).
 */
public class Polynomial {
    public static final int MAX_INPUT_DEGREE = 100;

    private int degree;
    private Rational[] coefficients; // coefficients[i] là hệ số a_i đi với x^i

    public Polynomial() {
        this.degree = 0;
        this.coefficients = new Rational[]{Rational.ZERO};
    }

    public Polynomial(int degree) {
        if (degree < 0 || degree > MAX_INPUT_DEGREE) {
            throw new IllegalArgumentException("Bậc của đa thức nhập vào phải nằm trong đoạn [0, 100].");
        }
        this.degree = degree;
        this.coefficients = new Rational[degree + 1];
        Arrays.fill(this.coefficients, Rational.ZERO);
    }

    public Polynomial(Rational[] coeffs) {
        this(coeffs, true);
    }

    private Polynomial(Rational[] coeffs, boolean validateMaxDegree) {
        if (coeffs == null || coeffs.length == 0) {
            this.degree = 0;
            this.coefficients = new Rational[]{Rational.ZERO};
            return;
        }
        int effectiveDeg = coeffs.length - 1;
        while (effectiveDeg > 0 && (coeffs[effectiveDeg] == null || coeffs[effectiveDeg].isZero())) {
            effectiveDeg--;
        }
        if (validateMaxDegree && effectiveDeg > MAX_INPUT_DEGREE) {
            throw new IllegalArgumentException("Bậc của đa thức vượt quá giới hạn 100: " + effectiveDeg);
        }
        this.degree = effectiveDeg;
        this.coefficients = new Rational[effectiveDeg + 1];
        for (int i = 0; i <= effectiveDeg; i++) {
            this.coefficients[i] = coeffs[i] != null ? coeffs[i] : Rational.ZERO;
        }
    }

    public int getDegree() {
        return degree;
    }

    public Rational getCoefficient(int power) {
        if (power < 0 || power > degree) {
            return Rational.ZERO;
        }
        return coefficients[power];
    }

    public void setCoefficient(int power, Rational value) {
        if (power < 0 || power > MAX_INPUT_DEGREE) {
            throw new IllegalArgumentException("Số mũ phải nằm trong đoạn [0, 100].");
        }
        if (power > degree) {
            Rational[] expanded = new Rational[power + 1];
            Arrays.fill(expanded, Rational.ZERO);
            System.arraycopy(this.coefficients, 0, expanded, 0, this.coefficients.length);
            this.coefficients = expanded;
            this.degree = power;
        }
        this.coefficients[power] = value != null ? value : Rational.ZERO;
        normalize();
    }

    private void normalize() {
        int newDeg = coefficients.length - 1;
        while (newDeg > 0 && coefficients[newDeg].isZero()) {
            newDeg--;
        }
        if (newDeg != degree) {
            this.degree = newDeg;
            this.coefficients = Arrays.copyOf(this.coefficients, newDeg + 1);
        }
    }

    /**
     * Tính giá trị của đa thức f(x0) tại điểm x0 thuộc Q bằng sơ đồ Horner O(n).
     */
    public Rational evaluate(Rational x0) {
        if (x0 == null) {
            throw new IllegalArgumentException("Điểm x0 không được null.");
        }
        Rational result = coefficients[degree];
        for (int i = degree - 1; i >= 0; i--) {
            result = result.multiply(x0).add(coefficients[i]);
        }
        return result;
    }

    /**
     * Tính tổng 2 đa thức: P(x) + Q(x)
     */
    public Polynomial add(Polynomial other) {
        if (other == null) return this;
        int maxDeg = Math.max(this.degree, other.degree);
        Rational[] sumCoeffs = new Rational[maxDeg + 1];
        for (int i = 0; i <= maxDeg; i++) {
            sumCoeffs[i] = this.getCoefficient(i).add(other.getCoefficient(i));
        }
        return new Polynomial(sumCoeffs, false);
    }

    /**
     * Tính tích 2 đa thức: P(x) * Q(x)
     */
    public Polynomial multiply(Polynomial other) {
        if (other == null) return new Polynomial();
        int prodDeg = this.degree + other.degree;
        Rational[] prodCoeffs = new Rational[prodDeg + 1];
        Arrays.fill(prodCoeffs, Rational.ZERO);

        for (int i = 0; i <= this.degree; i++) {
            if (this.coefficients[i].isZero()) continue;
            for (int j = 0; j <= other.degree; j++) {
                if (other.coefficients[j].isZero()) continue;
                Rational term = this.coefficients[i].multiply(other.coefficients[j]);
                prodCoeffs[i + j] = prodCoeffs[i + j].add(term);
            }
        }
        return new Polynomial(prodCoeffs, false);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = degree; i >= 0; i--) {
            Rational c = coefficients[i];
            if (c.isZero()) continue;

            boolean negative = c.isNegative();
            Rational absCoeff = c.abs();

            if (sb.length() == 0) {
                if (negative) sb.append("-");
            } else {
                sb.append(negative ? " - " : " + ");
            }

            if (i == 0 || !absCoeff.equals(Rational.ONE)) {
                sb.append(absCoeff.toString());
                if (i > 0) sb.append("*");
            }

            if (i == 1) {
                sb.append("x");
            } else if (i > 1) {
                sb.append("x^").append(i);
            }
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }
}
