package com.example.rescyouadmin

data class HotlinesDataClass (
    var dataName: String? = null,
    var dataPhone: String? = null,
    var key: String? = null
)

data class HotlinesDataModel (
    var dataName: String? = null,
    var dataPhone: String? = null
)
{

    fun replaceNewlines() {
        dataName = dataName?.replace("\\n", "\n")
        dataPhone = dataPhone?.replace("\\'", "\'")
    }

    fun toHotlines(): HotlinesDataModel {
        // Ensure that both dataName and dataPhone are not null before creating a com.example.rescyouadmin.HotlinesDataModel object
        requireNotNull(dataName) { "dataName must not be null" }
        requireNotNull(dataPhone) { "dataPhone must not be null" }

        return HotlinesDataModel(dataName = this.dataName!!, dataPhone = this.dataPhone!!)
    }
}