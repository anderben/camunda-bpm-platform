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
package org.camunda.bpm.engine.impl.cmmn.behavior;

import org.camunda.bpm.engine.impl.core.mapping.value.ConstantValueProvider;
import org.camunda.bpm.engine.impl.core.mapping.value.ParameterValueProvider;
import org.camunda.bpm.engine.impl.core.variable.CoreVariableScope;

/**
 * @author Roman Smirnov
 *
 */
public class CallableElementParameter {

  protected ParameterValueProvider sourceValueProvider;
  protected String target;
  protected boolean allVariables;

  // source ////////////////////////////////////////////////////////

  public Object getSource(CoreVariableScope<?> variableScope) {
    if (sourceValueProvider instanceof ConstantValueProvider) {
      String variableName = (String) sourceValueProvider.getValue(variableScope);
      return variableScope.getVariable(variableName);

    } else {
      return sourceValueProvider.getValue(variableScope);
    }

  }

  public ParameterValueProvider getSourceValueProvider() {
    return sourceValueProvider;
  }

  public void setSourceValueProvider(ParameterValueProvider source) {
    this.sourceValueProvider = source;
  }

  // target //////////////////////////////////////////////////////////

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  // all variables //////////////////////////////////////////////////

  public boolean isAllVariables() {
    return allVariables;
  }

  public void setAllVariables(boolean allVariables) {
    this.allVariables = allVariables;
  }

}
