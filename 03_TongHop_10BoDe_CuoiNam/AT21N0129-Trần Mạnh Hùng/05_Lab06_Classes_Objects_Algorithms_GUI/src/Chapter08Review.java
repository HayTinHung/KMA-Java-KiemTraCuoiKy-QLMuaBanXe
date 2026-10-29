import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.EnumSet;
import java.util.Locale;
import static java.lang.Math.*;

/**
 * BÀI THỰC HÀNH SỐ 6 - PHẦN 2: REVIEW CHAPTER 08 (Classes and Objects: A Deeper Look)
 * Bao gồm đầy đủ 8 chương trình chuẩn:
 * 1) Time1Test
 * 2) ThisTest
 * 3) Time2Test
 * 4) EmployeeTest (Chapter 08 Composition)
 * 5) EnumTest
 * 6) StaticImportTest
 * 7) PackageDataTest
 * 8) Interest (BigDecimal)
 */
public class Chapter08Review {

    // =========================================================================
    // 1) Time1 & Time1Test
    // =========================================================================
    public static class Time1 {
        private int hour;   // 0 - 23
        private int minute; // 0 - 59
        private int second; // 0 - 59

        public void setTime(int hour, int minute, int second) {
            if (hour < 0 || hour >= 24 || minute < 0 || minute >= 60 || second < 0 || second >= 60) {
                throw new IllegalArgumentException("hour, minute and/or second was out of range");
            }
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public String toUniversalString() {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }

        @Override
        public String toString() {
            return String.format("%d:%02d:%02d %s",
                    ((hour == 0 || hour == 12) ? 12 : hour % 12),
                    minute, second, (hour < 12 ? "AM" : "PM"));
        }
    }

    public static void runTime1Test() {
        System.out.println("=== 1) Time1Test ===");
        Time1 time = new Time1();
        System.out.println("Initial universal time: " + time.toUniversalString());
        System.out.println("Initial standard time : " + time.toString());

        time.setTime(13, 27, 6);
        System.out.println("Universal time after setTime: " + time.toUniversalString());
        System.out.println("Standard time after setTime : " + time.toString());

        try {
            time.setTime(99, 99, 99);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception caught as expected: " + e.getMessage());
        }
        System.out.println();
    }

    // =========================================================================
    // 2) ThisTest
    // =========================================================================
    public static class SimpleTime {
        private int hour;
        private int minute;
        private int second;

        public SimpleTime(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public String buildString() {
            return String.format("%24s: %s%n%24s: %s",
                    "this.toUniversalString()", this.toUniversalString(),
                    "toUniversalString()", toUniversalString());
        }

        public String toUniversalString() {
            return String.format("%02d:%02d:%02d", this.hour, this.minute, this.second);
        }
    }

    public static void runThisTest() {
        System.out.println("=== 2) ThisTest ===");
        SimpleTime time = new SimpleTime(15, 30, 19);
        System.out.println(time.buildString());
        System.out.println();
    }

    // =========================================================================
    // 3) Time2 & Time2Test (Overloaded Constructors)
    // =========================================================================
    public static class Time2 {
        private int hour;
        private int minute;
        private int second;

        public Time2() {
            this(0, 0, 0);
        }

        public Time2(int hour) {
            this(hour, 0, 0);
        }

        public Time2(int hour, int minute) {
            this(hour, minute, 0);
        }

        public Time2(int hour, int minute, int second) {
            if (hour < 0 || hour >= 24) throw new IllegalArgumentException("hour must be 0-23");
            if (minute < 0 || minute >= 60) throw new IllegalArgumentException("minute must be 0-59");
            if (second < 0 || second >= 60) throw new IllegalArgumentException("second must be 0-59");
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public Time2(Time2 time) {
            this(time.getHour(), time.getMinute(), time.getSecond());
        }

        public int getHour() { return hour; }
        public int getMinute() { return minute; }
        public int getSecond() { return second; }

        public String toUniversalString() {
            return String.format("%02d:%02d:%02d", getHour(), getMinute(), getSecond());
        }

        @Override
        public String toString() {
            return String.format("%d:%02d:%02d %s",
                    ((getHour() == 0 || getHour() == 12) ? 12 : getHour() % 12),
                    getMinute(), getSecond(), (getHour() < 12 ? "AM" : "PM"));
        }
    }

    public static void runTime2Test() {
        System.out.println("=== 3) Time2Test ===");
        Time2 t1 = new Time2();
        Time2 t2 = new Time2(2);
        Time2 t3 = new Time2(21, 34);
        Time2 t4 = new Time2(12, 25, 42);
        Time2 t5 = new Time2(t4);

        System.out.println("t1 (default)   : " + t1.toUniversalString() + " | " + t1);
        System.out.println("t2 (hour)      : " + t2.toUniversalString() + " | " + t2);
        System.out.println("t3 (hour, min) : " + t3.toUniversalString() + " | " + t3);
        System.out.println("t4 (h, m, s)   : " + t4.toUniversalString() + " | " + t4);
        System.out.println("t5 (copy of t4): " + t5.toUniversalString() + " | " + t5);
        System.out.println();
    }

    // =========================================================================
    // 4) EmployeeTest (Composition with Date)
    // =========================================================================
    public static class DateCh8 {
        private final int month;
        private final int day;
        private final int year;

        public DateCh8(int month, int day, int year) {
            if (month <= 0 || month > 12) throw new IllegalArgumentException("month must be 1-12");
            if (day <= 0 || day > 31) throw new IllegalArgumentException("day out of range");
            this.month = month;
            this.day = day;
            this.year = year;
        }

        @Override
        public String toString() {
            return String.format("%d/%d/%d", month, day, year);
        }
    }

    public static class EmployeeCh8 {
        private final String firstName;
        private final String lastName;
        private final DateCh8 birthDate;
        private final DateCh8 hireDate;

        public EmployeeCh8(String firstName, String lastName, DateCh8 birthDate, DateCh8 hireDate) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.hireDate = hireDate;
        }

        @Override
        public String toString() {
            return String.format("%s, %s  Hired: %s  Birthday: %s", lastName, firstName, hireDate, birthDate);
        }
    }

    public static void runEmployeeCh8Test() {
        System.out.println("=== 4) EmployeeTest (Chapter 08 Composition) ===");
        DateCh8 birth = new DateCh8(7, 24, 1989);
        DateCh8 hire = new DateCh8(3, 12, 2018);
        EmployeeCh8 employee = new EmployeeCh8("Bob", "Blue", birth, hire);
        System.out.println(employee);
        System.out.println();
    }

    // =========================================================================
    // 5) EnumTest
    // =========================================================================
    public enum Book {
        JHTP("Java How to Program", "2018"),
        CHTP("C How to Program", "2016"),
        IW3HTP("Internet & World Wide Web How to Program", "2012"),
        CPPHTP("C++ How to Program", "2017"),
        VBHTP("Visual Basic How to Program", "2014"),
        CSHARPHTP("Visual C# How to Program", "2017");

        private final String title;
        private final String copyrightYear;

        Book(String title, String copyrightYear) {
            this.title = title;
            this.copyrightYear = copyrightYear;
        }

        public String getTitle() { return title; }
        public String getCopyrightYear() { return copyrightYear; }
    }

    public static void runEnumTest() {
        System.out.println("=== 5) EnumTest ===");
        System.out.println("All books:");
        for (Book book : Book.values()) {
            System.out.printf("%-10s%-45s%s%n", book, book.getTitle(), book.getCopyrightYear());
        }
        System.out.println("\nDisplay a range of enum constants:");
        for (Book book : EnumSet.range(Book.JHTP, Book.CPPHTP)) {
            System.out.printf("%-10s%-45s%s%n", book, book.getTitle(), book.getCopyrightYear());
        }
        System.out.println();
    }

    // =========================================================================
    // 6) StaticImportTest
    // =========================================================================
    public static void runStaticImportTest() {
        System.out.println("=== 6) StaticImportTest ===");
        System.out.printf("sqrt(900.0) = %.1f%n", sqrt(900.0));
        System.out.printf("ceil(-9.8)  = %.1f%n", ceil(-9.8));
        System.out.printf("E           = %f%n", E);
        System.out.printf("PI          = %f%n", PI);
        System.out.println();
    }

    // =========================================================================
    // 7) PackageDataTest
    // =========================================================================
    static class PackageData {
        int number = 0;
        String string = "Hello";

        @Override
        public String toString() {
            return String.format("number: %d; string: %s", number, string);
        }
    }

    public static void runPackageDataTest() {
        System.out.println("=== 7) PackageDataTest ===");
        PackageData packageData = new PackageData();
        System.out.printf("After instantiation:%n%s%n", packageData);
        packageData.number = 77;
        packageData.string = "Goodbye";
        System.out.printf("After changing values:%n%s%n%n", packageData);
    }

    // =========================================================================
    // 8) Interest (BigDecimal)
    // =========================================================================
    public static void runInterestTest() {
        System.out.println("=== 8) Interest (BigDecimal) ===");
        BigDecimal principal = BigDecimal.valueOf(1000.0);
        BigDecimal rate = BigDecimal.valueOf(0.05);

        System.out.printf("%s%20s%n", "Year", "Amount on deposit");
        for (int year = 1; year <= 10; year++) {
            BigDecimal amount = principal.multiply(rate.add(BigDecimal.ONE).pow(year));
            System.out.printf("%4d%20s%n", year, NumberFormat.getCurrencyInstance(Locale.US).format(amount));
        }
        System.out.println();
    }

    public static void runAll() {
        runTime1Test();
        runThisTest();
        runTime2Test();
        runEmployeeCh8Test();
        runEnumTest();
        runStaticImportTest();
        runPackageDataTest();
        runInterestTest();
    }

    public static void main(String[] args) {
        runAll();
    }
}
