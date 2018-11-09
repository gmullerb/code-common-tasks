//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.code

import groovy.transform.CompileStatic

@CompileStatic
final class CodeCommonTasksConstants {
  private CodeCommonTasksConstants() { }

  public static final String GROUP_FOR_ASSESS_TASKS = 'Code Assessment'
  public static final String ASSESS_TASK = 'assess'
  public static final String ASSESS_MAIN_TASK = 'assessMain'
  public static final String ASSESS_TEST_TASK = 'assessTest'
  public static final String ASSESS_UNIT_TEST_TASK = 'assessUnitTest'
  public static final String ASSESS_INTEGRATION_TEST_TASK = 'assessIntegrationTest'
  public static final String GROUP_FOR_TEST_TASKS = 'Code Testing'
  public static final String TEST_TASK = 'test'
  public static final String UNIT_TEST_TASK = 'unitTest'
  public static final String INTEGRATION_TEST_TASK = 'integrationTest'
  public static final String GROUP_FOR_VERIFICATION_TASKS = 'Code Verification'
  public static final String CHECK_TASK = 'check'
  public static final String COVERAGE_TASK = 'coverage'
  public static final String GROUP_FOR_BUILD_TASKS = 'Build'
  public static final String ASSEMBLE_TASK = 'assemble'
  public static final String BUILD_TASK = 'build'
  public static final String GROUP_FOR_DOCUMENTATION_TASKS = 'Code Documentation'
  public static final String DOCUMENTATION_TASK = 'doc'
}
