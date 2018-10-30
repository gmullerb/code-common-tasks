//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Task
import org.gradle.api.tasks.testing.AbstractTestTask

@CompileStatic
final class TestTasksDetails {
  public final String location

  TestTasksDetails(final String location) {
    this.location = location
  }

  private static String obtainAbstractTest(final AbstractTestTask task) {
    task.reports.html.enabled
      ? task.reports.html.entryPoint
      : ''
  }

  static final TestTasksDetails of(final Task task) {
    new TestTasksDetails(task instanceof AbstractTestTask
      ? obtainAbstractTest((AbstractTestTask) task)
      : '')
  }
}
