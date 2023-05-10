import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Creature {
    //Состояние существа:
    //Координаты (x, y):
    private double x;
    private double y;
    //Прошлые координаты (x, y):
    private double lastX;
    private double lastY;

    private int health = -1; //Здоровье

    private long lastInteractionTime; //Последнее время взаимодействия существа (в формате UNIX)

    Vector velocity = new Vector(0, 0); //Скорость Creature
    Force[] forces = new Force[20]; //Force, приложенные к Creature.
    // Число 20 слишком большое, чтобы быть (продуктивно) занятым и слишком маленькое (надеюсь), чтобы перегрузить компьютер
    int kForces = 0; //Число элементов в списке forces
    private int index; //Индекс в списке Существ

    public boolean isDead = false; //Мертво ли

    public boolean flip = false; //Нужно ли поворачивать изображение (нет, если справа от персонажа игрока; да - если слева)

    double timePassedModifier = 0; //Коэффициент прошедшего между обновлениями времени (выполняет роль Δt)


    //Свойства существа:
    //Ширина и высота, направлены на увеличение соответствующих осей
    private int width;
    private int height;

    //Наличие столкновений по вертикали и горизонтали:
    private boolean hasVerticalGameObjectCollision = false;
    private boolean hasHorizontalGameObjectCollision = false;

    private int mass; //Масса, влияет изменение velocity под действием Force

    private int maxHealth = -1; //Максимальное здоровье; -1 - бессмертно

    private MovementPattern movementPattern; //Правило перемещения
    private Interaction creatureCollisionInteraction; //Взаимодействие при столкновении с Существом
    private Interaction gameObjectCollisionInteraction; //Взаимодействие при столкновении с Игровым Объектом

    private boolean isControlled = false; //Управляется ли игроком

    Image sprite; //Картинка, отображаемая на экране

    private boolean diesOnCollision = false; //Умирает ли при столкновении


    public Creature(double x, double y, int width, int height, int mass, long time, int index) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mass = mass;
        Vector[] noAction = new Vector[1];
        noAction[0] = new Vector(0, 0);
        this.movementPattern = new MPSequence(noAction);
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

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Force[] getForces() {
        return forces;
    }

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }

    public Interaction getCreatureCollisionInteraction() {
        return creatureCollisionInteraction;
    }

    public Interaction getGameObjectCollisionInteraction() {
        return gameObjectCollisionInteraction;
    }

    public boolean getIsControlled() {
        return isControlled;
    }

    public Image getSprite() {
        return sprite;
    }

    public boolean hasVerticalCollision() {
        return hasVerticalGameObjectCollision;
    }

    public boolean hasHorizontalCollision() {
        return hasHorizontalGameObjectCollision;
    }

    public boolean isDiesOnCollision() {
        return diesOnCollision;
    }

    public int getIndex() {
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

    public void setHealth(int health) {//Изменяет здоровье Существа (но не поднимает его выше максимума):
        if (health > maxHealth)
            this.health = maxHealth;
        else
            this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = this.maxHealth;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setMovementPattern(MovementPattern movementPattern) {
        this.movementPattern = movementPattern;
    }

    public void setCreatureCollisionInteraction(Interaction creatureCollisionInteraction) {
        this.creatureCollisionInteraction = creatureCollisionInteraction;
    }

    public void setGameObjectCollisionInteraction(Interaction gameObjectCollisionInteraction) {
        this.gameObjectCollisionInteraction = gameObjectCollisionInteraction;
    }

    public void setIsControlled(boolean isControlled) {
        this.isControlled = isControlled;
    }

    public void setHasHorizontalGameObjectCollision(boolean hasHorizontalGameObjectCollision) {
        this.hasHorizontalGameObjectCollision = hasHorizontalGameObjectCollision;
    }

    public void setHasVerticalGameObjectCollision(boolean hasVerticalGameObjectCollision) {
        this.hasVerticalGameObjectCollision = hasVerticalGameObjectCollision;
    }

    public void setDiesOnCollision(boolean diesOnCollision) {
        this.diesOnCollision = diesOnCollision;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public void setLastInteractionTime(long time) {
        this.lastInteractionTime = time;
    }

    //Внутренние функции:
    public void forcesPop(int index) { //Убирает элемент с индексом index из forces, сдвигает остаток массива влево, чтобы не было пропусков:
        if (index < kForces) {
            for (int i = index + 1; i < kForces; i++) {
                forces[i - 1] = forces[i];
            }
            forces[kForces - 1] = null;
            kForces--;
        }
    }

    public void loadSprite(String fileName) { //Загружает изображение из файла (и устанавливает его):
        File spriteFile = new File("./src/art/" + fileName);
        try {
            sprite = ImageIO.read(spriteFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(MainGame mainGame) { //Обновляет состояние Creature:
        timePassedModifier = (mainGame.endTime - mainGame.beginningTime) / 50.0; //Обрабатывает количество времени, которое прошло с прошлого обновления

        lastX = this.getX();
        lastY = this.getY();

        updateVelocity(mainGame);

        doCollisionInteractions(mainGame);

        //Передвигаем Существо
        this.setX(this.getX() + this.getVelocity().getX() * timePassedModifier);
        this.setY(this.getY() + this.getVelocity().getY() * timePassedModifier);

        checkOutOfBounds(mainGame);

        checkFlip(mainGame);

        if (hasVerticalGameObjectCollision) //Тормозим персонажа игрока, если тот стоит на земле
            if (isControlled)
                this.applyForce(new Force(this.velocity.getX() * (-0.2), 0, 1));
    }

    private void checkFlip(MainGame mainGame) { //Если слева от персонажа игрока, то изображение отзеркалено
        if (!flip && this.getX() < mainGame.creatures[mainGame.playerControlledCreatureId].getX()) {
            flip = true;
        }
        if (flip && this.getX() > mainGame.creatures[mainGame.playerControlledCreatureId].getX()) {
            flip = false;
        }
    }

    private void doCollisionInteractions(MainGame mainGame) {
        this.detectCreatureCollisions(mainGame); //Находим и обрабатываем столкновения с Существами

        if (hasHorizontalGameObjectCollision || hasVerticalGameObjectCollision) {//Обрабатываем столкновения
            if (gameObjectCollisionInteraction != null)
                gameObjectCollisionInteraction.interact(mainGame, this, -1);
            if (diesOnCollision)
                this.kill(mainGame);
        }
    }

    private void checkOutOfBounds(MainGame mainGame) {
        //Проверяет, попало ли существо за границы экрана по вертикали
        if (this.y <0) {
            if (this.isControlled) {//Если упало вниз и управляется игроком, то телепортируем вверх и наносим урон
                this.y = MainGame.SCREEN_HEIGHT;
                this.hurt(mainGame, 10);
            }
            else { //Если упало вниз и не управляется игроком, то убиваем
                this.kill(mainGame);
            }
        }
        if (this.y > 2*MainGame.SCREEN_HEIGHT) //Если слишком высоко, то убиваем
            this.kill(mainGame);
    }

    private void updateVelocity(MainGame mainGame) {
        if (!isControlled && movementPattern != null) {//Если нужно, двигаемся в соответствии с правилом перемещения
            this.move(movementPattern.getNextAction(mainGame, this));
        }

        if (mass != 0) {//Прикладываем к Существу силы, обрабатываем их действие и обновляем их состояние:
            for (int i = 0; i < kForces; i++) {
                this.velocity = this.velocity.add(forces[i].x(1.0 / mass).x(timePassedModifier));
                if (forces[i].getTime() > 1)
                    forces[i].decreaseTime();

                else if (forces[i].getTime() > -1)
                    forcesPop(i);
            }
        }
        if (velocity.getR() >= MainGame.CREATURE_MAX_VELOCITY) //Ограничиваем скорость существа максимальной
            velocity.setR(MainGame.CREATURE_MAX_VELOCITY);

        this.velocity = detectGameObjectCollisions(mainGame); //Ограничиваем скорость существа положением игровых объектов
    }

    public void applyForce(Force force) { //Добавляет новую Force в forces.
        // Если forces (т.е. там 50 элементов <=> kForces = 50) заполнен, то ничего не делает
        if (kForces < 50) {
            forces[kForces] = force;
            kForces++;
        }
    }

    public void applyGravity(Force gravity) {
        if (kForces < 50) {
            forces[kForces] = new Force(gravity.getX() * mass, gravity.getY() * mass, gravity.getTime());
            kForces++;
        }
    }

    public int checkGameObjectCollision(GameObject gameObject) {//Возвращает тип столкновения: 0 - столкновения нет, 1 - горизонтальное, 2 - вертикальное, 3 - идеальное угловое
        int res = 0;

        //Свойства выбранного Игрового Объекта:
        double selectedX = gameObject.getX();
        double selectedY = gameObject.getY();
        int selectedWidth = gameObject.getWidth();
        int selectedHeight = gameObject.getHeight();

        if ((selectedX + selectedWidth > this.x) && //Условие первое: левая часть объекта левее, чем правая часть данного существа
                (selectedX < this.x + this.width) && //Условие второе: правая часть объекта правее, чем левая часть данного существа
                (selectedY + selectedHeight > this.y) && //Условие третье: верхняя часть объекта выше, чем нижняя часть данного существа
                (selectedY < this.y + this.height)) { //Условие четвертое: нижняя часть объекта ниже, чем верхняя часть данного существа

            Point objectCenter = new Point(gameObject.getX() + gameObject.getWidth() / 2, gameObject.getY() + gameObject.getHeight() / 2);
            Point center = new Point(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2); //Центр данного Существа

            //Проверяем тип столкновения:
            //Находим кратчайший вектор (выбираем между векторами от центра Объекта к четырем вершинам границы Существа и его центру)
            //Затем смотрим, лежит ли он выше или ниже диагонали Объекта (выше - вертикальное, ниже - горизонтальное, на диагонали - идеальное угловое
            //Визуальное пояснение - https://www.geogebra.org/calculator/tf9hfdzh
            Vector[] possibleCollisionVectors = {new Vector(objectCenter, center), new Vector(objectCenter, new Point(this.x, this.y)), new Vector(objectCenter, new Point(this.x + this.width, this.y)),
                    new Vector(objectCenter, new Point(this.x + this.width, this.y + this.height)), new Vector(objectCenter, new Point(this.x, this.y + this.height))};
            Vector collisionVector = possibleCollisionVectors[0];
            for (int j = 1; j < possibleCollisionVectors.length; j++) {
                if (possibleCollisionVectors[j].getRSquared() < collisionVector.getRSquared()) {
                    collisionVector.setX(possibleCollisionVectors[j].getX());
                    collisionVector.setY(possibleCollisionVectors[j].getY());
                }
            }
            if (Math.abs(collisionVector.getX() * selectedHeight) < Math.abs(collisionVector.getY() * selectedWidth)) {
                res = 2;
            } else if (Math.abs(collisionVector.getX() * selectedHeight) > Math.abs(collisionVector.getY() * selectedWidth)) {
                res = 1;
            } else {
                res = 3;
            }
        }
        return res;
    }

    public Vector detectGameObjectCollisions(MainGame mainGame) {
        GameObject[] collisions = new GameObject[20]; //Список всех номеров столкновений, если их больше 20 - есть проблемы куда больше, чем нехватка места в списке
        short kCollisions = 0;

        //Сдвигаем существо туда, куда оно должно попасть по скорости:
        this.setX(this.getX() + this.velocity.getX() * timePassedModifier);
        this.setY(this.getY() + this.velocity.getY() * timePassedModifier);

        //Сбрасываем информацию о коллизиях:
        this.hasVerticalGameObjectCollision = false;
        this.hasHorizontalGameObjectCollision = false;

        //Проверяем каждый GameObject на столкновение и заносим их в список, если столкновение есть:
        for (short i = 0; i < mainGame.kGameObjects && !isDead; i++) {
            int res = checkGameObjectCollision(mainGame.gameObjects[i]);
            if (res == 1) {//Горизонтальное столкновение
                hasHorizontalGameObjectCollision = true;
                collisions[kCollisions] = mainGame.gameObjects[i];
                kCollisions++;
            } else if (res > 1) {//Вертикальное (2) или идеально диагональное (3) столкновения
                hasVerticalGameObjectCollision = true;
                collisions[kCollisions] = mainGame.gameObjects[i];
                kCollisions++;
            }
        }
        //Возвращаемся на изначальные координаты
        this.setX(this.lastX);
        this.setY(this.lastY);
        //Начинаем подбирать такой вектор скорости, при котором столкновения не будет:
        Vector finalVelocity = new Vector(this.getVelocity().getX() * timePassedModifier, this.getVelocity().getY() * timePassedModifier);
        Vector resultingVelocity;
        //Перебираем все объекты, с которыми можем столкнуться и для каждого подбираем максимальные по модулю координаты вектора скорости, при которых все ок
        //Если они меньше, чем те, что у finalVelocity, уменьшаем координаты finalVelocity
        for (int i = 0; i < kCollisions; i++) {
            resultingVelocity = resolveGameObjectCollisions(mainGame, collisions[i]);
            if (Math.abs(resultingVelocity.getX()) < Math.abs(finalVelocity.getX()))
                finalVelocity.setX(resultingVelocity.getX());
            if (Math.abs(resultingVelocity.getY()) < Math.abs(finalVelocity.getY()))
                finalVelocity.setY(resultingVelocity.getY());
        }

        //Вовзращаем полученный вектор скорости
        if (timePassedModifier > 0)
            return finalVelocity.x(1 / timePassedModifier);
        else
            return finalVelocity;
    }

    private Vector resolveGameObjectCollisions(MainGame mainGame, GameObject gameObject) {
        double beginningX = this.getX();
        double beginningY = this.getY();
        Vector partialVelocity = this.velocity.x(timePassedModifier).x(0.1);
        Vector resultingVelocity = new Vector(0, 0);
        boolean foundMaxVelX = false;
        boolean foundMaxVelY = false;
        for (int i = 0; i < 10; i++) {
            if (!foundMaxVelX) {
                this.setX(this.getX() + partialVelocity.getX());
                if (checkGameObjectCollision(gameObject) == 1) {
                    this.setX(this.getX() - partialVelocity.getX());
                    foundMaxVelX = true;
                } else {
                    resultingVelocity.addToThis(new Vector(partialVelocity.getX(), 0));
                }
            }
            if (!foundMaxVelY) {
                this.setY(this.getY() + partialVelocity.getY());
                if (checkGameObjectCollision(gameObject) == 2) {
                    this.setY(this.getY() - partialVelocity.getY());
                    foundMaxVelY = true;
                } else {
                    resultingVelocity.addToThis(new Vector(partialVelocity.getY(), 0));
                }
            }
        }
        this.setX(beginningX);
        this.setY(beginningY);
        return resultingVelocity;
    }

    public void move(Vector direction) {
        this.velocity.addToThis(direction);
    }

    public void detectCreatureCollisions(MainGame mainGame) {
        for (int i = 0; i < mainGame.kCreatures && !isDead; i++) {
            if (!(mainGame.creatures[i] == this)) {
                double selectedX = mainGame.creatures[i].getX();
                double selectedY = mainGame.creatures[i].getY();
                int selectedWidth = mainGame.creatures[i].getWidth();
                int selectedHeight = mainGame.creatures[i].getHeight();
                if ((selectedX + selectedWidth > this.x) && //Условие первое: левая часть существа левее, чем правая часть данного существа
                        (selectedX < this.x + this.width) && //Условие второе: правая часть существа правее, чем левая часть данного существа
                        (selectedY + selectedHeight > this.y) && //Условие третье: верхняя часть существа выше, чем нижняя часть данного существа
                        (selectedY < this.y + this.height)) { //Условие четвертое: нижняя часть существа ниже, чем верхняя часть данного существа
                    resolveCreatureCollision(mainGame, i);
                }
            }
        }
    }

    private void resolveCreatureCollision(MainGame mainGame, int target) {
        if (mainGame.endTime - lastInteractionTime > 500) {
            if (this.creatureCollisionInteraction != null) {
                this.creatureCollisionInteraction.interact(mainGame, this, target);
                lastInteractionTime = mainGame.endTime;
            }
            if (this.isDiesOnCollision())
                this.kill(mainGame);
        }
    }

    @Override
    public String toString() {
        return "Creature at:" + this.getX() + " " + this.getY() + "; Width:" + this.getWidth() + "; Height:" + this.getHeight();
    }

    public void hurt(MainGame mainGame, int damage) {
        if (this.getMaxHealth() > 0) {
            if (damage >= this.getHealth()) {
                this.kill(mainGame);
            } else {
                this.setHealth(this.getHealth() - damage);
            }
        }
    }

    public void kill(MainGame mainGame) {
        System.out.println("Killing " + this + " index: " + this.index);
        this.isDead = true;
        for (int i = this.index; i < mainGame.kCreatures - 1; i++) {
            mainGame.creatures[i] = mainGame.creatures[i + 1];
            mainGame.creatures[i].setIndex(mainGame.creatures[i].getIndex() - 1);
        }
        if (mainGame.kCreatures < 1000)
            mainGame.creatures[mainGame.kCreatures - 1] = mainGame.creatures[mainGame.kCreatures];
        else
            mainGame.creatures[mainGame.kCreatures - 1] = null;
        mainGame.kCreatures--;
        if (isControlled) {
            mainGame.panel.gameOver();
        }
    }

    public void loadCreature(String name, MainGame mainGame) {
        File file = new File("./src/creatures/" + name);
        try {
            Scanner scanner = new Scanner(file);
            this.setWidth(scanner.nextInt());
            this.setHeight(scanner.nextInt());
            this.setMaxHealth(scanner.nextInt());
            this.setMass(scanner.nextInt());
            if (this.velocity != null && this.velocity.getR() != 0)
                this.velocity.setR(scanner.nextInt());
            else
                scanner.nextInt();
            this.setDiesOnCollision(scanner.nextInt() != 0); //!=0 - перевод int в boolean: 0 - false; любое другое - 1;
            this.setIsControlled(scanner.nextInt() != 0);
            double gravityCoefficient = scanner.nextInt();
            if (gravityCoefficient != 0)
                this.applyForce(new Force(MainGame.GRAVITY.getX() * gravityCoefficient * this.mass, MainGame.GRAVITY.getY() * gravityCoefficient * this.mass, -1));
            scanner.nextLine();
            this.loadSprite(scanner.nextLine());
            this.setMovementPattern(MovementPattern.movementPatternFromString(mainGame, scanner.nextLine()));
            mainGame.log.println("loaded MovementPattern " + getMovementPattern());
            this.setCreatureCollisionInteraction(Interaction.interactionFromString(scanner.nextLine()));
            mainGame.log.println("loaded Interaction (for collision with Creatures) " + getCreatureCollisionInteraction());
            this.setGameObjectCollisionInteraction(Interaction.interactionFromString(scanner.nextLine()));
            mainGame.log.println("loaded Interaction (for collision with GameObjects) " + getGameObjectCollisionInteraction());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Creature stringToCreature(MainGame mainGame, String string, int index) {
        String[] parameters = string.split(" ");
        Creature res = new Creature(Integer.valueOf(parameters[0]), Integer.valueOf(parameters[1]), 0, 0, 0, mainGame.endTime, index);
        mainGame.log.println("loading creature " + res);
        res.loadCreature(parameters[2], mainGame);
        return res;
    }
}