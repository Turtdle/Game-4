package game;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import userinterface.Inventory;

public class Saver {
    static int windowHeight = 500;
    static int windowWidth = 1000;
    Game game;
    String PATHNAME = "saves";
    public void Save(Game game) {

        
        for(List < Integer > key : game.getTiles().keySet()) {
        	game.getTiles().get(key);
        }
        
    }
    /*
     * Save File will look like:
     * 
     * 
     * Ground{
     * Rect{posX,posY,r,g,b}
     * Rect{posX,posY,r,g,b}
     * Rect{posX,posY,r,g,b}
     * Rect{posX,posY,r,g,b}
     * etc...
     * }
     * Enemies{
     * Enemy{posX,posY}
     * EnemyRect{posX,posY}
     * 
     * }
     * Structures{
     * Tree{posX,posY}
     * Tree{posX,posY}
     * boulder_1{posX,posY}
     * Castle{posX,posY}
     * etc...
     * }
     * 
     */
    public void save(Game game, String saveName){
        saveInventory(saveName ,game);
        if(!new File(PATHNAME + "/" + saveName).exists()) {
        	makeSaveFolder(saveName);
        }
        
        for(List < Integer > key : game.getTiles().keySet()) {
        	Tile currentTile = game.getTiles().get(key);
            File currentFile = makeFile("Tile"+key.get(0)+"_"+key.get(1)+"_"+game.getTiles().get(key).getBiome().getClass().getName(), PATHNAME, saveName);
            String ground = "Ground{\n";
            for(GObject rect : currentTile.getObjects()) {
                if(rect instanceof GRect){
                    ground += "Rect[" + rect.getX() + "," + rect.getY() + "," + rect.getColor().getRed() + "," + rect.getColor().getGreen() + "," + rect.getColor().getBlue() + "]\n";
                }
            	
            }
            ground += "}";
            String enemies = "Enemies{\n";
            for(Enemy enemy : currentTile.getEnemies()) {
            	enemies += enemy.getClass().getName()+"[" + enemy.getX() + "," + enemy.getY() + "]\n";
            }
            enemies += "}";
            String structures = "Structures{\n";
            for(Structure structure : currentTile.getStructures()) {
            	structures += structure.getClass().getName()+"[" + structure.getX() + "," + structure.getY() + "]\n";
            }
            structures += "}";
            String colliders = "Colliders{\n";
            for(GObject collider : currentTile.getColliders()) {
            	colliders += "Collider[" + collider.getX() + "," + collider.getY() + "," + collider.getWidth() + "," + collider.getHeight() + "]\n";
            }
            colliders += "}";
            try {
                writeToFile(currentFile, ground + "\n" + enemies + "\n" + structures + "\n" + colliders);
                //print entire file path
                System.out.println("Saved: " + currentFile.getAbsolutePath());
            }catch(IOException e) {
            	e.printStackTrace();
            }
        }
        
    }
    public void saveInventory(String saveName, Game game){
            File currentFile = makeFile("Inventory", PATHNAME, saveName);
        	String inventory = "Inventory{\n";
        	for(Item item : game.getPlayer().getInventory().getInventory()) {
                if(item != null){
        		    inventory += item.getClass().getName() + "\n";
                }else{
                    inventory += "null\n";
                }
        	}
        	inventory += "}";
        	try {
                writeToFile(currentFile, inventory);
                //print entire file path
                System.out.println("Saved: " + currentFile.getAbsolutePath());
            }catch(IOException e) {
            	e.printStackTrace();
            }
    }
    public void makeSaveFolder(String saveName){
        File file = new File(PATHNAME + "/" + saveName);
        file.mkdirs();
    }

    public Game load(String saveName, GraphicsProgram graphicsProgram){

        //for file in saves folder
        int WINDOWHEIGHT = 500;
        Game game = new Game(1000, 500, graphicsProgram);
        for(File file : new File(PATHNAME + "/" + saveName).listFiles()){
            if(file.getName().equals("Inventory")){
                
                Inventory i = loadInventory(file, WINDOWHEIGHT);
                game.getPlayer().setInventory(i);
            }
            if(file.getName().contains("Tile")){
                Tile tile = loadTile(file, game);
                game.getTiles().put(tile.getKey(), tile);
            }
        }
        recalculateNeighbors(game);
        return game;
    }
    
    public void recalculateNeighbors(Game game) {
    	for(List < Integer > key : game.getTiles().keySet()) {
    		Tile tile = game.getTiles().get(key);
    		tile.setNeighbors(game.getNeighbors(tile.getKey()));
    	}
    }

    public Inventory loadInventory(File inventoryFile, int screenHeight){
        String fileText = "";
        try{
            fileText = readFile(inventoryFile);
        }catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }

        int inventorySize = fileText.split("\n").length - 1;
        Inventory inventory = new Inventory(inventorySize, screenHeight);
        for(String className : fileText.split("\n")){

            
            if(className.equals("Inventory{") || className.equals("}")){
                continue;
            }
            if(className.equals("null")){
                inventory.add(null);
            }else{
                try {
                    Class<?> itemClass = Class.forName(className);
                    Item item = (Item) itemClass.getDeclaredConstructor().newInstance();
                    inventory.add(item);
                }catch(Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        }
        return inventory;
    }

    public Tile loadTile(File tileFile, Game game){
        String fileText = "";
        try{
            fileText = readFile(tileFile);
        }catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
        ArrayList < Integer > tileKey = new ArrayList < Integer > ();
        tileKey.add(Integer.parseInt(tileFile.getName().split("Tile")[1].split("_")[0]));
        tileKey.add(Integer.parseInt(tileFile.getName().split("Tile")[1].split("_")[1]));

        Tile tile = new Tile( tileKey,game);
        String biomeClassName = tileFile.getName().split("Tile")[1].split("_")[2];
        try {
            Class<?> biomeClass = Class.forName(biomeClassName);
            Biome biome = (Biome) biomeClass.getDeclaredConstructor().newInstance();
            tile.setBiome(biome);
        }catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        ArrayList<GObject> rects = new ArrayList<GObject>();
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        ArrayList<Structure> structures = new ArrayList<Structure>();
        ArrayList<GLine> colliders = new ArrayList<GLine>();

        //split the file text by the Groud{}, Enemies{}, Structures{}, Colliders{}
        String ground = fileText.split("Ground\\{")[1].split("\\}")[0];
        String[] groundRects = ground.split("Rect\\[");
        for(String rect : groundRects){
            if(rect.equals("")){
                continue;
            }
            String[] rectInfo = rect.split("\\]")[0].split(",");
            //if every element in rect info is a space, newline, or empty, continue
            boolean allEmpty = true;
            for(String s : rectInfo){
                if(!s.equals("") && !s.equals(" ") && !s.equals("\n")){
                    allEmpty = false;
                }
            }
            if(allEmpty){
                continue;
            }
            GRect rectToAdd = new GRect(Double.parseDouble(rectInfo[0]), Double.parseDouble(rectInfo[1]), 10, 10);
            rectToAdd.setColor(new Color(Integer.parseInt(rectInfo[2]), Integer.parseInt(rectInfo[3]), Integer.parseInt(rectInfo[4])));
            Color color = new Color(Integer.parseInt(rectInfo[2]), Integer.parseInt(rectInfo[3]), Integer.parseInt(rectInfo[4]));
            rectToAdd.setFillColor(color);
            rectToAdd.setFilled(true);
            rects.add(rectToAdd);
        }


        String enemiesString = fileText.split("Enemies\\{")[1].split("\\}")[0];
        String[] enemiesStrings = enemiesString.split("\n");
        for(String enemyRaw : enemiesStrings){
            String className = enemyRaw.split("\\[")[0];
            if(!className.trim().equals("")){
                try {
                    Class<?> [] paramTypes = new Class[] {int.class, int.class, Game.class};
                    Object[] params = new Object[] {
                        (int) Double.parseDouble(enemyRaw.split("\\[")[1].split(",")[0]), 
                        (int) Double.parseDouble(enemyRaw.split("\\[")[1].split(",")[1].split("\\]")[0]),   
                        game};
                    Class<?> enemyClass = Class.forName(className);
                    Enemy enemy = (Enemy) enemyClass.getDeclaredConstructor(paramTypes).newInstance(params);
                    enemies.add(enemy);
                }catch(Exception e) {
                    System.out.println("Error: " + e.getMessage() + " : " + e.getStackTrace() + " : "  +"class name: " + className);
                }
            }
        }

        String structuresString = fileText.split("Structures\\{")[1].split("\\}")[0];
        String[] structuresStrings = structuresString.split("\n");
        for(String structureRaw : structuresStrings){
            String className = structureRaw.split("\\[")[0];
            if(!className.trim().equals("")){
                try {
                    Class<?> structureClass = Class.forName(className);
                    //param types are int, int

                    Object[] params = new Object[] {Integer.parseInt(structureRaw.split("\\[")[1].split(",")[0]), Integer.parseInt(structureRaw.split("\\[")[1].split(",")[1].split("\\]")[0])};
                    Class<?>[] paramTypes = new Class[] {int.class, int.class};
                    Structure structure = (Structure) structureClass.getDeclaredConstructor(paramTypes).newInstance(params);
                    structures.add(structure);
                }catch(Exception e) {
                    System.out.println("Error: " + e.getMessage() + " : " + e.getStackTrace() + " : "  +"class name: " + className);
                }
            }
        }

        String collidersString = fileText.split("Colliders\\{")[1].split("\\}")[0];
        String[] collidersStrings = collidersString.split("\n");
        for(String colliderRaw : collidersStrings){
            String[] colliderInfo = colliderRaw.split("\\[")[1].split("\\]")[0].split(",");
            GLine collider = new GLine(Double.parseDouble(colliderInfo[0]), Double.parseDouble(colliderInfo[1]), Double.parseDouble(colliderInfo[0]) + Double.parseDouble(colliderInfo[2]), Double.parseDouble(colliderInfo[1]) + Double.parseDouble(colliderInfo[3]));
            colliders.add(collider);
        }

        tile.setColliders(colliders);
        tile.setStructures(structures);
        tile.setObjects(rects);
        for(Structure struct : structures){
            tile.addObjects(tile.getObjects(), struct.getObjects());
        }

        tile.setEnemies(enemies);
        return tile;
    }

    public File makeFile(String fileName, String pathName, String saveName){

    	File file = new File(pathName + "/" + saveName + "/"+ fileName);
        //if file doesnt exists, then create it
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        }catch(IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
            return null;
        }
        return file;
    }
    //Overrite whatever is in file
    public void writeToFile(File file, String text) throws IOException {
        System.out.println("Writing to file: " + file.getAbsolutePath());
        FileWriter writer = new FileWriter(file);
        writer.write(text);
        writer.close();
    }
    public String readFile(File file) throws IOException {
        String fileText = "";
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()) {
            fileText += scanner.nextLine() + "\n";
        }
        scanner.close();
        return fileText;
    }
    public Game Load(String saveFile) {
        return null;
        
    }

}