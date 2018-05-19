import scala.actors._
import scala.collection.mutable.ListBuffer

object WorldActor extends Actor {
  private val haresPopulation = new ListBuffer[Hare]
  private val lynxPopulation = new ListBuffer[Lynx]

  def act() {
    Actor.loop {
      react {
        case h: Hare => handleHares(h)
        case l: Lynx => handleLynx(l)
        case "ticker" => simlulate()
        case ("whereishare", l: Lynx) => searchHaresForLynx(l)
        case "exit" => quit()
        case x: Any => displayMessage("Warning! Unknown message! " + x)
      }
    }
  }

  private def handleHares(hare: Hare) = {
    if (hare.getDying()) {
      displayMessage("hare: " + hare.hashCode() + " dead.")
      haresPopulation -= hare
    } else {
      displayMessage("hare: " + hare.hashCode() + " bron.")
      haresPopulation += hare
      hare.start()
    }
  }

  private def handleLynx(lynx: Lynx) = {
    if (lynx.getDying()) {
      displayMessage("lynx: " + lynx.hashCode() + " dead.")
      lynxPopulation -= lynx
    } else {
      displayMessage("lynx: " + lynx.hashCode() + " bron.")
      lynxPopulation += lynx
      lynx.start()
    }
  }
  
  private def searchHaresForLynx(lynx: Lynx): Option[Hare] = {
    val hare = haresPopulation.find(e => e.isOnThisPot(lynx.getX(), lynx.getY(), Configure.HareScopeRadius))
    if (!hare.isEmpty && !lynx.getDying()) {
      haresPopulation -= hare.get
      lynx ! hare.get
    }
    hare
  }
  
  private def simlulate() = {
    for (hare <- haresPopulation) {
      hare ! "alive"
    }

    for (lynx <- lynxPopulation) {
      lynx ! "alive"
    }

    outputResult()
  }
  
  private def outputResult() = {

    val h: Int = getHaresCount()
    val l: Int = getLynxCount()

    println("====Hares Population: " + h.toString())
    println("====Lynx Population: " + l.toString())
    
    WorldGUI.ShapeDrawingActor ! (haresPopulation, lynxPopulation)
  }
  
  def getHaresCount(): Int = haresPopulation.count(e => !e.getDying())

  def getLynxCount(): Int = lynxPopulation.count(e => !e.getDying())

  private def displayMessage(s: String) = {
    if (Configure.DisplayMessage)
      println(s)
  }
  
  def reset() = {
    haresPopulation.clear()
    lynxPopulation.clear()
  }

  private def initial(hares: Int, lynx: Int) = {
    println("initial...")

    reset()

    for (i <- (1 to hares)) {
      handleHares(new Hare(
        (math.random * Configure.HareMaxAge).toInt,
        Configure.HareMaxAge,
        Configure.HareBirthRate,
        Configure.HareReproduce,
        WorldSpace.getRandomX(),
        WorldSpace.getRandomY(),
        Configure.HareRunStep))
    }

    for (i <- (1 to lynx)) {
      handleLynx(new Lynx(
        (math.random * Configure.LynxMaxAge).toInt,
        Configure.LynxMaxAge,
        (math.random * Configure.LynxMaxEnergy).toInt + 1,
        Configure.EnergyGainPreHare,
        Configure.EnergyUseReproduce,
        WorldSpace.getRandomX(),
        WorldSpace.getRandomY(),
        Configure.LynxRunStep))
    }

    outputResult()

    start()
  }

  def run(running: Boolean): Unit = {
    if (running) {
      initial(Configure.InitialHares, Configure.InitialLynx)
      ClockActor.startClock()
    } else {
      ClockActor !? "stop"
    }
  }

  private def quit() = {
    exit()
    ClockActor ! "exit"

  }

  def main(args: Array[String]): Unit = {

    run(true)

    Thread.sleep(1000 * Configure.Runtime)

    run(false)

    Thread.sleep(1000)

    println("Restart agan...........................")

    run(true)

    Thread.sleep(1000 * Configure.Runtime)

    run(false)

    System.exit(0)
  }
}