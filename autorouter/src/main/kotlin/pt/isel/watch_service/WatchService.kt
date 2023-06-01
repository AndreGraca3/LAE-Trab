package pt.isel.watch_service

import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import kotlin.io.path.readLines


fun Path.watchNewFilesContent(): Sequence<Sequence<String>> {
    return sequence {
        this@watchNewFilesContent.fileSystem.newWatchService().use { service ->
            this@watchNewFilesContent.register(service, ENTRY_CREATE, ENTRY_MODIFY)

            // Start the infinite polling loop
            while (true) {
                // What is this key for?
                val key = service.take()
                // Dequeueing events
                for (watchEvent in key.pollEvents()) {
                    // Get the type of the event
                    when (watchEvent.kind()) {
                        OVERFLOW -> continue  // loop
                        ENTRY_CREATE, ENTRY_MODIFY -> {
                            val fileName = watchEvent.context().toString()
                            val filePath = this@watchNewFilesContent.resolve(fileName)
                            val lines = filePath.readLines()

                           yield(lines.asSequence())
                        }
                    }
                }
                if (!key.reset()) {
                    break // loop
                }
            }
        }
    }
}