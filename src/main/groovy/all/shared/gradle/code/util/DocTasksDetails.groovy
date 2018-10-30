//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code.util

import groovy.transform.CompileStatic

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.api.tasks.javadoc.Javadoc

@CompileStatic
final class DocTasksDetails {
  public final String types
  public final String locations

  DocTasksDetails(final String types, final String locations) {
    this.types = types
    this.locations = locations
  }

  static final DocTasksDetails of(final TaskContainer container, final Iterable<String> docTasks) {
    final List<String> types = []
    final List<String> locations = []
    docTasks?.each {
      final Task task = container.findByPath(it)
      if (task) {
        switch (it) {
          case 'javadoc':
            types << 'javadoc'
            locations << ((Javadoc) task).destinationDir.path
            break
          case 'groovydoc':
            types << 'groovydoc'
            locations << ((Groovydoc) task).destinationDir.path
            break
        }
      }
    }
    new DocTasksDetails(String.join('/', types.grep()) , String.join(' ', locations.grep()))
  }
}
