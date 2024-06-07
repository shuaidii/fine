package WizardTD;
import java.util.*;

import WizardTD.subtiles.PathTile;
import WizardTD.subtiles.WizardHouseTile;
/**
 * PathFinder class
 * 
 * This class implements the A* pathfinding algorithm,
 * 
 * Reference:
 * Hart, P. E., Nilsson, N. J., & Raphael, B. (1968). 
 * A formal basis for the heuristic determination of minimum cost paths. 
 * IEEE transactions on Systems Science and Cybernetics, 4(2), 100-107.
 */
public class PathFinder {

    public class Node {
        public int x, y;
        public float g, h, f;
        public Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.g = Float.POSITIVE_INFINITY;
            this.h = 0;
            this.f = 0;
            this.parent = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public float heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public List<Node> getNeighbors(Node node, Tile[][] tiles) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Down, Right, Up, Left

        for (int[] dir : directions) {
            int x = node.x + dir[0];
            int y = node.y + dir[1];
            if (x >= 0 && x < tiles[0].length && y >= 0 && y < tiles.length &&
                (tiles[y][x] instanceof PathTile || tiles[y][x] instanceof WizardHouseTile)) {
                neighbors.add(new Node(x, y));
            }
        }
        return neighbors;
    }

    public List<Node> findPath(Node start, Node goal, Tile[][] tiles) {
        Set<Node> openList = new HashSet<>();
        Set<Node> closedList = new HashSet<>();
        openList.add(start);
    
        start.g = 0;
        start.h = heuristic(start, goal);
        start.f = start.h;
    
        while (!openList.isEmpty()) {
            List<Node> currentNodes = getLowestFCostNodes(openList);
            Node current = currentNodes.get(new Random().nextInt(currentNodes.size()));
    
            
            if (current.equals(goal)) {
                return reconstructPath(current);
            }

            openList.remove(current);
            closedList.add(current);

            for (Node neighbor : getNeighbors(current, tiles)) {
                if (closedList.contains(neighbor)) {
                    continue;
                }
                
                float tentativeG = current.g + 1;
                if (tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic(neighbor, goal);
                    neighbor.f = neighbor.g + neighbor.h;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>(); // Return an empty list if no path is found
    }

    private List<Node> getLowestFCostNodes(Set<Node> nodes) {
        List<Node> lowestNodes = new ArrayList<>();
        float lowestFCost = Float.POSITIVE_INFINITY;
    
        for (Node node : nodes) {
            if (node.f < lowestFCost) {
                lowestFCost = node.f;
                lowestNodes.clear();
                lowestNodes.add(node);
            } else if (node.f == lowestFCost) {
                lowestNodes.add(node);
            }
        }
    
        return lowestNodes;
    }

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }
}