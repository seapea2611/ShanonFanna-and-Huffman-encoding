import java.util.Scanner;

public class Bai1 {
    // tim uoc chung lon nhat
    public static int UCLN(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    // lop dai dien cho phan so
    public static class Fraction {
        private int numerator;
        private int denominator;

        public int getNumerator() {
            return numerator;
        }

        public int getDenominator() {
            return denominator;
        }

        public Fraction(int numerator, int denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        // rut gon phan so
        public Fraction rutGon() {
            int ucln = UCLN(this.numerator,this.denominator);
            int Denominator = this.denominator / ucln;
            int Numerator = this.numerator / ucln;
            return new Fraction(Numerator, Denominator);
        }

        // cong PS
        public Fraction add(Fraction other) {
            if (other == null) {
                return this; // Trả về phân số hiện tại nếu other là null
            }
            int commonDenominator = this.denominator * other.denominator;
            int sumNumerator = (this.numerator * other.denominator) + (other.numerator * this.denominator);
            return new Fraction(sumNumerator, commonDenominator).rutGon();
        }

        // chia phan so
        public Fraction divide(Fraction other) {
            if(this.getNumerator() == 0 || other.getNumerator() == 0) {
                return new Fraction(0,1);
            }
            if(other == null)
                return this;
            int Denominator = this.denominator * other.numerator;
            int Numerator = this.numerator * other.denominator;
            return new Fraction(Numerator, Denominator).rutGon();
        }
        // nhan 1 phan so
        public Fraction multi(Fraction other) {
            if(other == null)
                return this;
            int Denominator = this.denominator * other.denominator;
            int Numerator = this.numerator * other.numerator;
            return new Fraction(Numerator, Denominator).rutGon();
        }

        @Override
        public String toString() {
            return numerator + "/" + denominator;
        }
    }

    // chuyen 1 string ve dinh dang phan so
    private static Fraction parseFraction(String input) {
        String[] parts = input.split("/");
        if (parts.length == 2) {
                int numerator = Integer.parseInt(parts[0].trim());
                int denominator = Integer.parseInt(parts[1].trim());
                return new Fraction(numerator, denominator);
        } else {
            return null;
        }
    }

    public static double log2(Fraction fraction) {
        // co phan so bang 0
        if(fraction.getNumerator() == 0) {
            return 0;
        }
        double a = Math.log(fraction.getNumerator()) / Math.log(2);
        double b = Math.log(fraction.getDenominator()) / Math.log(2);
        return a - b;
    }

    public static void main(String[] args) {
        // caau a
        Scanner sc = new Scanner(System.in);

        int m = 0,n = 0;
        System.out.printf("Nhập vào m(số hàng): ");
        m = sc.nextInt();
        System.out.printf("Nhập vào n(số cột): ");
        n = sc.nextInt();
        while(m <= 0 || n <= 0) {
            System.out.println("Xin hãy nhập lại số hàng m với m > 0: ");
            m = sc.nextInt();
            System.out.println("Xin hãy nhập lại số cột n với n > 0: ");
            n = sc.nextInt();
        }

        Fraction[][] matrix = new Fraction[m][n];
        // sum = 0/1 <=> sum = 0
        Fraction sum = new Fraction(0,1);
        boolean exit = true;
        int i,j;

        // nhap vao ma tran
        while(exit) {
            for (i = 0; i < m; i++) {
                for (j = 0; j < n; j++) {
                    System.out.printf("Xin mời nhập phần tử a[%d][%d]: " ,i,j);
                    String input = sc.next();
                    Fraction fraction = parseFraction(input);
                    if(fraction.numerator < 0 || fraction.denominator <= 0 || fraction.numerator / fraction.denominator >=1) {
                        j--;
                        System.out.println("Phân số không được âm hoặc lớn hơn 1 hoặc mẫu bằng 0");
                    }
                    else {
                        matrix[i][j] = fraction;
                        sum = sum.add(fraction);
                    }
                }

            }
            //System.out.println(sum.numerator + " " + sum.denominator);
            // sum != 1 tuc la ma tran nhap chua dung
            if(sum.numerator != sum.denominator) {
                System.out.println("Ma trận của bạn chưa đúng");
                i = 0;
                j = 0;
                sum = new Fraction(0,1);
                continue;
            }
            else {
                exit = false;
            }
        }

        System.out.println("Ma trận xác suất: ");
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                System.out.print(matrix[i][j].toString() + " ");
            }
            System.out.println("");
        }
        System.out.println("\nCâu b: ");
        //câu b
        // Tinh H(X)
        Fraction[] x = new Fraction[n];
        // tạo ma trận p(x)
        for(j = 0; j < n; j++) {
            x[j] = new Fraction(0,1);
            for (i = 0; i < m; i++) {
                x[j] = x[j].add(matrix[i][j]);
            }
        }

        System.out.print("H(X): ");
        double hx = 0;
        // tinh sigma cua hx
        for(i = 0; i < n; i++) {
            hx += log2(x[i]) * x[i].getNumerator() / x[i].getDenominator();
        }
        hx = 0 - hx;
        System.out.printf("%f bits \n", hx);

        // Tinh H(Y)
        Fraction[] y = new Fraction[m];
        double hy = 0;
        //tao ma tran p(y)
        for(i = 0; i < m; i++){
            y[i] = new Fraction(0,1);
            for(j = 0; j < n; j++) {
                y[i] = y[i].add(matrix[i][j]);
            }
        }
        // tinh toan
        System.out.print("H(Y): ");
        for(i = 0; i < m; i++) {
            hy += log2(y[i]) * y[i].getNumerator() / y[i].getDenominator();
        }
        hy = 0 - hy;
        System.out.printf("%f bits \n", hy);

        // Tinh H(Y|X)
        // tim p(y|x)
        Fraction[][] yx = new Fraction[m][n];
        for(i = 0; i < m; i++) {
            for(j = 0; j < n ; j++) {
                yx[i][j] = matrix[i][j].divide(x[j]);
            }
        }
        double hyx = 0;
        System.out.print("H(Y|X): ");
        for(i = 0; i < m; i++) {
            for(j = 0; j < n; j++) {
                hyx += log2(yx[i][j]) * matrix[i][j].getNumerator() / matrix[i][j].getDenominator();
            }
        }
        hyx = 0 - hyx;
        System.out.printf("%f bits \n", hyx);

        // Tinh H(X|Y)
        // Su dung CT: hy - hyx = hx - hxy
        double hxy = hx + hyx - hy;
        System.out.printf("H(X|Y): %f bits \n", hxy);

        // Tinh H(X,Y)
        // Ap dung CT CT H(X,Y) = H(X) + H(Y|X)
        double hx_y = hx + hyx;
        System.out.printf("H(X,Y): %f bits\n", hx_y);

        // TÍnh H(Y) - H(Y|X)
        double hy_xy =  hy - hyx;
        System.out.printf("H(Y) - H(Y|X): %f bits\n", hy_xy);

        // Tính I(X;Y)
        double i_xy = 0;
        for(i = 0; i < m; i++) {
            for(j = 0; j < n; j++) {
                i_xy += log2(matrix[i][j].divide( x[j].multi(y[i]) ) ) * matrix[i][j].numerator / matrix[i][j].denominator;
            }
        }
        System.out.printf("I(X;Y): %f bits\n", i_xy);

        // Câu c
        System.out.println("\nCâu c:");
        // nếu là ma trận vuông
        if(m == n) {
            // tính D(px || py)
            double dxy = 0;
            for (i = 0; i < m; i++) {
                dxy += log2(x[i].divide(y[i])) * x[i].numerator / x[i].denominator;
            }
            System.out.printf("D(P(x)||P(y)): %f bits\n", dxy);

            // tính D(py || px)
            double dyx = 0;
            for (i = 0; i < m; i++) {
                dyx += log2(y[i].divide(x[i])) * y[i].numerator / y[i].denominator;
            }
            System.out.printf("D(P(y)||P(x)): %f bits\n", dyx);
        }
        // không phải ma trận vuông thì không tính
        else {
            System.out.println("Không tính được relative entropy");
        }
    }
}
