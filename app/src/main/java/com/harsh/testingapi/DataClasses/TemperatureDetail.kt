package com.harsh.testingapi.DataClasses

data class TemperatureDetail(     val Value: Double,
                                  val Unit: String,
                                  val UnitType: Int,
                                  val Phrase: String? = null)
