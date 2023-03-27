public class MovementPattern { //Класс-"пустышка", нужен лишь для соединения различных видов паттернов поведения и возможности их реализовать.
    Vector nextAction = new Vector(0,0);
    Vector prevAction = new Vector(0,0);
    public MovementPattern(){
    }
    public Vector getNextAction(double x, double y, double width, double height){
        return this.nextAction.subtract(this.prevAction);
    }

    public void setNextAction(Vector nextAction) {
        this.nextAction = nextAction;
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
    public Vector getNextAction(double x, double y, double width, double height){
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
    private Creature target;
    public MPFollow(Creature target){
        super();
        this.target = target;
    }
    @Override
    public Vector getNextAction(double x, double y, double width, double height){
        this.prevAction = new Vector(this.nextAction);
        this.nextAction=new Vector(0,0);
        this.nextAction.setX(target.getX()+ target.getWidth()/2-x-width/2);
        this.nextAction.setY(target.getY()+ target.getHeight()/2-y-height/2);
        return this.nextAction.subtract(this.prevAction);
    }
}
