package entities;

import java.util.Random;


public class Basic_Entity {

    // Position of the entity ----------------------------------------------------------------------------------------------------------------------------------
    private double x=0;
    private double y=0;
    
    // Hitbox of the entity -----------------------------------------------------------------
    private int width=0;
    private int height=0;

    private Direction facing = Direction.DOWN;


    // Stats -----------------------------------------------------------------
    private int hp=1;
    private int defense=1; // Same unit as hp for now
    private int attack=1; // Same unit as hp for now
    private int range=1; // in pixel
    private int bullet_fast = 20; // initial speed of a bullet
    private boolean is_dead=false; // Is an entity dead
    private ProjectilesTypes projectilesTypes = ProjectilesTypes.Simple_Projectiles; // to define what the entity shoot
    private boolean is_undying=false; // to implement future damage method
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
    public void setRange(int range){
        this.range = range;
    }
    public void setHP(int hp){
        this.hp = hp;
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
    }

    public void setProjectilesTypes(ProjectilesTypes pt){
        this.projectilesTypes = pt;
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
    
    public ProjectilesTypes getProjectilesTypes(){
        return this.projectilesTypes;
    }

    // More Specific and practical
    public void take_damage(int damage){
        if (!is_undying){
            this.hp -= damage;
        }
    }

    



}
