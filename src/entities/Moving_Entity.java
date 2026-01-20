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
        int size = moving_Entities.size();
        Rectangle thisBounds = this.getBounds();
        double thisX = this.getX();
        double thisY = this.getY();
        int thisWidth = this.getWidthInPixels();
        int thisHeight = this.getHeightInPixels();
        double thisCenterX = thisX + thisWidth * 0.5;
        double thisCenterY = thisY + thisHeight * 0.5;
        
        for (int i = 0; i < size; i++) {
            Moving_Entity other = moving_Entities.get(i);
            if (other == this) {
                continue;
            }
            
            Rectangle otherBounds = other.getBounds();
            if (!thisBounds.intersects(otherBounds)) {
                continue;
            }
            
            // Calculate other entity properties once
            double otherX = other.getX();
            double otherY = other.getY();
            int otherWidth = other.getWidthInPixels();
            int otherHeight = other.getHeightInPixels();
            double otherCenterX = otherX + otherWidth * 0.5;
            double otherCenterY = otherY + otherHeight * 0.5;
            
            // Separation vector
            double dx = thisCenterX - otherCenterX;
            double dy = thisCenterY - otherCenterY;
            double distanceSq = dx * dx + dy * dy;
            
            // Avoid division by zero
            if (distanceSq < 0.000001) {
                dx = Math.random() - 0.5;
                dy = Math.random() - 0.5;
                distanceSq = dx * dx + dy * dy;
            }
            double distance = Math.sqrt(distanceSq);
            double invDistance = 1.0 / distance;
            double nx = dx * invDistance;
            double ny = dy * invDistance;
            
            // Calculate overlap
            double overlapX = Math.min(thisX + thisWidth - otherX, otherX + otherWidth - thisX);
            double overlapY = Math.min(thisY + thisHeight - otherY, otherY + otherHeight - thisY);
            double minOverlap = Math.min(overlapX, overlapY);
            
            // Calculate weights
            double thisWeight = this.getWeight();
            double otherWeight = other.getWeight();
            double totalWeight = (thisWeight + otherWeight < 0.001) ? 2.0 : (thisWeight + otherWeight);
            double thisImpulseFactor = otherWeight / totalWeight;
            double otherImpulseFactor = thisWeight / totalWeight;
            
            // Limit separation
            double separationDistance = Math.min(minOverlap * 0.5, 2.0);
            double thisSeparation = separationDistance * thisImpulseFactor;
            double otherSeparation = separationDistance * otherImpulseFactor;
            
            // Try to separate entities (check walls)
            if (map != null) {
                double newX = thisX + nx * thisSeparation;
                double newY = thisY + ny * thisSeparation;
                if (!map.collidesWithWall((int)newX, (int)newY, thisWidth, thisHeight)) {
                    this.setX(newX);
                    this.setY(newY);
                    this.setBounds();
                }
                
                double otherNewX = otherX - nx * otherSeparation;
                double otherNewY = otherY - ny * otherSeparation;
                if (!map.collidesWithWall((int)otherNewX, (int)otherNewY, otherWidth, otherHeight)) {
                    other.setX(otherNewX);
                    other.setY(otherNewY);
                    other.setBounds();
                }
            }
            
            // Apply repulsion force
            double repulsionStrength = Math.min(minOverlap * 0.3, 5.0);
            double repulseX = nx * repulsionStrength;
            double repulseY = ny * repulsionStrength;
            
            double thisVx = this.getVelocityX();
            double thisVy = this.getVelocityY();
            double otherVx = other.getVelocityX();
            double otherVy = other.getVelocityY();
            
            this.setVelocityX(thisVx + repulseX * thisImpulseFactor);
            this.setVelocityY(thisVy + repulseY * thisImpulseFactor);
            other.setVelocityX(otherVx - repulseX * otherImpulseFactor);
            other.setVelocityY(otherVy - repulseY * otherImpulseFactor);
            
            // Adjust velocities if approaching
            double relativeVelX = thisVx - otherVx;
            double relativeVelY = thisVy - otherVy;
            double dotProduct = relativeVelX * nx + relativeVelY * ny;
            
            if (dotProduct < 0) {
                double impulse = dotProduct * 0.4;
                double impulseX = impulse * nx;
                double impulseY = impulse * ny;
                
                this.setVelocityX(this.getVelocityX() - impulseX * thisImpulseFactor);
                this.setVelocityY(this.getVelocityY() - impulseY * thisImpulseFactor);
                other.setVelocityX(other.getVelocityX() + impulseX * otherImpulseFactor);
                other.setVelocityY(other.getVelocityY() + impulseY * otherImpulseFactor);
            }
        }
    }

    // Velocity update

    public void moveLeft(double speed) {
        velocityX =- speed;
    }
    
    public void moveRight(double speed) {
        velocityX = speed;
    }
    
    public void moveUp(double speed) {
        velocityY =- speed;
    }
    
    public void moveDown(double speed) {
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