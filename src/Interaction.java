public class Interaction {
    public Interaction(){

    }
    public void interact(Main main, Creature caster, int target){

    }
    public static Interaction interactionFromString(String str){
        String[] stuff = str.split(" ");
        switch (stuff[0]){
            case("-1"):
                return null;
            case ("0"):
                try {
                    int damage = Integer.valueOf(stuff[1]);
                    InteractionHurt res = new InteractionHurt(damage);
                    return res;
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            case ("1"):
                try {
                    int damage = Integer.valueOf(stuff[1]);
                    int radius = Integer.valueOf(stuff[2]);
                    InteractionBoom res = new InteractionBoom(damage, radius);
                    return res;
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            case ("2"):
                try{
                    int damage = Integer.valueOf(stuff[1]);
                    int width = Integer.valueOf(stuff[2]);
                    int height = Integer.valueOf(stuff[3]);
                    InteractionLightning res = new InteractionLightning(damage, width, height);
                    return res;
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            case ("3"):
                try{
                    int damage = Integer.valueOf(stuff[1]);
                    int count = Integer.valueOf(stuff[2]);
                    InteractionIceSpikes res = new InteractionIceSpikes(damage, count);
                    return res;
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            default:
                return null;
        }
    }
}

class InteractionHurt extends Interaction{
    private int damage;
    public InteractionHurt(int damage){
        super();
        this.damage=damage;
    }

    @Override
    public void interact(Main main, Creature caster, int target){
        main.creatures[target].hurt(main, damage);
    }

    @Override
    public String toString(){
        return "Interaction: deal "+damage+" damage";
    }
}
class InteractionBoom extends Interaction{
    private int damage;
    private int radius;
    public InteractionBoom(int damage, int radius){
        super();
        this.damage=damage;
        this.radius = radius;
    }
    public void interact(Main main, Creature caster, int target){
        Point casterCenter = new Point(caster.getX()+caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        Creature explosion = new Creature(casterCenter.getX()-radius, casterCenter.getY()-radius, 2*radius, 2*radius, 0, 0, main.kCreatures);
        explosion.setDiesOnCollision(true);
        //explosion.loadSprite();
        explosion.setMaxHealth(-1);
        explosion.setCreatureCollisionInteraction(new InteractionHurt(damage));
        main.creatures[main.kCreatures] = explosion;
        main.kCreatures++;
    }
    @Override
    public String toString(){
        return "Interaction: deal "+damage+" damage in radius of "+radius;
    }
}
class InteractionLightning extends Interaction{
    private int damage;
    private int width;
    private int height;
    public InteractionLightning(int damage, int width, int height){
        super();
        this.damage=damage;
        this.width = width;
        this.height = height;
    }
    public void interact(Main main, Creature caster, int target){
        Point casterCenter = new Point(caster.getX()+caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        Creature explosion = new Creature(casterCenter.getX()-width/2, casterCenter.getY()-height/2, width, height, 0, 0, main.kCreatures);
        explosion.setDiesOnCollision(true);
        //explosion.loadSprite();
        explosion.setMaxHealth(-1);
        explosion.setCreatureCollisionInteraction(new InteractionHurt(damage));
        main.creatures[main.kCreatures] = explosion;
        main.kCreatures++;
    }
    @Override
    public String toString(){
        return "Interaction: deal "+damage+" damage in a "+width+" by "+height+" box";
    }
}
class InteractionIceSpikes extends Interaction{
    private int damage;
    private int count;
    private static final int ICE_SPIKE_WIDTH = 24;
    private static final int ICE_SPIKE_HEIGHT = 32;
    public InteractionIceSpikes(int damage, int count){
        super();
        this.damage=damage;
        this.count = count;
    }
    public void interact(Main main, Creature caster, int target){
        Point casterCenter = new Point(caster.getX()+caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        if(caster.hasHorizontalCollision()){
            //Возможно, здесь так ничего и не будет. Возможно, я придумаю, как реализовать вертикальное расположение шипов
        }
        else {
            int startingX = (int) (casterCenter.getX() - (count / 2.0) * ICE_SPIKE_WIDTH);
            for (int i = 0; i < count; i++) {
                Creature iceSpike = new Creature(startingX + i * ICE_SPIKE_WIDTH, casterCenter.getY(), ICE_SPIKE_WIDTH, ICE_SPIKE_HEIGHT, 0, 0, main.kCreatures);
                iceSpike.loadCreature("iceSpike.txt", main);
                main.creatures[main.kCreatures] = iceSpike;
                main.kCreatures++;
            }
        }
    }
    @Override
    public String toString(){
        return "Interaction: create "+count+" ice spikes, each dealing "+damage+" damage";
    }
}