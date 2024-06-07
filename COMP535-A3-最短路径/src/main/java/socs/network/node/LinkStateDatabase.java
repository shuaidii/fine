package socs.network.node;

import socs.network.graph.Graph;
import socs.network.graph.Node;
import socs.network.message.LSA;
import socs.network.message.LinkDescription;

import java.util.*;


public class LinkStateDatabase {

    //linkID => LSAInstance
    HashMap<String, LSA> _store = new HashMap<String, LSA>();

    private RouterDescription rd = null;

    public LinkStateDatabase(RouterDescription routerDescription) {
        rd = routerDescription;
        LSA l = initLinkStateDatabase();
        _store.put(l.linkStateID, l);
    }

    /**
     * output the shortest path from this router to the destination with the given IP address
     */
    String getShortestPath(String destinationIP) {
        //init
        Graph graph = genGraph(destinationIP);
        for (Node node : graph.nodes) {
            node.cost = Double.POSITIVE_INFINITY;
            node.parent = null;
        }

        Node startNode = graph.getNode(rd.simulatedIPAddress);
        startNode.cost = 0;

        //start
        List<Node> visitedNodeList = new ArrayList<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(graph.nodes);
        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            visitedNodeList.add(currentNode);
            if (currentNode.id.equals(destinationIP)) {
                break;
            }
            for (Node nodeTmp : currentNode.neighbors) {
                double weight = graph.getWeight(currentNode, nodeTmp);
                if (nodeTmp.cost > currentNode.cost + weight) {
                    nodeTmp.cost = currentNode.cost + weight;
                    nodeTmp.parent = currentNode;
                    priorityQueue.remove(nodeTmp);
                    priorityQueue.add(nodeTmp);
                }
            }
        }

        // path
        List<Node> nodePathList = new ArrayList<>();
        int index = 0;
        Node currentNode = visitedNodeList.get(index);
        while (true) {
            if (currentNode.id.equals(destinationIP)){
                break;
            }
            index++;
            currentNode = visitedNodeList.get(index);
        }

        Node nodeTmp = currentNode;
        nodePathList.add(currentNode);
        while (true) {
            if (nodeTmp == startNode){
                break;
            }
            nodeTmp = nodeTmp.parent;
            nodePathList.add(nodeTmp);
        }
        Collections.reverse(nodePathList);

        return genPathMessage(graph, nodePathList);
    }

    private String genPathMessage(Graph graph, List<Node> nodePathList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodePathList.size() - 1; i++) {
            Node from = nodePathList.get(i);
            Node to = nodePathList.get(i + 1);
            builder.append(from.id).append(" ->(").append(graph.getWeight(from, to)).append(") ");
            if (to.isDestinationIP) {
                builder.append(to.id);
                break;
            }
        }
        return builder.toString();
    }

    private Graph genGraph(String destinationIP) {
        List<Node> nodes = new ArrayList<>();
        for (String id : _store.keySet()) {
            nodes.add(new Node(id, id.equals(destinationIP)));
        }

        Graph graph = new Graph(nodes);
        for (String key : _store.keySet()) {
            Node from = graph.getNode(key);
            for (LinkDescription linkDescription : _store.get(key).links) {
                if (linkDescription.linkID.equals(key)) {
                    continue;
                }
                Node to = graph.getNode(linkDescription.linkID);
                graph.addEdge(from, to, linkDescription.tosMetrics);
            }
        }
        return graph;
    }


    //initialize the linkstate database by adding an entry about the router itself
    private LSA initLinkStateDatabase() {
        LSA lsa = new LSA();
        lsa.linkStateID = rd.simulatedIPAddress;
        lsa.lsaSeqNumber = Integer.MIN_VALUE;
        LinkDescription ld = new LinkDescription();
        ld.linkID = rd.simulatedIPAddress;
        ld.portNum = -1;
        ld.tosMetrics = 0;
        lsa.links.add(ld);
        return lsa;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LSA lsa : _store.values()) {
            sb.append(lsa.linkStateID).append("(" + lsa.lsaSeqNumber + ")").append(":\t");
            for (LinkDescription ld : lsa.links) {
                sb.append(ld.linkID).append(",").append(ld.portNum).append(",").
                        append(ld.tosMetrics).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void addLink(Link link) {
        LSA lsa = _store.get(rd.simulatedIPAddress);
        LinkDescription description = new LinkDescription(link.router2.simulatedIPAddress, link.router2.processPortNumber, link.weight);
        lsa.links.add(description);
        lsa.lsaSeqNumber++;
    }

    public boolean lsaNotify(Vector<LSA> lsaArray) {
        boolean newRec = false;
        for (LSA lsa : lsaArray) {
            LSA lsaTmp = _store.get(lsa.linkStateID);
            if (lsaTmp == null || lsaTmp.lsaSeqNumber < lsa.lsaSeqNumber) {
                _store.put(lsa.linkStateID, lsa);
                newRec = true;
            }
        }
        return newRec;
    }
}
