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
    public void checkCollisions(Creature[] creatures){ //Проверяет на столкновения:
        boolean finished = false;
        int[] collisions = new int[1000]; //Список всех номеров существ, с которыми происходит столкновение
        int kCollisions = 0;
        for (int i = 0; i<creatures.length&&finished == false; i++){
            if (creatures[i]!=null){
                double selectedX = creatures[i].getX();
                double selectedY = creatures[i].getY();
                int selectedWidth = creatures[i].getWidth();
                int selectedHeight = creatures[i].getHeight();
                if ((selectedX+selectedWidth>=this.x)&& //Условие первое: левая часть объекта левее, чем правая часть данного существа
                        (selectedX<=this.x+this.width)&& //Условие второе: правая часть объекта правее, чем левая часть данного существа
                        (selectedY+selectedHeight>=this.y)&& //Условие третье: верхняя часть объекта выше, чем нижняя часть данного существа
                        (selectedY<=this.y+this.height)){ //Условие четвертое: нижняя часть объекта ниже, чем верхняя часть данного существа
                    collisions[kCollisions] = i;
                }
            }
            else
                finished = true;
            for (int i==0; i<kCollisions; i++){
                resolveCollision(collisions[i]);
            }
        }
    }
    public void resolveCollision(Creature creature, GameObject gameObject){
        Point objectCenter = new Point(gameObject.getX() + gameObject.getWidth()/2, gameObject.getY() + gameObject.getHeight()/2);
        Point creatureCenter = new Point(creature.getX() + creature.getWidth()/2, creature.getY() + gameObject.getHeight()/2);
    }
}
