package entities;

import map.Map;
import java.util.List;


import java.awt.Point;
import java.awt.Rectangle;

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

    // Weight and stun

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
            // Horizontal Movement
            double testX = this.getX() + stepX;
            if (!map.collidesWithWall((int)testX, (int)this.getY(), this.getWidthInPixels(), this.getHeightInPixels())) {
                this.setX(testX);
            } else {
                velocityX = 0;
                break; // Stop if collision
            }
            
        }
        for (int i = 0; i < steps; i++) {
            // Vertical Movement
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

    public void update_collision_withEntities(List<Moving_Entity> moving_Entities, Map map) {
        for (int i = 0; i < moving_Entities.size(); i++) {
            Moving_Entity other = moving_Entities.get(i);
            if (other == this) {
                continue;
            }
            
            Rectangle thisBounds = this.getBounds();
            Rectangle otherBounds = other.getBounds();
            
            if (thisBounds.intersects(otherBounds)) {
                // Calculate the centers of the two entities
                double thisCenterX = this.getX() + this.getWidthInPixels() / 2.0;
                double thisCenterY = this.getY() + this.getHeightInPixels() / 2.0;
                double otherCenterX = other.getX() + other.getWidthInPixels() / 2.0;
                double otherCenterY = other.getY() + other.getHeightInPixels() / 2.0;
                
                // separation vector
                double dx = thisCenterX - otherCenterX;
                double dy = thisCenterY - otherCenterY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                // avoid a division by zero error
                if (distance < 0.001) {
                    // if the entities are exactly at the same place, separate randomly
                    dx = Math.random() - 0.5;
                    dy = Math.random() - 0.5;
                    distance = Math.sqrt(dx * dx + dy * dy);
                }
                
                // normalize the vector 
                double nx = dx / distance;
                double ny = dy / distance;
                
                // calculate the depth of the intersection
                double overlapX = Math.min(
                    this.getX() + this.getWidthInPixels() - other.getX(),
                    other.getX() + other.getWidthInPixels() - this.getX()
                );
                double overlapY = Math.min(
                    this.getY() + this.getHeightInPixels() - other.getY(),
                    other.getY() + other.getHeightInPixels() - this.getY()
                );
                
                // use the smallest overlap for the separation
                double minOverlap = Math.min(overlapX, overlapY);
                
                // calculate the relative weights
                double thisWeight = this.getWeight();
                double otherWeight = other.getWeight();
                double totalWeight = thisWeight + otherWeight;
                
                if (totalWeight < 0.001) {
                    totalWeight = 2.0; // avoid a division by zero error
                }
                
                // Limit the separation to avoid crossing walls
                // use 50% of the overlap for the immediate separation
                double separationDistance = Math.min(minOverlap * 0.5, 2.0); // Max 2 pixels of separation
                double thisSeparation = separationDistance * (otherWeight / totalWeight);
                double otherSeparation = separationDistance * (thisWeight / totalWeight);
                
                // try to separate this entity only if it doesn't cause a collision with a wall
                double newX = this.getX() + nx * thisSeparation;
                double newY = this.getY() + ny * thisSeparation;
                if (map != null && !map.collidesWithWall((int)newX, (int)newY, this.getWidthInPixels(), this.getHeightInPixels())) {
                    this.setX(newX);
                    this.setY(newY);
                    this.setBounds();
                }
                
                // try to separate the other entity only if it doesn't cause a collision with a wall
                double otherNewX = other.getX() - nx * otherSeparation;
                double otherNewY = other.getY() - ny * otherSeparation;
                if (map != null && !map.collidesWithWall((int)otherNewX, (int)otherNewY, other.getWidthInPixels(), other.getHeightInPixels())) {
                    other.setX(otherNewX);
                    other.setY(otherNewY);
                    other.setBounds();
                }
                
                // adjust the velocities to create a repulsion force
                // Repulsion force based on the overlap
                double repulsionStrength = Math.min(minOverlap * 0.3, 5.0); // Limit max force
                
                // calculate the inverse weights for the force distribution
                double thisImpulseFactor = otherWeight / totalWeight;
                double otherImpulseFactor = thisWeight / totalWeight;
                
                // apply a repulsion force via the velocities
                this.setVelocityX(this.getVelocityX() + nx * repulsionStrength * thisImpulseFactor);
                this.setVelocityY(this.getVelocityY() + ny * repulsionStrength * thisImpulseFactor);
                
                other.setVelocityX(other.getVelocityX() - nx * repulsionStrength * otherImpulseFactor);
                other.setVelocityY(other.getVelocityY() - ny * repulsionStrength * otherImpulseFactor);
                
                    // adjust the velocities to avoid them to overlap again
                    // reduce the velocity component that pushes towards the other entity
                double relativeVelX = this.getVelocityX() - other.getVelocityX();
                double relativeVelY = this.getVelocityY() - other.getVelocityY();
                
                // project the relative velocity on the collision normal
                double dotProduct = relativeVelX * nx + relativeVelY * ny;
                
                // if the entities are approaching, invert the collision component
                if (dotProduct < 0) {
                    // restitution coefficient (0.4 = little bounce, more friction)
                    double restitution = 0.4;
                    double impulse = dotProduct * restitution;
                    
                    // adjust the velocities
                    double impulseX = impulse * nx;
                    double impulseY = impulse * ny;
                    
                    this.setVelocityX(this.getVelocityX() - impulseX * thisImpulseFactor);
                    this.setVelocityY(this.getVelocityY() - impulseY * thisImpulseFactor);
                    
                    other.setVelocityX(other.getVelocityX() + impulseX * otherImpulseFactor);
                    other.setVelocityY(other.getVelocityY() + impulseY * otherImpulseFactor);
                }
            }
        }
    }

    // Velocity update

    public void moveLeft() {
        velocityX =- speed;
    }
    
    public void moveRight() {
        velocityX = speed;
    }
    
    public void moveUp() {
        velocityY =- speed;
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

// APPLY knockback
    public void gotKnockback(){
        double vx = 0;
        double vy = 0;
        if(this.getJustKnockBack()){
            this.setJustKnockBack(false);
        }
        if(this.getIsKnockBack()){
            double angle = this.getImpactDirection();
            vx += -(Math.cos(angle) ) * getKnockBackIntensity()/getWeight() * (getKnockBackFrame()/getKnockBackCoolDown());
            vy += -(Math.sin(angle) ) * getKnockBackIntensity()/getWeight() * (getKnockBackFrame()/getKnockBackCoolDown());// Faut faire le calcul pour avoir une belle fonction avec poids etc
        }
        setVelocityX(vx);
        setVelocityY(vy);
    }

    
}