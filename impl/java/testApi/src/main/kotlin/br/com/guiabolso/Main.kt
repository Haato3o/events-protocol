package br.com.guiabolso

import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.TypedRequestEvent
import br.com.guiabolso.events.model.TypedResponseEvent
import br.com.guiabolso.events.server.SuspendingEventProcessor
import br.com.guiabolso.events.server.documentation.EventHandlerDocumentationEmitter
import br.com.guiabolso.events.server.documentation.EventHandlerDocumentationService
import br.com.guiabolso.events.server.documentation.SimpleEventHandlerMetadataRegistry
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistryFactory.exceptionHandler
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import br.com.guiabolso.events.server.handler.TypedEventHandler
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

data class RequestPayload(
    val name: String
)

data class ResponsePayload(
    val nameLength: Int
)

data class ComplexPayload(
    val strField: String,
    val intField: Int,
    val longField: Long,
    val listField: List<String>
)

class TestEventHandler : TypedEventHandler<RequestPayload, ResponsePayload>() {
    override val eventName: String = "test:event"

    override val eventVersion: Int = 1

    override suspend fun handle(event: TypedRequestEvent<RequestPayload>): TypedResponseEvent<ResponsePayload> {
        return TypedResponseEvent(
            name = "$eventName:response",
            version = eventVersion,
            id = event.id,
            flowId = event.flowId,
            payload = ResponsePayload(nameLength = event.payload.name.length),
            identity = TreeNode(),
            auth = TreeNode(),
            metadata = TreeNode()
        )
    }
}

class AnotherTestEventHandler : TypedEventHandler<ComplexPayload, Unit>() {
    override val eventName: String = "another:test:event"
    override val eventVersion: Int = 1

    override suspend fun handle(event: TypedRequestEvent<ComplexPayload>): TypedResponseEvent<Unit> =
        TypedResponseEvent(
            name = "$eventName:response",
            version = eventVersion,
            id = event.id,
            flowId = event.flowId,
            payload = Unit,
            identity = TreeNode(),
            auth = TreeNode(),
            metadata = TreeNode()
        )
}

fun buildDiscovery(handlers: List<EventHandler>) = SimpleEventHandlerRegistry().apply {
    handlers.forEach(::add)
}

fun buildMetadataRegistry(handlers: List<EventHandler>) = SimpleEventHandlerMetadataRegistry().apply {
    handlers.filterIsInstance<TypedEventHandler<*, *>>().forEach { add(it) }
}

fun main2() {
    val clazz = RequestPayload::class.java

}

fun main(): Unit = runBlocking {
    val applicationEnvironment = commandLineEnvironment(arrayOf("-port=8080"))
    val engine = NettyApplicationEngine(applicationEnvironment) { }

    val eventHandlers = listOf(
        TestEventHandler(),
        AnotherTestEventHandler()
    )

    val handlerRegistry = buildDiscovery(eventHandlers)
    val metadataRegistry = buildMetadataRegistry(eventHandlers)
    val suspendingEventProcessor = SuspendingEventProcessor(
        handlerRegistry,
        exceptionHandler()
    )
    val documentationService = EventHandlerDocumentationService(
        metadataRegistry,
        EventHandlerDocumentationEmitter()
    )

    engine.application.routing {
        post("/events") {
            call.respondText(
                text = suspendingEventProcessor.processEvent(call.receiveText()),
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.OK
            )
        }

        get("/docs") {
            call.respondText(
                text = documentationService.generate(),
                contentType = ContentType.Text.Plain,
                status = HttpStatusCode.OK
            )
        }
    }

    engine.start(true)
}

private fun NettyApplicationEngine.Configuration.loadConfiguration(config: ApplicationConfig) {
    val deploymentConfig = config.config("ktor.deployment")
    loadCommonConfiguration(deploymentConfig)

    deploymentConfig.booleanProperty("shareWorkGroup")?.let { shareWorkGroup = it }
    deploymentConfig.intProperty("requestQueueLimit")?.let { requestQueueLimit = it }
    deploymentConfig.intProperty("responseWriteTimeoutSeconds")?.let { responseWriteTimeoutSeconds = it }
    deploymentConfig.intProperty("callGroupSize")?.let { callGroupSize = it }
    deploymentConfig.intProperty("workerGroupSize")?.let { workerGroupSize = it }
    deploymentConfig.intProperty("runningLimit")?.let { runningLimit = it }
}

private fun ApplicationConfig.intProperty(name: String) = propertyOrNull(name)?.getString()?.toInt()

private fun ApplicationConfig.booleanProperty(name: String) = propertyOrNull(name)?.getString()?.toBoolean()