package PoolingLayer
import MemoryModule.ROM_Module.{inputs_ROM, weights_ROM}
import PoolingLayer.Pooling.{Max_Pooling, Mean_Pooling}
import chisel3._

import java.io.PrintWriter

object PoolingController extends App{
  class Pooling_Controller ( row_size: Int, bw: Int) extends Module {
    val io = IO(new Bundle {
      val InputMatrix = Input(Vec(row_size * row_size, UInt(bw.W)))
      val single_out = Output(Vec((row_size/2) * (row_size/2), UInt(bw.W)))
      //val values = Output(Vec(row_size * row_size, UInt(bw.W)))
    })
    var row = 0
    var col = 0
    var index = 0
    var count = 0
    var fp_count = ((row_size/2)*(row_size/2))
    val values = Wire(Vec(row_size * row_size,UInt(bw.W)))
// Max Pooling instantiation
    val Max_Poolings = for(i <- 0 until fp_count) yield {
      val Max_Pooling = Module(new Max_Pooling(bw))
      Max_Pooling
    }
    val Max_Poolings_io = Max_Poolings.map(_.io).toVector
// These loop takes sequence of inputs and converts to linear output
    for (i <- 0 until row_size/2) {
      for (j <- 0 until row_size/2) {
        for (n <- 0 until 2) {
          for (m <- 0 until 2) {
            row = (i * 2 + n)
            col = (j * 2 + m)
            index = (col + row_size * row ) //ex: row_size = 6 because 6*6 matrix
            count = count + 1
            values(count-1) := io.InputMatrix(index)
          }
        }
      }
    }
    var l = 0
    for (i <- 0 until fp_count) {
      Max_Poolings_io(i).pixel1 := values(l)
      Max_Poolings_io(i).pixel2 := values(l+1)
      Max_Poolings_io(i).pixel3 := values(l+2)
      Max_Poolings_io(i).pixel4 := values(l+3)
      io.single_out(i):= Max_Poolings_io(i).f_max
      l = l + 4
    }
  }
}
