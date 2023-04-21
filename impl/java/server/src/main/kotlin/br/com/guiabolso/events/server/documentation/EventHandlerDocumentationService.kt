package br.com.guiabolso.events.server.documentation

class EventHandlerDocumentationService(
    private val registry: EventHandlerMetadataRegistry
) {

    fun generate(): String = buildString {
        registry.getAll().forEach {
            append(it.name)
            append(":")
            append(it.version)
            append("\nRequest: ")
            append(it.requestType.name)
            append("\nResponse: ")
            append(it.responseType.name)
            append("\n\n")
        }
    }
}