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
package org.camunda.bpm.engine.impl.core.mapping.value;

import org.camunda.bpm.engine.impl.core.variable.CoreVariableScope;

/**
 * A constant parameter value.
 *
 * @author Daniel Meyer
 *
 */
public class ConstantValueProvider implements ParameterValueProvider {

  protected Object value;

  public ConstantValueProvider(Object value) {
    this.value = value;
  }

  public Object getValue(CoreVariableScope<?> scope) {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

}
