package com.lambdaschool.cs_build_week_2.models

class MoveWisely(val direction: String, val next_room_id: String?)

class Treasure(val name: String, val confirm: String? = null)

class Dash(val direction: String, val num_rooms: String, val next_room_ids: String)