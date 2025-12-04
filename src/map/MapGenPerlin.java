package map;

import java.util.Random;
import java.util.Queue;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;



public class MapGenPerlin {

    // Trying to do a Map generation using a Perlin's Noise as in Minecraft
    static final int WIDTH = 1000;
    static final int HEIGHT = 1000;

    private int height;
    private int width;
    static final long SEED = 12345;
    public int[][] tiles;
    public long seed = SEED;
    private List<Room> rooms_coordinates;

    public MapGenPerlin(int h, int w, long seed) {
        this.height = h;
        this.width = w;
        this.seed = seed;
        tiles = new int[height][width];
        PerlinNoise noiseGen = new PerlinNoise(this.seed);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double structure = getFractalNoise(noiseGen, x, y, 0.1, 4);
                double content = getFractalNoise(noiseGen, x + 5000, y + 5000, 0.08, 2);
                tiles[y][x] = getBlockID(structure, content);
            }
        }
        rooms_coordinates = createCorridor();
        placeMapBound();
        placeSpawn();
        for (int i=0; i<rooms_coordinates.size();i++){
        System.out.println(rooms_coordinates.get(i).id);
        }
    }
    
    public int[][] getTiles(){
        return this.tiles;
    }

    public List<Room> getRooms(){
        return this.rooms_coordinates;
    }

    static double getFractalNoise(PerlinNoise gen, double x, double y, double frequency, int octaves) {
        double total = 0;
        double amplitude = 1;
        double maxValue = 0;  

        for(int i=0; i<octaves; i++) {
            total += gen.noise(x * frequency, y * frequency) * amplitude;
            maxValue += amplitude;
            
            amplitude *= 0.5; // Persistence
            frequency *= 2.0; // Lacunarity
        }
        return total / maxValue;
    }

    static class PerlinNoise {
        int[] p = new int[512];
        public PerlinNoise(long seed) {
            Random r = new Random(seed);
            for(int i=0; i<256; i++) p[i] = i;
            for(int i=0; i<256; i++) { // Shuffle
                int swapIdx = r.nextInt(256);
                int temp = p[i]; p[i] = p[swapIdx]; p[swapIdx] = temp;
            }
            for(int i=0; i<256; i++) p[256+i] = p[i]; // Duplication 
        }
    
    public double noise(double x, double y) {
        int X = (int)Math.floor(x) & 255;
        int Y = (int)Math.floor(y) & 255;
        x -= Math.floor(x);
        y -= Math.floor(y);
        double u = fade(x);
        double v = fade(y);
        int A = p[X]+Y, B = p[X+1]+Y;
        return lerp(v, lerp(u, grad(p[A], x, y), grad(p[B], x-1, y)),
                       lerp(u, grad(p[A+1], x, y-1), grad(p[B+1], x-1, y-1)));
    }


    
    double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
    double lerp(double t, double a, double b) { return a + t * (b - a); }
    double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h<8 ? x : y, v = h<4 ? y : h==12||h==14 ? x : 0;
        return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
    }


}
    public int getBlockID(double structure, double content) {
        
    if (structure < 0.1) {
        return Map.WALL; 
    }
    
    if (content < 0.01) {
        return Map.ENEMY_SPAWN;
    }

    if (content > 0.30) {
        return Map.SPIKE; 
    }
    if (content > 0.90) {
        return Map.WATER; 
    }
    return Map.EMPTY; 
}

    public void placeSpawn(){
        double proba = 0.3;
        // Remove infinite loop and System.out.println to reduce CodeCache usage
        for (int attempt = 0; attempt < 1000; attempt++) { // Max 1000 attempts
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (Math.random() < proba && tiles[y][x] == Map.EMPTY) {
                        tiles[y][x] = Map.SPAWN;
                        return;
                    }
                }
            }
        }
        // Fallback: place spawn at first empty tile found
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[y][x] == Map.EMPTY) {
                    tiles[y][x] = Map.SPAWN;
                    return;
                }
            }
        }
    }
    public void placeMapBound(){
        for(int y = 0; y < height; y++){
            tiles[y][0] =Map.WALL;
            tiles[y][width-1] =Map.WALL;
        }
        for(int x = 0; x < width; x++){
            tiles[0][x] = Map.WALL;
            tiles[height-1][x ] = Map.WALL;
        }
    }

    public List<Room> createCorridor(){
        this.rooms_coordinates = identifyRooms(tiles, width, height, 10);
        for (int i=0;i<rooms_coordinates.size();i++ ){
            Room room1 = rooms_coordinates.get(i);
            for (int j=0;j<rooms_coordinates.size();j++){

                if(j!=i){
                    Room room2 = rooms_coordinates.get(j);
                    if ((room2.centerX-room1.centerX)*(room2.centerX-room1.centerX) + (room2.centerY-room1.centerY)*(room2.centerY-room1.centerY)<1000) {
                        createConnectionSimple(room1.centerX,room1.centerY,room2.centerX,room2.centerY, tiles, 0);
                    }
                }
            }
        }   
        return rooms_coordinates;
    }

    // Reuse Random instance to avoid allocations
    private static final Random connectionRandom = new Random();
    
    public void createConnectionSimple(int x1, int y1, int x2, int y2, int[][] tiles, float randomness){
        Random r = connectionRandom;
        int currentX = x1;
        int currentY = y1;

        // avoid a infinite loop
        int maxSteps = (Math.abs(x1 - x2) + Math.abs(y1 - y2)) * 3; 
        int steps = 0;

        while ((currentX != x2 || currentY != y2) && steps < maxSteps) {
            int dx = x2 - currentX;
            int dy = y2 - currentY;



            boolean moveX = false;
            boolean moveY = false;
            float totalDist = Math.abs(dx) + Math.abs(dy);


            if (dx == 0) moveY = true;
            else if (dy == 0) moveX = true;
            else {
                
                float chanceX = Math.abs(dx) / totalDist;
                
                
                if (r.nextFloat() < chanceX) {
                    
                    if (r.nextFloat() > randomness) moveX = true; 
                    else moveY = true; 
                } else {
                    
                    if (r.nextFloat() > randomness) moveY = true; 
                    else moveX = true; 
                }
            }

            

            if (moveX) {
                currentX += Integer.signum(dx);
            } else if (moveY) {
                currentY += Integer.signum(dy);
            }

            if (tiles[currentY][currentX]==Map.WALL){
                tiles[currentY][currentX] = Map.EMPTY;
            }

            steps++;
    }
}


        // Structure légère pour stocker les infos d'une salle sans copier tous les blocs 
        public static List<Room> identifyRooms(int[][] map, int width, int height, int minRoomSize) {
            List<Room> rooms = new ArrayList<>();
            
            // IDs Table to know which tiles are in which "rooms"
            // 0 = visited, >0 = ID of the room
            int[][] roomIds = new int[width][height]; 
            int nextId = 1;
    
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    
                    // if EMPTY and not processed
                    if (map[x][y] != Map.WALL && roomIds[x][y] == 0) {
                        
                        // FLOOD FILL
                        Room currentRoom = new Room(nextId);
                        int tilesCount = 0;
                        long sumX = 0, sumY = 0; // calculate the center
    
                        
                        Queue<Integer> q = new ArrayDeque<>();
                        q.add(x + y * width);
                        roomIds[x][y] = nextId;
    
                        while (!q.isEmpty()) {
                            int index = q.poll();
                            int cx = index % width;
                            int cy = index / width;
    
                            tilesCount++;
                            sumX += cx;
                            sumY += cy;
    
                            // verify neighbors
                            checkNeighbor(map, roomIds, width, height, q, cx + 1, cy, nextId);
                            checkNeighbor(map, roomIds, width, height, q, cx - 1, cy, nextId);
                            checkNeighbor(map, roomIds, width, height, q, cx, cy + 1, nextId);
                            checkNeighbor(map, roomIds, width, height, q, cx, cy - 1, nextId);
                        }
                        if (tilesCount >= minRoomSize) {
                            // if it s a true room 
                            currentRoom.size = tilesCount;
                            currentRoom.centerX = (int)(sumX / tilesCount);
                            currentRoom.centerY = (int)(sumY / tilesCount);
                            rooms.add(currentRoom);
                            nextId++;
                        } else {
                            // if too small we don't keep it
                            fillRoomWithWall(map, roomIds, x, y, width,height, nextId); 
                            
                        }
                    }
                }
            }
            return rooms;
        }

        private static void checkNeighbor(int[][] map, int[][] roomIds, int w, int h, Queue<Integer> q, int x, int y, int id) {
            if (x >= 0 && x < w && y >= 0 && y < h) {
                if (map[x][y] != Map.WALL /* EMPTY */ && roomIds[x][y] == 0) {
                    roomIds[x][y] = id; // Marquer comme visité tout de suite
                    q.add(x + y * w);
                }
            }
        }
        private static void fillRoomWithWall(int[][] map, int[][] roomIds, int startX, int startY, int w,int h, int idToFill) {

                        
                        Queue<Integer> q = new ArrayDeque<>();
                        q.add(startX + startY * w);
                        
                        while (!q.isEmpty()) {
                            int index = q.poll();
                            int cx = index % w;
                            int cy = index / w;
                            if (map[cx][cy] == Map.WALL /* WALL */) {
                                continue;
                            }                            
                            map[cx][cy] = Map.WALL; // WALL
                    
                            //we check only the neighbor with the same Id
                            pushIfSameRoom(q, map, roomIds, cx + 1, cy, w, h, idToFill);
                            pushIfSameRoom(q, map, roomIds, cx - 1, cy, w, h, idToFill);
                            pushIfSameRoom(q, map, roomIds, cx, cy + 1, w, h, idToFill);
                            pushIfSameRoom(q, map, roomIds, cx, cy - 1, w, h, idToFill);
                        }
                    }
                    
                    // utils for the loop
                    private static void pushIfSameRoom(Queue<Integer> q, int[][] map, int[][] roomIds, int x, int y, int w, int h, int targetId) {
                        // Check map boundaries
                        if (x >= 0 && x < w && y >= 0 && y < h) {
                           // if the same room and still empty
                            if (roomIds[x][y] == targetId && map[x][y] !=Map.WALL /* EMPTY */) {
                                q.add(x + y * w);
                            }
                        }
                    }
       

    }



