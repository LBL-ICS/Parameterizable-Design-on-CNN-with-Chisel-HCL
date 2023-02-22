package Testing

import FullyConnectedLayer.FCLayer.FC
import IEEEConversions.FPConvert.{convert_long_to_float, convert_string_to_IEEE_754}
import chisel3._
import chisel3.tester._
import chisel3.tester.RawTester.test

import java.io.PrintWriter

object FCLayerCircuit extends App{
/*  test (new FC(32,2,5,5,1)) { e=>
    val input = Seq(9.0,0.0,0.0,0.0,0.0,1.0,2.0,3.0,4.0,5.0)
    val weights = Seq(1.0,2.0,3.0,4.0,5.0)
    val bw = 32
    val bias = Seq(1.0,1.0)
    for (i <-0 until 10) {
      e.io.matA(i).poke(convert_string_to_IEEE_754(input(i).toString, 32).asUInt)
    }
    for (i <- 0 until 5) {
      e.io.matB(i).poke(convert_string_to_IEEE_754(weights(i).toString,32).asUInt)
    }
    for (i <- 0 until 2) {
      e.io.biasParameter(i).poke(convert_string_to_IEEE_754(bias(i).toString, 32).asUInt)
    }
    //e.io.biasParameter.poke(1.U)
    println(s"output FC 0: ${convert_long_to_float(e.io.fC_out(0).peek().litValue,32)}")
    println(s"output FC 1: ${convert_long_to_float(e.io.fC_out(1).peek().litValue,32)}")
    //println(s"Testing Digit: ${convert_long_to_float(e.io.TestDigit.peek().litValue,32)}")
  }*/
  println("[{(Generating Verilog)}")
  val pw = new PrintWriter("FC_16.v")
  pw.println(getVerilogString(new FC(16, 10,16,16,1)))
  pw.close()
//  println("[{(Generating Verilog)}")
//  (new chisel3.stage.ChiselStage).emitVerilog(new FC(32, 10,3168,3168,1))
}
