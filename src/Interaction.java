import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Interaction { //Класс - "пустышка", нужен лишь для наличия общего метода interact(mainGame, caster, target) у разных видов взаимодействий
    public Interaction(){

    }
    public void interact(MainGame mainGame, Creature caster, int target){

    }
    public static Interaction interactionFromString(String str){ //Получение взаимодействия из строки, используется при загрузке уровня
        String[] stuff = str.split(" ");
        switch (stuff[0]){
            case("-1"): //Отсутствует взаимодействие
                return null;
            case ("0"): //Нанесение урона при столкновении
                try {
                    int damage = Integer.valueOf(stuff[1]);
                    InteractionHurt res = new InteractionHurt(damage);
                    return res;
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            case ("1"): //Взрыв при столкновении
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
            case ("2"): //"Молния" при столкновении
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
            case ("3"): //Появление ледяных шипов при столкновении
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
            default: //Отсутствует взаимодействие
                return null;
        }
    }
}

class InteractionHurt extends Interaction{ //Нанесение урона при столкновении
    private final int damage;
    public InteractionHurt(int damage){
        super();
        this.damage=damage;
    }

    @Override
    public void interact(MainGame mainGame, Creature caster, int target){
        mainGame.creatures[target].hurt(mainGame, damage);
    }

    @Override
    public String toString(){
        return "Interaction: deal "+damage+" damage";
    }
}
class InteractionBoom extends Interaction{ //Взрыв при столкновении
    private final int damage;
    private final int radius;
    public InteractionBoom(int damage, int radius){
        super();
        this.damage=damage;
        this.radius = radius;
    }
    public void interact(MainGame mainGame, Creature caster, int target){
        Point casterCenter = new Point(caster.getX()+caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        Creature explosion = new Creature(casterCenter.getX()-radius, casterCenter.getY()-radius, 2*radius, 2*radius, 0, 0, mainGame.kCreatures);
        explosion.setDiesOnCollision(true);
        explosion.setMaxHealth(-1);
        explosion.setCreatureCollisionInteraction(new InteractionHurt(damage));
        mainGame.creatures[mainGame.kCreatures] = explosion;
        mainGame.kCreatures++;
        TemporaryArt boom = new TemporaryArt(casterCenter.getX() - radius, casterCenter.getY() - radius, radius*2, radius*2, false, 200,
                mainGame.endTime, mainGame.kTemporaryArts, new File("./src/art/explosion.png"));
        mainGame.temporaryArts[mainGame.kTemporaryArts] = boom;
        mainGame.kTemporaryArts++;
    }
    @Override
    public String toString(){
        return "Interaction: deal "+damage+" damage in radius of "+radius;
    }
}
class InteractionLightning extends Interaction{ //"молния" при столкновении
    private final int damage;
    private final int width;
    private final int height;
    public InteractionLightning(int damage, int width, int height){
        super();
        this.damage=damage;
        this.width = width;
        this.height = height;
    }
    public void interact(MainGame mainGame, Creature caster, int target){
        Point casterCenter = new Point(caster.getX()+caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        Creature ligtningStrike = new Creature(casterCenter.getX()-width/2, casterCenter.getY()-height/2, width, height, 0, 0, mainGame.kCreatures);
        ligtningStrike.setDiesOnCollision(true);
        //explosion.loadSprite();
        ligtningStrike.setMaxHealth(-1);
        ligtningStrike.setCreatureCollisionInteraction(new InteractionHurt(damage));
        mainGame.creatures[mainGame.kCreatures] = ligtningStrike;
        mainGame.kCreatures++;
        TemporaryArt lightning = new TemporaryArt(casterCenter.getX() - width / 2, casterCenter.getY() - height / 2, width, height, false, 200,
                    mainGame.endTime, mainGame.kTemporaryArts, new File("./src/art/lightning.png"));
        mainGame.temporaryArts[mainGame.kTemporaryArts] = lightning;
        mainGame.kTemporaryArts++;
    }
    @Override
    public String toString(){
        return "Interaction: deal "+damage+" damage in a "+width+" by "+height+" box";
    }
}
class InteractionIceSpikes extends Interaction{ //Появление ледяных шипов при столкновении
    private final int damage;
    private final int count;
    private static final int ICE_SPIKE_WIDTH = 24;
    private static final int ICE_SPIKE_HEIGHT = 32;
    public InteractionIceSpikes(int damage, int count){
        super();
        this.damage=damage;
        this.count = count;
    }
    public void interact(MainGame mainGame, Creature caster, int target){
        Point casterCenter = new Point(caster.getX()+caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        if(caster.hasHorizontalCollision()){
            //Возможно, здесь так ничего и не будет. Возможно, я придумаю, как реализовать вертикальное расположение шипов
        }
        else {
            int startingX = (int) (casterCenter.getX() - (count / 2.0) * ICE_SPIKE_WIDTH);
            for (int i = 0; i < count; i++) {
                Creature iceSpike = new Creature(startingX + i * ICE_SPIKE_WIDTH, casterCenter.getY(), ICE_SPIKE_WIDTH, ICE_SPIKE_HEIGHT, 0, 0, mainGame.kCreatures);
                iceSpike.loadCreature("iceSpike.txt", mainGame);
                mainGame.creatures[mainGame.kCreatures] = iceSpike;
                mainGame.kCreatures++;
            }
        }
    }
    @Override
    public String toString(){
        return "Interaction: create "+count+" ice spikes, each dealing "+damage+" damage";
    }
}