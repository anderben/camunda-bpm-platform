/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.delegate;

import java.util.Map;

/**
 * @author Thorben Lindhauer
 */
public interface PersistentVariableScope extends VariableScope<PersistentVariableInstance> {

  /**
   * Sets a variable's value from its serialized representation.
   * If this variable already exists in the scope hierarchy above this scope, it is updated in its
   * corresponding scope (which could be this scope, the parent, grandparent, etc.).
   * Otherwise, the variable is created in the top most scope.
   *
   * @param variableName
   * @param value
   * @param variableTypeName
   * @param configuration
   */
  void setVariableFromSerialized(String variableName, Object value, String variableTypeName, Map<String, Object> configuration);

  /**
   * Sets a variable's value from its serialized representation in this scope.
   *
   * @param variableName
   * @param value
   * @param variableTypeName
   * @param configuration
   */
  void setVariableLocalFromSerialized(String variableName, Object value, String variableTypeName, Map<String, Object> configuration);
}
