package MemoryModule

import IEEEConversions.FPConvert.convert_string_to_IEEE_754
import chisel3._

object ROM_Module {

  class inputs_ROM(size: Int, bw: Int) extends Module{//probably not an ideal implementation, but i'll work on these later on when I have more details
    val io = IO(new Bundle{
      val out_s = Output(Vec( size, UInt(bw.W)))
    })
    val mem = scala.io.Source.fromFile("inputs.txt").getLines().toIndexedSeq.map(x => convert_string_to_IEEE_754(x, bw).asUInt)
    val file = WireInit(VecInit(mem))
    io.out_s := file
  }

  class weights_ROM(size: Int, bw: Int) extends Module{//probably not an ideal implementation, but i'll work on these later on when I have more details
    val io = IO(new Bundle{
      val out_s = Output(Vec( size, UInt(bw.W)))
    })
    val mem = scala.io.Source.fromFile("weights.txt").getLines().toIndexedSeq.map(x => convert_string_to_IEEE_754(x, bw).asUInt)
    val file = WireInit(VecInit(mem))
    io.out_s := file
  }

}
