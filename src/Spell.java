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
            case (1): //Огненный шар
                main.creatures[main.kCreatures] = new Creature(casterX, casterY, 16, 16, 1);
                Vector vel = new Vector(new Point(casterX, casterY), new Point(main.cameraX+click.getX(), main.cameraY+click.getY()));
                vel.setR(10);
                main.creatures[main.kCreatures].setVelocity(vel);
                main.creatures[main.kCreatures].loadSprite("fireball.png");
                main.creatures[main.kCreatures].setInteraction(new InteractionHurt(10));
                main.creatures[main.kCreatures].setDiesOnCollision(true);
                main.kCreatures++;
            default:
        }
    }
}

