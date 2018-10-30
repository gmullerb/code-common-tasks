//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.Project
import org.gradle.api.Task

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

import static org.mockito.Mockito.spy
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions

@CompileStatic
final class CodeCommonTasksComplementActionTest {
  private final Project spyProject = SpyProjectFactory.build()

  @Test
  void shouldComplementTask() {
    final CodeCommonTasksExtension extension = new CodeCommonTasksExtension()
    final CodeCommonTasksComplementAction action = new CodeCommonTasksComplementAction(extension)
    final Task task = spyProject.tasks.create(extension.assessTask)

    action.execute(spyProject)

    assertEquals(extension.groupForAssessTasks, task.group)
  }

  @Test
  void shouldNotComplementTaskWhenNotFound() {
    final CodeCommonTasksExtension spyExtension = spy(new CodeCommonTasksExtension())
    final CodeCommonTasksComplementAction action = new CodeCommonTasksComplementAction(spyExtension)
    spyProject.tasks.create('notFoundTaskName')

    action.execute(spyProject)

    verify(spyExtension)
      .obtainMappings()
    verifyNoMoreInteractions(spyExtension)
  }
}
