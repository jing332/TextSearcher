package com.github.jing332.text_searcher.model.source

import android.net.Uri
import android.os.Parcelable
import androidx.compose.ui.text.font.FontWeight
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Parcelize
@Serializable
data class Appearance(
    @Serializable(UriSerializer::class)
    val fontUri: Uri = Uri.EMPTY,

    val fontWeight: Int = FontWeight.Normal.weight,
    val fontSize: Int,
    val lineWidthScale: Float,

    // Unit: DP
    val horizontalMargin: Float = 2f,
    val verticalMargin: Float = 2f,
) : Parcelable

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Uri::class)
object UriSerializer : KSerializer<Uri> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }
}