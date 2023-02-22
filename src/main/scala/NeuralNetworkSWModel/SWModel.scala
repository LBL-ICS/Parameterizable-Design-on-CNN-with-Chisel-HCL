package NeuralNetworkSWModel

import scala.collection.mutable
import scala.io.Source._

object SWModel {
  def compute (in_matA_size: Int, in_matB_size: Int): Unit = {
    val inputs = fromFile("inputs.txt").getLines().toIndexedSeq.map(x => x.toDouble)
    val weights = fromFile("weights.txt").getLines().toIndexedSeq.map(x => x.toDouble)
    val bias_value = 0.0
    var row = 0
    var col = 0
    var index = 0
    val stride = 1
    var position = 0
    var con_val = 0.0
    var con_lay = mutable.ArrayBuffer[Double]()
    var con_out = mutable.ArrayBuffer[Double]()
    for(i <- 0 until in_matA_size-2) {
      for(j <- 0 until in_matA_size-2) {
        for (n <- 0 until in_matB_size) {
          for(m <- 0 until in_matB_size) {
            row = i * stride + n
            col = j * stride + m
            index = (col + in_matA_size * row)
            con_lay += inputs(index)
          }
        }
      }
    }
    for(i <- 0 until (in_matA_size-2) * (in_matA_size-2)) {
      for(j <- 0 until in_matB_size*in_matB_size) {
        con_val = con_val + (con_lay(j+position) * weights(j) )
      }
      con_val = con_val + bias_value
      if (con_val < 0.0) {
        con_out += 0.0
      }
      else
        con_out += con_val
      position = position + (in_matB_size*in_matB_size)
      con_val = 0.0
    }
    // Printing Convolutional Layer results
    for(i <- 0 until (in_matA_size-in_matB_size+1)*(in_matA_size-in_matB_size+1)){
      println(s"output${i} = ${con_out.toIndexedSeq(i)}")
    }

    //-----------------Pooling Layer----------------
    val row_size = (in_matA_size-in_matB_size+1)
    var pool_layer_ele = mutable.ArrayBuffer[Double]()
    var out_pool_layer = mutable.ArrayBuffer[Double]()
    val fp_count = ((row_size/2)*(row_size/2))
    for (i <- 0 until row_size/2) {
      for (j <- 0 until row_size/2) {
        for (n <- 0 until 2) {
          for (m <- 0 until 2) {
            row = (i * 2 + n)
            col = (j * 2 + m)
            index = (col + row_size * row ) //ex: row_size = 6 because 6*6 matrix
            pool_layer_ele += con_out(index)
          }
        }
      }
    }
    var max = 0.0
    var l = 0
    for (i <- 0 until fp_count) {
      max = 0.0
      if (pool_layer_ele(l) >= max)
        max = pool_layer_ele(l)
      if (pool_layer_ele(l+1) >= max)
        max = pool_layer_ele(l+1)
      if (pool_layer_ele(l+2) >= max)
        max = pool_layer_ele(l+2)
      if (pool_layer_ele(l+3) >= max)
        max = pool_layer_ele(l+3)
      out_pool_layer += max
      l = l + 4
    }
    // Printing Convolutional Layer results
//    for(i <- 0 until fp_count){
//       println(s"output${i} = ${out_pool_layer.toIndexedSeq(i)}")
//    }

    //------------Fully Connected Layer---------
    val fc_weights = fromFile("FCLayerWeights.txt").getLines().toIndexedSeq.map(x => x.toDouble)
    val fc_bias = fromFile("FCLayerBiasParameter.txt").getLines().toIndexedSeq.map(x => x.toDouble)
    var temp = 0.0
    var fc_out = mutable.ArrayBuffer[Double]()
    var fc_out_with_bias = mutable.ArrayBuffer[Double]()
    for(i <- 0 until 10) {
        temp = 0.0
        for(k <- 0 until 16) {
          temp = temp + fc_weights(i * 16 + k) * out_pool_layer(k)
        }
      fc_out += temp
    }
    for (i <- 0 until 10) {
      temp = (fc_out(i) + fc_bias(i))
      fc_out_with_bias += temp
    }
    // Printing FC Layer results
//    for(i <- 0 until 10){
//      println(s"output${i} = ${fc_out_with_bias.toIndexedSeq(i)}")
//    }

    //-------------------------Softmax Layer---------------
    var Digit = 0
    max = 0
    for (i <- 0 until 10) {
      if (fc_out_with_bias(i) >= max) {
        Digit = i
        max = fc_out_with_bias(i)
      }
    }
    println(s"Digit ${Digit}")
  }
}
