package Testing

import IEEEConversions.FPConvert._
import chisel3._
import chisel3.tester._
import chisel3.tester.RawTester.test

import java.io.PrintWriter
import FloatingPointDesigns.FPArithmetic._
//import GenerateRandom.RandomTxtFile.genRandom
import scala.collection.mutable
import scala.util.Random
import NeuralNetworkSWModel.SWModel
object FP_test {
  def main(args: Array[String]):Unit= {
    val bw = 16
    //val in1 = Seq("0.01", "0.123", "1.23", "3.41", "-1.995", "-0.123", "-0.001","8.992","16.5002","-22.0002","-0.998")
    //val in2 = Seq("0.01", "0.123", "1.23", "3.41", "-1.995", "-0.123", "-0.001","8.992","16.5002","-22.0002","-0.998")
    val in1 = Seq(1.0, 2.0)
    val in2 = Seq(3.0, 4.0)
    val in3 = Seq(5.0, 6.0)
    val in4 = Seq(7.0, 8.0)
    val in5 = 5.0
    val in6 = 6.0
    val in7 = 12.5002
    val in8 = 10.41

//    println("[{(Generating Verilog)}")
//    val pw = new PrintWriter("FP_Comparitor_32.v")
//    pw.println(getVerilogString(new FP_Comparitor(32)))
//    pw.close()

 /*   test(new sumcontroller(bw)){c =>
      c.io.a_in(0).poke(1.U)
      c.io.b_in(0).poke(3.U)
      c.io.a_in(1).poke(2.U)
      c.io.b_in(1).poke(4.U)
      c.clock.step(1)
      c.io.a_in(0).poke(5.U)
      c.io.b_in(0).poke(7.U)
      c.clock.step(1)
      println(s"Out: ${c.io.out.peek().litValue}")
      c.clock.step(1)
      println(s"Out2: ${c.io.out.peek().litValue}")
    }*/

    test (new demux1_8(bw)) { c =>
      c.io.in.poke(1.U)
      c.io.sel.poke(1.U)
      println(s"Out: ${c.io.out(1).peek().litValue}")
    }

 /*   test (new sum2(bw)) { c =>
      c.io.a.poke(1.U)
      c.io.b.poke(3.U)
      println(s"Out: ${c.io.summ.peek().litValue}")
    }*/
/*    test(new FP_Comparitor(bw)){c =>
      c.io.in_a.poke(convert_string_to_IEEE_754(in1.toString, bw).U)
      c.io.in_b.poke(convert_string_to_IEEE_754(in2.toString, bw).U)
      println(s"Maximum: ${convert_long_to_float(c.io.out_s.peek().litValue, bw)}")
    }*/

//    test(new Comparitor(bw)){c =>
//      c.io.in_a.poke(in1.U)
//      c.io.in_b.poke(in2.U)
//      println(s"Maximum: ${convert_long_to_float(c.io.max.peek().litValue, bw)}")
//    }
  }
}
