package pt.isel

import pt.isel.autorouter.watchNewFilesContent
import java.io.File
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class WatchServiceTest {

    private val folder = "../watched"
    private val directory = File(folder)
    private val file = File("$folder/test.txt")

    @BeforeTest
    fun setup() {
        if (!directory.exists()) directory.mkdirs()
        if (file.exists()) file.delete()
        file.writeText("")
    }

    @AfterTest
    fun deleteTestFile() {
        if (file.exists()) file.delete()
    }

    @Test
    fun `test create and modify file`() {
        val path = directory.toPath()

        // Watch for new files and modifications
        val sequences = path.watchNewFilesContent()

        repeat(2) {
            thread {
                Thread.sleep(1000 + (it * 1000).toLong())
                val content = "Modifying\n this file\n for ${it + 1} time."
                file.writeText(content)
            }
        }

        // Assert that we get 2 fileSequences inside sequences
        val firstSeq: Sequence<String> = sequences.take(1).first()
        assertEquals(3, firstSeq.count())
        val secSeq: Sequence<String> = sequences.drop(1).first()
        assertEquals(3, secSeq.count())
    }
}
