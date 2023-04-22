package br.com.guiabolso.events.server.documentation

import br.com.guiabolso.events.server.documentation.model.EventHandlerMetadata
import br.com.guiabolso.events.server.documentation.reflect.Deserializer.deserialize

class EventHandlerDocumentationEmitter {

    fun emit(metadata: EventHandlerMetadata<*, *>): String = buildString {
        append("Event name: ")
        append(metadata.name)
        append("\nEvent version: ")
        append(metadata.version)
        append("\nRequest:\n")
        append(deserialize(metadata.requestType))
        append("\n\nResponse:\n")
        append(deserialize(metadata.responseType))
        append("\n")
    }
}