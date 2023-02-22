package NeuralNetworkEngine
import BasicDesigns.MatMult.MatMult1
import ConvolutionalLayer.Convolution.Matrix_Controller
import FloatingPointDesigns.FPArithmetic.{Mux8, demux1_8}
import FullyConnectedLayer.FCLayer.FC
import IEEEConversions.FPConvert.convert_string_to_IEEE_754
import MemoryModule.ROM_Module.{inputs_ROM, weights_ROM}
import PoolingLayer.PoolingController.Pooling_Controller
import SoftMaxLayer.SoftMax.DigitPrediction
import chisel3._

object NeuralNetwork extends App{
  class CNNEngine (InputMatrix_A_row : Int, InputMatrix_A_col : Int, InputMatrix_B_row : Int, InputMatrix_B_col : Int, bw: Int) extends Module {
    require (InputMatrix_A_row == InputMatrix_A_col && InputMatrix_B_row == InputMatrix_B_col && InputMatrix_A_row >= InputMatrix_B_row)
    val io = IO(new Bundle {
      val inputA = Input(Vec(9, UInt(bw.W)))
      val inputB = Input(Vec(9, UInt(bw.W)))
      //val ConOut = Output(Vec(64, UInt(bw.W)))  //64 - convolutional 16- pooling layer

      val DemuxSel0 = Input(UInt(3.W))
      val DemuxSel = Input(UInt(3.W))

      val DemuxEn0 = Input(Vec(8, UInt(1.W)))
      val DemuxEn = Input(Vec(64, UInt(1.W)))

      //val MuxSel = Input(UInt(3.W))
      val FCMuxSel = Input(UInt(2.W))
      val FCDemuxEn = Input(Vec(4, UInt(1.W)))
      val Digit = Output(UInt(4.W))
    })
    val ConvolutionBiasParameter = 0  // Bias Parameter for Convolution Module

    var position = 0
    var row = 0
    var col = 0
    var stride = 1
    var index = 0
    var count = 0
    var inst = 0

    //val input = Module(new inputs_ROM(InputMatrix_A_row * InputMatrix_A_col, bw)) // for the inputs
    //val weight = Module(new weights_ROM(InputMatrix_B_row * InputMatrix_B_col, bw)) // for the weights
    val reginA = Reg(Vec(9, UInt(bw.W)))
    val reginB = Reg(Vec(9, UInt(bw.W)))
    //val reg_out = Reg(Vec(8, UInt(bw.W)))
    //val reg_out = Reg(UInt(bw.W))
    val MatrixMuls = for(i <- 0 until 1) yield {
      val MatrixMul = Module(new MatMult1(InputMatrix_B_row * InputMatrix_B_col, InputMatrix_B_row * InputMatrix_B_col, bw))
      MatrixMul
    }
    val MatrixMuls_io = MatrixMuls.map(_.io).toVector

    for(i <- 0 until 9) {
      reginA(i) := io.inputA(i)
      reginB(i) := io.inputB(i)
    }

    val demuxinst0 = for(i <- 0 until 1) yield {
      val demux0 = Module(new demux1_8(bw))
      demux0
    }
    val demuxinstOut = demuxinst0.map(_.io).toVector

    val demuxinst1 = for(i <- 0 until 8) yield {
      val demux = Module(new demux1_8(bw))
      demux
    }
    val demuxinst = demuxinst1.map(_.io).toVector

      for(j <- 0 until 9){
        MatrixMuls_io(0).matA(j) := reginA(j)
        MatrixMuls_io(0).matB(j) := reginB(j)//weight.io.out_s(j)
      }
      MatrixMuls_io(0).biasParameter := ConvolutionBiasParameter.U
      //reg_out := MatrixMuls_io(0).f_mac
      demuxinstOut(0).in := MatrixMuls_io(0).f_mac
      demuxinstOut(0).sel := io.DemuxSel0
      //io.ConOut(i) := reg_out(i)
//    }

    for (i <- 0 until 8) {
      demuxinst(i).sel := io.DemuxSel
    }
    for(i <- 0 until 8) {
      demuxinst(i).in := demuxinstOut(0).out(i)
    }
    for (i <- 0 until 8) {
      demuxinstOut(0).enable(i) := io.DemuxEn0(i)
    }

    for(i <- 0 until 8) {
      for(j <- 0 until 8) {
        demuxinst(i).enable(j) := io.DemuxEn(j + i * 8)
      }
    }
    //io.ConOut(0) := demuxinst(0).out(0)

    var PoolingMatSize = InputMatrix_A_row - InputMatrix_B_row +1
    val PoolingControl = Module(new Pooling_Controller(PoolingMatSize,bw))
    for (i <- 0 until PoolingMatSize ) {
      for(j <- 0 until 8) {
//        io.ConOut(j + i*8) := demuxinst(i).out(j)                    // Testing Convolutional Layer outputs
        PoolingControl.io.InputMatrix(i + j*8) := demuxinst(i).out(j)
      }
    }

    var FCSize = (PoolingMatSize/2) * (PoolingMatSize/2)
    val FullyConnected = Module(new FC(bw, 10, FCSize, FCSize, 1))
    for(i <- 0 until FCSize) {
      //io.ConOut(i) := PoolingControl.io.single_out(i)                     // Testing Pooling layer output
      FullyConnected.io.matB(i) := PoolingControl.io.single_out(i)
    }
    val mem = scala.io.Source.fromFile("FCLayerWeights.txt").getLines().toIndexedSeq.map(x => convert_string_to_IEEE_754(x, bw).asUInt)
    val file = WireInit(VecInit(mem))
    for(i <- 0 until 10*FCSize) {
      FullyConnected.io.matA(i) := file(i)
    }
    val mem2 = scala.io.Source.fromFile("FCLayerBiasParameter.txt").getLines().toIndexedSeq.map(y => convert_string_to_IEEE_754(y, bw).asUInt)
    val file2 = WireInit(VecInit(mem2))
    for(i <- 0 until 10) {
      FullyConnected.io.biasParameter(i) := file2(i)
    }
    FullyConnected.io.MuxSel := io.FCMuxSel
    FullyConnected.io.DemuxEn := io.FCDemuxEn

    val SoftMaxModule = Module(new DigitPrediction(bw))
    for(i <- 0 until 10) {
//      io.ConOut(i) := FullyConnected.io.fC_out(i)                        // Testing Softmax layer output
      SoftMaxModule.io.input(i) := FullyConnected.io.fC_out(i)
    }
    io.Digit := SoftMaxModule.io.Digit
  }
//  println("[{(Generating Verilog)}")
//  (new chisel3.stage.ChiselStage).emitVerilog(new CNNEngine(10, 10,3,3,64))
}
