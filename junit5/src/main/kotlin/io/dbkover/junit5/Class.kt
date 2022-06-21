package io.dbkover.junit5

import org.junit.platform.commons.util.*

internal fun <T, R : Annotation> Class<T>.findMethodsWithAnnotation(annotation: Class<R>) =
    AnnotationUtils.findAnnotatedMethods(this, annotation, ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP)