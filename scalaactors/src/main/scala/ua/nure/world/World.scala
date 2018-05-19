package ua.nure.world

class World {

    val entrance: PathNode

    val entrance = new PathNode(null)
    val n1 = new PathNode(entrance)
    val n2 = new PathNode(entrance)
    val n3 = new PathNode(entrance)
    val entrance.addChild(n1, n2, n3)
    val n11 = new PathNode(n1)
    val n12 = new PathNode(n1)
    val n1.addChild(n11, n12)
    val n21 = new PathNode(n2)
    val n2.addChild(n21)
    val n31 = new PathNode(n3)
    val n3.addChild(n31)
    val n211 = new PathNode(n21)
    val n212 = new PathNode(n21)
    val n21.addChild(n211, n212)
}
