package WizardTD;

import processing.data.JSONArray;
import java.awt.Point;


import java.util.*;


public class SpawnAndPath {
    private LinkedList<Point> curPathSolution;
    private JSONArray curSpawnLocationPixels;

    public SpawnAndPath(LinkedList<Point> curPathSolution, JSONArray curSpawnLocationPixels) {
        this.curPathSolution = curPathSolution;
        this.curSpawnLocationPixels = curSpawnLocationPixels;
    }

    public LinkedList<Point> getSolution() {
        return curPathSolution;
    }

    public JSONArray getSpawnLocation() {
        return curSpawnLocationPixels;
    }
    
}
