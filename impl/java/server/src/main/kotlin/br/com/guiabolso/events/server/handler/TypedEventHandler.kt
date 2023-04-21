package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.model.TypedRequestEvent
import br.com.guiabolso.events.model.TypedResponseEvent
import br.com.guiabolso.events.model.toTypedEvent


abstract class TypedEventHandler<TIn, TOut> : EventHandler {

    abstract val typeIn: Class<TIn>
    abstract val typeOut: Class<TOut>

    final override suspend fun handle(event: RequestEvent): ResponseEvent {
        return with(event.toTypedEvent(typeIn)) {
            handle(this).toResponse()
        }
    }

    abstract suspend fun handle(event: TypedRequestEvent<TIn>): TypedResponseEvent<TOut>
}