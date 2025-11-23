package entities;

public class Basic_Entity {

    // Position of the entity ----------------------------------------------------------------------------------------------------------------------------------
    private double x=0;
    private double y=0;
    
    // Hitbox of the entity -----------------------------------------------------------------
    private int width=0;
    private int height=0;


    // Stats -----------------------------------------------------------------
    private int hp=0;
    private int defense=0; // Same unit as hp for now
    private int attack=0; // Same unit as hp for now
    private int range=0; // in pixel

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


}
