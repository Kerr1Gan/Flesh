package com.ecjtu.flesh.model.models

import java.io.Serializable

data class UpdateBean(var versionCode: Int = 0, var versionName: String = "", var packageName: String = "", var url: String = "") : Serializable