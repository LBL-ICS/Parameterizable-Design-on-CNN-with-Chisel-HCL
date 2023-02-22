package SoftMaxLayer

import PoolingLayer.Pooling.{ Mean_Pooling}
import FloatingPointDesigns.FPArithmetic.{FP_adder, FP_divider, FP_Comparitor}
import chisel3._

object SoftMax extends App {
  class DigitPrediction (bw: Int) extends Module {
    val io = IO(new Bundle {
      val input = Input(Vec(10, UInt(bw.W)))
      val Digit = Output(UInt(4.W))
    })

    val out1 = Wire(UInt(bw.W))
    val out2 = Wire(UInt(bw.W))
    val out3 = Wire(UInt(bw.W))
    val out4 = Wire(UInt(bw.W))
    val out5 = Wire(UInt(bw.W))
    val out6 = Wire(UInt(bw.W))
    val out7 = Wire(UInt(bw.W))
    val out8 = Wire(UInt(bw.W))
    val maximum = Wire(UInt(bw.W))

    val position = Wire(UInt(4.W))
    val Digit = Reg(UInt(4.W))

    val fp1 = Module(new FP_Comparitor(bw))
    fp1.io.in_a := io.input(0)
    fp1.io.in_b := io.input(1)
    out1 := fp1.io.out_s

    val fp2 = Module(new FP_Comparitor(bw))
    fp2.io.in_a := io.input(2)
    fp2.io.in_b := io.input(3)
    out2 := fp2.io.out_s

    val fp3 = Module(new FP_Comparitor(bw))
    fp3.io.in_a := io.input(4)
    fp3.io.in_b := io.input(5)
    out3 := fp3.io.out_s

    val fp4 = Module(new FP_Comparitor(bw))
    fp4.io.in_a := io.input(6)
    fp4.io.in_b := io.input(7)
    out4 := fp4.io.out_s

    val fp5 = Module(new FP_Comparitor(bw))
    fp5.io.in_a := io.input(8)
    fp5.io.in_b := io.input(9)
    out5 := fp5.io.out_s

    val fp6 = Module(new FP_Comparitor(bw))
    fp6.io.in_a := out1
    fp6.io.in_b := out2
    out6 := fp6.io.out_s

    val fp7 = Module(new FP_Comparitor(bw))
    fp7.io.in_a := out3
    fp7.io.in_b := out4
    out7 := fp7.io.out_s

    val fp8 = Module(new FP_Comparitor(bw))
    fp8.io.in_a := out6
    fp8.io.in_b := out7
    out8 := fp8.io.out_s

    val fp9 = Module(new FP_Comparitor(bw))
    fp9.io.in_a := out8
    fp9.io.in_b := out5
    maximum := fp9.io.out_s
    /*for (i <- 0 until 10) {
      when (maximum === io.input(i)) {
        position := i.U
      }
    }*/
    when (maximum === io.input(0)) {
        position := 0.U
      } .elsewhen(maximum === io.input(1)) {
        position := 1.U
      } .elsewhen(maximum === io.input(2)) {
        position := 2.U
      } .elsewhen(maximum === io.input(3)) {
        position := 3.U
      } .elsewhen(maximum === io.input(4)) {
        position := 4.U
      } .elsewhen(maximum === io.input(5)) {
        position := 5.U
      } .elsewhen(maximum === io.input(6)) {
        position := 6.U
      } .elsewhen(maximum === io.input(7)) {
        position := 7.U
      } .elsewhen(maximum === io.input(8)) {
        position := 8.U
      } .otherwise {
        position := 9.U
      }
    Digit := position
    io.Digit := Digit
  }
//  println("[{(Generating Verilog)}")
//  (new chisel3.stage.ChiselStage).emitVerilog(new DigitPrediction(16))
}
