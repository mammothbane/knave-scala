package com.avaglir.knave.map

import com.avaglir.knave.util._

case class Map(tiles: Array[Array[Tile]]) {
  /**
    * Calculate vision at the given location for a camera with the given (circular) field of view radius.
    * @param loc The camera's location.
    * @param radius The vision radius.
    * @return A list of locations that can be seen from the specified location with the specified field of view.
    */
  def vision(loc: Vector2, radius: Int): List[Vector2] = ShadowRaycast.calculate(loc, radius, { this(_).transparent })

  def apply(x: Int)(y: Int): Tile = tiles(x)(y)
  def apply(loc: Vector2): Tile = tiles(loc.x)(loc.y)

  def height: Int = this.tiles.length
  def width: Int = this.tiles.head.length

  def pathableNear(loc: Vector2, radius: Int): List[Vector2] = circle_simple(loc, radius).filter(this(_).pathable)

  def log(): Unit = {
    val out = (0 to height).flatMap { y =>
      (0 to width).map { x =>
        apply(x)(y).char
      }
    }

    println(out)
  }
}
