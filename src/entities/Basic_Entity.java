package entities;

import java.awt.Rectangle;
import java.util.Random;


public class Basic_Entity {

    // Position of the entity ----------------------------------------------------------------------------------------------------------------------------------
    private double x=0;
    private double y=0;
    
    // Hitbox of the entity -----------------------------------------------------------------
    private int width=0;
    private int height=0;

    private Direction facing = Direction.DOWN;
    private double facingAngle = Direction.DOWN.toAngle();

    private Rectangle bound;


    //Cycle for animation
    private static int CYCLE_FRAME = 10;
    private int whichFrame = 0;

    // Frames for stun , weight and knockback
    private int STUN_COOLDOWN = 40;//20
    private int actual_stuned_time = 0;
    private boolean isStun = false;

    private int kNOCKBACK_COOLDOWN = 40;//20
    private int actual_knockback_time = 0;
    private boolean isKnockBack = false;
    private int knockbackIntensity = 0;

    // This indicate at which frame the impact between bullet and entity happens
    private boolean justKnockBack = false; // 

    private int weight = 1;
    
    //Rendering 
    //Brigntness distance
    private int ShadowDistance = 0;

    // Temporary state for 

    // Stats -----------------------------------------------------------------
    private int hp=1;
    private int max_hp =1;
    private int defense=1; // Same unit as hp for now
    private int attack=1; // Same unit as hp for now
    private int range=1; // in pixel
    private int bullet_fast = 20; // initial speed of a bullet
    private boolean is_dead=false; // Is an entity dead
    private ProjectilesTypes projectilesTypes = ProjectilesTypes.Simple_Projectiles; // to define what the entity shoot
    private boolean is_undying=false; // to implement future damage method
    private double impactDirection;
    // Constructor -----------------------------------------------------------------

    public Basic_Entity() {
    }

    // Setters -----------------------------------------------------------------

    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }

    public void setBound(){
        this.bound = new Rectangle((int) this.x,(int) this.y,width,height);
    }
    public void setRange(int range){
        this.range = range;
    }
    public void setHP(int hp){
        this.hp = Math.min(hp,this.max_hp);
    }
    public void setAttack(int attack){
        this.attack = attack;
    }
    public void setDefense(int defense){
        this.defense = defense;
    }
    public void setHeightinPixel(int height){
        this.height = height;
    }
    public void setWidthinPixel(int width){
        this.width = width;
    }

    public void setBulletFast(int new_BP){
        this.bullet_fast = new_BP;
    }

    public void setIsUndying(boolean undying){
        this.is_undying = undying;
    }

    public void kill(){
        this.is_dead = true;
    }

    public void setFacing(Direction face){
        this.facing = face;
        this.facingAngle = face.toAngle();
    }

    public void setFacingAngle(double angle){
        this.facingAngle = Direction.normalize(angle);
        this.facing = Direction.fromAngle(this.facingAngle);
    }

    public void setProjectilesTypes(ProjectilesTypes pt){
        this.projectilesTypes = pt;
    }

    public void setBounds(){
        // Reuse existing Rectangle if possible to avoid allocations
        if (this.bound == null) {
            this.bound = new Rectangle((int) this.x, (int) this.y, width, height);
        } else {
            this.bound.setBounds((int) this.x, (int) this.y, width, height);
        }
    }

    public void setFrame(int f){
        this.whichFrame = f;
    }

    public void setWeight(int w){
        this.weight= w;
    }

    public void setStun(boolean s){
        this.isStun = (actual_stuned_time>0);
    }

    public void setStunFrame(int f){
        this.actual_stuned_time = f;
    }


    public void setKnockBack(boolean b){
        this.isKnockBack = b;
    }

    public void setJustKnockBack(boolean b){
        this.justKnockBack = b;
    }

    public void setKnockBackFrame(int f){
        this.actual_knockback_time = f;
    }

    public void setKnockBackIntensity(int intensity){
        this.knockbackIntensity = intensity;
    }

    public void setImpactDirection(double dir){
        this.impactDirection = dir;
    }

    public void setStunCoolDown(int frames){
        this.STUN_COOLDOWN = frames;
    }

    public void setKnockBackCoolDown(int frames){
        this.kNOCKBACK_COOLDOWN = frames;
    }

    public void setMaxHp(int max){
        this.max_hp = max;
    }

    public void setShadowDistance(int sd){
        this.ShadowDistance = sd;
    }


    // Getters -----------------------------------------------------------------
    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public int getWidthInPixels(){
        return this.width;
    }

    public int getHeightInPixels(){
        return this.height;
    }

    public int getDefense(){
        return this.defense;
    }

    public int getRange(){
        return this.range;
    }

    public int getAttack(){
        return this.attack;
    }

    public int getHP(){
        return this.hp;
    }

    public boolean getIsDead(){
        return this.is_dead;
    }

    public int getBulletFast(){
        return this.bullet_fast;
    }

    public boolean getIsUndying(){
        return this.is_undying;
    }

    public Direction getFacing(){
        return this.facing;
    }

    public double getFacingAngle(){
        return this.facingAngle;
    }
    
    public ProjectilesTypes getProjectilesTypes(){
        return this.projectilesTypes;
    }

    // More Specific and practical
    public void take_damage(int damage){
        if (!is_undying){
            // Prevent "healing" when defense > dazmage and clamp minimum damage to 0
            int effectiveDamage = damage - this.defense;
            if (effectiveDamage < 0) {
                effectiveDamage = 0;
            }
            this.hp -= effectiveDamage;
        }
        if (this.hp <= 0){
            this.is_dead=true;
        }
    }

    public void TimerUpdate(){
        if (this.actual_knockback_time>0){
            this.actual_knockback_time-=1;
            if (this.actual_knockback_time==0){
                this.isKnockBack = false;
                this.knockbackIntensity = 0;
            }
        }
        if (this.actual_stuned_time>0){
            this.actual_stuned_time-=1;
            if (this.actual_stuned_time==0){
                this.isStun = false;
            }
        }
    }

    
    public Rectangle getBounds(){
        return this.bound;
    }

    public int getFrame(){
        return this.whichFrame;
    }

    public int getWeight(){
        return weight;
    }

    public boolean getStun(){
        return this.isStun;
    }

    public int getStunFrame(){
        return this.actual_stuned_time;
    }

    public boolean getIsKnockBack(){
        return this.isKnockBack;
    }

    public int getKnockBackFrame(){
        return this.actual_knockback_time;
    }

    public boolean getJustKnockBack(){
        return this.justKnockBack;
    }

    public int getStunCoolDown(){
        return STUN_COOLDOWN;
    }

    public int getKnockBackCoolDown(){
        return kNOCKBACK_COOLDOWN;
    }

    public int getKnockBackIntensity(){
        return knockbackIntensity;
    }

    public double getImpactDirection(){
        return this.impactDirection;
    }

    public int getMaxHP(){
        return this.max_hp;
    }

    public int getShadowDistance(){
        return this.ShadowDistance;
    }





}
