import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

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

    Image sprite; //Спрайт Creature, пока не используется

    private boolean hasVerticalCollision = false;
    private boolean hasHorizontalCollision = false;

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

    public Image getSprite(){return sprite;}

    public boolean hasVerticalCollision() {
        return hasVerticalCollision;
    }

    public boolean hasHorizontalCollision() {
        return hasHorizontalCollision;
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
    public void setSprite(Image sprite){this.sprite=sprite;}

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
    public void loadSprite(String fileName){
        File spriteFile = new File("./src/art/"+fileName);
        try{
            sprite = ImageIO.read(spriteFile);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        this.sprite = sprite;
    }


    //Взаимодействие с Creature:
    public void update(GameObject[] gameObjects){ //Обновляет состояние Creature:
        //[ПЕРЕПИСАТЬ ПОЯСНЯЮЩИЙ КОММЕНТАРИЙ]
        hasHorizontalCollision = false;
        hasVerticalCollision = false;
        if(!isControlled&&movementPattern!=null){
            this.move(movementPattern.getNextAction(this.x, this.y, this.width, this.height));
        }
        for (int i = 0; i<kForces; i++){
            velocity.addToThis(forces[i].x(1/mass));
            if (forces[i].getTime()>1)
                forces[i].decreaseTime();
            else if (forces[i].getTime()>-1)
                forcesPop(i);
            if (velocity.getR()>=Main.CREATURE_MAX_VELOCITY)
                velocity.setR(Main.CREATURE_MAX_VELOCITY);
            this.velocity = detectCollisions(gameObjects);
        }

        lastX = this.getX();
        lastY = this.getY();
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
    public int checkCollision(GameObject[] gameObjects, int i) {//Возвращает тип столкновения: 0 - столкновения нет, 1 - горизонтальное, 2 - вертикальное, 3 - идеальное угловое
        int res = 0;
        double selectedX = gameObjects[i].getX();
        double selectedY = gameObjects[i].getY();
        int selectedWidth = gameObjects[i].getWidth();
        int selectedHeight = gameObjects[i].getHeight();
        if ((selectedX + selectedWidth > this.x) && //Условие первое: левая часть объекта левее, чем правая часть данного существа
                (selectedX < this.x + this.width) && //Условие второе: правая часть объекта правее, чем левая часть данного существа
                (selectedY + selectedHeight > this.y) && //Условие третье: верхняя часть объекта выше, чем нижняя часть данного существа
                (selectedY < this.y + this.height)) { //Условие четвертое: нижняя часть объекта ниже, чем верхняя часть данного существа
            GameObject gameObject = gameObjects[i];
            Point objectCenter = new Point(gameObject.getX()+gameObject.getWidth()/2, gameObject.getY()+gameObject.getHeight()/2);
            Point center = new Point(this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2);
            Vector[] possibleCollisionVectors = {new Vector(objectCenter, center), new Vector(center, new Point(this.x, this.y)), new Vector(center, new Point(this.x+this.width, this.y)),
                    new Vector(center, new Point(this.x+this.width, this.y+this.height)), new Vector(center, new Point(this.x, this.y+this.height))};
            Vector collisionVector = possibleCollisionVectors[0];
            for (int j = 1; j<possibleCollisionVectors.length; j++){
                if (possibleCollisionVectors[j].getRSquared()<collisionVector.getRSquared()) {
                    collisionVector.setX(possibleCollisionVectors[i].getX());
                    collisionVector.setY(possibleCollisionVectors[i].getY());
                }
            }
            collisionVector.setX(gameObject.getHeight()/(2*Math.tan(collisionVector.getTheta())));
            if (Math.abs(collisionVector.getX())<gameObject.getWidth()/2) {
                res = 2;
                this.hasVerticalCollision = true;
            }
            else if (Math.abs(collisionVector.getX())>gameObject.getWidth()/2) {
                res = 1;
                this.hasHorizontalCollision = true;
            }
            else {
                res = 3;
                this.hasVerticalCollision = true;
                this.hasHorizontalCollision = true;
            }
        }
//        System.out.println("Checking collision with Gameobject "+i+"; result: "+res);
        return res;
    }

    private Vector detectCollisions(GameObject[] gameObjects){
        boolean finished = false;
        int[] xCollisions = new int[1000]; //Список всех номеров существ, с которыми происходит столкновение
        int kXCollisions = 0;
        int[] yCollisions = new int[1000];
        int kYCollisions = 0;
        this.setX(this.getX()+this.velocity.getX());
        this.setY(this.getY()+this.velocity.getY());
        for (int i = 0; i<gameObjects.length&&finished == false; i++){
            if (gameObjects[i]!=null){
                if (checkCollision(gameObjects, i)==1) {//Горизонтальное столкновение
                    hasHorizontalCollision = true;
                    xCollisions[kXCollisions] = i;
                    kXCollisions++;
                }
                else if (checkCollision(gameObjects, i)>1) {//Вертикальное (2) или идеально диагональное (3) столкновения
                    hasVerticalCollision = true;
                    yCollisions[kYCollisions] = i;
                    kYCollisions++;
                }
            }
            else
                finished = true;
        }
        this.setX(this.getX()-this.velocity.getX());
        this.setY(this.getY()-this.velocity.getY());
        Vector finalVelocity = new Vector(this.getVelocity().getX(), this.getVelocity().getY());
        double j;
        for (int i=0; i<kXCollisions; i++){ //Горизонтальные столкновения
            j = resolveXCollisions(gameObjects, xCollisions[i]);
            if(Math.abs(j)< Math.abs(finalVelocity.getX()))
                finalVelocity.setX(j);
        }
        double k;
        for (int i=0; i<kYCollisions; i++){ //Вертикальные столкновения
            k = resolveYCollisions(gameObjects, xCollisions[i]);
            if(Math.abs(k)< Math.abs(finalVelocity.getY()))
                finalVelocity.setY(k);
        }
        return finalVelocity;
    }
    private double resolveXCollisions(GameObject[] gameObjects, int collision){
        double beginningX = this.getX();
        double velX = this.getVelocity().getX();
        double partialVelX = velX/5;
        double resVelX = 0;
        for (int i = 0; i<5; i++){
            this.setX(this.getX()+partialVelX);
            resVelX += partialVelX;
            if(checkCollision(gameObjects, collision)==1){
                this.setX(this.getX()-partialVelX);
                resVelX -= partialVelX;
                break;
            }
        }
        this.setX(beginningX);
        return resVelX;
    }
    private double resolveYCollisions(GameObject[] gameObjects, int collision){
        double beginningY = this.getY();
        double velY = this.getVelocity().getY();
        double partialVelY = velY/5;
        double resVelY = 0;
        for (int i = 0; i<5; i++){
            this.setY(this.getY()+partialVelY);
            resVelY += partialVelY;
            if(checkCollision(gameObjects, collision)==2){
                this.setY(this.getY()-partialVelY);
                resVelY -= partialVelY;
                break;
            }
        }
        this.setY(beginningY);
        return resVelY;
    }
    public void move(Vector direction){
        this.applyForce(new Force(direction.getX(), direction.getY(), 0));
    }

    @Override
    public String toString(){
        return "Creature at:"+this.getX()+" "+this.getY()+"; Width:"+this.getWidth()+"; Height:"+this.getHeight();
    }
}
