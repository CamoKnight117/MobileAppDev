package com.lifestyle.main

import android.location.Location
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A serializable wrapper around the [android.location.Location] class.
 */
@Serializable(with = SerializableLocation.Companion.LocationSerializer::class)
@SerialName("Location")
class SerializableLocation(val location: Location) {
    @Serializable
    @SerialName("Location")
    private class LocationSurrogate(val latitude: Double, val longitude: Double) { }

    companion object {
        object LocationSerializer : KSerializer<SerializableLocation> {
            override val descriptor: SerialDescriptor = LocationSurrogate.serializer().descriptor

            override fun serialize(encoder: Encoder, value: SerializableLocation) {
                val surrogate = LocationSurrogate(value.location.latitude, value.location.longitude)
                encoder.encodeSerializableValue(LocationSurrogate.serializer(), surrogate)
            }

            override fun deserialize(decoder: Decoder): SerializableLocation {
                val surrogate = decoder.decodeSerializableValue(LocationSurrogate.serializer())
                val location = Location("") // Provider name is not needed.
                location.latitude = surrogate.latitude
                location.longitude = surrogate.longitude
                return SerializableLocation(location)
            }
        }
    }
}