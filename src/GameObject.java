public class GameObject {
    private double x; //Координаты (x, y)
    private double y;
    int width; //Ширина и высота
    int height;


    public GameObject(){
        this.x=0;
        this.y=0;
        this.width=1;
        this.height=1;
    }
    public GameObject(double x, double y, int width, int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }


    //Геттеры:
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    //Сеттеры:
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    //Прочее:
    @Override
    public String toString(){
        return "GameObject at:"+this.getX()+" "+this.getY()+"; Width:"+this.getWidth()+"; Height:"+this.getHeight();
    }
    public static GameObject stringToGameObject(String string){//Получение игрового объекта из строки вида "x y width height", используется при загрузке уровня
        String[] parameters = string.split(" ");
        return new GameObject(Integer.valueOf(parameters[0]), Integer.valueOf(parameters[1]), Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]));
    }
}
