//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Task
import org.gradle.api.logging.Logger

import org.junit.jupiter.api.Test

import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

@CompileStatic
final class LogMessageActionTest {

  @Test
  void shouldLogMessage() {
    final LogMessageAction action = new LogMessageAction('theMessage')
    final Task mockTask = mock(Task)
    final Logger mockLogger = mock(Logger)
    doReturn(mockLogger)
      .when(mockTask)
      .getLogger()

    action.execute(mockTask)

    verify(mockLogger)
      .quiet(eq('theMessage'))
  }
}
