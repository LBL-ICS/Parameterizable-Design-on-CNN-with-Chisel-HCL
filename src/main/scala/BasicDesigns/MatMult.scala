package BasicDesigns
import FloatingPointDesigns.FPArithmetic.{FP_adder, FP_multiply_sum}
import chisel3._

object MatMult extends App {
  class MatMult1(matASize: Int, matBSize: Int, bw: Int) extends Module {
    val io = IO(new Bundle {
      val matA = Input(Vec(matASize, UInt(bw.W)))
      val matB = Input(Vec(matBSize, UInt(bw.W)))
      val biasParameter = Input(UInt(bw.W))
      val f_mac = Output(UInt(bw.W))
    })
    var sum = Wire(UInt(bw.W))
    sum := 0.asUInt(bw.W)
    var zero = Wire(UInt(bw.W))
    zero := 0.asUInt(bw.W)
    val f_adder = Module(new FP_adder(bw))
    val mul = Module(new FP_multiply_sum(matBSize,bw))
    for (i <- 0 until matBSize) {
      mul.io.in_a(i) := io.matA(i)
      mul.io.in_b(i) := io.matB(i)
    }
    f_adder.io.in_a := io.biasParameter
    f_adder.io.in_b := mul.io.out_s
    sum = f_adder.io.out_s // adding bias parameter to the summed value
    when(sum(bw-1) === 1.U){
      io.f_mac := zero
    }.otherwise{
      io.f_mac := sum
    }
  }
}