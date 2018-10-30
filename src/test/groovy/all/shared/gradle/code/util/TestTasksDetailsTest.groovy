//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Task
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.TestTaskReports
import org.gradle.api.reporting.DirectoryReport

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock

@CompileStatic
final class TestTasksDetailsTest {
  @Test
  void shouldCreateAbstractTestDetails() {
    final AbstractTestTask mockTask = mock(AbstractTestTask)
    final TestTaskReports mockReportContainer = mock(TestTaskReports)
    doReturn(mockReportContainer)
      .when(mockTask)
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

    final TestTasksDetails result = TestTasksDetails.of(mockTask)

    assertEquals('reportAt', result.location)
  }

  @Test
  void shouldCreateEmptyAbstractTestDetailsWhenDisabled() {
    final AbstractTestTask mockTask = mock(AbstractTestTask)
    final TestTaskReports mockReportContainer = mock(TestTaskReports)
    doReturn(mockReportContainer)
      .when(mockTask)
      .getReports()
    final DirectoryReport mockReport = mock(DirectoryReport)
    doReturn(mockReport)
      .when(mockReportContainer)
      .getHtml()
    doReturn(false)
      .when(mockReport)
      .isEnabled()

    final TestTasksDetails result = TestTasksDetails.of(mockTask)

    assertEquals('', result.location)
  }

  @Test
  void shouldCreateEmptyAbstractTestDetailsWhenNotAbstractTest() {
    final TestTasksDetails result = TestTasksDetails.of(mock(Task))

    assertEquals('', result.location)
  }
}
