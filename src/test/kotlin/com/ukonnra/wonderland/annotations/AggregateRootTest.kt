package com.ukonnra.wonderland.annotations

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AggregateRootTest {
  companion object {
    private val PROCESSOR = AggregateRootProcessor()
  }

  @Test
  fun testCodeGen() {
    val kotlinSource = SourceFile.kotlin(
      "User.kt",
      """
        package com.ukonnra.wonderland.annotations

        @AggregateRoot("Annotations")
        data class User(
          val id: String,
          val age: Int,
        )
    """
    )

    val result = KotlinCompilation().apply {
      sources = listOf(kotlinSource)
      annotationProcessors = listOf(PROCESSOR)
      inheritClassPath = true
      messageOutputStream = System.out
    }
      .compile()

    Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
  }
}
