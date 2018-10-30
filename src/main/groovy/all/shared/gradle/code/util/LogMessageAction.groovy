//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.Task

@CompileStatic
class LogMessageAction implements Action<Task> {
  private final String message

  LogMessageAction(final String message) {
    this.message = message
  }

  void execute(final Task task) {
    task.logger.quiet message
  }
}
