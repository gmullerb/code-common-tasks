//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import all.shared.gradle.code.util.LogMessageAction
import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test as TestTask
import org.gradle.api.tasks.testing.TestTaskReports
import org.gradle.api.reporting.DirectoryReport
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

@CompileStatic
final class CodeCommonTasksExtensionTest {
  private final CodeCommonTasksExtension extension = new CodeCommonTasksExtension()
  private final Project spyProject = SpyProjectFactory.build()
  private final Task spyTask = spy(spyProject.tasks.create('taskForTest'))
  final Map<String, Closure<Task>> mappings = extension.obtainMappings()

  @BeforeEach
  void beforeTest() {
    doReturn(mock(Logger))
      .when(spyTask)
      .getLogger()
  }

  @Test
  void shouldComplementAssessTaskWithDependencies() {
    spyProject.tasks.create(extension.assessMainTask)
    spyProject.tasks.create(extension.assessTestTask)
    spyTask.description = 'theDescription'

    extension.complementAssessTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.assessMainTask, extension.assessTestTask]))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessTask() {
    mappings[extension.assessTask] (spyTask)

    assertEquals('Analyze and assess code (Main & Test).', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.empty)
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessMainTaskWithDependencies() {
    extension.toolsForMainAssess.each { spyProject.tasks.create(it) }
    spyTask.description = 'theDescription'

    extension.complementAssessMainTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll(extension.toolsForMainAssess as Set))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessMainTask() {
    mappings[extension.assessMainTask] (spyTask)

    assertEquals('Analyze and assess Main code.', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessTestTaskWithDependencies() {
    spyProject.tasks.create(extension.assessUnitTestTask)
    spyProject.tasks.create(extension.assessIntegrationTestTask)
    spyProject.tasks.create(extension.assessMainTask)
    spyTask.description = 'theDescription'

    extension.complementAssessTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.assessUnitTestTask, extension.assessIntegrationTestTask]))
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessTestTaskWithOnlyAssessUnitTestTask() {
    spyProject.tasks.create(extension.assessUnitTestTask)
    spyProject.tasks.create(extension.assessMainTask)

    extension.complementAssessTestTask(spyTask)

    assertEquals('Analyze and assess Test code (Unit & Integration).', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.contains(extension.assessUnitTestTask))
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessTestTaskWithOnlyAssessIntegrationTestTask() {
    spyProject.tasks.create(extension.assessIntegrationTestTask)
    spyProject.tasks.create(extension.assessMainTask)

    extension.complementAssessTestTask(spyTask)

    assertEquals('Analyze and assess Test code (Unit & Integration).', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.contains(extension.assessIntegrationTestTask))
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessTestTaskWithExternalDependencies() {
    extension.toolsForTestAssess.each { spyProject.tasks.create(it) }
    spyProject.tasks.create(extension.assessMainTask)

    extension.complementAssessTestTask(spyTask)

    assertEquals('Analyze and assess Test code (Unit & Integration).', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll(extension.toolsForTestAssess as Set))
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessTestTask() {
    mappings[extension.assessTestTask] (spyTask)

    assertEquals('Analyze and assess Test code (Unit & Integration).', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.empty)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessUnitTestTaskWithDependencies() {
    spyProject.tasks.create(extension.assessMainTask)
    spyTask.description = 'theDescription'

    extension.complementAssessUnitTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessUnitTestTask() {
    mappings[extension.assessUnitTestTask] (spyTask)

    assertEquals('Analyze and assess Unit test code.', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessIntegrationTestTaskWithDependencies() {
    spyProject.tasks.create(extension.assessMainTask)
    spyProject.tasks.create(extension.assessUnitTestTask)
    spyTask.description = 'theDescription'

    extension.complementAssessIntegrationTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessUnitTestTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssessIntegrationTestTask() {
    mappings[extension.assessIntegrationTestTask] (spyTask)

    assertEquals('Analyze and assess Integration test code.', spyTask.description)
    assertEquals(extension.groupForAssessTasks, spyTask.group)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementTestTaskWithDependencies() {
    spyProject.tasks.create(extension.unitTestTask)
    spyProject.tasks.create(extension.integrationTestTask)
    spyProject.tasks.create(extension.assessTask)
    spyProject.tasks.create('jacocoTestReport', JacocoReport)
    spyTask.extensions.add(CodeCommonTasksExtension.REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.extensions.add(CodeCommonTasksExtension.COVERAGE_REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.description = 'theDescription'

    extension.complementTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.unitTestTask, extension.integrationTestTask]))
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessTask))
    verify(spyTask, never())
      .finalizedBy(any())
    verify(spyTask, never())
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementTestTaskWithDependenciesWhenNoUnitTest() {
    spyProject.tasks.create(extension.integrationTestTask)
    spyProject.tasks.create(extension.assessTask)
    spyTask.extensions.add(CodeCommonTasksExtension.REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.description = 'theDescription'

    extension.complementTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.integrationTestTask]))
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessTask))
    verify(spyTask, times(1))
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementTestTaskWithAbstractTestReportWhenNoUnitTest() {
    final TestTask spyTestTask = spy(spyProject.tasks.create(extension.testTask, TestTask))
    final TestTaskReports mockReportContainer = mock(TestTaskReports)
    doReturn(mockReportContainer)
      .when(spyTestTask)
      .getReports()
    final DirectoryReport mockReport = mock(DirectoryReport)
    doReturn(mockReport)
      .when(mockReportContainer)
      .getHtml()
    doReturn(true)
      .when(mockReport)
      .isEnabled()
    doReturn(new File('reportAt'))
      .when(mockReport)
      .getEntryPoint()

    extension.complementTestTask(spyTestTask)

    verify(spyTestTask, times(1))
      .doLast(any(LogMessageAction))
  }

  @Test
  void shouldComplementTestTaskWithJacocoTestReportWhenNoUnitTest() {
    final JacocoReport mockJacocoReportTask = mock(JacocoReport)
    doReturn('jacocoTestReport')
      .when(mockJacocoReportTask)
      .getName()
    final JacocoReportsContainer mockReportContainer = mock(JacocoReportsContainer)
    doReturn(mockReportContainer)
      .when(mockJacocoReportTask)
      .getReports()
    final DirectoryReport mockReport = mock(DirectoryReport)
    doReturn(mockReport)
      .when(mockReportContainer)
      .getHtml()
    doReturn(true)
      .when(mockReport)
      .isEnabled()
    doReturn(new File('jacocoTestReportAt'))
      .when(mockReport)
      .getEntryPoint()
    spyProject.tasks.add(mockJacocoReportTask)
    spyProject.tasks.create(extension.assessTask)

    extension.complementTestTask(spyTask)

    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessTask))
    verify(spyTask)
      .finalizedBy(eq('jacocoTestReport'))
    verify(spyTask, times(1))
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementTestTask() {
    mappings[extension.testTask] (spyTask)

    assertEquals('Runs the Unit & Integration tests.', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.empty)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask, never())
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementUnitTestTaskWithAssessUnitTestTasks() {
    spyProject.tasks.create(extension.assessUnitTestTask)
    spyTask.extensions.add(CodeCommonTasksExtension.REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.extensions.add(CodeCommonTasksExtension.COVERAGE_REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.description = 'theDescription'

    extension.complementUnitTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessUnitTestTask))
    verify(spyTask, times(2))
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementUnitTestTaskWithJacocoTestReport() {
    final JacocoReport mockJacocoReportTask = mock(JacocoReport)
    doReturn('jacocoTestReport')
      .when(mockJacocoReportTask)
      .getName()
    final JacocoReportsContainer mockReportContainer = mock(JacocoReportsContainer)
    doReturn(mockReportContainer)
      .when(mockJacocoReportTask)
      .getReports()
    final DirectoryReport mockReport = mock(DirectoryReport)
    doReturn(mockReport)
      .when(mockReportContainer)
      .getHtml()
    doReturn(true)
      .when(mockReport)
      .isEnabled()
    doReturn(new File('jacocoTestReportAt'))
      .when(mockReport)
      .getEntryPoint()
    spyProject.tasks.add(mockJacocoReportTask)

    extension.complementUnitTestTask(spyTask)

    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask)
      .finalizedBy(eq('jacocoTestReport'))
    verify(spyTask, times(1))
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementUnitTestTaskWithAssessTask() {
    spyProject.tasks.create(extension.assessTask)
    spyTask.extensions.add(CodeCommonTasksExtension.REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.extensions.add(CodeCommonTasksExtension.COVERAGE_REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.description = 'theDescription'

    extension.complementUnitTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessTask))
    verify(spyTask, never())
      .finalizedBy(any())
    verify(spyTask, times(2))
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementUnitTestTask() {
    mappings[extension.unitTestTask] (spyTask)

    assertEquals('Runs the Unit Tests.', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask, never())
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementIntegrationTestTaskWithAssessIntegrationTestTask() {
    spyProject.tasks.create(extension.assessIntegrationTestTask)
    spyProject.tasks.create(extension.unitTestTask)
    spyTask.extensions.add(CodeCommonTasksExtension.REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.description = 'theDescription'

    extension.complementIntegrationTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessIntegrationTestTask))
    verify(spyTask)
      .shouldRunAfter(eq(extension.unitTestTask))
    verify(spyTask)
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementIntegrationTestTaskWithAssessTask() {
    spyProject.tasks.create(extension.assessTask)
    spyProject.tasks.create(extension.unitTestTask)
    spyTask.extensions.add(CodeCommonTasksExtension.REPORT_LOCATION_TASK_PROPERTY, 'at')
    spyTask.description = 'theDescription'

    extension.complementIntegrationTestTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessTask))
    verify(spyTask)
      .shouldRunAfter(eq(extension.unitTestTask))
    verify(spyTask)
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementIntegrationTestTask() {
    mappings[extension.integrationTestTask] (spyTask)

    assertEquals('Runs the Integration Tests.', spyTask.description)
    assertEquals(extension.groupForTestTasks, spyTask.group)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask, never())
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementCoverageTaskWithUnitTestTask() {
    spyProject.tasks.create(extension.unitTestTask)
    spyTask.description = 'theDescription'

    extension.complementCoverageTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForVerificationTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.unitTestTask]))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementCoverageTaskWithTestTask() {
    spyProject.tasks.create(extension.testTask)

    extension.complementCoverageTask(spyTask)

    assertEquals('Calculates and analyzes code coverage.', spyTask.description)
    assertEquals(extension.groupForVerificationTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.contains(extension.testTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementCoverageTask() {
    mappings[extension.coverageTask] (spyTask)

    assertEquals('Calculates and analyzes code coverage.', spyTask.description)
    assertEquals(extension.groupForVerificationTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.empty)
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementCheckTaskWithDependencies() {
    spyProject.tasks.create(extension.assessTask)
    spyProject.tasks.create(extension.coverageTask)
    spyProject.tasks.create(extension.integrationTestTask)
    spyTask.description = 'theDescription'

    extension.complementCheckTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForVerificationTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.assessTask, extension.coverageTask, extension.integrationTestTask]))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementCheckTaskWithTestTask() {
    spyProject.tasks.create(extension.assessTask)
    spyProject.tasks.create(extension.testTask)

    extension.complementCheckTask(spyTask)

    assertEquals('Runs all checks (assess & test).', spyTask.description)
    assertEquals(extension.groupForVerificationTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.assessTask, extension.testTask]))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementCheckTask() {
    mappings[extension.checkTask] (spyTask)

    assertEquals('Runs all checks (assess & test).', spyTask.description)
    assertEquals(extension.groupForVerificationTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.empty)
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssembleTaskWithDescription() {
    spyProject.tasks.create(extension.checkTask)
    spyTask.description = 'theDescription'

    extension.complementAssembleTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForBuildTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.checkTask))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementAssembleTask() {
    mappings[extension.assembleTask] (spyTask)

    assertEquals('Assembles binary from Main code.', spyTask.description)
    assertEquals(extension.groupForBuildTasks, spyTask.group)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementBuildTaskWithDependencies() {
    spyProject.tasks.create(extension.checkTask)
    spyProject.tasks.create(extension.assembleTask)
    spyTask.description = 'theDescription'

    extension.complementBuildTask(spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForBuildTasks, spyTask.group)
    assertTrue(spyTask.dependsOn.containsAll([extension.checkTask, extension.assembleTask]))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementBuildTask() {
    mappings[extension.buildTask] (spyTask)

    assertEquals('Checks and Assembles this project.', spyTask.description)
    assertEquals(extension.groupForBuildTasks, spyTask.group)
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementDocumentationTaskWithDependencies() {
    spyProject.tasks.create(extension.assessMainTask)
    spyTask.extensions.add(CodeCommonTasksExtension.DOCUMENTATION_TYPE_TASK_PROPERTY, 'type')
    spyTask.extensions.add(CodeCommonTasksExtension.DOCUMENTATION_LOCATION_TASK_PROPERTY, 'at')

    extension.complementDocumentationTask(spyTask)

    assertEquals('Generates type documentation for Main code.', spyTask.description)
    assertEquals(extension.groupForDocumentationTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask)
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementDocumentationTaskWithToolsDependencies() {
    spyProject.tasks.create(extension.assessMainTask)
    final Javadoc task = spyProject.tasks.create('javadoc', Javadoc)
    task.destinationDir = new File('testPath')

    extension.complementDocumentationTask(spyTask)

    assertEquals('Generates javadoc documentation for Main code.', spyTask.description)
    assertEquals(extension.groupForDocumentationTasks, spyTask.group)
    verify(spyTask)
      .shouldRunAfter(eq(extension.assessMainTask))
    verify(spyTask)
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementDocumentationTask() {
    mappings[extension.documentationTask] (spyTask)

    assertEquals('Generates documentation for Main code.', spyTask.description)
    assertEquals(extension.groupForDocumentationTasks, spyTask.group)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask, never())
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }

  @Test
  void shouldComplementDocumentationTaskWithDescription() {
    spyTask.description = 'theDescription'

    mappings[extension.documentationTask] (spyTask)

    assertEquals('theDescription', spyTask.description)
    assertEquals(extension.groupForDocumentationTasks, spyTask.group)
    verify(spyTask, never())
      .shouldRunAfter(any())
    verify(spyTask, never())
      .doLast(any(LogMessageAction))
    verify(spyTask.logger)
      .debug(eq('{} task was complemented'), eq(spyTask))
  }
}
