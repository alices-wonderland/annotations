package com.ukonnra.wonderland.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AggregateRoot(val service: String)
