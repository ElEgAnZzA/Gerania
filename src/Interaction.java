public class Interaction {
    public Interaction(){

    }
    public void interact(Main main, int target){

    }
}

class InteractionHurt extends Interaction{
    private int damage;
    public InteractionHurt(int damage){
        super();
        this.damage=damage;
    }

    @Override
    public void interact(Main main, int target){
        main.creatures[target].hurt(main, damage);
    }
}
