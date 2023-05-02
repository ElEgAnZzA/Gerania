public class Point {
    //Переменные:
    private double x; //Координаты (x, y)
    private double y;


    //Конструкторы:
    public Point(){
        x = 0;
        y = 0;
    }
    public Point (double x, double y){
        this.x=x;
        this.y=y;
    }


    //Геттеры:
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    //Сеттеры:
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    //Функции:
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", this.x, this.y);
    }
    public double distanceToZero(){
        return Math.sqrt(x*x+y*y);
    }
    public double distanceTo(Point a){ //Расстояние до точки а
        double dx = a.x-x;
        double dy = a.y-y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    public double distanceToSquared(Point a){ //Квадрат расстояния до точки а. Срезает вычислительнозатратную операцию нахождения корня
        double dx = a.x-x;
        double dy = a.y-y;
        return dx*dx + dy*dy;
    }
    public Point enlarged(double n){//Увеличить расстояние от центра в n раз
        return new Point(x*n, y*n);
    }
    public static Point enlarged (double n, Point a){//Увеличить расстояние от центра до точки а в n раз
        return new Point(a.x*n, a.y*n);
    }
    public void enlarge(double n) { //Увеличить расстояние от центра в n раз
        x *= n;
        y *= n;
    }
}
