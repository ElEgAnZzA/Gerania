import java.util.Random;
public class CreatureBossScholar extends Creature{
    static final Spell BOSS_FIREBALL = new Spell(-1);
    static final Spell BOSS_LIGHTNING_BALL = new Spell(-2);
    static final Spell BOSS_ICE_BALL = new Spell(-3);
    static final Spell[] BOSS_SPELLS = {BOSS_FIREBALL, BOSS_LIGHTNING_BALL, BOSS_ICE_BALL};
    static final int BOSS_WIDTH = 96;
    static final int BOSS_HEIGHT = 144;
    static final int BOSS_MASS = 20;

    Vector jumpMoveDirection = new Vector(0,0);
    long lastTimeUsedSpell = 0;

    public CreatureBossScholar(double x, double y, long time, int index){
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_MASS, time, index);
        this.setIsControlled(false);
        this.setMaxHealth(200);
        this.loadSprite("bossScholar.png");
        this.setCreatureCollisionInteraction(new InteractionHurt(10));
        this.lastTimeUsedSpell = time;
    }
    public void jump(Main main){
        if (hasVerticalCollision()){
            Creature player = main.creatures[main.getPlayerControlledCreatureId()];
            Vector jump = new Vector(0, -20);
            this.move(jump);
            jumpMoveDirection = new Vector((player.getX()-this.getX())/200.0,0);
            jumpMoveDirection.setX(jumpMoveDirection.getX() + player.getVelocity().getX());
            this.move(jumpMoveDirection);
        }
        else{
            this.move(jumpMoveDirection);
        }
    }
    public void cast(Main main){
        if (!hasVerticalCollision()&&(main.endTime-lastTimeUsedSpell)>3000) {
            Random random = new Random();
            int spellId = random.nextInt(3);
            Creature playerCharacter = main.creatures[main.getPlayerControlledCreatureId()];
            Point playerCenter = new Point(playerCharacter.getX() + playerCharacter.getWidth() / 2, playerCharacter.getY() + playerCharacter.getHeight() / 2);
            Point playerCenterOnScreen = new Point(playerCenter.getX() - main.cameraX, playerCenter.getY() - main.cameraY);
            System.out.println("Boss casting "+ BOSS_SPELLS[spellId]);
            BOSS_SPELLS[spellId].cast(getIndex(), playerCenterOnScreen, main);
            lastTimeUsedSpell = main.endTime;
        }
    }
    @Override
    public void update(Main main){ //Обновляет состояние Creature:
        //[ПЕРЕПИСАТЬ ПОЯСНЯЮЩИЙ КОММЕНТАРИЙ]
        timePassedModifier = (main.endTime - main.beginningTime)/20.0;
        this.detectCreatureCollisions(main);
        this.setHasHorizontalCollision(false);
        this.setHasVerticalCollision(false);

        this.setLastX(this.getX());
        this.setLastY(this.getY());

        for (int i = 0; i<kForces; i++){
            velocity.addToThis(forces[i].x(1.0/this.getMass()));
            if (forces[i].getTime()>1)
                forces[i].decreaseTime();
            else if (forces[i].getTime()>-1)
                forcesPop(i);
            if (velocity.getR()>=Main.CREATURE_MAX_VELOCITY)
                velocity.setR(Main.CREATURE_MAX_VELOCITY);
            this.velocity = detectGameObjectCollisions(main);
        }

        this.setX(this.getX()+this.velocity.getX());
        this.setY(this.getY()+this.velocity.getY());

        if(this.getY() > main.SCREEN_HEIGHT){
            this.setY(this.getY() - main.SCREEN_HEIGHT);
            this.hurt(main, 10);
        }
        if(this.getY()<-main.SCREEN_HEIGHT)
            this.kill(main);


        if (hasVerticalCollision()){
            this.applyForce(new Force(this.velocity.getX()*(-0.01), 0, 1));
        }

        this.jump(main);
        this.cast(main);
    }
}
