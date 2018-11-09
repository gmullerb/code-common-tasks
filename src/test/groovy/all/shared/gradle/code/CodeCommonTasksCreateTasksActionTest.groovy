//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.Project

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertNotNull

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions

@CompileStatic
final class CodeCommonTasksCreateTasksActionTest {

  @Test
  void shouldCreateTasks() {
    final CodeCommonTasksExtension extension = new CodeCommonTasksExtension()
    final CodeCommonTasksCreateTasksAction action = new CodeCommonTasksCreateTasksAction(extension)
    final Project spyProject = SpyProjectFactory.build()
    extension.tasksForMainAssess.each { spyProject.tasks.create(it) }
    extension.tasksForTestAssess.each { spyProject.tasks.create(it) }
    spyProject.tasks.create(extension.unitTestTask)
    spyProject.tasks.create(extension.assembleTask)
    extension.tasksForDocumentation.each { spyProject.tasks.create(it) }

    action.execute(spyProject)

    assertNotNull(spyProject.tasks.getByPath(extension.assessTask))
    assertNotNull(spyProject.tasks.getByPath(extension.coverageTask))
    assertNotNull(spyProject.tasks.getByPath(extension.checkTask))
    assertNotNull(spyProject.tasks.getByPath(extension.buildTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.assessMainTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.assessTestTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.assessTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.testTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.coverageTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.checkTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.buildTask))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(extension.documentationTask))
  }

  @Test
  void shouldNotCreateTasksWhenNoBindings() {
    final CodeCommonTasksExtension extension = new CodeCommonTasksExtension()
    final CodeCommonTasksCreateTasksAction action = new CodeCommonTasksCreateTasksAction(extension)
    final Project spyProject = SpyProjectFactory.build()

    action.execute(spyProject)

    verify(spyProject.logger)
      .error(eq('{} task is not defined, and can not be created since not even one of dependencies {} are not found'),
        eq(extension.assessTask), any(Iterable))
    verify(spyProject.logger)
      .error(eq('{} task is not defined, and can not be created since not even one of dependencies {} are not found'),
        eq(extension.coverageTask), any(Iterable))
    verify(spyProject.logger)
      .error(eq('{} task is not defined, and can not be created since not even one of dependencies {} are not found'),
        eq(extension.checkTask), any(Iterable))
    verifyNoMoreInteractions(spyProject.logger)
  }

  @Test
  void shouldNotCreateTasksWhenAlreadyDefined() {
    final CodeCommonTasksExtension extension = new CodeCommonTasksExtension()
    final CodeCommonTasksCreateTasksAction action = new CodeCommonTasksCreateTasksAction(extension)
    final Project spyProject = SpyProjectFactory.build()
    spyProject.tasks.create(extension.assessMainTask)
    spyProject.tasks.create(extension.assessTestTask)
    spyProject.tasks.create(extension.assessTask)
    spyProject.tasks.create(extension.testTask)
    spyProject.tasks.create(extension.coverageTask)
    spyProject.tasks.create(extension.checkTask)
    spyProject.tasks.create(extension.buildTask)
    spyProject.tasks.create(extension.documentationTask)

    action.execute(spyProject)

    verify(spyProject.logger, never())
      .debug(anyString(), anyString())
  }
}
