package entities;

import java.awt.Graphics;
import java.awt.Color;
import java.util.List;

import input.InputHandler;
import map.Map;

public class Player extends Moving_Entity{
    private static final Color COLOR_PLAYER = Color.BLACK;
    // private String name = "Test_H";
    private boolean healable = false;
    private boolean interact= false;
    private int level=-1;
    

    public Player(double x, double y, int width, int height, int hp, int attack, int defense,int range){
        super();
        this.setMaxHp(hp);
        this.setHP(hp);
        this.setAttack(attack);
        this.setDefense(defense);
        this.setX(x);
        this.setY(y);
        this.setWidthinPixel(width);
        this.setHeightinPixel(height);
        this.setBounds();
        this.setShadowDistance(600);
        this.level = 0;
    }

    public void update_input(Map map, InputHandler input, List<Moving_Entity> movingEntity) {
        // 1. handle input
        handleInput(input);
        //update the collision first
        //normalizeVelocity();
        update_collision_withEntities(movingEntity, map);

        // 
        // 2. Apply the movement
        this.update(map);
    }

    // Input managing

    public void handleInput(InputHandler input){
        boolean moved_horizontal = false;
        boolean moved_vertical = false;
        boolean moved_left = input.isMovingLeft();
        boolean moved_down = input.isMovingDown();
        boolean moved_up = input.isMovingUp();
        boolean moved_right=  input.isMovingRight();
        int i = 0;
        if(moved_right||moved_left){
            i+=1;
        }
        if(moved_up||moved_down){
            i+=1;
        }
        
        if(i==1){
            double speed = getSpeed();
            if (input.isMovingLeft()) {
                this.moveLeft(speed);
                moved_horizontal = true;
            } 
            if (input.isMovingRight()) {
                this.moveRight(speed);
                moved_horizontal = true;
            }
            if (input.isMovingUp()) {
                this.moveUp(speed);
                moved_vertical = true;
            }
            if (input.isMovingDown()) {
                this.moveDown(speed);
                moved_vertical = true;
            }    
        }
        if(i==2){
            double speed = getSpeed()/Math.pow(2,0.5);
            if (input.isMovingLeft()) {
                this.moveLeft(speed);
                moved_horizontal = true;
            } 
            if (input.isMovingRight()) {
                this.moveRight(speed);
                moved_horizontal = true;
            }
            if (input.isMovingUp()) {
                this.moveUp(speed);
                moved_vertical = true;
            }
            if (input.isMovingDown()) {
                this.moveDown(speed);
                moved_vertical = true;
            }    
        }

        if (input.isInteractPressed()){
            this.interact=true;
        }
        else{
            this.interact=false;
        }
        


        //int horizontal = input.getHorizontalDirection();
        //int vertical = input.getVerticalDirection();
        /*if ((moved_vertical||moved_horizontal) && (horizontal != 0 || vertical != 0)) {
            this.setFacingAngle(Math.atan2(vertical, horizontal));
        }*/
    }

    public void setHealable(boolean a){
        this.healable = a;
    }
    public boolean getHealable(){
        return this.healable;
    }
    public boolean getInteract(){
        return this.interact;
    }

    public void healing(int a){
        this.setHP(getHP()+a);
    }

    //  Rendering
    public void render(Graphics g, int x, int y){
        int width = getWidthInPixels();
        int height = getHeightInPixels();
        int centerX =x+width/2 ;
        int centerY = y+height/2;
        int[] xPoints = {centerX +(int) (width*Math.cos(getFacingAngle())), centerX+ +(int) (width*Math.cos(getFacingAngle()-Math.PI/2)/1.5) , centerX +(int) (width*Math.cos(getFacingAngle()+Math.PI/2)/1.5)};
        int[] yPoints = {centerY+(int) (height*Math.sin(getFacingAngle())), centerY+(int) (height*Math.sin(getFacingAngle()-Math.PI/2)/1.5)   ,centerY +(int) (height*Math.sin(getFacingAngle()+Math.PI/2)/1.5)}; 
        g.setColor(Color.BLUE); 
        g.fillPolygon(xPoints, yPoints, 3); 
        
        g.setColor(COLOR_PLAYER);
        g.fillOval(x, y, width, height);
        g.drawRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, width, height);
        
    }

    public void normalizeVelocity(){
        double velocityX= getVelocityX();
        double velocityY = getVelocityY();
        
        double norm = Math.pow(Math.pow(velocityX,2) +  Math.pow(velocityY,2),0.5);

        setVelocityX((velocityX/norm)* getSpeed());
        setVelocityY((velocityY/norm) * getSpeed());
    }

    public int getLevel(){
        return level;
    }
    public void LevelUp(int a){
        level += a;
    }

    

    }
    





