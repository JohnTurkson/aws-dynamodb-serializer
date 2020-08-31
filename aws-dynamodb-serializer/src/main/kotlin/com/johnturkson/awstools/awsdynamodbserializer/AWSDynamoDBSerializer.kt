package com.johnturkson.awstools.awsdynamodbserializer

import kotlinx.serialization.json.*

object AWSDynamoDBSerializer {
    private const val NULL = "NULL"
    private const val STRING = "S"
    private const val BOOLEAN = "BOOL"
    private const val NUMBER = "N"
    private const val LIST = "L"
    
    fun encodeToString(configuration: Json = Json, data: String): String {
        val encoded = configuration.parseToJsonElement(data).encoded()
        return configuration.encodeToString(JsonElement.serializer(), encoded)
    }
    
    fun decodeFromString(configuration: Json = Json, data: String): String {
        val decoded = configuration.parseToJsonElement(data).decoded()
        return configuration.encodeToString(JsonElement.serializer(), decoded)
    }
    
    private fun JsonElement.encoded(): JsonElement {
        return when (this) {
            is JsonObject -> this.encoded()
            is JsonArray -> this.encoded()
            is JsonPrimitive -> this.encoded()
        }
    }
    
    private fun JsonObject.encoded(): JsonObject {
        return JsonObject(this.mapValues { (_, value) -> value.encoded() })
    }
    
    private fun JsonArray.encoded(): JsonObject {
        return JsonObject(mapOf(LIST to JsonArray(this.map { element -> element.encoded() })))
    }
    
    private fun JsonPrimitive.encoded(): JsonObject {
        return if (this.isString) {
            JsonObject(mapOf(STRING to JsonPrimitive(content)))
        } else {
            this.booleanOrNull?.let { boolean -> JsonObject(mapOf(BOOLEAN to JsonPrimitive(boolean))) }
                ?: this.intOrNull?.let { int -> JsonObject(mapOf(NUMBER to JsonPrimitive(int.toString()))) }
                ?: this.longOrNull?.let { long -> JsonObject(mapOf(NUMBER to JsonPrimitive(long.toString()))) }
                ?: this.floatOrNull?.let { float -> JsonObject(mapOf(NUMBER to JsonPrimitive(float.toString()))) }
                ?: this.doubleOrNull?.let { double -> JsonObject(mapOf(NUMBER to JsonPrimitive(double.toString()))) }
                ?: JsonObject(mapOf(NULL to JsonPrimitive(true)))
        }
    }
    
    private fun JsonElement.decoded(): JsonElement {
        return when (this) {
            is JsonObject -> this.decoded()
            is JsonArray -> throw Exception("Unsupported top level document type")
            is JsonPrimitive -> throw Exception("Unsupported top level scalar type")
        }
    }
    
    private fun JsonObject.decoded(): JsonElement {
        return when (this.entries.singleOrNull()) {
            null -> JsonObject(this.mapValues { (_, value) -> value.decoded() })
            else -> {
                val (key, value) = this.entries.single()
                when (value) {
                    is JsonPrimitive -> value.decoded(key)
                    is JsonArray -> value.decoded()
                    else -> JsonObject(mapOf(key to value.decoded()))
                }
            }
        }
    }
    
    private fun JsonArray.decoded(): JsonArray {
        return JsonArray(this.map { element -> element.decoded() })
    }
    
    private fun JsonPrimitive.decoded(key: String): JsonPrimitive {
        return when (key) {
            NULL -> JsonNull
            STRING -> JsonPrimitive(this.content)
            BOOLEAN -> this.booleanOrNull?.let { boolean -> JsonPrimitive(boolean) }
                ?: throw Exception("Unable to parse boolean type")
            NUMBER -> this.intOrNull?.let { int -> JsonPrimitive(int) }
                ?: this.longOrNull?.let { long -> JsonPrimitive(long) }
                ?: this.floatOrNull?.let { float -> JsonPrimitive(float) }
                ?: this.doubleOrNull?.let { double -> JsonPrimitive(double) }
                ?: throw Exception("Unable to parse number type")
            else -> throw Exception("Unsupported data type")
        }
    }
}
