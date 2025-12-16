package entities;

import core.Game;
import core.GameWorld;
import java.awt.Graphics;
import java.util.List;
import map.Map;

public abstract class Enemy extends Moving_Entity{
    private int shootCooldownFrames;
    private int shootCooldown;
    private EnemyStates state ;

    public Enemy(double x, double y, int width, int height, int hp, int attack, int defense,int range,int speed){
        super();
        this.setHP(hp);
        this.setMaxHp(hp);
        this.setAttack(attack);
        this.setDefense(defense);
        this.setRange(range);
        this.setX(x);
        this.setY(y);
        this.setWidthinPixel(width);
        this.setHeightinPixel(height);
        this.setBounds();
        this.setSpeed(speed);
        this.shootCooldownFrames = 0;
        this.shootCooldown = 0;
    }

    protected void setShootCooldownFrames(int frames){
        this.shootCooldownFrames = Math.max(0, frames);
    }

    protected boolean isShootReady(){
        return shootCooldown == 0;
    }

    protected void resetShootCooldown(){
        if (shootCooldownFrames > 0){
            shootCooldown = shootCooldownFrames;
        }
    }

    protected void tickCooldowns(){
        if (shootCooldown > 0){
            shootCooldown--;
        }
    }

    protected void facePlayer(Player player){
        double dx = player.getX() - this.getX();
        double dy = player.getY() - this.getY();
        if (Math.abs(dx) > 0.001 || Math.abs(dy) > 0.001) {
            this.setFacingAngle(Math.atan2(dy, dx));
        }
    }

    public Projectiles updateBehavior(Map map, Player player, List<Moving_Entity> movingEntity, GameWorld gameWorld){
        tickCooldowns();
        TimerUpdate();
        gotKnockback();
        updateMovement(player,map, gameWorld);
        // Note: player should already be in the list from GameWorld
        update_collision_withEntities(movingEntity, map);
        update(map);
        return attemptSpecialAction(player);
    }

    protected abstract void updateMovement(Player player,Map map, GameWorld gameWorld); // I want them to move differently for each enemies

    protected Projectiles attemptSpecialAction(Player player){
        return null;
    }

    public abstract void render(Graphics g,int x, int y , int screenHeight, int screenWidth, int SHADOW_DISTANCE); // Abstract method to make each enemy unique

    public void setEnemyState(EnemyStates state){
        this.state = state;
    }

    public EnemyStates getEnemyStates(){
        return this.state;
    }

    public int[] Djikstra(GameWorld gameWorld, Map map,double ex, double ey){
            int exTile = (int)(ex / map.getTileSize());
            int eyTile = (int)(ey / map.getTileSize());
            
            int bestDist = Integer.MAX_VALUE;
            int bestX = -1;
            int bestY = -1;
    
            int[][] dist = gameWorld.getDistToPlayer();
            if (dist != null && eyTile >= 0 && eyTile < dist.length &&
                exTile >= 0 && exTile < dist[0].length) {
    
                int currentDist = dist[eyTile][exTile];
                
                if (currentDist >= 0) {
                    // Include diagonal for smoother movement
                    final int[][] DIRS = { 
                        {1,0}, {-1,0}, {0,1}, {0,-1},
                        {1,1}, {1,-1}, {-1,1}, {-1,-1}
                    };
                    
                    for (int[] d : DIRS) {
                        int nx = exTile + d[0];
                        int ny = eyTile + d[1];
                        
                        if (nx < 0 || ny < 0 ||
                            nx >= map.getWidthInTiles() || ny >= map.getHeightInTiles()) {
                            continue;
                        }
                        
                        // Verify diagonals
                        boolean isDiagonal = (d[0] != 0 && d[1] != 0);
                        if (isDiagonal) {
                            if (map.isWall(exTile + d[0], eyTile) || map.isWall(exTile, eyTile + d[1])) {
                                continue;
                            }
                        }
                        
                        int val = dist[ny][nx];
                        
                        if (val >= 0 && val < currentDist && val < bestDist) {
                            bestDist = val;
                            bestX = nx;
                            bestY = ny;
                        }
                    }
                }
            }

            return new int[]{bestX,bestY};
    }

}
