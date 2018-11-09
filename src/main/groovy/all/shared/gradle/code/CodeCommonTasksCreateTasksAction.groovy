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
          project.logger.error('{} task is not defined, and can not be created since not even one of dependencies {} are not found',
            taskName, bindingTasks)
        }
      }
    }
  }

  void execute(final Project project) {
    createTaskIfBinding(extension.tasksForMainAssess, project, extension.assessMainTask, false)
    createTaskIfBinding([extension.assessUnitTestTask, extension.assessIntegrationTestTask] + extension.tasksForTestAssess,
      project, extension.assessTestTask, false)
    createTaskIfBinding(extension.tasksForAssess, project, extension.assessTask)
    createTaskIfBinding([extension.unitTestTask, extension.integrationTestTask], project, extension.testTask, false)
    createTaskIfBinding([extension.testTask] + extension.tasksForCoverage, project, extension.coverageTask)
    createTaskIfBinding([extension.assessTask, extension.testTask, extension.coverageTask, extension.integrationTestTask],
      project, extension.checkTask)
    createTaskIfBinding([extension.assembleTask], project, extension.buildTask, false)
    createTaskIfBinding(extension.tasksForDocumentation, project, extension.documentationTask, false)
  }
}
