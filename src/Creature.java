public class Creature {

    //Координаты (x, y):
    private double x; // x - слева направо
    private double y; //y - сверху вниз
    private double lastX;
    private double lastY;
    int width; //Ширина и высота, направлены на увеличение соответствующих осей
    int height;

    int mass; //Масса, влияет изменение velocity под действием Force

    Vector velocity = new Vector(0,0); //Скорость Creature
    Force[] forces = new Force[50]; //Force, приложенные к Creature.
    // Число 50 слишком большое, чтобы быть (продуктивно) занятым и слишком маленькое (надеюсь), чтобы перегрузить компьютер
    int kForces = 0; //Число элементов в списке forces

    private MovementPattern movementPattern;
    private boolean isControlled = false;

    String sprite; //Спрайт Creature, пока не используется

    public Creature(){
        this.x=0;
        this.y=0;
        this.width=1;
        this.height=1;
        this.mass = 1;
        Vector[] noAction = new Vector[1];
        noAction[0] = new Vector(0,0);
        this.movementPattern = new MPSequence(noAction);
    }

    public Creature(double x, double y, int width, int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = 1;
        Vector[] noAction = new Vector[1];
        noAction[0] = new Vector(0,0);
        this.movementPattern = new MPSequence(noAction);
    }

    public Creature(double x, double y, int width, int height, int mass){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = mass;
        Vector[] noAction = new Vector[1];
        noAction[0] = new Vector(0,0);
        this.movementPattern = new MPSequence(noAction);
    }

    public Creature(double x, double y, int width, int height, MovementPattern movementPattern){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = 1;
        this.movementPattern = movementPattern;
    }

    public Creature(double x, double y, int width, int height, int mass, MovementPattern movementPattern){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = mass;
        this.movementPattern = movementPattern;
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

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }

    public boolean getIsControlled() {
        return isControlled;
    }

    //Сеттеры:
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
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

    public void setMovementPattern(MovementPattern movementPattern) {
        this.movementPattern = movementPattern;
    }

    public void setIsControlled(boolean isControlled) {
        this.isControlled = isControlled;
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
    public void update(GameObject[] gameObjects){ //Обновляет состояние Creature:
        //1. Прикладывает силы перемещения от паттерна поведения
        //2. Прикладывает все Force из forces к Velocity
        //3. Обновляет все Force из forces, удаляя те, time которых меньше 1 и больше -1, а также удаляя и создавая моментные (time = 0) противовесы силам, у которых time = -2
        //4. Меняет координаты (x, y) Creature в соответствии с velocity

        if(!isControlled&&movementPattern!=null){
            this.move(movementPattern.getNextAction(this.x, this.y, this.width, this.height));
        }
        for (int i = 0; i<kForces; i++){
            velocity.addToThis(forces[i].x(1/mass));
            if (forces[i].getTime()>1)
                forces[i].decreaseTime();
            else if (forces[i].getTime()>-1)
                forcesPop(i);
        }
        if (velocity.getR()>=Main.CREATURE_MAX_VELOCITY)
            velocity.setR(Main.CREATURE_MAX_VELOCITY);

        lastX = this.getX();
        lastY = this.getY();
        this.velocity = detectCollisions(gameObjects);
        x += this.velocity.getX();
        y += this.velocity.getY();
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
    public boolean checkCollision(GameObject[] gameObjects, int i){
        boolean res = false;
        double selectedX = gameObjects[i].getX();
        double selectedY = gameObjects[i].getY();
        int selectedWidth = gameObjects[i].getWidth();
        int selectedHeight = gameObjects[i].getHeight();
        if ((selectedX+selectedWidth>this.x)&& //Условие первое: левая часть объекта левее, чем правая часть данного существа
                (selectedX<this.x+this.width)&& //Условие второе: правая часть объекта правее, чем левая часть данного существа
                (selectedY+selectedHeight>this.y)&& //Условие третье: верхняя часть объекта выше, чем нижняя часть данного существа
                (selectedY<this.y+this.height)){ //Условие четвертое: нижняя часть объекта ниже, чем верхняя часть данного существа
            res = true;
        }
//        System.out.println("Checking collision with Gameobject "+i+"; result: "+res);
        return res;
    }

    private Vector detectCollisions(GameObject[] gameObjects){
        boolean finished = false;
        int[] collisions = new int[1000]; //Список всех номеров существ, с которыми происходит столкновение
        int kCollisions = 0;
        for (int i = 0; i<gameObjects.length&&finished == false; i++){
            if (gameObjects[i]!=null){
                if (checkCollision(gameObjects, i)) {
                    collisions[kCollisions] = i;
                    kCollisions++;
                }
            }
            else
                finished = true;
        }
        Vector finalVelocity = new Vector(this.getVelocity().getX(), this.getVelocity().getY());
        Vector j;
        for (int i=0; i<kCollisions; i++){
            j = resolveCollisions(gameObjects, collisions[i]);
            if(Math.abs(j.getX())< Math.abs(finalVelocity.getX()))
                finalVelocity.setX(j.getX());
            if(Math.abs(j.getY())< Math.abs(finalVelocity.getY()))
                finalVelocity.setY(j.getY());
        }
        return finalVelocity;
    }
    private Vector resolveCollisions(GameObject[] gameObjects, int collision){
        double beginningX = this.getX();
        double beginningY = this.getY();
        Vector partialVelocity = this.getVelocity().x(1/5);
        Vector resVelocity = new Vector(0,0);
        for (int i = 0; i<5; i++){
            this.setX(this.getX()+partialVelocity.getX());
            resVelocity.setX(resVelocity.getX()+partialVelocity.getX());
            if(checkCollision(gameObjects, collision)){
                this.setX(this.getX()-partialVelocity.getX());
                resVelocity.setX(resVelocity.getX()-partialVelocity.getX());
                break;
            }
        }
        this.setX(beginningX);
        for (int i = 0; i<5; i++){
            this.setY(this.getY()+partialVelocity.getY());
            resVelocity.setY(resVelocity.getY()+partialVelocity.getY());
            if(checkCollision(gameObjects, collision)){
                this.setY(this.getY()-partialVelocity.getY());
                resVelocity.setY(resVelocity.getY()-partialVelocity.getY());
                break;
            }
        }
        this.setY(beginningY);
        return resVelocity;
    }

    public void move(Vector direction){
        this.applyForce(new Force(direction.getX(), direction.getY(), 0));
    }

    @Override
    public String toString(){
        return "Creature at:"+this.getX()+" "+this.getY()+"; Width:"+this.getWidth()+"; Height:"+this.getHeight();
    }
}
