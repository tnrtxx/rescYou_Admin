package com.example.rescyouadmin

import java.io.Serializable

data class EvacuationCenterData(
    var evacuationCenterId: String? = null,
    var placeId: String? = null,
    var name: String? = null,
    var address: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var status: String? = null,
    var inCharge: String? = null,
    var inChargeContactNum: String? = null,
    var occupants: String? = null,
): Serializable