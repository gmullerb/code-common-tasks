//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import groovy.transform.CompileStatic

import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class CodeCommonTasksPlugin implements Plugin<Project> {
  public static final String EXTENSION_NAME = 'codeCommonTasks'

  static final boolean complement(final Project project) {
    if (project.extensions.findByName(EXTENSION_NAME) == null) {
      final CodeCommonTasksExtension extension = project.extensions.create(EXTENSION_NAME, CodeCommonTasksExtension)

      project.afterEvaluate(new CodeCommonTasksCreateTasksAction(extension))
      project.afterEvaluate(new CodeCommonTasksComplementAction(extension))

      project.logger.debug('Added code-common-tasks extension')
      true
    }
    else {
      project.logger.error('Couldn\'t add code-common-tasks extension')
      false
    }
  }

  void apply(final Project project) {
    complement(project)
  }
}
