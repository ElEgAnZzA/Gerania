import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Creature {

    //Координаты (x, y):
    private double x; // x - слева направо
    private double y; //y - сверху вниз
    private double lastX;
    private double lastY;
    private int width; //Ширина и высота, направлены на увеличение соответствующих осей
    private int height;

    private int mass; //Масса, влияет изменение velocity под действием Force
    private int maxHealth=-1;
    private int health=-1;
    private long lastInteractionTime;

    Vector velocity = new Vector(0,0); //Скорость Creature
    Force[] forces = new Force[50]; //Force, приложенные к Creature.
    // Число 50 слишком большое, чтобы быть (продуктивно) занятым и слишком маленькое (надеюсь), чтобы перегрузить компьютер
    int kForces = 0; //Число элементов в списке forces

    private MovementPattern movementPattern;
    private Interaction creatureCollisionInteraction;
    private Interaction gameObjectCollisionInteraction;
    private boolean isControlled = false;
    private int index;

    Image sprite; //Спрайт Creature, пока не используется

    private boolean hasVerticalCollision = false;
    private boolean hasHorizontalCollision = false;
    private boolean diesOnCollision = false;
    public boolean isDead = false;
    public boolean flip = false;
    double timePassedModifier = 0;

    public Creature(long time, int index){
        this.x=0;
        this.y=0;
        this.width=1;
        this.height=1;
        this.mass = 1;
        Vector[] noAction = new Vector[1];
        noAction[0] = new Vector(0,0);
        this.movementPattern = new MPSequence(noAction);
        this.lastInteractionTime = time;
        this.index = index;
    }

    public Creature(double x, double y, int width, int height, int mass, long time, int index){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = mass;
        Vector[] noAction = new Vector[1];
        noAction[0] = new Vector(0,0);
        this.movementPattern = new MPSequence(noAction);
        lastInteractionTime = time;
        this.index = index;
    }

    public Creature(double x, double y, int width, int height, int mass, MovementPattern movementPattern, long time, int index){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.mass = mass;
        this.movementPattern = movementPattern;
        lastInteractionTime = time;
        this.index = index;
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
    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return maxHealth;
    }

    public Vector getVelocity() {
        return velocity;
    }
    public Force[] getForces(){
        return forces;
    }

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }
    public Interaction getCreatureCollisionInteraction(){
        return creatureCollisionInteraction;
    }
    public Interaction getGameObjectCollisionInteraction(){
        return gameObjectCollisionInteraction;
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
    public boolean isDiesOnCollision(){
        return diesOnCollision;
    }
    public int getIndex(){
        return index;
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
    public void setHealth(int health){
        if (health>maxHealth)
            this.health = maxHealth;
        else
            this.health=health;
    }
    public void setMaxHealth(int maxHealth){
        this.maxHealth = maxHealth;
        this.health = this.maxHealth;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setMovementPattern(MovementPattern movementPattern) {
        this.movementPattern = movementPattern;
    }
    public void setCreatureCollisionInteraction(Interaction creatureCollisionInteraction){
        this.creatureCollisionInteraction = creatureCollisionInteraction;
    }
    public void setGameObjectCollisionInteraction(Interaction gameObjectCollisionInteraction){
        this.gameObjectCollisionInteraction = gameObjectCollisionInteraction;
    }

    public void setIsControlled(boolean isControlled) {
        this.isControlled = isControlled;
    }
    public void setHasHorizontalCollision(boolean hasHorizontalCollision){
        this.hasHorizontalCollision = hasHorizontalCollision;
    }
    public void setHasVerticalCollision(boolean hasVerticalCollision){
        this.hasVerticalCollision = hasVerticalCollision;
    }

    public void setDiesOnCollision(boolean diesOnCollision) {
        this.diesOnCollision = diesOnCollision;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSprite(Image sprite){this.sprite=sprite;}

    //Внутренние функции:
    public void forcesPop(int num){ //Убирает элемент с индексом num из forces, сдвигает остаток массива влево, чтобы не было пропусков
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
    public void update(Main main){ //Обновляет состояние Creature:
        //[ПЕРЕПИСАТЬ ПОЯСНЯЮЩИЙ КОММЕНТАРИЙ]
        timePassedModifier = (main.endTime - main.beginningTime)/50.0;
        this.detectCreatureCollisions(main);
//        hasHorizontalCollision = false;
//        hasVerticalCollision = false;

        lastX = this.getX();
        lastY = this.getY();

        if(!isControlled&&movementPattern!=null){
//            System.out.println(this.index+" | "+ main.kCreatures);
            this.move(movementPattern.getNextAction(main, this));
        }
        if(mass!=0) {
            for (int i = 0; i < kForces; i++) {
                this.velocity = this.velocity.add(forces[i].x(1.0 / mass).x(timePassedModifier));
                if (forces[i].getTime() > 1)
                    forces[i].decreaseTime();
                else if (forces[i].getTime() > -1)
                    forcesPop(i);
                if (velocity.getR() >= Main.CREATURE_MAX_VELOCITY)
                    velocity.setR(Main.CREATURE_MAX_VELOCITY);

            }
        }
        this.velocity = detectGameObjectCollisions(main);

        if (hasHorizontalCollision||hasVerticalCollision) {
            if(gameObjectCollisionInteraction!=null)
                gameObjectCollisionInteraction.interact(main, this, -1);
            if(diesOnCollision)
                this.kill(main);
        }
        this.setX(this.getX()+ this.getVelocity().getX()*timePassedModifier);
        this.setY(this.getY()+ this.getVelocity().getY()*timePassedModifier);

        if(this.y > main.SCREEN_HEIGHT){
            if(this.isControlled==true){
                this.y -= main.SCREEN_HEIGHT;
                this.hurt(main, 10);
            }
            else{
                this.kill(main);
            }
        }
        if(this.y<-main.SCREEN_HEIGHT)
            this.kill(main);

        if(flip==false&&this.getX()<main.creatures[main.playerControlledCreatureId].getX()){
            flip = true;
        }
        if(flip==true&&this.getX()>main.creatures[main.playerControlledCreatureId].getX()){
            flip = false;
        }

        if(hasVerticalCollision)
            if(isControlled)
                this.applyForce(new Force(this.velocity.getX()*(-0.2), 0, 1));
        }
    public void applyForce(Force force){ //Добавляет новую Force в forces.
        // Если forces (т.е. там 50 элементов <=> kForces = 50) заполнен, то ничего не делает
        if (kForces<50){
            forces[kForces]=force;
            kForces++;
        }
    }
    public void applyGravity(Force gravity){
        if (kForces<50){
            forces[kForces]=new Force(gravity.getX()*mass, gravity.getY()*mass, gravity.getTime());
            kForces++;
        }
    }
    public int checkGameObjectCollision(GameObject gameObject) {//Возвращает тип столкновения: 0 - столкновения нет, 1 - горизонтальное, 2 - вертикальное, 3 - идеальное угловое
        int res = 0;
        double selectedX = gameObject.getX();
        double selectedY = gameObject.getY();
        int selectedWidth = gameObject.getWidth();
        int selectedHeight = gameObject.getHeight();
        if ((selectedX + selectedWidth > this.x) && //Условие первое: левая часть объекта левее, чем правая часть данного существа
                (selectedX < this.x + this.width) && //Условие второе: правая часть объекта правее, чем левая часть данного существа
                (selectedY + selectedHeight > this.y) && //Условие третье: верхняя часть объекта выше, чем нижняя часть данного существа
                (selectedY < this.y + this.height)) { //Условие четвертое: нижняя часть объекта ниже, чем верхняя часть данного существа
            Point objectCenter = new Point(gameObject.getX()+gameObject.getWidth()/2, gameObject.getY()+gameObject.getHeight()/2);
            Point center = new Point(this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2);
            Vector[] possibleCollisionVectors = {new Vector(objectCenter, new Point(this.x, this.y)), new Vector(objectCenter, new Point(this.x+this.width, this.y)),
                    new Vector(objectCenter, new Point(this.x+this.width, this.y+this.height)), new Vector(objectCenter, new Point(this.x, this.y+this.height))};
            Vector collisionVector = possibleCollisionVectors[0];
            for (int j = 1; j<possibleCollisionVectors.length; j++){
                if (possibleCollisionVectors[j].getRSquared()<collisionVector.getRSquared()) {
                    collisionVector.setX(possibleCollisionVectors[j].getX());
                    collisionVector.setY(possibleCollisionVectors[j].getY());
                }
            }
            if (Math.abs(collisionVector.getX()*selectedHeight)<Math.abs(collisionVector.getY()*selectedWidth)) {
                res = 2;
            }
            else if (Math.abs(collisionVector.getX()*selectedHeight)>Math.abs(collisionVector.getY()*selectedWidth)) {
                res = 1;
            }
            else {
                res = 3;
            }
        }
        return res;
    }

    public Vector detectGameObjectCollisions(Main main){
        GameObject[] collisions = new GameObject[100]; //Список всех номеров столкновений, если их больше 100 - есть проблемы куда больше, чем нехватка места в списке
        short kCollisions = 0;
        this.setX(this.getX()+this.velocity.getX()*timePassedModifier);
        this.setY(this.getY()+this.velocity.getY()*timePassedModifier);
        for (short i = 0; i<main.kGameObjects&&!isDead; i++){
            int res = checkGameObjectCollision(main.gameObjects[i]);
            if (res == 1) {//Горизонтальное столкновение
                hasHorizontalCollision = true;
                collisions[kCollisions] = main.gameObjects[i];
                kCollisions++;
            }
            else if (res>1) {//Вертикальное (2) или идеально диагональное (3) столкновения
                hasVerticalCollision = true;
                collisions[kCollisions] = main.gameObjects[i];
                kCollisions++;
            }
        }
        this.setX(this.lastX);
        this.setY(this.lastY);
        Vector finalVelocity = new Vector(this.getVelocity().getX()*timePassedModifier, this.getVelocity().getY()*timePassedModifier);
        Vector resultingVelocity;
        for (int i = 0; i<kCollisions; i++){
            resultingVelocity = resolveGameObjectCollisions(main, collisions[i]);
            if(Math.abs(resultingVelocity.getX())< Math.abs(finalVelocity.getX()))
                finalVelocity.setX(resultingVelocity.getX());
            if(Math.abs(resultingVelocity.getY())< Math.abs(finalVelocity.getY()))
                finalVelocity.setY(resultingVelocity.getY());
        }
        if (timePassedModifier>0)
            return finalVelocity.x(1/timePassedModifier);
        else
            return finalVelocity;
    }
    private Vector resolveGameObjectCollisions(Main main, GameObject gameObject){
        double beginningX = this.getX();
        double beginningY = this.getY();
        Vector partialVelocity = this.velocity.x(timePassedModifier).x(0.1);
        Vector resultingVelocity = new Vector(0,0);
        boolean foundMaxVelX = false;
        boolean foundMaxVelY = false;
        for (int i = 0; i<10; i++){
            if(!foundMaxVelX) {
                this.setX(this.getX() + partialVelocity.getX());
                if(checkGameObjectCollision(gameObject)==1) {
                    this.setX(this.getX() - partialVelocity.getX());
                    foundMaxVelX = true;
                }
                else{
                    resultingVelocity.addToThis(new Vector(partialVelocity.getX(), 0));
                }
            }
            if(!foundMaxVelY) {
                this.setY(this.getY() + partialVelocity.getY());
                if (checkGameObjectCollision(gameObject)==2) {
                    this.setY(this.getY() - partialVelocity.getY());
                    foundMaxVelY = true;
                }
                else {
                    resultingVelocity.addToThis(new Vector(partialVelocity.getY(), 0));
                }
            }
        }
        this.setX(beginningX);
        this.setY(beginningY);
        return resultingVelocity;
    }
    public void move(Vector direction){
        this.velocity.addToThis(direction);
    }
    public void detectCreatureCollisions(Main main){
        for (int i = 0; i<main.kCreatures&&!isDead; i++){
            if (!(main.creatures[i]==this)){
                double selectedX = main.creatures[i].getX();
                double selectedY = main.creatures[i].getY();
                int selectedWidth = main.creatures[i].getWidth();
                int selectedHeight = main.creatures[i].getHeight();
                if ((selectedX + selectedWidth > this.x) && //Условие первое: левая часть существа левее, чем правая часть данного существа
                        (selectedX < this.x + this.width) && //Условие второе: правая часть существа правее, чем левая часть данного существа
                        (selectedY + selectedHeight > this.y) && //Условие третье: верхняя часть существа выше, чем нижняя часть данного существа
                        (selectedY < this.y + this.height)) { //Условие четвертое: нижняя часть существа ниже, чем верхняя часть данного существа
                    resolveCreatureCollision(main, i);
                }
            }
        }
    }
    private void resolveCreatureCollision(Main main, int target){
        if(main.endTime- lastInteractionTime >250) {
            if (this.creatureCollisionInteraction != null) {
                this.creatureCollisionInteraction.interact(main, this, target);
                lastInteractionTime = main.endTime;
            }
            if (this.isDiesOnCollision())
                this.kill(main);
        }
    }

    @Override
    public String toString(){
        return "Creature at:"+this.getX()+" "+this.getY()+"; Width:"+this.getWidth()+"; Height:"+this.getHeight();
    }
    public void hurt(Main main, int damage){
        if (this.getMaxHealth()>0){
            if(damage>=this.getHealth()){
                this.kill(main);
            }
            else{
                this.setHealth(this.getHealth()-damage);
            }
        }
    }
    public void kill(Main main){
        System.out.println("Killing "+this+" index: "+this.index);
        this.isDead=true;
        for (int i =this.index; i<main.kCreatures-1;i++){
            main.creatures[i] = main.creatures[i+1];
            main.creatures[i].setIndex(main.creatures[i].getIndex()-1);
        }
        if (main.kCreatures<1000)
            main.creatures[main.kCreatures-1] = main.creatures[main.kCreatures];
        else
            main.creatures[main.kCreatures-1]=null;
        main.kCreatures--;
        if(isControlled){
            main.panel.gameOver();
        }
    }
    public void loadCreature(String name, Main main){
        File file = new File("./src/creatures/"+name);
        try{
            Scanner scanner = new Scanner(file);
            this.setWidth(scanner.nextInt());
            this.setHeight(scanner.nextInt());
            this.setMaxHealth(scanner.nextInt());
            this.setMass(scanner.nextInt());
            if(this.velocity!=null&&this.velocity.getR()!=0)
                this.velocity.setR(scanner.nextInt());
            else
                scanner.nextInt();
            this.setDiesOnCollision(scanner.nextInt()!=0); //!=0 - перевод int в boolean: 0 - false; любое другое - 1;
            this.setIsControlled(scanner.nextInt()!=0);
            double gravityCoefficient = scanner.nextInt();
            if (gravityCoefficient!=0)
                this.applyForce(new Force(main.GRAVITY.getX()*gravityCoefficient*this.mass, main.GRAVITY.getY()*gravityCoefficient*this.mass, -1));
            scanner.nextLine();
            this.loadSprite(scanner.nextLine());
            this.setMovementPattern(MovementPattern.movementPatternFromString(main, scanner.nextLine()));
            main.log.println("loaded MovementPattern "+getMovementPattern());
            this.setCreatureCollisionInteraction(Interaction.interactionFromString(scanner.nextLine()));
            main.log.println("loaded Interaction (for collision with Creatures) "+getCreatureCollisionInteraction());
            this.setGameObjectCollisionInteraction(Interaction.interactionFromString(scanner.nextLine()));
            main.log.println("loaded Interaction (for collision with GameObjects) "+getGameObjectCollisionInteraction());
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    public static Creature stringToCreature(Main main, String string, int index){
        String[] parameters = string.split(" ");
        Creature res = new Creature(Integer.valueOf(parameters[0]), Integer.valueOf(parameters[1]), 0, 0, 0, main.endTime, index);
        main.log.println("loading creature "+res);
        res.loadCreature(parameters[2], main);
        return res;
    }
}