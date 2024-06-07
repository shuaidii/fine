package socs.network.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public List<Node> nodes;
    public List<Edge> edges;

    public Graph(List<Node> nodes) {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>();
    }

    public void addEdge(Node from, Node to, int weight) {
        Edge edge = new Edge(from, to, weight);
        if (getEdge(from, to) != null) {
            return;
        }
        this.edges.add(edge);
        from.addNeighbor(to);
    }

    public Node getNode(String id) {
        return this.nodes.stream().filter(node -> id.equals(node.id)).findFirst().orElse(null);
    }

    public double getWeight(Node from, Node to) {
        Edge edge = getEdge(from, to);

        if (edge == null) {
            throw new IllegalArgumentException("No Edge found");
        }
        return edge.weight;
    }

    private Edge getEdge(Node from, Node to) {
        for (Edge edgeTmp : edges) {
            if ((edgeTmp.from.equals(from) && edgeTmp.to.equals(to))
                    || (edgeTmp.from.equals(to) && edgeTmp.to.equals(from))) {
                return edgeTmp;
            }
        }
        return null;
    }
}
