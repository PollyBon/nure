package ua.nure.world;

public class World {

    public static PathNode entrance;

    static {
        entrance = new PathNode(null);
        PathNode n1 = new PathNode(entrance);
        PathNode n2 = new PathNode(entrance);
        PathNode n3 = new PathNode(entrance);
        entrance.addChild(n1, n2, n3);
        PathNode n11 = new PathNode(n1);
        PathNode n12 = new PathNode(n1);
        n1.addChild(n11, n12);
        PathNode n21 = new PathNode(n2);
        n2.addChild(n21);
        PathNode n31 = new PathNode(n3);
        n3.addChild(n31);
        PathNode n211 = new PathNode(n21);
        PathNode n212 = new PathNode(n21);
        n21.addChild(n211, n212);
    }
}
