package com.example.rescyouadmin

data class EvacuationCenterData(
    var name: String? = null,
    var address: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var status: String? = "Available",
    var inCharge: String? = null,
    var inChargeContactNum: String? = null,
    var occupants: String? = null,
) {


}