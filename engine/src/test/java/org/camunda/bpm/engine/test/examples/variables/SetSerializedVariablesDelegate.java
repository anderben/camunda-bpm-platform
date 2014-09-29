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
package org.camunda.bpm.engine.test.examples.variables;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.PersistentVariableInstance;
import org.camunda.bpm.engine.variable.VariableType;
import org.junit.Assert;

/**
 * @author Thorben Lindhauer
 *
 */
public class SetSerializedVariablesDelegate implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {

    String variableName = (String) execution.getVariable("variableToSet");

    // first set variable to some string
    execution.setVariableFromSerialized(variableName, "test", VariableType.STRING.getName(), null);

    PersistentVariableInstance variable = execution.getVariableInstance(variableName);
    Assert.assertEquals(variableName, variable.getName());
    VariableAssertionUtil.assertVariableHasValueAndType(variable, "test", VariableType.STRING.getName());

    // update variable instance to the same type in same transaction
    execution.setVariableFromSerialized(variableName, "another Value", VariableType.STRING.getName(), null);
    Assert.assertEquals(variableName, variable.getName());
    VariableAssertionUtil.assertVariableHasValueAndType(variable, "another Value", VariableType.STRING.getName());

    // update variable instance to another type in same transaction
    execution.setVariableFromSerialized(variableName, 42, VariableType.INTEGER.getName(), null);

    Assert.assertEquals(variableName, variable.getName());
    VariableAssertionUtil.assertVariableHasValueAndType(variable, 42, VariableType.INTEGER.getName());
  }

}
