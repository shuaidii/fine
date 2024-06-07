package socs.network.node;

public class Link {
    public RouterDescription router1;
    public RouterDescription router2;
    public short weight;

    public Link(RouterDescription r1, RouterDescription r2) {
        router1 = r1;
        router2 = r2;
        weight = 1;
    }

    public Link(RouterDescription router1, RouterDescription router2, short weight) {
        this.router1 = router1;
        this.router2 = router2;
        this.weight = weight;
    }

    public void setSIP(String aSIP) {
        router2.simulatedIPAddress = aSIP;
    }

    public void setWeight(short weight) {
        this.weight = weight;
    }
}
