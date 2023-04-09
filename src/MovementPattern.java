public class MovementPattern { //Класс-"пустышка", нужен лишь для соединения различных видов паттернов поведения и возможности их реализовать.
    Vector nextAction = new Vector(0,0);
    Vector prevAction = new Vector(0,0);
    public MovementPattern(){
    }
    public Vector getNextAction(Main main, Creature caster){
        return this.nextAction.subtract(this.prevAction);
    }

    public void setNextAction(Vector nextAction) {
        this.nextAction = nextAction;
    }
    public static MovementPattern movementPatternFromString(String str){
        String[] stuff = str.split(" ");
        switch (stuff[0]){
            case ("-1"):
                return null;
            case ("0"):
                try {
                    Vector[] actionSequence = new Vector[stuff.length-1];
                    for (int i =1; i<stuff.length; i+=2){
                        actionSequence[(i-1)/2] = new Vector(Integer.valueOf(stuff[i]), Integer.valueOf(stuff[i+1]));
                    }
                    MPSequence res = new MPSequence(actionSequence);
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

class MPSequence extends MovementPattern{
    private Vector[] actionSequence;
    private int sequenceStep = 0;

    public MPSequence(Vector[] actionSequence){
        super();
        this.actionSequence=actionSequence;
    }

    @Override
    public Vector getNextAction(Main main, Creature caster){
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
    private int targetId;
    public MPFollow(int targetId){
        super();
        this.targetId = targetId;
    }
    @Override
    public Vector getNextAction(Main main, Creature caster){
        this.prevAction = new Vector(this.nextAction);
        this.nextAction=new Vector(0,0);
        this.nextAction.setX(main.creatures[targetId].getX()+ main.creatures[targetId].getWidth()/2-caster.getX()-caster.getWidth()/2);
        this.nextAction.setY(main.creatures[targetId].getY()+ main.creatures[targetId].getHeight()/2-caster.getY()-caster.getHeight()/2);
        return this.nextAction.subtract(this.prevAction);
    }
}
