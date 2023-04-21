package br.com.guiabolso.events.server.documentation.model

data class EventHandlerMetadata<TIn, TOut>(
    val name: String,
    val version: Int,
    val requestType: Class<TIn>,
    val responseType: Class<TOut>
)
