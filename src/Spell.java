public class Spell {
    private int type;
    public Spell(int type){
        this.type = type;
    }
    public void cast(int casterIndex, Point click, Main main){
        //Колдует заклинание и возвращает 0 - не нужно менять ни одну из переменных, 1 - нужно увеличить kGameObjects, 2 - нужно увеличить kCreatures
        double casterX = main.creatures[casterIndex].getX()+main.creatures[casterIndex].getWidth()/2;
        double casterY = main.creatures[casterIndex].getY()+main.creatures[casterIndex].getHeight()/2;
        Point realClick = new Point(click.getX(), main.SCREEN_HEIGHT-click.getY());
        Point casterPoint = new Point(casterX, casterY);
        Point targetPoint;
        Vector projectileVel = new Vector(new Point(casterX, casterY), new Point(main.cameraX+realClick.getX(), main.cameraY+realClick.getY()));
        switch (type){
            case (-4)://Ультразвук летучей мыши
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                main.creatures[main.kCreatures].setVelocity(projectileVel);
                main.creatures[main.kCreatures].loadCreature("batSonicBall.txt", main);
                main.kCreatures++;
                break;
            case (-3)://Ледяной шар босса
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                main.creatures[main.kCreatures].setVelocity(projectileVel);
                main.creatures[main.kCreatures].loadCreature("iceBall.txt", main);
                main.kCreatures++;
                break;
            case (-2)://Молния босса
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                main.creatures[main.kCreatures].setVelocity(projectileVel);
                main.creatures[main.kCreatures].loadCreature("lightningBall.txt", main);
                main.kCreatures++;
                break;
            case (-1)://Огненный шар босса
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                main.creatures[main.kCreatures].setVelocity(projectileVel);
                main.creatures[main.kCreatures].loadCreature("bossFireball.txt", main);
                main.kCreatures++;
                break;
            case (1): //Огненный шар
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                main.creatures[main.kCreatures].setVelocity(projectileVel);
                main.creatures[main.kCreatures].loadCreature("fireball.txt", main);
                main.kCreatures++;
                break;
            case (2): //Лечение (на 15 хп)
                main.creatures[casterIndex].setHealth(main.creatures[casterIndex].getHealth()+15);
                break;
            case (3): //Отталкивание всех вблизи

                Vector push;
                for (int i = 0; i<main.kCreatures; i++){
                    targetPoint = new Point(main.creatures[i].getX()+main.creatures[i].getWidth()/2,
                            main.creatures[i].getY()+main.creatures[i].getHealth()/2);
                    push = new Vector(casterPoint, targetPoint);
                    if (push.getR()<=50&&i!=casterIndex&&main.creatures[i].getMass()!=0){
                        push.setR(50);
                    }
                    else if (push.getR()<=250&&i!=casterIndex&&main.creatures[i].getMass()!=0){
                        push.setR(12500/(push.getR()*push.getR()));
                        main.creatures[i].applyForce(new Force(push, 2));
                    }
                    System.out.println("Pushing "+main.creatures[i]+" in "+push);
                }
                break;
            case (4): //Самонаводящийся снаряд
                if(main.kCreatures>1) {
                    short closestIndex = 1000; //Индекс, который точно не может быть у элемента списка creatures, т.к. там 1000 значений
                    Point closestPoint = new Point(0, 0);
                    for (int i = 0; i < main.kCreatures; i++) {
                        if (i != casterIndex && main.creatures[i].getMass() != -1) {
                            targetPoint = new Point(main.creatures[i].getX() + main.creatures[i].getWidth() / 2,
                                    main.creatures[i].getY() + main.creatures[i].getHealth() / 2);
                            if (closestIndex >= 1000 || casterPoint.distanceToSquared(targetPoint) < casterPoint.distanceToSquared(closestPoint)) {
                                closestIndex = (short) i;
                                closestPoint = new Point(main.creatures[closestIndex].getX() + main.creatures[closestIndex].getWidth() / 2,
                                        main.creatures[closestIndex].getY() + main.creatures[closestIndex].getHealth() / 2);
                            }
                        }
                    }
                    main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                    main.creatures[main.kCreatures].setVelocity(projectileVel);
                    main.creatures[main.kCreatures].loadCreature("homingBall.txt", main);
                    main.creatures[main.kCreatures].setMovementPattern(new MPFollow(main.creatures[closestIndex], 15));
                    main.kCreatures++;
                }
                break;
            case (5): //Магический блок на 30 хп
                targetPoint = new Point(realClick.getX()-32, realClick.getY()-32);
                main.creatures[main.kCreatures] = new Creature(targetPoint.getX(), targetPoint.getY(), 1, 1, 1, main.endTime, main.kCreatures);
                main.creatures[main.kCreatures].loadCreature("magicBrick.txt", main);
                main.kCreatures++;
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

