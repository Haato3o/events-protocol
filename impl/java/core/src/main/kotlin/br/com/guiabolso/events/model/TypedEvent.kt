package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.TreeNode

sealed class TypedEvent<T> {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: T
    abstract val identity: TreeNode
    abstract val auth: TreeNode
    abstract val metadata: TreeNode


}

data class TypedRequestEvent<T>(
    override val name: String,
    override val version: Int,
    override val id: String,
    override val flowId: String,
    override val payload: T,
    override val identity: TreeNode,
    override val auth: TreeNode,
    override val metadata: TreeNode
) : TypedEvent<T>()

fun <T> RequestEvent.toTypedEvent(klass: Class<T>): TypedRequestEvent<T> =
    TypedRequestEvent(
        name = name,
        version = version,
        id = id,
        flowId = flowId,
        payload = payloadAs(klass),
        identity = identity,
        auth = auth,
        metadata = metadata
    )

data class TypedResponseEvent<T>(
    override val name: String,
    override val version: Int,
    override val id: String,
    override val flowId: String,
    override val payload: T,
    override val identity: TreeNode,
    override val auth: TreeNode,
    override val metadata: TreeNode
) : TypedEvent<T>() {

    fun isSuccess() = name.endsWith(":response")

    fun isRedirect() = name.endsWith(":redirect")

    fun isError() = !isSuccess() && !isRedirect()

    fun getErrorType(): EventErrorType =
        when(isSuccess()) {
            true -> throw IllegalStateException("This is not an error event.")
            else -> EventErrorType.getErrorType(name.substringAfterLast(":"))
        }

    fun toResponse(): ResponseEvent =
            ResponseEvent(
                name = name,
                version = version,
                id = id,
                flowId = flowId,
                payload = mapper.toJsonTree(payload),
                identity = identity,
                auth = auth,
                metadata = metadata
            )
}