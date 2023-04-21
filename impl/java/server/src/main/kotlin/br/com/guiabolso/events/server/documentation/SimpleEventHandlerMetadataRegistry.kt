package br.com.guiabolso.events.server.documentation

import br.com.guiabolso.events.server.documentation.model.EventHandlerMetadata
import br.com.guiabolso.events.server.handler.TypedEventHandler

class SimpleEventHandlerMetadataRegistry : EventHandlerMetadataRegistry {

    private val registry = mutableListOf<EventHandlerMetadata<*, *>>()

    override fun <TIn, TOut> add(handler: TypedEventHandler<TIn, TOut>) {
        handler.buildMetadata()
            .let { registry.add(it) }
    }

    private fun <TIn, TOut> TypedEventHandler<TIn, TOut>.buildMetadata(): EventHandlerMetadata<TIn, TOut> {
        return EventHandlerMetadata(
            name = eventName,
            version = eventVersion,
            requestType = typeIn,
            responseType = typeOut
        )
    }
}