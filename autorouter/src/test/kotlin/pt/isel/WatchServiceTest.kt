package pt.isel

import org.junit.jupiter.api.Test
import pt.isel.autorouter.watchNewFilesContent
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import kotlin.test.assertEquals

class WatchServiceTest {

    private val usersDir = System.getProperty("user.dir")
    private val path = Paths.get(usersDir, "watched")

    @BeforeTest
    fun setup() {
        // Create the directory if it doesn't exist
        val directory = File(path.toString())
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    @Test
    fun `test create and modify file`() {
        // Create the directory if it doesn't exist
        val directory = File(path.toString())

        val file = File(directory, "myFile.txt")

        val filePath = file.toPath()

        // Watch for new files and modifications
        val sequences = path.watchNewFilesContent()

        // create the file with no content
        Files.write(filePath, "".toByteArray())

        // modify the file 1st time
        thread {
            Thread.sleep(1000)
            val content = "This is the content of my file."
            Files.write(filePath, content.toByteArray())
        }

        // Assert that at least one sequence of lines is received
        val firstSequence = sequences.take(1)
        assertEquals(1, firstSequence.count())

        // Modify the file 2nd time
        thread {
            Thread.sleep(1000)
            val modifiedContent = "Modified content"
            Files.write(filePath, modifiedContent.toByteArray())
        }

        // Assert that another sequence of lines is received
        val secondSequence = sequences.take(2)
        assertEquals(2, secondSequence.count())
    }

    @AfterTest
    fun teardown() {
        // Delete the directory
        val directory = File(path.toString())
        if (directory.exists()) {
            directory.deleteRecursively()
        }
    }

}
