package entities;

import map.Map;


import java.awt.Point;

public class Moving_Entity extends Basic_Entity{

    // Speed
    private int lifeTime = 0;
    private double speed=10; // pixels per frame ? 

    // Direction of speed
    private double velocityX = 0; 
    private double velocityY = 0;

    // friction (to limitate the speed of the entity)
    private double friction = 0.85;
    private boolean is_moving = false;



    public Moving_Entity(){
        super();
    }

    // Setters -----------------------------------------------------------------
    public void setVelocityX(double vx){
        this.velocityX = vx;
    }

    public void setVelocityY(double vy){
        this.velocityY = vy;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public void setFriction(double friction){
        this.friction = friction;
    }

    public void setMoving(boolean moving_really){
        this.is_moving = moving_really;
    }

    public void setLifeTime(int life){
        this.lifeTime = life;
    }

    
    // Getters -----------------------------------------------------------------
    public double getVelocityX(){
        return this.velocityX;
    }

    public double getVelocityY(){
        return this.velocityY;
    }

    public double getSpeed(){
        return this.speed;
    }

    public double getFriction(){
        return this.friction;
    }

    public int getLifeTime(){
        return this.lifeTime;
    }

    
    public boolean getMoving(){
        return this.is_moving;
    }


    //Moving in the Map ----------------------------------------------------------------------------------------------

    public void update(Map map) {
        // Calculer la nouvelle position

        int steps = (int)Math.ceil(Math.max(Math.abs(velocityX), Math.abs(velocityY)));
        steps = Math.max(1, Math.min(steps, 20));
        double stepX = velocityX / steps;
        double stepY = velocityY / steps;

        for (int i = 0; i < steps; i++) {
            // Mouvement horizontal
            double testX = this.getX() + stepX;
            if (!map.collidesWithWall((int)testX, (int)this.getY(), this.getWidthInPixels(), this.getHeightInPixels())) {
                this.setX(testX);
            } else {
                velocityX = 0;
                break; // Arrêter si collision
            }
        }
        
        for (int i = 0; i < steps; i++) {
            // Mouvement vertical
            double testY = this.getY() + stepY;
            if (!map.collidesWithWall((int)this.getX(), (int)testY, this.getWidthInPixels(), this.getHeightInPixels())) {
                this.setY(testY);
            } else {
                velocityY = 0;
                break;
            }
        }
        
        // Slow down in any case to have a cap for our speed
        velocityX *= this.friction;
        velocityY *= this.friction;

        lifeTime+= 1;
        this.setBounds();
    }

    // Velocity update

    public void moveLeft() {
        velocityX = -speed;
    }
    
    public void moveRight() {
        velocityX = speed;
    }
    
    public void moveUp() {
        velocityY = -speed;
    }
    
    public void moveDown() {
        velocityY = speed;
    }
    
    public void stop() {
        velocityX = 0;
        velocityY = 0;
    }

 // Conversion PIXELS - TILES ( debug or for game logic) ------------------------------------------------

    public Point getTilePosition(int tileSize) {
        int tileX = (int)(this.getX() / tileSize);
        int tileY = (int)(this.getY() / tileSize);
        return new Point(tileX, tileY);
    }


    
}