package FullyConnectedLayer
import FloatingPointDesigns.FPArithmetic.{Demux1_2, Demux1_4, FP_adder, FP_multiply_sum, Mux2, Mux4, Mux8}
import chisel3._
object FCLayer extends App{

  class FC (bw: Int, MatARows: Int, MatACol: Int, MatBRows: Int, MatBCol: Int) extends Module {
    require(MatACol == MatBRows)
    // Mat A will the weigths and Mat B will be the ouput of Pooling Layer.
    // The Output of Fully Connected Layer strictly should be a 10 elements only for digit recognition.
    // Mat A = 10 * x  and Mat B = x * 1 (Output of Pooling Layer); x can be any value.
    val io = IO(new Bundle {
      val matA = Input(Vec(MatACol*MatARows, UInt(bw.W)))
      val matB = Input(Vec(MatBCol*MatBRows, UInt(bw.W)))
      val MuxSel = Input(UInt(2.W))
      val DemuxEn = Input(Vec(4, UInt(1.W)))
      val biasParameter = Input(Vec(MatARows*MatBCol, UInt(bw.W)))
      val fC_out = Output(Vec(MatARows * MatBCol, UInt(bw.W)))
    })
/*   for (i <- 0 until MatARows*MatBCol) {
      val mul = Module(new FP_multiply_sum(MatACol,bw))
      for (j <- 0 until MatACol) {
        mul.io.in_a(j) := io.matA(j + (i * MatACol))
        mul.io.in_b(j) := io.matB(j)
      }
      val fp_adder = Module(new FP_adder(bw))
      fp_adder.io.in_a := mul.io.out_s
      fp_adder.io.in_b := io.biasParameter(i)
      io.fC_out(i) := fp_adder.io.out_s
    } */
    val mul1 = for(i <- 0 until 3) yield {
      val mul = Module (new FP_multiply_sum(MatACol, bw))
      mul
    }
    val mul_1 = mul1.map(_.io).toVector

    val sum = for(i <- 0 until 3) yield {
      val summ = Module (new FP_adder(bw))
      summ
    }
    val fp_sum = sum.map(_.io).toVector

    val Muxinst1 = for(i <- 0 until 16) yield {
      val Mux = Module(new Mux4(bw))
      Mux
    }
    val Muxinst_1 = Muxinst1.map(_.io).toVector

    val Muxinst2 = for(i <- 0 until 16) yield {
      val Mux = Module(new Mux4(bw))
      Mux
    }
    val Muxinst_2 = Muxinst2.map(_.io).toVector

    val Muxinst3 = for(i <- 0 until 16) yield {
      val Mux = Module(new Mux2(bw))
      Mux
    }
    val Muxinst_3 = Muxinst3.map(_.io).toVector

    val DemuxInst_1 = Module(new Demux1_4(bw))
    val DemuxInst_2 = Module(new Demux1_4(bw))
    val DemuxInst_3 = Module(new Demux1_2(bw))
//---------------Connecting Mux output with Multiplier input-------------
    for(j <- 0 until 16) {
      mul_1(0).in_a(j) := Muxinst_1(j).out
      mul_1(0).in_b(j) := io.matB(j)
    }
    for(j <- 0 until 16) {
      mul_1(1).in_a(j) := Muxinst_2(j).out
      mul_1(1).in_b(j) := io.matB(j)
    }
    for(j <- 0 until 16) {
      mul_1(2).in_a(j) := Muxinst_3(j).out
      mul_1(2).in_b(j) := io.matB(j)
    }
//--------------Connecting Inputs to Mux inputs-------

    for(i <- 0 until 4) {
      for(j <- 0 until 16) {
        Muxinst_1(j).in(i) := io.matA(j + 16 * i)
      }
    }
    for(i <- 0 until 4) {
      for(j <- 0 until 16) {
        Muxinst_2(j).in(i) := io.matA(j + 16 * i + 64)
      }
    }
    for(i <- 0 until 2) {
      for(j <- 0 until 16) {
        Muxinst_3(j).in(i) := io.matA(j + 16 * i + 128)
      }
    }
//-------------------Connecting Mux sel input lines------
    for(i <- 0 until 16){
      Muxinst_1(i).sel := io.MuxSel
    }
    for(i <- 0 until 16){
      Muxinst_2(i).sel := io.MuxSel
    }
    for(i <- 0 until 16){
      Muxinst_3(i).sel := io.MuxSel(1)
    }
//-------------------Mux for FP_adder---------------------
    val MuxInst1 = Module(new Mux4(bw))
    val MuxInst2 = Module(new Mux4(bw))
    val MuxInst3 = Module(new Mux2(bw))

    for(i <- 0 until 4){
      MuxInst1.io.in(i) := io.biasParameter(i)
      MuxInst1.io.sel := io.MuxSel
      DemuxInst_1.io.sel := io.MuxSel
    }

    for(i <- 0 until 4){
      MuxInst2.io.in(i) := io.biasParameter(i+4)
      MuxInst2.io.sel := io.MuxSel
      DemuxInst_2.io.sel := io.MuxSel
    }

    for(i <- 0 until 2){
      MuxInst3.io.in(i) := io.biasParameter(i+8)
      MuxInst3.io.sel := io.MuxSel(1)
      DemuxInst_3.io.sel := io.MuxSel(1)
    }
//---------------FP_sum input connection--------
    fp_sum(0).in_a := mul_1(0).out_s
    fp_sum(0).in_b := MuxInst1.io.out

    fp_sum(1).in_a := mul_1(1).out_s
    fp_sum(1).in_b := MuxInst2.io.out

    fp_sum(2).in_a := mul_1(2).out_s
    fp_sum(2).in_b := MuxInst3.io.out
//--------- Demux at output of FP_Sum------------


    DemuxInst_1.io.in := fp_sum(0).out_s
    DemuxInst_2.io.in := fp_sum(1).out_s
    DemuxInst_3.io.in := fp_sum(2).out_s

    for(i <- 0 until 4){
      DemuxInst_1.io.enable(i) := io.DemuxEn(i)
      DemuxInst_2.io.enable(i) := io.DemuxEn(i)
    }
    DemuxInst_3.io.enable(0) := io.DemuxEn(0)
    DemuxInst_3.io.enable(1) := io.DemuxEn(2)

    for(i <- 0 until 4){
      io.fC_out(i) := DemuxInst_1.io.out(i)
    }
    for(i <- 4 until 8){
      io.fC_out(i) := DemuxInst_2.io.out(i-4)
    }
    for(i <- 8 until 10){
      io.fC_out(i) := DemuxInst_3.io.out(i-8)
    }

  }
  println("[{(Generating Verilog)}")
  (new chisel3.stage.ChiselStage).emitVerilog(new FC(32, 10,16,16,1))
}
