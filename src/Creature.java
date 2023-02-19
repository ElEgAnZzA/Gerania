public class Creature {
    int x; //Координаты (x, y)
    int y;
    int width; //Ширина и высота
    int height;

    int mass; //Масса, влияет изменение velocity под действием Force

    Vector velocity = new Vector(0,0); //Скорость Creature
    Force[] forces = new Force[50]; //Force, приложенные к Creature.
    // Число 50 слишком большое, чтобы быть (продуктивно) занятым и слишком маленькое (надеюсь), чтобы перегрузить компьютер
    int kForces = 0; //Число элементов в списке forces

    String sprite; //Спрайт Creature, пока не используется

    public Creature(int x, int y, int width, int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = 1;
    }

    public Creature(int x, int y, int width, int height, int mass){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = mass;
    }


    //Геттеры:
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

}
