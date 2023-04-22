public class Spell {
    private final int type;
    public Spell(int type){
        this.type = type;
    }
    public void cast(int casterIndex, Point click, MainGame mainGame){
        //Колдует заклинание и возвращает 0 - не нужно менять ни одну из переменных, 1 - нужно увеличить kGameObjects, 2 - нужно увеличить kCreatures
        double casterX = mainGame.creatures[casterIndex].getX()+ mainGame.creatures[casterIndex].getWidth()/2;
        double casterY = mainGame.creatures[casterIndex].getY()+ mainGame.creatures[casterIndex].getHeight()/2;
        Point realClick = new Point(click.getX(), MainGame.SCREEN_HEIGHT -click.getY());
        Point casterPoint = new Point(casterX, casterY);
        Point targetPoint;
        Vector projectileVel = new Vector(new Point(casterX, casterY), new Point(mainGame.cameraX+realClick.getX(), mainGame.cameraY+realClick.getY()));
        switch (type){
            case (-4)://Ультразвук летучей мыши
                mainGame.creatures[mainGame.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, mainGame.endTime, mainGame.kCreatures);
                mainGame.creatures[mainGame.kCreatures].setVelocity(projectileVel);
                mainGame.creatures[mainGame.kCreatures].loadCreature("batSonicBall.txt", mainGame);
                mainGame.kCreatures++;
                break;
            case (-3)://Ледяной шар босса
                mainGame.creatures[mainGame.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, mainGame.endTime, mainGame.kCreatures);
                mainGame.creatures[mainGame.kCreatures].setVelocity(projectileVel);
                mainGame.creatures[mainGame.kCreatures].loadCreature("iceBall.txt", mainGame);
                mainGame.kCreatures++;
                break;
            case (-2)://Молния босса
                mainGame.creatures[mainGame.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, mainGame.endTime, mainGame.kCreatures);
                mainGame.creatures[mainGame.kCreatures].setVelocity(projectileVel);
                mainGame.creatures[mainGame.kCreatures].loadCreature("lightningBall.txt", mainGame);
                mainGame.kCreatures++;
                break;
            case (-1)://Огненный шар босса
                mainGame.creatures[mainGame.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, mainGame.endTime, mainGame.kCreatures);
                mainGame.creatures[mainGame.kCreatures].setVelocity(projectileVel);
                mainGame.creatures[mainGame.kCreatures].loadCreature("bossFireball.txt", mainGame);
                mainGame.kCreatures++;
                break;
            case (1): //Огненный шар
                mainGame.creatures[mainGame.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, mainGame.endTime, mainGame.kCreatures);
                mainGame.creatures[mainGame.kCreatures].setVelocity(projectileVel);
                mainGame.creatures[mainGame.kCreatures].loadCreature("fireball.txt", mainGame);
                mainGame.kCreatures++;
                break;
            case (2): //Лечение (на 15 хп)
                mainGame.creatures[casterIndex].setHealth(mainGame.creatures[casterIndex].getHealth()+15);
                break;
            case (3): //Отталкивание всех вблизи

                Vector push;
                for (int i = 0; i< mainGame.kCreatures; i++){
                    targetPoint = new Point(mainGame.creatures[i].getX()+ mainGame.creatures[i].getWidth()/2,
                            mainGame.creatures[i].getY()+ mainGame.creatures[i].getHealth()/2);
                    push = new Vector(casterPoint, targetPoint);
                    if (push.getR()<=300&&i!=casterIndex&& mainGame.creatures[i].getMass()!=0) {
                        push.setR(20);
                        mainGame.creatures[i].applyForce(new Force(push, 2));
                        mainGame.creatures[i].applyForce(new Force(push.x(-0.5), 4));
                    }
                    System.out.println("Pushing "+ mainGame.creatures[i]+" in "+push);
                }
                break;
            case (4): //Самонаводящийся снаряд
                if(mainGame.kCreatures>1) {
                    short closestIndex = 1000; //Индекс, который точно не может быть у элемента списка creatures, т.к. там 1000 значений
                    Point closestPoint = new Point(0, 0);
                    for (int i = 0; i < mainGame.kCreatures; i++) {
                        if (i != casterIndex && mainGame.creatures[i].getMass() != -1) {
                            targetPoint = new Point(mainGame.creatures[i].getX() + mainGame.creatures[i].getWidth() / 2,
                                    mainGame.creatures[i].getY() + mainGame.creatures[i].getHealth() / 2);
                            if (closestIndex >= 1000 || casterPoint.distanceToSquared(targetPoint) < casterPoint.distanceToSquared(closestPoint)) {
                                closestIndex = (short) i;
                                closestPoint = new Point(mainGame.creatures[closestIndex].getX() + mainGame.creatures[closestIndex].getWidth() / 2,
                                        mainGame.creatures[closestIndex].getY() + mainGame.creatures[closestIndex].getHealth() / 2);
                            }
                        }
                    }
                    mainGame.creatures[mainGame.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, mainGame.endTime, mainGame.kCreatures);
                    mainGame.creatures[mainGame.kCreatures].setVelocity(projectileVel);
                    mainGame.creatures[mainGame.kCreatures].loadCreature("homingBall.txt", mainGame);
                    mainGame.creatures[mainGame.kCreatures].setMovementPattern(new MPFollow(mainGame.creatures[closestIndex], 15));
                    mainGame.kCreatures++;
                }
                break;
            case (5): //Магический блок на 30 хп
                targetPoint = new Point(realClick.getX()-32, realClick.getY()-32);
                mainGame.creatures[mainGame.kCreatures] = new Creature(targetPoint.getX(), targetPoint.getY(), 1, 1, 1, mainGame.endTime, mainGame.kCreatures);
                mainGame.creatures[mainGame.kCreatures].loadCreature("magicBrick.txt", mainGame);
                mainGame.kCreatures++;
                break;
            default:
        }
    }
    public int spellCost(){
        switch (type){
            case (1): //Огненный шар
                return 10;
            case (2): //Лечение (на 15 хп)
                return 80;
            case (3): //Отталкивание всех вблизи
                return 20;
            case (4):
                return 20;
            case (5): //Магический блок на 30 хп
                return 30;
            default:
                return 0;
        }
    }
    public String toString(){
        return "Spell with type "+type;
    }
}

