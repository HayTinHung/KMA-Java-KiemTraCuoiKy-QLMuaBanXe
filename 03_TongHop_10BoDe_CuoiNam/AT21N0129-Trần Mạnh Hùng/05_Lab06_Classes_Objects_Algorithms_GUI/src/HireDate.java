/**
 * BÀI TẬP 2 - CẤU TRÚC NGÀY THUÊ (HIREDATE)
 * Bao gồm ngày (day), tháng (month), năm (year) có kiểm tra tính hợp lệ của lịch.
 */
public final class HireDate implements Comparable<HireDate> {
    private int day;
    private int month;
    private int year;

    public HireDate() {
        this(1, 1, 2020);
    }

    public HireDate(int day, int month, int year) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        int maxDays = daysInMonth(this.month, this.year);
        if (day < 1 || day > maxDays) {
            this.day = 1;
        } else {
            this.day = day;
        }
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        if (month < 1 || month > 12) {
            this.month = 1;
        } else {
            this.month = month;
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (year < 1900 || year > 2100) {
            this.year = 2020;
        } else {
            this.year = year;
        }
    }

    public static boolean isLeapYear(int y) {
        return (y % 400 == 0) || (y % 4 == 0 && y % 100 != 0);
    }

    public static int daysInMonth(int m, int y) {
        switch (m) {
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isLeapYear(y) ? 29 : 28;
            default:
                return 31;
        }
    }

    @Override
    public int compareTo(HireDate o) {
        if (this.year != o.year) return Integer.compare(this.year, o.year);
        if (this.month != o.month) return Integer.compare(this.month, o.month);
        return Integer.compare(this.day, o.day);
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", day, month, year);
    }
}
