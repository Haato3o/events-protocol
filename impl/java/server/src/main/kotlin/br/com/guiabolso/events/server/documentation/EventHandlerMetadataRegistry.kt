package br.com.guiabolso.events.server.documentation

import br.com.guiabolso.events.server.documentation.model.EventHandlerMetadata
import br.com.guiabolso.events.server.handler.TypedEventHandler

interface EventHandlerMetadataRegistry {
    fun <TIn, TOut> add(handler: TypedEventHandler<TIn, TOut>)

    fun getAll(): List<EventHandlerMetadata<*, *>>
}