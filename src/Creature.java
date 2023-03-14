public class Creature {
    private double x; //Координаты (x, y)
    private double y;
    private double lastX;
    private double lastY;
    int width; //Ширина и высота
    int height;

    int mass; //Масса, влияет изменение velocity под действием Force

    Vector velocity = new Vector(0,0); //Скорость Creature
    Force[] forces = new Force[50]; //Force, приложенные к Creature.
    // Число 50 слишком большое, чтобы быть (продуктивно) занятым и слишком маленькое (надеюсь), чтобы перегрузить компьютер
    int kForces = 0; //Число элементов в списке forces

    String sprite; //Спрайт Creature, пока не используется

    public Creature(){
        this.x=0;
        this.y=0;
        this.width=1;
        this.height=1;
        this.mass = 1;
    }

    public Creature(double x, double y, int width, int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = 1;
    }

    public Creature(double x, double y, int width, int height, int mass){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = mass;
    }


    //Геттеры:
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMass() {
        return mass;
    }

    public Vector getVelocity() {
        return velocity;
    }


    //Сеттеры:
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setLastX(double lastX) {
        this.lastX = lastX;
    }

    public void setLastY(double lastY) {
        this.lastY = lastY;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }


    //Внутренние функции:
    private void forcesPop(int num){ //Убирает элемент с индексом num из forces, сдвигает остаток массива влево, чтобы не было пропусков
        if (num<kForces){
            for (int i = num+1; i<kForces; i++){
                forces[i-1]=forces[i];
            }
            forces [kForces-1] = null;
            kForces--;
        }
    }


    //Взаимодействие с Creature:
    public void update(){ //Обновляет состояние Creature:
        //1. Прикладывает все Force из forces к Velocity
        //2. Обновляет все Force из forces, удаляя те, time которых меньше 1 и больше -1
        //2. Меняет координаты (x, y) Creature в соответствии с velocity
        for (int i = 0; i<kForces; i++){
            velocity.addToThis(forces[i].x(1/mass));
            if (forces[i].getTime()>1)
                forces[i].decreaseTime();
            else if (forces[i].getTime()>-1)
                forcesPop(i);

        }
        lastX = x;
        lastY = y;
        x += velocity.getX();
        y += velocity.getY();
    }
    public void applyForce(Force force){ //Добавляет новую Force в forces.
        // Если forces (т.е. там 50 элементов <=> kForces = 50) заполнен, то ничего не делает
        if (kForces<50){
            forces[kForces]=force;
            kForces++;
        }
    }

    public Point[] getBounds(){ //Выдает массив из двух точек: [(x, y), (x+width, y+height)] - граници объекта
        Point[] a = new Point[2];
        a[0] = new Point(this.getX(), this.getY());
        a[1] = new Point(this.getX()+this.getWidth(), this.getY()+this.getHeight());
        return a;
    }
    private boolean checkCollision(GameObject[] gameObjects, int i){
        boolean res = false;
        double selectedX = gameObjects[i].getX();
        double selectedY = gameObjects[i].getY();
        int selectedWidth = gameObjects[i].getWidth();
        int selectedHeight = gameObjects[i].getHeight();
        if ((selectedX+selectedWidth>=this.x)&& //Условие первое: левая часть объекта левее, чем правая часть данного существа
                (selectedX<=this.x+this.width)&& //Условие второе: правая часть объекта правее, чем левая часть данного существа
                (selectedY+selectedHeight>=this.y)&& //Условие третье: верхняя часть объекта выше, чем нижняя часть данного существа
                (selectedY<=this.y+this.height)){ //Условие четвертое: нижняя часть объекта ниже, чем верхняя часть данного существа
            res = true;
        }
        return res;
    }

    private void detectCollisions(GameObject[] gameObjects){
        boolean finished = false;
        int[] collisions = new int[1000]; //Список всех номеров существ, с которыми происходит столкновение
        int kCollisions = 0;
        for (int i = 0; i<gameObjects.length&&finished == false; i++){
            if (gameObjects[i]!=null){
                if (checkCollision(gameObjects, i))
                    collisions[kCollisions] = i;
            }
            else
                finished = true;
        }
        for (int i=0; i<kCollisions; i++){
            resolveCollisions(collisions[i]);
        }
    }
    private void resolveCollisions(int collision){
        Vector partialVelocity = this.velocity.x(1/4);

        for (int i = 0; i<4; i++){

        }
    }
}
