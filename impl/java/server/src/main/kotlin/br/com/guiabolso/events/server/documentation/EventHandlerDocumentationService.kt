package br.com.guiabolso.events.server.documentation

class EventHandlerDocumentationService(
    private val registry: EventHandlerMetadataRegistry,
    private val emitter: EventHandlerDocumentationEmitter
) {

    fun generate(): String = buildString {
        registry.getAll().forEach {
            append(emitter.emit(it))
            append("\n----------------------------------\n")
        }
    }
}