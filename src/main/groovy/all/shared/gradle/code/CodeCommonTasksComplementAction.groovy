//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

@CompileStatic
class CodeCommonTasksComplementAction implements Action<Project> {
  private final CodeCommonTasksExtension extension

  CodeCommonTasksComplementAction(final CodeCommonTasksExtension extension) {
    this.extension = extension
  }

  void execute(final Project project) {
    final TaskContainer tasks = project.tasks
    extension.obtainMappings().each { taskName, complementAction ->
      final Task task = tasks.findByPath(taskName)
      if (task) {
        complementAction(task)
      }
    }
  }
}
