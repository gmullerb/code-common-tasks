//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer
import org.gradle.api.reporting.DirectoryReport

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock

@CompileStatic
final class CoverageTasksDetailsTest {
  @Test
  void shouldCreateCoverageTasksDetails() {
    final TaskContainer mockContainer = mock(TaskContainer)
    final JacocoReport mockJacocoReportTask = mock(JacocoReport)
    doReturn(mockJacocoReportTask)
      .when(mockContainer)
      .findByPath(eq('jacocoTestReport'))
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

    final CoverageTasksDetails result = CoverageTasksDetails.of(mockContainer, ['jacocoTestReport'])

    assertEquals('jacocoTestReportAt', result.locations)
  }

  @Test
 void shouldCreateEmptyCoverageTasksDetailsWhenDisabled() {
    final TaskContainer mockContainer = mock(TaskContainer)
    final JacocoReport mockJacocoReportTask = mock(JacocoReport)
    doReturn(mockJacocoReportTask)
      .when(mockContainer)
      .findByPath(eq('jacocoTestReport'))
    final JacocoReportsContainer mockReportContainer = mock(JacocoReportsContainer)
    doReturn(mockReportContainer)
      .when(mockJacocoReportTask)
      .getReports()
    final DirectoryReport mockReport = mock(DirectoryReport)
    doReturn(mockReport)
      .when(mockReportContainer)
      .getHtml()
    doReturn(false)
      .when(mockReport)
      .isEnabled()

    final CoverageTasksDetails result = CoverageTasksDetails.of(mockContainer, ['jacocoTestReport'])

    assertEquals('', result.locations)
  }

  @Test
  void shouldCreateEmptyCoverageTasksDetailsWhenNotFound() {
    final CoverageTasksDetails result = CoverageTasksDetails.of(mock(TaskContainer), ['jacocoTestReport'])

    assertEquals('', result.locations)
  }

  @Test
  void shouldCreateEmptyCoverageTasksDetailsWhenNotDefined() {
    final TaskContainer mockContainer = mock(TaskContainer)
    final Task mockTask = mock(Task)
    doReturn(mockTask)
      .when(mockContainer)
      .findByPath(eq('testmocktask'))

    final CoverageTasksDetails result = CoverageTasksDetails.of(mockContainer, ['testmocktask'])

    assertEquals('', result.locations)
  }

  @Test
  void shouldCreateEmptyCoverageTasksDetailsWhenNull() {
    final CoverageTasksDetails result = CoverageTasksDetails.of(mock(TaskContainer), null)

    assertEquals('', result.locations)
  }
}
