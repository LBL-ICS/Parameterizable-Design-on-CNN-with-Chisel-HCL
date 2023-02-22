package Testing

import IEEEConversions.FPConvert.{convert_long_to_float, convert_string_to_IEEE_754}
import MemoryModule.ROM_Module.{inputs_ROM, weights_ROM}
import PoolingLayer.Pooling.{Max_Pooling, Mean_Pooling}
import PoolingLayer.PoolingController.Pooling_Controller
import chisel3._
import chisel3.tester._
//import utest.test
import chisel3.tester.RawTester.test

object PoolingCircuit extends App{
  val bw = 32
  test(new Pooling_Controller(6,32)){c=>
    for (i <- 0 until 9) {
      println(s"Pool Layer Output: ${convert_long_to_float(c.io.single_out(i).peek().litValue,32)}")
      //println(s"Pool Layer Output: ${convert_long_to_float(c.io.values(i).peek().litValue,32)}")
      //println(s"Pool Layer Output: ${c.io.single_out(i).peek().litValue}")
    }
//    for (i <- 0 until 4) {
//      println(s"Pool Layer Output: ${convert_long_to_float(c.io.val_out(i).peek().litValue,32)}")
//    }
  }

 /* test (new inputs_ROM(36,bw)) {c=>
    var row = 0
    var col = 0
    var index = 0
    for (i <- 0 until 2) {
      for (j <- 0 until 2) {
        for (n <- 0 until 2) {
          for (m <- 0 until 2) {
            row = (i * 2 + n)
            col = (j * 2 + m)
            index = (col + 6 * row) //6 because 6*6 matrix
            println(s"ROM Data: ${convert_long_to_float(c.io.out_s(index).peek().litValue,32)}")
          }
        }
      }
    }
  }*/

 /* test(new Mean_Pooling(bw)){c=>
      val input = Seq(1.2,3.3,4.1,-0.6)
      val divnum = 4.0
      c.io.pixel1.poke(convert_string_to_IEEE_754(input(0).toString, bw).asUInt)
      c.io.pixel2.poke(convert_string_to_IEEE_754(input(1).toString,bw).asUInt)
      c.io.pixel3.poke(convert_string_to_IEEE_754(input(2).toString, bw).asUInt)
      c.io.pixel4.poke(convert_string_to_IEEE_754(input(3).toString,bw).asUInt)
      c.io.DivNum.poke(convert_string_to_IEEE_754(divnum.toString,bw).asUInt)
      println(s" MaxPool Layer Output: ${convert_long_to_float(c.io.f_mean.peek().litValue,bw)}")
  }*/

  /*test (new inputs_ROM (10,32)) { d=>
    for (i <- 0 until 10) {
      println(s"output file.txt ${i}: ${convert_long_to_float(d.io.out_s(i).peek().litValue, 32)}")
    }
  }*/
  println("[{(Generating Verilog)}")
  (new chisel3.stage.ChiselStage).emitVerilog(new Pooling_Controller(6,bw))
}
