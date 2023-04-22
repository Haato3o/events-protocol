package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.*
import br.com.guiabolso.events.server.documentation.reflect.underlyingType

abstract class TypedEventHandler<TIn, TOut> : EventHandler {

    val typeIn: Class<TIn> = underlyingType(0)
    val typeOut: Class<TOut> = underlyingType(1)

    final override suspend fun handle(event: RequestEvent): ResponseEvent {
        return with(event.toTypedEvent(typeIn)) {
            handle(this).toResponse()
        }
    }

    abstract suspend fun handle(event: TypedRequestEvent<TIn>): TypedResponseEvent<TOut>
}