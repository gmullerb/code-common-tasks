//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.testing.jacoco.tasks.JacocoReport

@CompileStatic
final class CoverageTasksDetails {
  public final String locations

  CoverageTasksDetails(final String locations) {
    this.locations = locations
  }

  private static String obtainJacocoReport(final JacocoReport task) {
    task.reports.html.enabled
      ? task.reports.html.entryPoint
      : null
  }

  static final CoverageTasksDetails of(final TaskContainer container, final Iterable<String> coverageTasks) {
    final List<String> locations = []
    coverageTasks?.each {
      final Task task = container.findByPath(it)
      if (task) {
        switch (it) {
          case 'jacocoTestReport':
            locations << obtainJacocoReport((JacocoReport) task)
            break
        }
      }
    }
    new CoverageTasksDetails(String.join(' ', locations.grep()))
  }
}
