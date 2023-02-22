package PoolingLayer
import FloatingPointDesigns.FPArithmetic.{FP_Comparitor, FP_adder, FP_divider, FP_multiplier}
import IEEEConversions.FPConvert.convert_string_to_IEEE_754
import chisel3._

object Pooling extends App {
  class Max_Pooling (bw: Int) extends Module {
    val io = IO(new Bundle {

      val pixel1 = Input( UInt(bw.W))
      val pixel2 = Input(UInt(bw.W))
      val pixel3 = Input(UInt(bw.W))
      val pixel4 = Input(UInt(bw.W))
      val f_max = Output(UInt(bw.W))
    })
    var out1 = Wire(UInt(bw.W))
    var out2 = Wire(UInt(bw.W))

    val fp1 = Module(new FP_Comparitor(bw))
    fp1.io.in_a := io.pixel1
    fp1.io.in_b := io.pixel2
    out1 := fp1.io.out_s

    val fp2 = Module(new FP_Comparitor(bw))
    fp2.io.in_a := io.pixel3
    fp2.io.in_b := io.pixel4
    out2 := fp2.io.out_s

    val fp3 = Module(new FP_Comparitor(bw))
    fp3.io.in_a := out1
    fp3.io.in_b := out2
    io.f_max := fp3.io.out_s

  }

  class Mean_Pooling (bw: Int) extends Module {
    val io = IO(new Bundle {

      val pixel1 = Input( UInt(bw.W))
      val pixel2 = Input(UInt(bw.W))
      val pixel3 = Input(UInt(bw.W))
      val pixel4 = Input(UInt(bw.W))
      val DivNum = Input(UInt(bw.W))
      val f_mean = Output(UInt(bw.W))
    })
    var out1 = Wire(UInt(bw.W))
    var out2 = Wire(UInt(bw.W))
    var mean = Wire(UInt(bw.W))
    //var DivNum = RegInit(UInt(3.W),4.U)

    val f_adder1 = Module(new FP_adder(bw))
    f_adder1.io.in_a := io.pixel1
    f_adder1.io.in_b := io.pixel2
    out1 := f_adder1.io.out_s

    val f_adder2 = Module(new FP_adder(bw))
    f_adder2.io.in_a := io.pixel3
    f_adder2.io.in_b := io.pixel4
    out2 := f_adder2.io.out_s

    val f_adder3 = Module (new FP_adder(bw))
    f_adder3.io.in_a := out1
    f_adder3.io.in_b := out2
    mean := f_adder3.io.out_s

    val f_divide = Module (new FP_divider(bw))
    f_divide.io.in_a := mean
    f_divide.io.in_b := io.DivNum
    io.f_mean := f_divide.io.out_s
  }

  println("[{(Generating Verilog)}")
  (new chisel3.stage.ChiselStage).emitVerilog(new Mean_Pooling(32))
}
