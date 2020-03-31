package com.lambdaschool.cs_build_week_2.models

import android.graphics.Color
import okhttp3.internal.toHexString

class CellDetails(val gridX: Int = 0, val gridY: Int = 0, val color: String = Color.RED.toHexString())