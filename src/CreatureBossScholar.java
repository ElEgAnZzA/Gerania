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
    boolean jumping = false;

    public CreatureBossScholar(double x, double y, long time, int index){
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_MASS, time, index);
        this.setIsControlled(false);
        this.setMaxHealth(200);
        this.loadSprite("bossScholar.png");
        this.setCreatureCollisionInteraction(new InteractionHurt(10));
        this.lastTimeUsedSpell = time;
    }
    public void jump(MainGame mainGame){
        if (!jumping){
            Creature player = mainGame.creatures[mainGame.getPlayerControlledCreatureId()];
            Vector jump = new Vector(0, 15);
            this.move(jump);
            jumpMoveDirection = new Vector((player.getX()-this.getX())/300.0,0);
            jumpMoveDirection.setX(jumpMoveDirection.getX() + player.getVelocity().getX()/5);
            System.out.println(jumpMoveDirection+" - ");
            if (jumpMoveDirection.getR()<1)
                jumpMoveDirection.setR(1);
            System.out.println("- "+jumpMoveDirection);
            this.move(jumpMoveDirection);
            jumping = true;
        }
        else{
            jumpMoveDirection.setX(jumpMoveDirection.getX()*0.9);
            this.move(jumpMoveDirection);
        }
    }
    public void cast(MainGame mainGame){
        if (!hasVerticalCollision()&&(mainGame.endTime-lastTimeUsedSpell)>3000) {
            Random random = new Random();
            int spellId = random.nextInt(3);
            Creature playerCharacter = mainGame.creatures[mainGame.getPlayerControlledCreatureId()];
            Point playerCenter = new Point(playerCharacter.getX() + playerCharacter.getWidth() / 2, playerCharacter.getY() + playerCharacter.getHeight() / 2);
            Point playerCenterOnScreen = new Point(playerCenter.getX() - mainGame.cameraX, MainGame.SCREEN_HEIGHT - playerCenter.getY() + mainGame.cameraY);
            System.out.println("Boss casting "+ BOSS_SPELLS[spellId]);
            BOSS_SPELLS[spellId].cast(getIndex(), playerCenterOnScreen, mainGame);
            lastTimeUsedSpell = mainGame.endTime;
        }
    }
    @Override
    public void update(MainGame mainGame){ //Обновляет состояние Creature:
        //[ПЕРЕПИСАТЬ ПОЯСНЯЮЩИЙ КОММЕНТАРИЙ]
        timePassedModifier = (mainGame.endTime - mainGame.beginningTime)/50.0;
        this.detectCreatureCollisions(mainGame);

        if (hasVerticalCollision())
            jumping=false;
        this.setHasHorizontalGameObjectCollision(false);
        this.setHasVerticalGameObjectCollision(false);

        this.setLastX(this.getX());
        this.setLastY(this.getY());

        for (int i = 0; i<kForces; i++){
            velocity.addToThis(forces[i].x(1.0/this.getMass()));
            if (forces[i].getTime()>1)
                forces[i].decreaseTime();
            else if (forces[i].getTime()>-1)
                forcesPop(i);
            if (velocity.getR()>=2* MainGame.CREATURE_MAX_VELOCITY)
                velocity.setR(2* MainGame.CREATURE_MAX_VELOCITY);
        }
        this.velocity = detectGameObjectCollisions(mainGame);


        this.setX(this.getX()+this.velocity.getX());
        this.setY(this.getY()+this.velocity.getY());

        if(this.getY() > MainGame.SCREEN_HEIGHT){
            this.setY(this.getY() - MainGame.SCREEN_HEIGHT);
            this.hurt(mainGame, 10);
        }
        if(this.getY()<-MainGame.SCREEN_HEIGHT)
            this.kill(mainGame);

        if(!flip &&this.getX()< mainGame.creatures[mainGame.playerControlledCreatureId].getX()){
            flip = true;
        }
        if(flip &&this.getX()> mainGame.creatures[mainGame.playerControlledCreatureId].getX()){
            flip = false;
        }


        this.applyForce(new Force(this.velocity.getX()*(-0.05), 0, 1));

        this.jump(mainGame);
        this.cast(mainGame);
    }

    @Override
    public void kill(MainGame mainGame){
        System.out.println("Killing "+this+" index: "+this.getIndex());
        this.isDead=true;
        for (int i = this.getIndex(); i< mainGame.kCreatures-1; i++){
            mainGame.creatures[i] = mainGame.creatures[i+1];
            mainGame.creatures[i].setIndex(mainGame.creatures[i].getIndex()-1);
        }
        if (mainGame.kCreatures<1000)
            mainGame.creatures[mainGame.kCreatures-1] = mainGame.creatures[mainGame.kCreatures];
        else
            mainGame.creatures[mainGame.kCreatures-1]=null;
        mainGame.kCreatures--;
        mainGame.hasBoss = false;
    }
}
