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


    //Вывод в строку:
    @Override
    public String toString(){
        return String.format("Vector (%.4d; %.4d)", this.getX(), this.getY());
    }


    //Сеттеры:
    public void setX(double x) { //Установка значения x
        this.x = x;
    }
    public void setY(double y) { //Установка значения y
        this.y = y;
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
        Vector result = this.add(this.getNormal(a).x(-1)); //Находим нормальную составляющую и вычитаем её из вектора this
        return result;
    }
}
