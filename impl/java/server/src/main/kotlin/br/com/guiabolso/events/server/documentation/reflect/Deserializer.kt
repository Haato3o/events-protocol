package br.com.guiabolso.events.server.documentation.reflect

object Deserializer {

    fun deserialize(clazz: Class<*>): String = buildString {
        append("{\n")
        clazz.declaredFields.forEach {
            append("    \"")
            append(it.name)
            append("\": ")
            if (it.type.isPrimitive) {
                append(deserializePrimitive(it.type))
            } else {
                append(it.type.name)
            }
            append(",\n")
        }
        append("}")
    }

    private fun deserializePrimitive(clazz: Class<*>): String {
        return when (clazz.name) {
            String::class.java.name -> "string"
            Double::class.java.name -> "double"
            Float::class.java.name -> "float"
            Long::class.java.name -> "long"
            Int::class.java.name -> "int"
            else -> "unknown"
        }
    }
}