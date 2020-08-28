package constants

object PhoneBillConstants{

  val CallFormatRegex = "\\d{2}:\\d{2}:\\d{2},\\d{3}-\\d{3}-\\d{3}".r
  val DurationFormatRegex = "(\\d{2}):(\\d{2}):(\\d{2})".r
  val NumberFormatRegex = "(\\d{3})-(\\d{3})-(\\d{3})".r

  val FiveValue = 5
  val ZeroValue = 0
  val OneValue = 1
  val ThreeValue  = 3
  val OneHundredFiftyValue  = 150
  val OneHundredValue  = 100
  val SixtyValue  = 60

  val BadFormatStr = " does not conform to the format."
  val TotalBillStr = "The total sum of the bill is "
  val ArrowStr = " ==> "
  val NumberStr = "Number: "
  val EndStr = "\n"
  val PriceStr = "Price: "
  val DetailBillStr = "-----------Detail Bill-----------"
  val RememberFreeNumberStr= " (This is the free Number)"
  val MoneySavedStr = "MONEY SAVED: "
  val ProgramStoppedStr = "The program stopped because a call"
  val TheCallStr = "The call "
  val BillValidatorStr = "---------------BILL VALIDATOR---------------"
  val OptionTxtStr = "1. Read for TXT the calls"
  val UserStr = "2. User introduce the calls"
  val ExitStr = "0. Exit"
  val ChooseOptionStr = "Choose you option: "
  val ScoreStr = "--------------------------------------------"
  val HowManyLinesStr = "How many lines are you going to introduce?"
  val ByeStr = "Bye && Thanks :)"
  val ErrorOptionStr = "This Option does not exit. Try another time please."
  val OtherNumberStr = "You must put a number between 1 and 100. Repeat it! (0 to Exit)"
  val CalculatingBillStr = "==CALCULATING BILLING=="
  val CentStr = " Cents"
  val NumberExceptionStr = "You must introduce a number. Try another time please."

  val FileExceptionStr = "Couldn't find the file: "
  val FileName = "bill.txt"




}
