//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.Project

import org.junit.jupiter.api.Test

import org.mockito.InOrder

import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.inOrder

@CompileStatic
final class CodeCommonTasksPluginTest {
  private final Project spyProject = SpyProjectFactory.build()

  @Test
  void shouldComplement() {
    final boolean result = CodeCommonTasksPlugin.complement(spyProject)

    assertTrue(result)
    final InOrder verifyInOrder = inOrder(spyProject, spyProject.logger)
    verifyInOrder.verify(spyProject)
      .afterEvaluate(any(CodeCommonTasksCreateTasksAction))
    verifyInOrder.verify(spyProject)
      .afterEvaluate(any(CodeCommonTasksComplementAction))
    verifyInOrder.verify(spyProject.logger)
      .debug(eq('Added code-common-tasks extension'))
  }

  @Test
  void shouldNotComplement() {
    spyProject.extensions.add(CodeCommonTasksPlugin.EXTENSION_NAME, 'someValue')

    final boolean result = CodeCommonTasksPlugin.complement(spyProject)

    assertFalse(result)
    verify(spyProject.logger)
      .error(eq('Couldn\'t add code-common-tasks extension'))
  }

  @Test
  void shouldApplyPlugin() {
    final CodeCommonTasksPlugin plugin = new CodeCommonTasksPlugin()

    plugin.apply(spyProject)

    verify(spyProject.logger)
      .debug(eq('Added code-common-tasks extension'))
  }
}
