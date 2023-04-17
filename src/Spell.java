public class Spell {
    private int type;
    public Spell(int type){
        this.type = type;
    }
    public void cast(int casterIndex, Point click, Main main){
        //Колдует заклинание и возвращает 0 - не нужно менять ни одну из переменных, 1 - нужно увеличить kGameObjects, 2 - нужно увеличить kCreatures
        double casterX = main.creatures[casterIndex].getX()+main.creatures[casterIndex].getWidth()/2;
        double casterY = main.creatures[casterIndex].getY()+main.creatures[casterIndex].getHeight()/2;
        switch (type){
            case (-3)://Ледяной шар босса
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                Vector bossIceBallVel = new Vector(new Point(casterX, casterY), new Point(main.cameraX+click.getX(), main.cameraY+click.getY()));
                main.creatures[main.kCreatures].setVelocity(bossIceBallVel);
                main.creatures[main.kCreatures].loadCreature("iceBall.txt", main);
                main.kCreatures++;
                break;
            case (-2)://Молния босса
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                Vector bossLightningBallVel = new Vector(new Point(casterX, casterY), new Point(main.cameraX+click.getX(), main.cameraY+click.getY()));
                main.creatures[main.kCreatures].setVelocity(bossLightningBallVel);
                main.creatures[main.kCreatures].loadCreature("lightningBall.txt", main);
                main.kCreatures++;
                break;
            case (-1)://Огненный шар босса
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                Vector bossFireballVel = new Vector(new Point(casterX, casterY), new Point(main.cameraX+click.getX(), main.cameraY+click.getY()));
                main.creatures[main.kCreatures].setVelocity(bossFireballVel);
                main.creatures[main.kCreatures].loadCreature("bossFireball.txt", main);
                main.kCreatures++;
                break;
            case (1): //Огненный шар
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1, main.endTime, main.kCreatures);
                Vector fireballVel = new Vector(new Point(casterX, casterY), new Point(main.cameraX+click.getX(), main.cameraY+click.getY()));
                main.creatures[main.kCreatures].setVelocity(fireballVel);
                main.creatures[main.kCreatures].loadCreature("fireball.txt", main);
                main.kCreatures++;
                break;
            case (2): //Лечение (на 15 хп)
                main.creatures[casterIndex].setHealth(main.creatures[casterIndex].getHealth()+15);
                break;
            case(3): //Отталкивание всех вблизи
                Point casterPoint = new Point(casterX, casterY);
                Point targetPoint;
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
            default:
                return 0;
        }
    }
    public String toString(){
        return "Spell with type "+type;
    }
}

