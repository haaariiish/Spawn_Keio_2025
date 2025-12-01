package map;

public class Room {
    public int id;
    public int size; // Nombre de blocs
    public int centerX, centerY; // Pour relier les salles plus tard
    
    public Room(int id) { this.id = id; }
}