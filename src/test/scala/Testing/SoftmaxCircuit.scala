package Testing

import IEEEConversions.FPConvert._
import SoftMaxLayer.SoftMax.DigitPrediction
import chisel3._
import chisel3.tester._
import chisel3.tester.RawTester.test

import scala.collection.mutable
import scala.util.Random
import java.io.PrintWriter
object SoftmaxCircuit {
  def main(args: Array[String]):Unit= {
    val bw = 16
    val in1 = Seq(0.01, 0.123, 3.41, -1.995, -0.123, -0.001,8.992,1.23,  -22.0002, 16.5002)
//    val in2 = Seq("0.01", "0.123", "1.23", "3.41", "-1.995", "-0.123", "-0.001","8.992","16.5002","-22.0002","-0.998")
//    val in1 = "-0.01"
//    val in2 = "22.123"

    println("[{(Generating Verilog)}")
    val pw = new PrintWriter("DigitPrediction_16.v")
    pw.println(getVerilogString(new DigitPrediction(bw)))
    pw.close()

    test(new DigitPrediction(bw)){c =>
      for (i <- 0 until 10){
        c.io.input(i).poke(convert_string_to_IEEE_754(in1(i).toString, bw).U)
        //println(s"input: ${convert_string_to_IEEE_754(in1.toList(i), bw)}")
        //println("\n")
      }
      //println(s"Digit: ${convert_long_to_float(c.io.Digit.peek().litValue, bw)}")
      println(s"Digit: ${c.io.Digit.peek().litValue}")
    }
  }
}
