package com.avaglir.knave.map

import com.avaglir.knave.util._
import simplex.SimplexNoise

object GenMap extends Persist {
  var large = SimplexNoise()
  var small = SimplexNoise()

  private val smallScale = 13.1
  private val largeScale = 30

  private val smallWeight = 0.33
  private val largeWeight = 0.6

  private val totalScale = 500
  private val ctrMag = math.sqrt(totalScale/2 * totalScale/2 + totalScale/2 * totalScale/2)

  private val smallFalloff = 0.96
  private val largeFalloff = 0.04
  private val smallRadius = smallFalloff * ctrMag
  private val largeRadius = largeFalloff * ctrMag

  val DIMENS = 4096

  def apply(x: IntVec, scale: Int = DIMENS): Float = apply(x.x, x.y, scale)
  def apply(x: Int, y: Int, scale: Int): Float = apply(x.toFloat/scale, y.toFloat/scale)

  def apply(x: Float, y: Float): Float = {
    val dx = (x - 0.5) * totalScale
    val dy = (y - 0.5) * totalScale
    val ctrDist = math.sqrt(dx * dx + dy * dy)

    val sm = if (ctrDist > smallRadius) 1 - (ctrDist - smallRadius) / (ctrMag - smallRadius) else 1
    val lg = if (ctrDist > largeRadius) 1 - (ctrDist - largeRadius) / (ctrMag - largeRadius) else 1

    val smVal = small.eval(dx / smallScale, dy / smallScale) * smallWeight * (sm * sm)
    val lgVal = large.eval(dx / largeScale, dy / largeScale) * largeWeight * (lg * lg)

    (lgVal + smVal).toFloat.clamp
  }

  /**
    * Builds a square map of the specified side length.
    * @param res The side length of the map.
    * @return A square 2d array of the specified dimensions, filled with the map
    *         generated by GenMap.
    */
  def emit(res: Int): Array[Array[Float]] = {
    val out = Array.ofDim[Float](res, res)

    for (i <- 0 until res; j <- 0 until res) {
      out(i)(j) = apply(i.toFloat / res, j.toFloat / res)
    }

    out
  }

  def locs(res: Int): Map[IntVec, Float] = {
    Range(0, res).flatMap { x =>
      Range(0, res).map { y =>
        (Vector2(x, y), this(x.toFloat/res, y.toFloat/res))
      }
    }.toMap
  }

  import com.avaglir.knave.util.storage.Pickling._
  import prickle._
  override def persist(): Map[Symbol, String] = Map(
    'simplex_large -> Pickle.intoString(large),
    'simplex_small -> Pickle.intoString(small)
  )

  override def restore(v: Map[Symbol, String]): Unit = {
    large = Unpickle[SimplexNoise].fromString(v('simplex_large)).get
    small = Unpickle[SimplexNoise].fromString(v('simplex_small)).get
  }
  override def key: Symbol = 'mapgen
}
