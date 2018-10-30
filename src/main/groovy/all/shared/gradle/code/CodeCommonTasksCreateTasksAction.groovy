//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer

@CompileStatic
class CodeCommonTasksCreateTasksAction implements Action<Project> {
  private final CodeCommonTasksExtension extension

  CodeCommonTasksCreateTasksAction(final CodeCommonTasksExtension extension) {
    this.extension = extension
  }

  private void createTaskIfBinding(
      final Iterable<String> bindingTasks,
      final Project project,
      final String taskName,
      final boolean required = true) {
    final TaskContainer tasks = project.tasks
    if (tasks.findByPath(taskName) == null) {
      if (bindingTasks.any { tasks.findByPath(it) }) {
        tasks.create(taskName)
        project.logger.debug('Added {} task', taskName)
      }
      else {
        if (required) {
          project.logger.error('Task {} is not defined, and can not be created since dependencies {} are not defined', taskName, bindingTasks)
        }
      }
    }
  }

  void execute(final Project project) {
    createTaskIfBinding(extension.toolsForMainAssess, project, extension.assessMainTask, false)
    createTaskIfBinding([extension.assessUnitTestTask, extension.assessIntegrationTestTask] + extension.toolsForTestAssess,
      project, extension.assessTestTask, false)
    createTaskIfBinding([extension.assessMainTask, extension.assessTestTask], project, extension.assessTask)
    createTaskIfBinding([extension.unitTestTask, extension.integrationTestTask], project, extension.testTask, false)
    createTaskIfBinding([extension.testTask] + extension.toolsForCoverage, project, extension.coverageTask)
    createTaskIfBinding([extension.assessTask, extension.testTask, extension.coverageTask, extension.integrationTestTask],
      project, extension.checkTask)
    createTaskIfBinding([extension.assembleTask], project, extension.buildTask, false)
    createTaskIfBinding(extension.toolsForDocumentation, project, extension.documentationTask, false)
  }
}
