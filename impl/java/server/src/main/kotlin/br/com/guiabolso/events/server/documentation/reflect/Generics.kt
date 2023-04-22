package br.com.guiabolso.events.server.documentation.reflect

import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
fun <T : Any, TOut> T.underlyingType(index: Int): Class<TOut> {
    return with((javaClass.genericSuperclass as ParameterizedType)) {
        actualTypeArguments[index] as Class<TOut>
    }
}