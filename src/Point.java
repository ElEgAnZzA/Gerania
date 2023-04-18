public class Point {
    //Переменные:
    private double x;
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
    public double distanceTo(Point a){
        double dx = a.x-x;
        double dy = a.y-y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    public double distanceToSquared(Point a){
        double dx = a.x-x;
        double dy = a.y-y;
        return dx*dx + dy*dy;
    }
    public Point enlarged(double n){
        return new Point(x*n, y*n);
    }
    public static Point enlarged (double n, Point a){
        return new Point(a.x*n, a.y*n);
    }
    public void enlarge(double n) {
        x *= n;
        y *= n;
    }
    public boolean includeZero(Point a){
        double x0 = (x+a.x)/2;
        double y0 = (y+a.y)/2;
        double r = distanceTo(a)/2;
        return (new Point(x0, y0).distanceToZero()<=r);
    }
}
