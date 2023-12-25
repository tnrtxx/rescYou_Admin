package com.example.rescyouadmin

import java.io.Serializable

data class EvacuationCenterData(
    var evacuationCenterId: String? = null,     // Primary key
    var placeId: String? = null,                // Foreign key to Google Places
    var name: String? = null,
    var address: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var status: String? = null,
    var inCharge: String? = null,
    var inChargeContactNum: String? = null,
    var occupants: String? = null,
) : Serializable {
    override fun toString(): String {
        return "EvacuationCenterData(evacuationCenterId=$evacuationCenterId, " +
                "placeId=$placeId, name=$name, address=$address, " +
                "latitude=$latitude, longitude=$longitude, status=$status, " +
                "inCharge=$inCharge, inChargeContactNum=$inChargeContactNum, occupants=$occupants)"
    }

}