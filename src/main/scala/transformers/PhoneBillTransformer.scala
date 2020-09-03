package transformers

import java.io.FileNotFoundException

import constants.PhoneBillConstants
import utils.RegexUtils._

import scala.collection.mutable.HashMap
import scala.io.Source.fromFile
import scala.util.control.Breaks.{break, breakable}

object PhoneBillTransformer {

  /**
    * This method show the menu where you can choose different ways to enter calls
    * @return list of Calls
    */
  def menu(): List[String]= {

    //Menu messages
    show(PhoneBillConstants.BillValidatorStr)
    show(PhoneBillConstants.OptionTxtStr)
    show(PhoneBillConstants.UserStr)
    show(PhoneBillConstants.ExitStr)
    show(PhoneBillConstants.ChooseOptionStr)
    show(PhoneBillConstants.ScoreStr)

    try {
      val option = readInt

      option match {
        case 1 =>
          //We read the file and list each of the calls
          readTxt(PhoneBillConstants.FileName)

        case 2 =>
          var numLines = 1
          var ok = false

          //The user must enter the number of calls and then each of the calls
          show(PhoneBillConstants.HowManyLinesStr)

          //The number must be [1--100]
          while (!ok && numLines != PhoneBillConstants.ZeroValue) {
            try {
              numLines = readInt
              if (numLines > PhoneBillConstants.OneHundredValue)
                show(PhoneBillConstants.OtherNumberStr)
              else if (numLines <= PhoneBillConstants.OneHundredValue && numLines != PhoneBillConstants.ZeroValue)
                ok = true
            }
            catch { //the user does not enter a number
              case e:
                NumberFormatException => show(PhoneBillConstants.NumberExceptionStr)
            }
          }

          if (ok)
            readFromUser(numLines)
          else{
            show(PhoneBillConstants.ByeStr)
            List.empty[String]
          }

        case 0 =>
          //The user has decided to leave the program
          show(PhoneBillConstants.ByeStr)
          List.empty[String]

        case _ =>
          //The user has introduced a nonexistent option.
          show(PhoneBillConstants.ErrorOptionStr)
          List.empty[String]
      }
    }
    catch { //the user does not enter a number
      case e:
        NumberFormatException => show(PhoneBillConstants.NumberExceptionStr)
        List.empty[String]
    }
  }

  /**
    * This method can read one txt and returns the lines.
    *
    * @param fileName
    * @return List of calls that have been read in a TXT file.
    */
  private def readTxt(fileName: String): List[String] = {
    try{
      val source = fromFile(fileName, "utf-8")
      val lines = try source.getLines.toList finally source.close()
      lines
    }
    catch{
      case e:
        FileNotFoundException => show(PhoneBillConstants.FileExceptionStr  + PhoneBillConstants.FileName)
        List.empty[String]
    }
  }

  /**
    * This method allows the user to enter as many lines as there are in "numLines".
    *
    * @param numLines Before entering the lines, the user must enter how many to enter.
    *
    * @return List of calls that have been introduced by a user
    */
  private def readFromUser(numLines: Int): List[String] = {
    var counter=0
    var calls = List.empty[String]

    //We expect as many calls as the user has indicated previously
    while(counter < numLines) {
      calls = readLine() :: calls
      counter += 1
    }
    calls
  }

  /**
    * This method show one String
    *
    * @param string
    */
  private def show(string: String): Unit = {
    println(string)
  }

  /**
    * This method calculates the entire bill.
    * Calculate the maximum number used to mark as free.
    *
    * @param numberPriceMap Hashmap <Number,Price>
    * @return The money saved thanks to the free number.
    */
  def calculateFullBill(numberPriceMap:HashMap[String,Int]) : Int = {
    //This number will be the most used and therefore the free one.
    var freeNumber = ""

    //We take the maximum value of the map. That is, the number with the highest price.
    val max = numberPriceMap.maxBy(_._2)

    //We check how many numbers there are with that maximum price
    val maxNumberPriceMap = numberPriceMap.filter(it => it._2 == max._2)

    if(maxNumberPriceMap.size>1)
    //If there are several numbers with the maximum duration we have to tiebreaker and look for the minimum number.
      freeNumber = tiebreaker(maxNumberPriceMap)
    else
    //If the maxNumberPriceMap only has one element, it is the number with the maximum duration
      freeNumber = maxNumberPriceMap.head._1

    //The max price have to be free.
    val savedMoney = numberPriceMap.apply(freeNumber)
    numberPriceMap.put(freeNumber,PhoneBillConstants.ZeroValue)

    savedMoney
  }

  /**
    * Method that checks the format of each call.
    * If they are correct, for each call take out the number and price. After it, enter them in a "numberPriceMap".
    *
    * @param calls
    * @param numberPriceMap Hashmap <Number,Price>
    * @return a boolean indicating whether all calls are successful or not.
    */
  def calculateBasicBill(calls: List[String], numberPriceMap:HashMap[String,Int]): Boolean = {
    var correct = true

    breakable {
      for (call <- calls) {
        if (validateCall(call)) {

          //We obtain the number and duration of each corresponding call.
          val duration = call.split(",").head
          val number = call.split(",").last

          //we apply the necessary logic to calculate the price depending on the duration
          val price = calculatePrice(duration)

          //if the number already existed, we update the total value of the price of that number
          if (numberPriceMap.contains(number)) {
            numberPriceMap.put(number, price + numberPriceMap.apply(number))
          }
          else //if the number not existed yet, we add <number,price> in the hashMap
            numberPriceMap.put(number, price)
        }
        else {
          show(PhoneBillConstants.TheCallStr + call + PhoneBillConstants.BadFormatStr)
          correct=false
          break
        }
      }
    }
    correct
  }

  /**
    * This method breaks the tie by seeing which numerical value of the phone numbers is less
    *
    * @param numberPriceMap Hashmap <Number,Price>
    * @return The Winning number
    */

  private def tiebreaker(numberPriceMap:HashMap[String, Int]): String ={
    val blacklist: List[String] = List("-") //Items you want to delete

    val listOfNumbers = numberPriceMap.keys.toList //You get all the numbers with maximum duration to break the tie

    //We clear the list of character we don't want
    val clearList = listOfNumbers.map(line => blacklist.foldLeft(line)(_.replace(_, "")))

    //We get the same List, but now with int. Now we can calculate the min number.
    val integerList = clearList.map(s => s.toInt)

    //We get the min number of the list.
    val minNumber = integerList.min

    val indexMinNumber = integerList.indexOf(minNumber)

    listOfNumbers.apply(indexMinNumber)
  }

  /**
    * This method calculates the price of the call based on the duration.
    *
    * @param duration
    * @return the price of the call
    */
  private def calculatePrice(duration:String): Int = {
    val durationRegex = PhoneBillConstants.DurationFormatRegex.findAllIn(duration).matchData.toList(0)
    val hour = durationRegex.group(1).toInt
    val min = durationRegex.group(2).toInt
    val sec = durationRegex.group(3).toInt

    //We check if there are hours or not.
    if(hour == PhoneBillConstants.ZeroValue){
      min match {
        //If the call is less than 5 min
        case min if min < PhoneBillConstants.FiveValue =>
          lessFiveMin(hour,min,sec)

        //If the call is greater or equal than 5 minutes
        case min if min >= PhoneBillConstants.FiveValue =>
          greaterFiveMin(hour,min,sec)
        }
      }
      //if there is any hour, obviously the duration is greater than 5 minutes.
      else
        greaterFiveMin(hour,min,sec)
    }

  /**
    * This method applied the logic when the duration of the call is less than 5 min
    *
    * @param hour
    * @param min
    * @param sec
    * @return the price of the call
    */
  private def lessFiveMin(hour: Int, min: Int, sec: Int): Int ={
    ((min * PhoneBillConstants.SixtyValue) + sec) * PhoneBillConstants.ThreeValue //((Mins in Sec) + secs) * 3
  }

  /**
    * This method applied the logic when the duration of the call is greater or equal than 5 min
    *
    * @param hour
    * @param min
    * @param sec
    * @return the price of the call
    */

  private def greaterFiveMin(hour: Int, min: Int, sec: Int): Int ={
    val hourInMin = hour * PhoneBillConstants.SixtyValue //We convert the hours to minutes

    //If that minute already has a second, we must calculate the price with the next minute
    if(sec == PhoneBillConstants.ZeroValue)
      (hourInMin + min) * PhoneBillConstants.OneHundredFiftyValue  //(Hours in Min + Min) * 150

    else
      (hourInMin + (min+PhoneBillConstants.OneValue)) * PhoneBillConstants.OneHundredFiftyValue //(Hours in Min + (Min+1)) * 150
  }

  /**
    * This method validate that the call has the correct format.
    *
    * @param call
    * @return if the call is valid or not
    */
  private def validateCall(call:String): Boolean={
    PhoneBillConstants.CallFormatRegex matches call
  }

  /**
    * This method Show the total price of the Bill and after the detail of the bill.
    * @param result
    */

  def showTotalBill(result: Int):Unit = {
    show(PhoneBillConstants.TotalBillStr + PhoneBillConstants.ArrowStr +  result + PhoneBillConstants.CentStr)
    show(PhoneBillConstants.EndStr)
    show(PhoneBillConstants.DetailBillStr)
  }

  /**
    * This method show a welcome message.
    */
  def showWelcomeMessage():Unit = {
    show(PhoneBillConstants.EndStr + PhoneBillConstants.CalculatingBillStr + PhoneBillConstants.EndStr)
  }

  /**
    * This method show an error message.
    */
  def showErorBill():Unit = {
    show(PhoneBillConstants.ProgramStoppedStr + PhoneBillConstants.BadFormatStr)
  }

  /**
    * This Method show a hashmap and therefore the detailed bill.
    *
    * @param numberPriceMap Hashmap <Number,Price>
    * @param savedMoney
    */
  def showDetailBill(numberPriceMap:HashMap[String, Int], savedMoney:Int): Unit ={
    numberPriceMap.foreach{
      keyval =>
        if(keyval._2 == PhoneBillConstants.ZeroValue){
          show(PhoneBillConstants.NumberStr + keyval._1 + PhoneBillConstants.ArrowStr + PhoneBillConstants.PriceStr + keyval._2
            + PhoneBillConstants.CentStr + PhoneBillConstants.RememberFreeNumberStr + PhoneBillConstants.ArrowStr
            + PhoneBillConstants.MoneySavedStr + savedMoney + PhoneBillConstants.CentStr)
        }
        else
          show(PhoneBillConstants.NumberStr + keyval._1 + PhoneBillConstants.ArrowStr + PhoneBillConstants.PriceStr + keyval._2
            + PhoneBillConstants.CentStr)
    }
  }
}
