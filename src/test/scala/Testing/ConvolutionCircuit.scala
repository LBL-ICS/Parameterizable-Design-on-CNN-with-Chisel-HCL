package Testing

import BasicDesigns.MatMult.MatMult1
import ConvolutionalLayer.Convolution.Matrix_Controller
import IEEEConversions.FPConvert.convert_string_to_IEEE_754
import MemoryModule.ROM_Module.{inputs_ROM, weights_ROM}

import java.io.PrintWriter
//import utest.test
import chisel3._
import chisel3.tester._
import chisel3.tester.RawTester.test
import ConvolutionalLayer.Convolution
import IEEEConversions.FPConvert.convert_long_to_float
import NeuralNetworkSWModel.SWModel._
object ConvolutionCircuit extends App{
  val bw = 32
//  println(" SW Results\n")
//  compute(5,3)
  test(new Matrix_Controller(5, 3, bw)){c=>
    val input = Seq(0.10, 1.20, 9.30, 1.40, 1.50,
      2.60, 3.40, 5.68, 7.88, 4.55,
      3.43, 2.44, 5.44, 6.57, 8.87,
      6.77, 3.33, 2.33, 5.55, 1.23,
      3.43, 3.23, 8.67, 9.88, 0.78) // for the inputs
    val weight = Seq(0.08990000000000001, 1.9108, 0.24500000000000002, 2.4161, 3.7161, 3.5102,4.7182,4.1788,0.6475000000000001)  // for the weights
    for (i <- 0 until 25) {
      c.io.input(i).poke(convert_string_to_IEEE_754(input(i).toString, 32).asUInt)
    }
    for (i <- 0 until 9) {
      c.io.weight(i).poke(convert_string_to_IEEE_754(weight(i).toString,32).asUInt)
    }
    c.io.BiasValue.poke(1.U)

    for (i <- 0 until 9) { // (6-2) * (6-2) MatARowSize = 6
      println(s"output from Matrix controller: ${convert_long_to_float(c.io.single_out(i).peek().litValue, bw)}")
    }
  }

/*  test (new MatMult1(9,9,32)) { e=>
    val input = Seq(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
    val weights = Seq(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
    for (i <-0 until 9) {
      e.io.matA(i).poke(convert_string_to_IEEE_754(input(i).toString, 32).asUInt)
      e.io.matB(i).poke(convert_string_to_IEEE_754(weights(i).toString,32).asUInt)
    }
    //e.io.biasParameter.poke(1.U)
    println(s"output MatMult: ${convert_long_to_float(e.io.f_mac.peek().litValue,32)}")
  }*/
  println("[{(Generating Verilog)}")
  val pw = new PrintWriter("Matrix_Controller_16.v")
  pw.println(getVerilogString(new Matrix_Controller(10, 3, bw)))
  pw.close()
}
