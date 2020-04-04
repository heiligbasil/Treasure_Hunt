package com.lambdaschool.cs_build_week_2.models

class MoveWisely(val direction: String, val next_room_id: String?)

class Treasure(val name: String, val confirm: String? = null)

class Dash(var direction: String, var num_rooms: String, var next_room_ids: String)

class Mine(val proof: Int)