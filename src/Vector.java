public class Vector {
    //Переменные:
    private double x;
    private double y;


    //Конструкторы:
    public Vector(double x, double y) { //Простой конструктор,на входе координаты (x, y)
        this.x = x;
        this.y = y;
    }
    public Vector(double r, double theta, boolean ignored) { //Конструктор, на входе координаты (r, θ) и булевская переменная
        //(нужна, чтобы отличить от обычного конструктора, значение не важно)
        this.x = r * Math.cos(theta);
        this.y = r * Math.sin(theta);
    }
    public Vector(Point a){
        this.x = a.getX();
        this.y = a.getY();
    }
    public Vector(Point a, Point b){
        this.x = a.getX()-b.getX();
        this.y = a.getY()-b.getY();
    }
    public Vector(Vector a){
        this.x = a.getX();
        this.y = a.getY();
    }


    //Вывод в строку:
    @Override
    public String toString(){
        return String.format("Vector (%.4f; %.4f)", this.getX(), this.getY());
    }


    //Сеттеры:
    public void setX(double x) { //Установка значения x
        this.x = x;
    }
    public void setY(double y) { //Установка значения y
        this.y = y;
    }
    public void setR(double r){
        double rMult = r/this.getR();
        this.setX(this.getX()*rMult);
        this.setY(this.getY()*rMult);
    }


    //Геттеры:
    public double getX() { //Получение значения x
        return x;
    }
    public double getY() { //Получение значения y
        return y;
    }
    public double getR() { //Получение модуля вектора
        double r = Math.sqrt(x * x + y * y);
        return r;
    }
    public double getTheta(){ //Получение угла между вектором и горизонталью
        double theta = Math.atan2(y, x);
        return theta;
    }


    //Операции с векторами:
    public void times(double k){ //Умножение вектора на число (изменяет текущий)
        this.x*=k;
        this.y*=k;
    }
    public Vector x(double k){ //Умножение вектора на число (возвращает новый)
        double x = this.x*k;
        double y = this.y*k;
        Vector result = new Vector(x, y);
        return result;
    }
    public Vector add(Vector a){ //Сложение двух векторов
        double xNew = this.getX()+a.getX();
        double yNew = this.getY()+a.getY();
        Vector vectorNew = new Vector(xNew, yNew);
        return vectorNew;
    }

    public void addToThis(Vector a){ //Сложение двух векторов
        double xNew = this.getX()+a.getX();
        double yNew = this.getY()+a.getY();
        this.x = xNew;
        this.y = yNew;
    }

    public Vector subtract(Vector a){
        double xNew = this.getX()-a.getX();
        double yNew = this.getY()-a.getY();
        Vector vectorNew = new Vector(xNew, yNew);
        return vectorNew;
    }
    public void subtractFromThis(Vector a){
        double xNew = this.getX()-a.getX();
        double yNew = this.getY()-a.getY();
        this.x = xNew;
        this.y = yNew;
    }

    public double projectOn(Vector a){ //Проекция вектора this на данный вектор a
        double alpha = Math.abs(this.getTheta()-a.getTheta()); //Угол между векторами
        double projection = this.getR()*Math.cos(alpha);
        return projection;
    }
    public Vector getNormal(Vector a){ //Получение составляющей вектора this, нормальной к вектору a
        Vector normal = new Vector(1, a.getTheta()+(Math.PI/2),true); //Вектор normal - нормаль к вектору a
        double normalLength = this.projectOn(normal); //Проекцируем вектор this на нормаль normal
        int sign = (int)(Math.abs(normalLength)/normalLength); //Знак normalLength
        Vector result = new Vector(Math.abs(normalLength), a.getTheta()+sign*Math.PI/2); //Создаем вектор, параллельный нормалю normal и равный по модулю normalLength
        return result;
    }
    public Vector getTangent(Vector a){ //Получение составляющей вектора this, тангенциальной к вектору a
        Vector result = this.subtract(this.getNormal(a)); //Находим нормальную составляющую и вычитаем её из вектора this
        return result;
    }
}

class Force extends Vector{
    int time; //Время действия силы, с каждым обновлением Creature, которому она приложена, уменьшается на 1
    //Если time = -1, то сила действует условно вечно


    //Конструкторы
    public Force(double x, double y, int time){ //Простой конструктор,на входе координаты (x, y) и время действия силы time
        super(x, y);
        this.time = time;
    }

    public Force(double r, double theta, int time, boolean ignored){ //Конструктор, на входе координаты (r, θ), время действия силы time и булевская переменная
        //(нужна, чтобы отличить от обычного конструктора, значение не важно)
        super(r, theta);
        this.time = time;
    }


    //Геттеры:
    public int getTime() {
        return time;
    }


    //Сеттеры:
    public void setTime(int time){
        this.time = time;
    }

    public void decreaseTime(){ //Уменьшает время на 1
        this.time--;
    }


    @Override
    public String toString(){
        return String.format("Force (%.4f; %.4f), will last for %d", this.getX(), this.getY(), this.getTime());
    }
}
