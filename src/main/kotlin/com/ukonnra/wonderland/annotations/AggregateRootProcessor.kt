package com.ukonnra.wonderland.annotations

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedOptions(AggregateRootProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class AggregateRootProcessor : AbstractProcessor() {
  companion object {
    internal const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
  }

  private val template: Template

  init {
    val loader: TemplateLoader = ClassPathTemplateLoader()
    loader.prefix = "/templates"
    loader.suffix = ".hbs"
    val handlebars = Handlebars(loader)
    this.template = handlebars.compile("aggregate-root")
  }

  override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(AggregateRoot::class.java.name)

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

  override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
    val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
      processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
      return false
    }

    roundEnv.getElementsAnnotatedWith(AggregateRoot::class.java).forEach {
      val className = it.simpleName.toString()
      val pack = processingEnv.elementUtils.getPackageOf(it).toString()
      val code = this.template.apply(mapOf("package" to pack, "className" to className))
      File(kaptKotlinGeneratedDir, "testGenerated.kt").apply {
        parentFile.mkdirs()

        writeText(code)
      }
    }
    return true
  }
}
