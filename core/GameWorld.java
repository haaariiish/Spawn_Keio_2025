package core;

import java.util.ArrayList;
import java.util.List;

import input.InputHandler;
import entities.Boss;
import entities.Enemy;
import entities.Player;
import entities.Projectiles;
import entities.Simple_Projectiles;
import map.Map;

import java.awt.Point;

//A new class to divide the management of the game structure and the global management done by game
public class GameWorld {
    private Player player;
    private Map map;
    private List<Enemy> enemies;
    private Game game;

    //private float spawn proabilities;

    
    //private Boss currentBoss;
    private List<Projectiles> projectilesList;
    //private List<Projectile> enemyProjectiles;
    //private List<Item> items;
    //private List<Particle> particles;

    //private Map<String, Enemy> enemyMap;

    private int worldWidth;
    private int worldHeight;
    private int tileSize;

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
        
    }

    public void restart(){
        // Initialisation of the map ( default map for now)
        map = new Map(this.worldWidth / this.tileSize, this.worldHeight / this.tileSize, this.tileSize);
        map.createDefaultMap();

        // Initialisation of lists

        enemies = new ArrayList<>();
        projectilesList = new ArrayList<>();
        
        //  spawn point
        Point spawn = map.getSpawnPoint();
        player = new Player(spawn.x, spawn.y,20,20,100,1,1,100);
    }

    public void update(InputHandler input){
        this.player.update_input(this.map, input);

        if (input.isShootPressed()){
            playerShoot();
        }
        updateProjectiles();
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

    public List<Projectiles> getListProjectiles(){
        return this.projectilesList;
    }


    //SETTERS

    // UPDATERS OF FEATURES

    public void playerShoot(){
        Projectiles proj ;
        switch(player.getProjectilesTypes()){
            case Simple_Projectiles:
                proj = new Simple_Projectiles(player, 5, 5, 1);
                break;
            default:
                proj = new Simple_Projectiles(player, 3, 3, 5);
                break;
        }
        projectilesList.add(proj);
        
    }
    public void updateProjectiles(){
        for (int i = projectilesList.size() - 1; i >= 0; i--) {
            Projectiles proj = projectilesList.get(i);
            proj.update(map);
            if (proj.toDestroy()) {
                projectilesList.remove(i);
            }
        }
    }
    //public void update_enemy(){}
    // public void handleCollisions(){}

}
