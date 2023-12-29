package com.example.rescyouadmin

data class PinDataClass(
    var key: String? = null,
    var attachmentList: ArrayList<String> = ArrayList(),
    var date: String? = null,
    var description: String? = null,
    var disasterType: String? = null,
    var pinName: String? = null,
    var pinRescuer: String? = null,
    var pinRescuerID: String? = null,
    var rate: String? = null,
    var resolved: String="false",
    var sitio: String? = null,
    var time: String? = null
) {
}