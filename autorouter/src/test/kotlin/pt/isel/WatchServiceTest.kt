package pt.isel

import org.junit.jupiter.api.Test
import pt.isel.autorouter.watchNewFilesContent
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.CountDownLatch
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
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

//    @AfterTest
//    fun teardown() {
//        // Delete the directory
//        val directory = File(path.toString())
//        if (directory.exists()) {
//            directory.deleteRecursively()
//        }
//    }

    @Test
    fun `test create and modify file`() {
        println(path.toString())

        // Create the directory if it doesn't exist
        val directory = File(path.toString())

        val file = File(directory, "myFile.txt")

        val filePath = file.toPath()

        val content = "This is the content of my file."
        Files.write(filePath, content.toByteArray())

        // Watch for new files and modifications
        val sequences = path.watchNewFilesContent()

        // Assert that at least one sequence of lines is received
        val firstSequence = sequences.take(1).toList()
        assertEquals(1, firstSequence.size)

        // Modify the file
//        val modifiedContent = "Modified content"
//        Files.write(filePath, modifiedContent.toByteArray())
//
//        // Assert that another sequence of lines is received
//        val secondSequence = sequences.take(1).toList()
//        assertEquals(1, secondSequence.size)

        // Delete the file
        file.delete()
    }
}
