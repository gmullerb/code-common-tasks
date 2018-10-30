//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.api.tasks.javadoc.Javadoc

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock

@CompileStatic
final class DocTasksDetailsTest {
  @Test
  void shouldCreateDocTasksDetails() {
    final TaskContainer mockContainer = mock(TaskContainer)
    final Javadoc mockJavadocTask = mock(Javadoc)
    doReturn(mockJavadocTask)
      .when(mockContainer)
      .findByPath(eq('javadoc'))
    doReturn(new File('javadocDir'))
      .when(mockJavadocTask)
      .getDestinationDir()
    final Groovydoc mockGroovydocTask = mock(Groovydoc)
    doReturn(mockGroovydocTask)
      .when(mockContainer)
      .findByPath(eq('groovydoc'))
    doReturn(new File('groovydocDir'))
      .when(mockGroovydocTask)
      .getDestinationDir()

    final DocTasksDetails result = DocTasksDetails.of(mockContainer, ['javadoc', 'groovydoc'])

    assertEquals('javadoc/groovydoc', result.types)
    assertEquals('javadocDir groovydocDir', result.locations)
  }

  @Test
  void shouldCreateEmptyDocTasksDetailsWhenAtIsEmptyOrNull() {
    final TaskContainer mockContainer = mock(TaskContainer)
    final Javadoc mockJavadocTask = mock(Javadoc)
    doReturn(mockJavadocTask)
      .when(mockContainer)
      .findByPath(eq('javadoc'))
    doReturn(new File(''))
      .when(mockJavadocTask)
      .getDestinationDir()
    final Groovydoc mockGroovydocTask = mock(Groovydoc)
    doReturn(mockGroovydocTask)
      .when(mockContainer)
      .findByPath(eq('groovydoc'))
    doReturn(new File(''))
      .when(mockGroovydocTask)
      .getDestinationDir()

    final DocTasksDetails result = DocTasksDetails.of(mockContainer, ['javadoc', 'groovydoc'])

    assertEquals('javadoc/groovydoc', result.types)
    assertEquals('', result.locations)
  }

  @Test
  void shouldCreateEmptyDocTasksDetailsWhenNotFound() {
    final DocTasksDetails result = DocTasksDetails.of(mock(TaskContainer), ['javadoc', 'groovydoc'])

    assertEquals('', result.types)
    assertEquals('', result.locations)
  }

  @Test
  void shouldCreateEmptyDocTasksDetailsWhenNotDefined() {
    final TaskContainer mockContainer = mock(TaskContainer)
    final Task mockTask = mock(Task)
    doReturn(mockTask)
      .when(mockContainer)
      .findByPath(eq('testdocmocktask'))

    final DocTasksDetails result = DocTasksDetails.of(mockContainer, ['testdocmocktask'])

    assertEquals('', result.types)
    assertEquals('', result.locations)
  }

  @Test
  void shouldCreateEmptyDocTasksDetailsWhenNull() {
    final DocTasksDetails result = DocTasksDetails.of(mock(TaskContainer), null)

    assertEquals('', result.types)
    assertEquals('', result.locations)
  }
}
