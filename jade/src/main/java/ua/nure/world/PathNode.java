package ua.nure.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathNode {

    public PathNode parent;

    public List<PathNode> childs;

    public PathNode(PathNode parent) {
        this.parent = parent;
        childs = new ArrayList<>();
    }

    public void addChild(PathNode... child) {
        childs.addAll(Arrays.asList(child));
    }

}
