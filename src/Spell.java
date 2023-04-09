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
            default:
        }
    }
    public String toString(){
        return "Spell with type "+type;
    }
}

