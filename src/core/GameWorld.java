package core;

import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

import input.InputHandler;
import entities.Basic_Entity;
import entities.ChargerEnemy;
import entities.Enemy;
import entities.HeavyEnemy;
import entities.Moving_Entity;
import entities.Player;
import entities.Projectiles;
import entities.RangedEnemy;
import entities.Simple_Projectiles;
import map.Map;

import java.awt.Point;

//A new class to divide the management of the game structure and the global management done by game
public class GameWorld {
    private Player player;
    
    
    private Map map;
    private List<Enemy> enemies;
    private Game game;

    private double spawnEnemyproba=0.05;
    private int score=0;
    //private int initialEnemy =1; not used
    private int maxEnemy ;
    private int remainingEnemies = 20;
    private int wave=1;
    private int currentWave = 0;
    private final int SPIKEDAMAGE=3;
    private final int COOLDOWNSPIKES = 120;

    //private Boss currentBoss;
    private List<Projectiles> projectilesList;
    //private List<Projectile> enemyProjectiles;
    //private List<Item> items;
    //private List<Particle> particles;

    //private Map<String, Enemy> enemyMap;
    

    private int worldWidth;
    private int worldHeight;
    private int tileSize;
    
    private static final int PLAYER_SHOOT_COOLDOWN_FRAMES = 5;
    private int playerShootCooldown = 0;

    private int playerDamageCooldown = 0;
    private static final int PLAYER_DAMAGE_COOLDOWN_FRAMES = 10;
    
    // Reusable temporary list to avoid allocations
    private final List<Moving_Entity> tempMovingEntitiesList = new ArrayList<>();

    // Pathfinding / distance-to-player grid (in tile coordinates)
    // -1 means unreachable / not yet computed
    private int[][] distToPlayer = null;
    private int lastPathFrame = -1000;
    private int lastPlayerTileX = -1;
    private int lastPlayerTileY = -1;
    private boolean pathDirty = true;
    private double statMultiplier = 0;

    public GameWorld(int worldWidth, int worldHeight, int tileSize,Game game) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.game = game;
        this.tileSize = tileSize;
        
    
        // Spawn of some Ennemy (to implement)
        //spawnEnemy(200, 200);
        //spawnEnemy(300, 250);
    }

    public void reset(){
        this.player = null;
        this.enemies = null;
        this.score = 0;
        this.wave = 1;
        this.statMultiplier = 1;
        this.remainingEnemies = 20;
        this.maxEnemy = remainingEnemies/4 + wave;

        
    }

    public void restart(int totalSteps){
        int progression = 1;
        // Initialisation of the map ( default map for now)
        this.score = 0;
        this.wave=1;
        this.statMultiplier = 1;
        this.remainingEnemies = 20;
        this.maxEnemy = remainingEnemies/4+wave;
        
        
        this.playerShootCooldown = 0;
        this.playerDamageCooldown = 0;
        game.updateLoadingProgress(progression,totalSteps);
        progression+=1;
        //  spawn point
        Point spawn = changeMap(this.worldWidth,this.worldHeight);

        game.updateLoadingProgress(progression,totalSteps);
        progression+=1;



        player = new Player(spawn.x, spawn.y,this.tileSize/2-1,this.tileSize/2-1,50,10,1,this.tileSize*5/2);
        game.updateLoadingProgress(progression,totalSteps);
    }

    public void nextWave(){
        this.wave +=1;
        this.statMultiplier *= Math.exp(0.04);
        this.remainingEnemies = (int)(20*Math.pow(1.07, wave));
        this.maxEnemy = Math.min(remainingEnemies/4 + wave,20);

        
        Point spawn = changeMap(worldWidth, worldHeight);
        player.setX(spawn.x);
        player.setY(spawn.y);
        player.healing(player.getMaxHP()/4);
    }

    public Point changeMap(int width, int height){
        this.getGame().getFrame().getGamePanel().setCacheMapinitialised(false);
        map = new Map(width/ this.tileSize, height / this.tileSize, this.tileSize,game.getSubDivision());
        map.createMapPerlinNoise(this.getGame().getOpenTime());
        
        tempMovingEntitiesList.clear();
        enemies = new ArrayList<>();
        projectilesList = new ArrayList<>();
        distToPlayer = null;
        pathDirty = true;
        lastPathFrame = -1000;
        lastPlayerTileX = -1;
        lastPlayerTileY = -1;
        //initialEnemy = 1;

        
        // Respawning and setting the player
        return map.getSpawnPoint();
    }

    public void update(InputHandler input, int which_frame_in_cycle) {
        // For debugging reason I had this following line to check if some adds have been mades in higher waves
        if(input.isNextWaveCheatPressed()){
            score += remainingEnemies*wave;
            this.nextWave();
        }

        if(input.isInteractPressed()){
            if (remainingEnemies == 0 && enemies.size() == 0 && map.getTileAtPixel((int) player.getX(), (int) player.getY())==Map.GATELEVEL) {
                this.getGame().changeGameState(GameState.WAVE_LOADING);
            }}
        // Reuse temporary list instead of creating new one
        tempMovingEntitiesList.clear();
        tempMovingEntitiesList.addAll(this.enemies);
        this.player.update_input(this.map, input, tempMovingEntitiesList);

        // --- Pathfinding update (distance-to-player grid) ---
        if (player != null && map != null) {
            // Use player center to determine tile, not top-left, for better pathing
            double playerCenterX = player.getX() + player.getWidthInPixels() * 0.5;
            double playerCenterY = player.getY() + player.getHeightInPixels() * 0.5;
            int pxTile = (int)(playerCenterX / tileSize);
            int pyTile = (int)(playerCenterY / tileSize);

            // Mark path as dirty only if the player moved enough in tile space
            if (lastPlayerTileX == -1 || lastPlayerTileY == -1 ||
                Math.abs(pxTile - lastPlayerTileX) + Math.abs(pyTile - lastPlayerTileY) >= 2) {
                pathDirty = true;
                lastPlayerTileX = pxTile;
                lastPlayerTileY = pyTile;
            }

            // Recompute distances at most every 60 frames, and only when needed
            if (which_frame_in_cycle - lastPathFrame >= 60 && pathDirty) {
                recomputeDistances(pxTile, pyTile);
                lastPathFrame = which_frame_in_cycle;
                pathDirty = false;
                //System.out.println(distToPlayer);
            }
        }
        //System.out.println("Player bounds: " + this.player.getBounds());
        updateEnemiesPosition(this.map,this.player);
        if (which_frame_in_cycle%30==0){ // If we are in a precise moment, the game will try to make spawn some enemies
        updateEnemiesSpawn(this.map);
        }
        if (playerShootCooldown > 0) {
            playerShootCooldown--;
        }
        
        handlePlayerShooting(input);
        updateProjectiles();
        calculateAllSpikeDamage(which_frame_in_cycle);
        
        handleCollisions();
        if (playerDamageCooldown > 0) {
            playerDamageCooldown--;
        }
        updateEnemiesDeath();
        checkPlayerStatus();

    }


    //GETTERS

    public Player getPlayer(){
        return this.player;
    }

    public List<Enemy> getEnemy(){
        return this.enemies;
    }

    public Map getMap(){
        return this.map;
    }
    public int getWave(){
        return wave;
    }

    public Game getGame(){
        return this.game;
    }

    public int getWidthInPixels(){
        return this.worldWidth;
    }

    public int getHeightInPixels(){
        return this.worldHeight;
    }

    public int getTileSize(){
        return this.tileSize;
    }

    public int getScore(){
        return this.score;
    }

    public int getRemainingEnemies(){
        return remainingEnemies;
    }
    

    public List<Projectiles> getListProjectiles(){
        return this.projectilesList;
    }

    // Distance grid getter for enemies (in tile coordinates)
    // distToPlayer[y][x] gives the distance in tiles from (x,y) to the player, or -1 if unreachable
    public int[][] getDistToPlayer(){
        return this.distToPlayer;
    }


    //SETTERS
    // UPDATERS OF FEATURES

    public void playerShoot(){
        Projectiles proj ;
        switch(player.getProjectilesTypes()){
            case Simple_Projectiles:
                proj = new Simple_Projectiles(player, (player.getWidthInPixels()+1)/2, (player.getWidthInPixels()+1)/2, 0.95);
                break;
            default:
                proj = new Simple_Projectiles(player, 3, 3,2);
                break;
        }
        projectilesList.add(proj);
        
    }
    public void updateProjectiles(){
        int size = projectilesList.size();
        for (int i = size - 1; i >= 0; i--) {
            Projectiles proj = projectilesList.get(i);
            proj.update(map);
            // setBounds() is already called inside update(), no need to call it again
            if (proj.toDestroy()) {
                projectilesList.remove(i);
            }
        }
    }
    public void updateEnemiesPosition(Map map, Player player){
        // Reuse temporary list for all enemies
        tempMovingEntitiesList.clear();
        tempMovingEntitiesList.addAll(enemies);
        tempMovingEntitiesList.add(player);
        
        int size = enemies.size();
        for (int i = size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            Projectiles shot = enemy.updateBehavior(map, player, tempMovingEntitiesList,this);
            if (shot != null) {
                projectilesList.add(shot);
            }
        }
    }
 
    public void updateEnemiesSpawn(Map map){
        if (enemies.size() > maxEnemy||remainingEnemies==0) {
            return;
        }
        List<Point> spawnPoints = map.getEnemySpawnPoints();
        int size = spawnPoints.size();
        for (int i = size - 1; i >= 0; i--) {
            if(remainingEnemies<=0){
                return;
            }
            if (enemies.size() > this.maxEnemy) {
                return;
            }
            if (Math.random() < this.spawnEnemyproba) {
                Enemy n_enemy = createRandomEnemy(spawnPoints.get(i));
                if (n_enemy != null) {
                    enemies.add(n_enemy);
                    remainingEnemies-=1;
                }
            }
            
        }
    }

    public void updateEnemiesDeath(){
        int size = enemies.size();
        for (int i = size - 1; i >= 0; i--) {
            if (enemies.get(i).getIsDead()){
                enemies.remove(i);
                score+= wave;
                
            }
        }
        
    }

    public void handleCollisions(){
        Rectangle playerBounds = player.getBounds();
        int projSize = projectilesList.size();
        
        for (int i = projSize - 1; i >= 0; i--) {
            Projectiles proj = projectilesList.get(i);
            Rectangle projBounds = proj.getBounds();
            boolean hitSomething = false;
            
            if (projBounds.intersects(playerBounds)) {
                player.take_damage(proj.getSourceEntity().getAttack());
                player.setImpactDirection(proj.getDirectionAngle());
                hitSomething = true;
            }

            if (!hitSomething) {
                int enemySize = enemies.size();
                for (int j = enemySize - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    if (proj.getSourceEntity() != enemy && projBounds.intersects(enemy.getBounds())) {
                        int knockBackIntensity = enemy.getKnockBackIntensity() + (int)(proj.getKnockBack() * 10);
                        enemy.setJustKnockBack(true);
                        enemy.setKnockBack(true);
                        enemy.setStun(true);
                        enemy.setKnockBackFrame(enemy.getKnockBackCoolDown());
                        enemy.setStunFrame(enemy.getStunCoolDown());
                        enemy.setKnockBackIntensity(knockBackIntensity);
                        enemy.setImpactDirection(proj.getDirectionAngle());
                        enemy.take_damage(proj.getSourceEntity().getAttack());
                        hitSomething = true;
                        break; // Only one hit per projectile
                    }
                }
            }
            
            if (hitSomething) {
                projectilesList.remove(i);
            }
        }
        
        if (playerDamageCooldown == 0) {
            Rectangle playerRect = player.getBounds();
            int enemySize = enemies.size();
            for (int j = enemySize - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (enemy.getBounds().intersects(playerRect)) {
                    player.take_damage(enemy.getAttack());
                }
            }
            playerDamageCooldown = PLAYER_DAMAGE_COOLDOWN_FRAMES;
        }

    }

    private void checkPlayerStatus(){
        if (player == null) {
            return;
        }
        if (player.getIsDead() || player.getHP() <= 0) {
            if (projectilesList != null) {
                projectilesList.clear();
            }
            game.changeGameState(GameState.GAMEOVER);
        }
    }

    private void handlePlayerShooting(InputHandler input){
        if (player == null) {
            return;
        }
        boolean attemptedShot = false;
        Double aimAngle = null;

        Double arrowAngle = input.getShootAngleFromArrows();
        if (arrowAngle != null) {
            aimAngle = arrowAngle;
            attemptedShot = true;
        } else if (input.isMouseShootPressed()) {
            aimAngle = determineMouseAngle(input);
            attemptedShot = aimAngle != null;
        } else if (input.isShootPressed()) {
            aimAngle = player.getFacingAngle();
            attemptedShot = true;
        }

        if (attemptedShot && aimAngle != null && playerShootCooldown == 0) {
            player.setFacingAngle(aimAngle);
            playerShoot();
            playerShootCooldown = PLAYER_SHOOT_COOLDOWN_FRAMES;
        }
    }

    private Double determineMouseAngle(InputHandler input){
        if (player == null) {
            return null;
        }
        if (game == null || game.getFrame() == null) {
            return null;
        }
        java.awt.Component panel = game.getFrame().getGamePanel();
        if (panel == null) {
            return null;
        }
        java.awt.Point mouse = input.getMousePosition();
        if (mouse == null) {
            return null;
        }

        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();
        if (panelWidth == 0 || panelHeight == 0) {
            return null;
        }

        double playerCenterX = player.getX() + player.getWidthInPixels() / 2.0;
        double playerCenterY = player.getY() + player.getHeightInPixels() / 2.0;
        double cameraX = playerCenterX - panelWidth / 2.0;
        double cameraY = playerCenterY - panelHeight / 2.0;

        double targetX = cameraX + mouse.x;
        double targetY = cameraY + mouse.y;

        double dx = targetX - playerCenterX;
        double dy = targetY - playerCenterY;

        if (Math.abs(dx) < 0.001 && Math.abs(dy) < 0.001) {
            return null;
        }

        return Math.atan2(dy, dx);
    }

    // -------------------------------------------------------------------------
    // Pathfinding helpers (Dijkstra from player position on the tile grid)
    // -------------------------------------------------------------------------

    private void recomputeDistances(int startTileX, int startTileY){
        if (map == null) {
            return;
        }

        int w = map.getWidthInTiles();
        int h = map.getHeightInTiles();

        if (distToPlayer == null || distToPlayer.length != h || distToPlayer[0].length != w) {
            distToPlayer = new int[h][w];
        }

        // Initialize distances to -1 (unreachable)
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                distToPlayer[y][x] = -1;
            }
        }

        // If the starting tile is not walkable for enemies, nothing to do
        if (map.isWall(startTileX, startTileY)) {
            return;
        }

        java.util.ArrayDeque<int[]> queue = new java.util.ArrayDeque<>();
        distToPlayer[startTileY][startTileX] = 0;
        queue.add(new int[]{startTileX, startTileY});

        final int[][] DIRS = { {1,0}, {-1,0}, {0,1}, {0,-1}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int cx = cur[0];
            int cy = cur[1];
            int baseDist = distToPlayer[cy][cx];

            for (int[] d : DIRS) {
                int nx = cx + d[0];
                int ny = cy + d[1];

                if (nx < 0 || ny < 0 || nx >= w || ny >= h) {
                    continue;
                }
                if (map.isWall(nx, ny)) {
                    continue;
                }
                if (distToPlayer[ny][nx] != -1) {
                    continue;
                }
                int penalty = 0;

                if(map.getTileAt(nx, ny)==Map.SPIKE){
                    penalty = 5;
                    
                }
                distToPlayer[ny][nx] = baseDist + 1 + penalty;
                queue.add(new int[]{nx, ny});
            }
        }
    }

    private Enemy createRandomEnemy(Point spawnPoint){
        double roll = Math.random();
        if (roll < 0.35) {
            ChargerEnemy charger = new ChargerEnemy(spawnPoint.x, spawnPoint.y, this.tileSize/2-1,this.tileSize/2-1, statMultiplier);
            return charger;
        } else if (roll < 0.7) {
            RangedEnemy ranged = new RangedEnemy(spawnPoint.x, spawnPoint.y, this.tileSize/2-1,this.tileSize/2-1, this.tileSize, statMultiplier);
            return ranged;
        } else {
            HeavyEnemy heavy = new HeavyEnemy(spawnPoint.x, spawnPoint.y, this.tileSize/2-1,this.tileSize/2-1, statMultiplier);
            return heavy;
        }
    }

    private void inflictSpikeDamage(Basic_Entity entity, int spikeFrame){
        if((spikeFrame<0)||(spikeFrame-entity.getSpikeFrame()<COOLDOWNSPIKES)){
            return;
        }
        else{
            if(map.getTileAtPixel((int) entity.getX(), (int) entity.getY())==Map.SPIKE){
                entity.take_damage(SPIKEDAMAGE);
                entity.setSpikeFrame(spikeFrame);
            }
        }
        
    }

    private void calculateAllSpikeDamage(int which_frame){
        for(int k=0;k< tempMovingEntitiesList.size();k++){
            inflictSpikeDamage(tempMovingEntitiesList.get(k), which_frame);
        }
    }


    public int getMaxEnemy(){
        return this.maxEnemy;
    }

    public void setMaxEnemy(int me){
        this.maxEnemy = me;
    }

    public void setRemainingEnemies(int Re){
        this.remainingEnemies = Re;
        }

    public void setScore( int a){
        score =a;
    }

    public void setStatMultiplier(double a){
        statMultiplier =a;
    }

    public double getStatMultiplier(){
        return statMultiplier ;
    }


}

