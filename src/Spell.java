public class Spell {
    private int type;
    public Spell(int type){
        this.type = type;
    }
    public int cast(int casterIndex, Point click, double cameraX, double cameraY, GameObject[] gameObjects, int kGameObjects, Creature[] creatures, int kCreatures){
        //Колдует заклинание и возвращает 0 - не нужно менять ни одну из переменных, 1 - нужно увеличить kGameObjects, 2 - нужно увеличить kCreatures
        double casterX = creatures[casterIndex].getX()+creatures[casterIndex].getWidth()/2;
        double casterY = creatures[casterIndex].getY()+creatures[casterIndex].getHeight()/2;
        switch (type){
            case (1): //Огненный шар
                creatures[kCreatures] = new Creature(casterX, casterY, 16, 16);
                Vector vel = new Vector(new Point(casterX, casterY), new Point(cameraX+click.getX(), cameraY+click.getY()));
                vel.setR(10);
                creatures[kCreatures].setVelocity(vel);
                creatures[kCreatures].loadSprite("fireball.png");
                return 2;
            default:
                return 0;
        }
    }
}

