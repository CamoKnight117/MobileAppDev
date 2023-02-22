package com.lifestyle.map

import kotlinx.serialization.Serializable

@Serializable
class TextLocation {
    var city:String? = null
    var state:String? = null
    var country:String? = null
    var streetAddress:String? = null
    var zipCode:String? = null
}