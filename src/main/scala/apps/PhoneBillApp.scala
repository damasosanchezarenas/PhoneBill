package apps

import transformers.PhoneBillTransformer

import scala.collection.mutable.HashMap

object PhoneBillApp {

  def main (args: Array[String]):Unit = {

    //We get the calls the way the user has chosen
    val calls = PhoneBillTransformer.menu()

    if (!calls.isEmpty){

      PhoneBillTransformer.showWelcomeMessage()

      //In this hashmap<number,price> we will save each number and the total price of all calls to that number.
      val numberPriceMap = new HashMap[String, Int]()

      //If we can, we calculate the basic part of the bill, in other case we report the error.
      if (PhoneBillTransformer.calculateBasicBill(calls, numberPriceMap)) {

        //We calculate the full bill and the saved money to show it in detail Bill.
        val savedMoney = PhoneBillTransformer.calculateFullBill(numberPriceMap)

        //We must sum all the number-price relationships and we will obtain the total price of the bill.
        val result = numberPriceMap.foldLeft(0)(_ + _._2)


        PhoneBillTransformer.showTotalBill(result)
        PhoneBillTransformer.showDetailBill(numberPriceMap, savedMoney)
      }
      //When trying to calculate the basic part of the bill, we see a call with the wrong format, we will show the error.
      else
        PhoneBillTransformer.showErorBill
    }
  }
}
