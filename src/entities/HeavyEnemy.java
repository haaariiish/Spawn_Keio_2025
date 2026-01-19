package entities;
import java.awt.Graphics;
import java.awt.Color;
import map.Map;
import core.GameWorld;


public class HeavyEnemy extends Enemy {
    public double brighness = 0;

    public HeavyEnemy(double x, double y, int width, int height) {
        super(x, y,width, height, 80, 6, 4,40,(int) Math.round(4+Math.random()));
        setStunCoolDown(10);
    }

    protected void updateMovement(Player player, Map map, GameWorld gameWorld) {
        double vx = getVelocityX();
        double vy = getVelocityY();
        if (!getStun()){   
            double ex = getX() + getWidthInPixels() * 0.5;
            double ey = getY() + getHeightInPixels() * 0.5;
            double px = player.getX() + player.getWidthInPixels() * 0.5;
            double py = player.getY() + player.getHeightInPixels() * 0.5;

            boolean hasLOS = map.hasLineOfSight(ex, ey, px, py);

            if (hasLOS) {
                // Ligne de vue claire → aller tout droit vers le joueur
                double angle = Math.atan2(py - ey, px - ex);
                setFacingAngle(angle);
                double speed = getSpeed();
                vx += Math.cos(angle) * speed;
                vy += Math.sin(angle) * speed;
            } else {
                    // if not seen-able, we use djikstra
                    int[] best = Djikstra(gameWorld, map, ex, ey);
        
                    int bestX = best[0];
                    int bestY = best[1];
                    if (bestX != -1 && bestY != -1) {
                        // calculate the angle to go to the center of the aimed tile
                        double targetCenterX = bestX * map.getTileSize() + map.getTileSize() * 0.5;
                        double targetCenterY = bestY * map.getTileSize() + map.getTileSize() * 0.5;
                        
                        double angle = Math.atan2(targetCenterY - ey, targetCenterX - ex);
                        setFacingAngle(angle);
                        
                        double speed = getSpeed();
                        vx += Math.cos(angle) * speed;
                        vy += Math.sin(angle) * speed;
                    }
                else {    
                    // Fallback : old behavior
                    //System.out.println("else");
                facePlayer(player);
                double speed = getSpeed();
                double angle = this.getFacingAngle();
                
                vx += Math.cos(angle) * speed;
                vy += Math.sin(angle) * speed;   
        }
    }
}
        setVelocityX(vx);
        setVelocityY(vy);
    }

    public void render(Graphics g,int x, int y, int screenHeight, int screenWidth, int SHADOW_DISTANCE, boolean[][] visibilityMap, int subdiv, int subtile){
        int x_subtile =(int) (getX()/subtile);
        int y_subtile =(int) (getY()/subtile);
        if(visibilityMap[y_subtile][x_subtile]){
        // Calculate color without creating new Color every frame
        int knockBackFrame = getKnockBackFrame();
        int green = 255 - 5 * knockBackFrame;
        if (green < 0) green = 0;
        if (green > 255) green = 255;
        
        int screenX = (int)this.getX() - x;
        int screenY = (int)this.getY() - y;
        int width = this.getWidthInPixels();
        int height = this.getHeightInPixels();
        int centerX =screenX+width/2 ;
        int centerY = screenY+height/2;
        int[] xPoints = {centerX +(int) (width*Math.cos(getFacingAngle())), centerX+ +(int) (width*Math.cos(getFacingAngle()-Math.PI/2)/1.5) , centerX +(int) (width*Math.cos(getFacingAngle()+Math.PI/2)/1.5)};
        int[] yPoints = {centerY+(int) (height*Math.sin(getFacingAngle())), centerY+(int) (height*Math.sin(getFacingAngle()-Math.PI/2)/1.5)   ,centerY +(int) (height*Math.sin(getFacingAngle()+Math.PI/2)/1.5)}; 
        //System.out.println(screenWidth+screenHeight);
        //System.out.println(screenWidth);
        //System.out.println((1 - 2*Math.sqrt((screenX) *(screenX)  + (screenY)*(screenY))/ (screenWidth+screenHeight)));
        brighness = Math.max(1 - Math.sqrt((centerX-screenWidth/2) *(centerX-screenWidth/2)  + (centerY-screenHeight/2)*(centerY-screenHeight/2)) / SHADOW_DISTANCE,0);
        g.setColor(Color.BLUE); 
        g.setColor(new Color((int)(brighness*g.getColor().getRed()), (int) (g.getColor().getGreen()*brighness), (int) (brighness*g.getColor().getBlue())));
        g.fillPolygon(xPoints, yPoints, 3); 
        g.setColor(new Color((int)(brighness*100), (int)(brighness*green), 0));
        g.fillOval(screenX, screenY, width, height);
        g.setColor(Color.WHITE);
        g.setColor(new Color((int)(brighness*g.getColor().getRed()), (int) (g.getColor().getGreen()*brighness), (int) (brighness*g.getColor().getBlue())));
        g.drawOval(screenX, screenY, width, height);
        }
        //g.drawRect(screenX, screenY, width, height); // hitbox
    }
}

