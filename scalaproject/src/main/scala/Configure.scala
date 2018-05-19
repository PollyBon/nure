import java.awt.{Color}

object Configure {
  val WorldWidth: Int = 400			
  val WorldHeight: Int = 400
  
  val HareColor: Color = Color.WHITE
  val LynxColor: Color = Color.RED
  val LynxCtrolColor: Color = Color.BLUE
  
  val HareSize: Int = 6
  val LynxSize: Int = 12
  
  val HareScopeRadius: Int = 10		
  val LynxScopeRadius: Int = 25		
  val HareRunStep: Int = 15			
  val LynxRunStep: Int = 26			
  
  val HareMaxAge: Int = 7			
  val HareBirthRate: Double = 0.08	
  var HareReproduce: Int = 5		
  val LynxMaxAge: Int = 11		
  val LynxMaxEnergy: Int = 40		
  val EnergyGainPreHare: Int = 6	
  val EnergyUseReproduce: Int = 15	

  val InitialHares: Int = 70
  val InitialLynx: Int = 8			
  
  val Ticker: Int = 1000			
  val Runtime: Int = 10 			
  val DisplayMessage: Boolean = false
}