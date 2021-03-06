package com.avaglir.knave.map

import com.avaglir.knave.entities.GameObject
import com.avaglir.knave.util.{Color, RenderTile}

case class Tile(
    name: String,
    repr: RenderTile,
    pathable: Boolean = false,
    opaque: Boolean = false,
    debug: Boolean = false,
  ) extends GameObject {

  def transparent: Boolean = !opaque

  override def toString: String = s"Tile('${repr.char}', pathable = $pathable, opaque = $opaque)"
}

object Tile {
  val WALL: Tile = Tile("wall", RenderTile('#'), opaque = true)

  val FLOOR: Tile =
    Tile("floor", RenderTile('.', fg = Color.BROWN, bg = Color.BROWN.darker), pathable = true)

  val WATER: Tile =
    Tile("Water", RenderTile('~', fg = Color.WHITE.darker, bg = Color.BLUE.desaturated))
}
