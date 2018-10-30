//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import all.shared.gradle.code.util.CoverageTasksDetails
import all.shared.gradle.code.util.DocTasksDetails
import all.shared.gradle.code.util.LogMessageAction
import all.shared.gradle.code.util.TestTasksDetails

import groovy.transform.CompileStatic

import org.gradle.api.Task

@CompileStatic
class CodeCommonTasksExtension {
  public static final String COVERAGE_REPORT_LOCATION_TASK_PROPERTY = 'coverageReportAt'
  public static final String DOCUMENTATION_LOCATION_TASK_PROPERTY = 'documentationAt'
  public static final String DOCUMENTATION_TYPE_TASK_PROPERTY = 'documentationType'
  public static final String REPORT_LOCATION_TASK_PROPERTY = 'reportAt'

  String groupForAssessTasks = 'Code Assessment'
  String assessTask = 'assess'
  String assessMainTask = 'assessMain'
  String assessTestTask = 'assessTest'
  String assessUnitTestTask = 'assessUnitTest'
  String assessIntegrationTestTask = 'assessIntegrationTest'
  Iterable<String> toolsForMainAssess = ['codenarcMain', 'checkstyleMain', 'pmdMain']
  Iterable<String> toolsForTestAssess = ['codenarcTest', 'checkstyleTest', 'pmdTest']
  Iterable<String> toolsForCoverage = ['jacocoTestReport', 'jacocoTestCoverageVerification']
  Iterable<String> toolsForDocumentation = ['javadoc', 'groovydoc']

  String groupForTestTasks = 'Code Testing'
  String testTask = 'test'
  String unitTestTask = 'unitTest'
  String integrationTestTask = 'integrationTest'

  String groupForVerificationTasks = 'Code Verification'
  String checkTask = 'check'
  String coverageTask = 'coverage'

  String groupForBuildTasks = 'Build'
  String assembleTask = 'assemble'
  String buildTask = 'build'

  String groupForDocumentationTasks = 'Code Documentation'
  String documentationTask = 'doc'

  private void establishDescription(final Task task, final String description) {
    if (!task.description) {
      task.description = description
    }
  }

  private boolean hasTask(final Task task, final String taskName) {
    task.project.tasks.findByPath(taskName)
  }

  private void shouldRunAfterTask(final Task task, final String taskName) {
    if (hasTask(task, taskName)) {
      task.shouldRunAfter taskName
    }
  }

  private void dependsOnTask(final Task task, final String taskName) {
    if (hasTask(task, taskName)) {
      task.dependsOn taskName
    }
  }

  private void logReportLocation(final Task task) {
    if (task.hasProperty(REPORT_LOCATION_TASK_PROPERTY)) {
      task.doLast new LogMessageAction("See $task.name report at ${task.property(REPORT_LOCATION_TASK_PROPERTY)}")
    }
    else {
      final TestTasksDetails details = TestTasksDetails.of(task)
      if (details.location) {
        task.doLast new LogMessageAction("See $task.name report at $details.location")
      }
    }
  }

  private void logCoverageReportLocation(final Task task) {
    if (task.hasProperty(COVERAGE_REPORT_LOCATION_TASK_PROPERTY)) {
      task.doLast new LogMessageAction("See coverage report at ${task.property(COVERAGE_REPORT_LOCATION_TASK_PROPERTY)}")
    }
    else {
      final CoverageTasksDetails details = CoverageTasksDetails.of(task.project.tasks, toolsForCoverage)
      if (details.locations) {
        task.doLast new LogMessageAction("See coverage report at $details.locations")
      }
    }
  }

  private void complementTest(final Task task) {
    logReportLocation(task)
    logCoverageReportLocation(task)
    toolsForCoverage.each {
      if (hasTask(task, it)) {
        task.finalizedBy(it)
      }
    }
  }

  private void logDocumentationLocation(final Task task, final String extraLocations) {
    if (task.hasProperty(DOCUMENTATION_LOCATION_TASK_PROPERTY)) {
      task.doLast new LogMessageAction("See generated documentation at ${task.property(DOCUMENTATION_LOCATION_TASK_PROPERTY)}")
    }
    else {
      if (extraLocations) {
        task.doLast new LogMessageAction("See generated documentation at $extraLocations")
      }
    }
  }

  private void log(final Task task) {
    task.logger.debug('{} task was complemented', task)
  }

  void complementAssessTask(final Task task) {
    task.group = groupForAssessTasks
    establishDescription(task, 'Analyze and assess code (Main & Test).')
    dependsOnTask(task, assessMainTask)
    dependsOnTask(task, assessTestTask)
    log(task)
  }

  void complementAssessMainTask(final Task task) {
    establishDescription(task, 'Analyze and assess Main code.')
    task.group = groupForAssessTasks
    toolsForMainAssess.each { dependsOnTask(task, it) }
    log(task)
  }

  void complementAssessTestTask(final Task task) {
    task.group = groupForAssessTasks
    establishDescription(task, 'Analyze and assess Test code (Unit & Integration).')
    if (!hasTask(task, assessUnitTestTask) && !hasTask(task, assessIntegrationTestTask)) {
      toolsForTestAssess.each { dependsOnTask(task, it) }
    }
    else {
      dependsOnTask(task, assessUnitTestTask)
      dependsOnTask(task, assessIntegrationTestTask)
    }
    shouldRunAfterTask(task, assessMainTask)
    log(task)
  }

  void complementAssessUnitTestTask(final Task task) {
    task.group = groupForAssessTasks
    establishDescription(task, 'Analyze and assess Unit test code.')
    shouldRunAfterTask(task, assessMainTask)
    log(task)
  }

  void complementAssessIntegrationTestTask(final Task task) {
    establishDescription(task, 'Analyze and assess Integration test code.')
    task.group = groupForAssessTasks
    shouldRunAfterTask(task, assessMainTask)
    shouldRunAfterTask(task, assessUnitTestTask)
    log(task)
  }

  void complementTestTask(final Task task) {
    task.group = groupForTestTasks
    establishDescription(task, 'Runs the Unit & Integration tests.')
    dependsOnTask(task, unitTestTask)
    dependsOnTask(task, integrationTestTask)
    shouldRunAfterTask(task, assessTask)
    if (!hasTask(task, unitTestTask)) {
      complementTest(task)
    }
    log(task)
  }

  void complementUnitTestTask(final Task task) {
    task.group = groupForTestTasks
    establishDescription(task, 'Runs the Unit Tests.')
    if (hasTask(task, assessUnitTestTask)) {
      task.shouldRunAfter assessUnitTestTask
    }
    else {
      shouldRunAfterTask(task, assessTask)
    }
    complementTest(task)
    log(task)
  }

  void complementIntegrationTestTask(final Task task) {
    task.group = groupForTestTasks
    establishDescription(task, 'Runs the Integration Tests.')
    if (hasTask(task, assessIntegrationTestTask)) {
      task.shouldRunAfter assessIntegrationTestTask
    }
    else {
      shouldRunAfterTask(task, assessTask)
    }
    shouldRunAfterTask(task, unitTestTask)
    logReportLocation(task)
    log(task)
  }

  void complementCoverageTask(final Task task) {
    task.group = groupForVerificationTasks
    establishDescription(task, 'Calculates and analyzes code coverage.')
    if (hasTask(task, unitTestTask)) {
      task.dependsOn unitTestTask
    }
    else {
      dependsOnTask(task, testTask)
    }
    log(task)
  }

  void complementCheckTask(final Task task) {
    task.group = groupForVerificationTasks
    establishDescription(task, 'Runs all checks (assess & test).')
    dependsOnTask(task, assessTask)
    if (hasTask(task, coverageTask)) {
      task.dependsOn coverageTask
      dependsOnTask(task, integrationTestTask)
    }
    else {
      dependsOnTask(task, testTask)
    }
    log(task)
  }

  void complementAssembleTask(final Task task) {
    task.group = groupForBuildTasks
    establishDescription(task, 'Assembles binary from Main code.')
    shouldRunAfterTask(task, checkTask)
    log(task)
  }

  void complementBuildTask(final Task task) {
    task.group = groupForBuildTasks
    establishDescription(task, 'Checks and Assembles this project.')
    dependsOnTask(task, checkTask)
    dependsOnTask(task, assembleTask)
    log(task)
  }

  void complementDocumentationTask(final Task task) {
    task.group = groupForDocumentationTasks
    toolsForDocumentation.each { dependsOnTask(task, it) }
    if (hasTask(task, assessMainTask)) {
      task.shouldRunAfter assessMainTask
    }
    else {
      shouldRunAfterTask(task, assessMainTask)
    }
    final DocTasksDetails docTasks = DocTasksDetails.of(task.project.tasks, toolsForDocumentation)
    if (!task.description) {
      task.description = task.hasProperty(DOCUMENTATION_TYPE_TASK_PROPERTY)
        ? "Generates ${task.property(DOCUMENTATION_TYPE_TASK_PROPERTY)} documentation for Main code."
        : docTasks.types
          ? "Generates ${docTasks.types} documentation for Main code."
          : 'Generates documentation for Main code.'
    }
    logDocumentationLocation(task, docTasks.locations)
    log(task)
  }

  Map<String, Closure<Task>> obtainMappings() {
    [(assessTask): this.&complementAssessTask,
      (assessMainTask): this.&complementAssessMainTask,
      (assessTestTask): this.&complementAssessTestTask,
      (assessUnitTestTask): this.&complementAssessUnitTestTask,
      (assessIntegrationTestTask): this.&complementAssessIntegrationTestTask,
      (testTask): this.&complementTestTask,
      (unitTestTask): this.&complementUnitTestTask,
      (integrationTestTask): this.&complementIntegrationTestTask,
      (checkTask): this.&complementCheckTask,
      (coverageTask): this.&complementCoverageTask,
      (assembleTask): this.&complementAssembleTask,
      (buildTask): this.&complementBuildTask,
      (documentationTask): this.&complementDocumentationTask]
  }
}
