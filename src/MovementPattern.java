public class MovementPattern { //Класс-"пустышка", нужен лишь для соединения различных видов паттернов поведения и возможности их реализовать.
    Vector nextAction = new Vector(0,0);
    Vector prevAction = new Vector(0,0);
    public MovementPattern(){
    }
    public Vector getNextAction(MainGame mainGame, Creature caster){
        return this.nextAction.subtract(this.prevAction);
    }

    public void setNextAction(Vector nextAction) {
        this.nextAction = nextAction;
    }
    public static MovementPattern movementPatternFromString(MainGame mainGame, String str){
        String[] stuff = str.split(" ");
        MovementPattern res;
        switch (stuff[0]){
            case ("-1"):
                return null;
            case ("0"): //MPSequence
                try {
                    Vector[] actionSequence = new Vector[(stuff.length-1)/2];
                    for (int i =1; i<stuff.length; i+=2){
                        actionSequence[(i-1)/2] = new Vector(Integer.valueOf(stuff[i]), Integer.valueOf(stuff[i+1]));
                    }
                    res = new MPSequence(actionSequence);
                    return res;
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            case ("1"): //MPFollow
                res = new MPFollow(mainGame.creatures[mainGame.getPlayerControlledCreatureId()], Integer.valueOf(stuff[1]));
                return res;
            case ("2"): //MPFollowCast
                res = new MPFollowCast(mainGame.creatures[mainGame.getPlayerControlledCreatureId()], Integer.valueOf(stuff[1]), Integer.valueOf(stuff[2]), mainGame.endTime);
                return res;
            default:
                return null;
        }
    }
}

class MPSequence extends MovementPattern{
    private final Vector[] actionSequence;
    private int sequenceStep = 0;

    public MPSequence(Vector[] actionSequence){
        super();
        this.actionSequence=actionSequence;
    }

    @Override
    public Vector getNextAction(MainGame mainGame, Creature caster){
        this.prevAction = new Vector(this.nextAction);
        this.setNextAction(actionSequence[sequenceStep]);
        if (sequenceStep==actionSequence.length-1)
            sequenceStep=0;
        else
            sequenceStep++;
        return this.nextAction.subtract(this.prevAction);
    }
}

class MPFollow extends MovementPattern{
    public Creature target;
    public int speed;
    public MPFollow(Creature target, int speed){
        super();
        this.target = target;
        this.speed = speed;
    }
    @Override
    public Vector getNextAction(MainGame mainGame, Creature caster){
        this.prevAction = new Vector(this.nextAction);
        Point casterPoint = new Point (caster.getX()+ caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        Point targetPoint = new Point(target.getX()+ target.getWidth()/2, target.getY()+target.getHeight()/2);
        Vector targetVector = new Vector(casterPoint, targetPoint);
        targetVector.setR(speed);
        this.nextAction = targetVector;
        return this.nextAction.subtract(this.prevAction);
    }
}
class MPFollowCast extends MPFollow{
    private long lastTimeCast;
    private final Spell spell;
    public MPFollowCast(Creature target, int speed, int spellId, long time){
        super(target, speed);
        this.spell = new Spell(spellId);
        this.lastTimeCast = time;
    }
    @Override
    public Vector getNextAction(MainGame mainGame, Creature caster){
        this.prevAction = new Vector(this.nextAction);
        Point casterPoint = new Point (caster.getX()+ caster.getWidth()/2, caster.getY()+caster.getHeight()/2);
        Point targetPoint = new Point(target.getX()+ target.getWidth()/2, target.getY()+target.getHeight()/2);
        Vector targetVector = new Vector(casterPoint, targetPoint);
        targetVector.setR(speed);
        this.nextAction = targetVector;
        if(mainGame.endTime-lastTimeCast>2000) {
            System.out.println(spell);
            Point imaginaryClick = new Point(caster.getX() - mainGame.cameraX, MainGame.SCREEN_HEIGHT - caster.getY() + mainGame.cameraY);
            spell.cast(caster.getIndex(), imaginaryClick, mainGame);
            lastTimeCast = mainGame.endTime;
        }
        return this.nextAction.subtract(this.prevAction);

    }
}
