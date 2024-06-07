package socs.network.graph;

import java.util.ArrayList;

public class Node implements Comparable<Node>{
    public String id;
    public boolean isDestinationIP;
    public double cost;

    public Node parent;
    public ArrayList<Node> neighbors;

    public Node() {
        this.neighbors = new ArrayList<>();
    }

    public Node(String id, boolean isDestinationIP){
        this.id = id;
        this.isDestinationIP = isDestinationIP;
        this.neighbors = new ArrayList<>();
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(cost, o.cost);
    }

    public void addNeighbor(Node node) {
        if (!neighbors.contains(node)) {
            neighbors.add(node);
            node.addNeighbor(this);
        }
    }


}