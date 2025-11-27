package map;
import map.Map;
import java.util.Random;

public class MapGenPerlin {

    // Trying to do a Map generation using a Perlin's Noise as in Minecraft
    static final int WIDTH = 1000;
    static final int HEIGHT = 1000;

    private int height;
    private int width;
    static final long SEED = 12345;
    public int[][] tiles;

    public MapGenPerlin(int h, int w) {
        this.height = h;
        this.width = w;
        tiles = new int[height][width];
        PerlinNoise noiseGen = new PerlinNoise(SEED);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double structure = getFractalNoise(noiseGen, x, y, 0.05, 4);
                double content = getFractalNoise(noiseGen, x + 5000, y + 5000, 0.08, 2);
                tiles[y][x] = getBlockID(structure, content);
            }
        }
        placeSpawn();

    }
    
    public int[][] getTiles(){
        return this.tiles;
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
        
    if (structure > 0.10) {
        return Map.WALL; 
    }
    
    if (content < 0.25) {
        return Map.EMPTY;
    }

    if (content > 0.60) {
        return Map.WATER; 
    }
    if (content > 0.90) {
        return Map.SPIKE; 
    }
    return Map.ENEMY_SPAWN; 
}

    public void placeSpawn(){
        double proba = 0.3;
        while(true){
        System.out.println("La boucle continue ");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[y][x]==Map.EMPTY){
                    System.out.println("x = "+x +" and y =" +y);
                }
                if(Math.random()<proba&&tiles[y][x]==Map.EMPTY){
                    tiles[y][x]=Map.SPAWN;
                    return;
                }
            }
        }
    }
}
}



