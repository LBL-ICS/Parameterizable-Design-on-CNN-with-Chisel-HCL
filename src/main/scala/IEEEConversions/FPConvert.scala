package IEEEConversions

import scala.collection.mutable

object FPConvert extends App{ // not related to the Chisel language, just used for making some conversions
  def convert_long_to_float(num: BigInt, bw: Int): BigDecimal = {
    var exponent = 0
    var mantissa = 0
    if (bw == 16){
      exponent = 5
      mantissa = 10
    }else if (bw == 32){
      exponent = 8
      mantissa = 23
    }else if(bw == 64){
      exponent = 11
      mantissa = 52
    }else if(bw == 128){
      exponent = 15
      mantissa = 112
    }
    var n = num
    var list = mutable.ArrayBuffer[String]()
    while(n != 0){
      list += (n % 2).toString
      n = n/2
    }
    while(list.length < bw){
      list += 0.toString
    }
    val sign = list.toList(bw-1)
    val exp = list.slice(mantissa, bw-1).reduce(_+_)
    var sum = binary_string_to_Double(exp, bw)
    var mant = list.slice(0, mantissa).reduce(_+_).reverse
    var new_mant = binary_string_to_Double_Frac(mant, bw)
    new_mant = new_mant * Math.pow(2, (sum-(Math.pow(2, exponent - 1) - 1)).toDouble)
    if(sign.toInt == 1)
      new_mant = new_mant * -1
    new_mant
  }

  def binary_string_to_Double(str: String, bw:Int): BigDecimal = {
    var sum:BigDecimal = 0.0
    for(i <- 0 until str.length){
      if(str(i).equals('1')){
        sum += scala.BigDecimal(Math.pow(2, i))
      }
    }
    sum
  }

  def binary_string_to_Double_Frac(str: String, bw: Int): BigDecimal = {
    var sum:BigDecimal = 0.0
    for(i <- 1 to str.length){
      if(str(i-1).equals('1')){
        sum += scala.BigDecimal(Math.pow(2, -i))
      }
    }
    sum + 1.0
  }

  def convert_string_to_IEEE_754 (str: String, bw: Int):BigInt = {
    var exponent = 0
    var mantissa = 0
    if (bw == 16){
      exponent = 5
      mantissa = 10
    }else if (bw == 32){
      exponent = 8
      mantissa = 23
    }else if(bw == 64){
      exponent = 11
      mantissa = 52
    }else if(bw == 128){
      exponent = 15
      mantissa = 112
    }
    if (str.equals("0.0") || str.equals('0')){
      return scala.BigInt(0)
    }
    var sign = '0'
    var num = str
    if(str(0).equals('-')){
      sign = '1'
      num = str.slice(1,str.length)
    }
    var part = num.split('.')
    var whole = scala.BigInt(part(0))
    var frac = scala.BigDecimal(("0." + part(1)))

    var list1 = mutable.ArrayBuffer[String]()
    while(whole != 0){
      list1 += (whole % 2).toString
      whole = whole/2
    }
    var whole_str = ""
    if(list1.isEmpty){
      whole_str = "0"
    }else{
      whole_str = list1.reverse.reduce(_+_)
    }

    var new_exp = (whole_str.length - 1 + Math.pow(2, exponent - 1) - 1).toInt
//    if(new_exp > Math.pow(2, exponent) - 2){
//      new_exp = (Math.pow(2, exponent) - 2).toInt
//    }

    var list2 = mutable.ArrayBuffer[String]()
    for(i <- 0 until mantissa){
      frac = frac * 2
      if(frac >= 1.0){
        list2 += 1.toString
        frac = frac - 1.0
      }else{
        list2 += 0.toString
      }
    }
    var frac_str = list2.reduce(_+_)
    var fullstr = whole_str+frac_str
    var exp_adj = 0
    var slicefrac = ""
    var i = -1
    do{
      i+=1
      if(fullstr(i) == '0'){
        exp_adj += 1
      }else{
        slicefrac = fullstr.slice(i+1, fullstr.length)
      }
    }while(fullstr(i) != '1')
    new_exp -= exp_adj

    while(slicefrac.length < mantissa){
      slicefrac += '0'
    }

    var list5 = mutable.ArrayBuffer[String]()
    while(new_exp != 0){
      list5 += (new_exp % 2).toString
      new_exp /= 2
    }
    while(list5.length < exponent){
      list5 += "0"
    }
    var final_exp = list5.reverse.reduce(_+_)

//    var neg_adj = 0
//    var pos_adj = 0
//    var append_frac = ""
//
//    if(whole_str(0).equals('0')){
//      neg_adj += 1
//      while(frac_str(0).equals('0')){
//        neg_adj += 1
//        frac_str = frac_str.slice(1,frac_str.length)
//      }
//      append_frac = frac_str.slice(1, frac_str.length)
//    }else{
//      if(whole_str.length != 1){
//        pos_adj = whole_str.length - 1
//        append_frac = whole_str.slice(1,whole_str.length) + frac_str.slice(0, frac_str.length - whole_str.length - 1)
//      }else{
//        append_frac = frac_str
//      }
//    }
//    while(append_frac.length < mantissa){
//      append_frac += "0"
//    }
//    var exp = Math.pow(2,exponent-1).toInt - 1 + pos_adj - neg_adj
//
//    var list3 = mutable.ArrayBuffer[String]()
//    while(exp != 0){
//      list3 += (exp % 2).toString
//      exp = exp/2
//    }
//
//    while(list3.length < exponent){
//      list3 += 0.toString
//    }
//
//    var append_exp = list3.reverse.reduce(_+_)

    //var final_str = sign + append_exp + slicefrac.slice(0,mantissa)
    var final_str = sign + final_exp + slicefrac.slice(0,mantissa)
    (binary_string_to_Double(final_str.reverse, bw)).toBigInt

  }
}
