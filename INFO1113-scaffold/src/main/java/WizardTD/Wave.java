package WizardTD;

import java.util.List;
import java.util.ArrayList;
import processing.data.JSONObject;
import processing.core.PApplet;

public class Wave {
    private int duration;
    private float preWavePause;
    private List<Monster> monsters = new ArrayList<>();

    private Board board;
    private PApplet app;
    private float speed;

    private float spawnInterval;  // Time interval (in frames) between spawning each monster
    private float currentSpawnTime = 0;  // Elapsed time (in frames) since the last spawn
    private int monstersToSpawn;  // Number of monsters left to spawn

    private float hp;
    private float armour;
    private float manaGainedOnKill;
    private String type;


    public Wave(int duration, float preWavePause, JSONObject monsterConfig, Board board, PApplet app) {
        this.duration = duration;
        this.preWavePause = preWavePause;
        this.board = board;
        this.app = app;
        this.speed = monsterConfig.getFloat("speed");

        int quantity = monsterConfig.getInt("quantity");
        
        // Calculate the spawn interval in frames
        this.spawnInterval = (duration * App.FPS) / quantity;  // Assuming 60 fps
        this.monstersToSpawn = quantity;

        this.hp = monsterConfig.getFloat("hp");
        this.armour = monsterConfig.getFloat("armour");
        this.manaGainedOnKill = monsterConfig.getFloat("mana_gained_on_kill");
        this.type = monsterConfig.getString("type");
    }

    public void update() {
        currentSpawnTime += 1;  // Increment the elapsed time since the last spawn
        
        // If it's time to spawn a new monster and there are still monsters left to spawn
        if (currentSpawnTime >= spawnInterval && monstersToSpawn > 0) {
            Monster newMonster = new Monster(board, app, speed, 0, hp, armour, manaGainedOnKill, type, (App) app);
            newMonster.setManaMultiplier(((App) app).getCurrentManaMultiplier());  // Assuming you have a method in App class that returns the current multiplier
            monsters.add(newMonster);
            // Add the new monster to the activeMonsters list in App.java directly
            ((App) app).addActiveMonster(newMonster);
            monstersToSpawn--;
            currentSpawnTime = 0;  // Reset the elapsed time since the last spawn
        }
    }
    
    public void render(PApplet app) {
        // Logic to render monsters in this wave
        for (Monster monster : monsters) {
            monster.render(app);
        }
    }

    public int getDuration() {
        return duration;
    }

    public float getPreWavePause() {
        return preWavePause;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }
    
}
