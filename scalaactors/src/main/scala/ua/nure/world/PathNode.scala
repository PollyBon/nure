package ua.nure.world


class PathNode {

    var parent: PathNode

    var childs: List[PathNode]

    def PathNode(parent: PathNode) {
        this.parent = parent
    }

    def addChild(child: List[PathNode]) {
        childs  = List.concat(childs, child)
    }

}
