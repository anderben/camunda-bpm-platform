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
package org.camunda.bpm.engine.impl.cmd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.delegate.PersistentVariableInstance;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.runtime.VariableInstance;

/**
 * @author Daniel Meyer
 *
 */
public class GetTaskFormVariablesCmd extends AbstractGetFormVariablesCmd {

  private final static Logger log = Logger.getLogger(GetTaskFormVariablesCmd.class.getName());

  public GetTaskFormVariablesCmd(String taskId, Collection<String> variableNames) {
    super(taskId, variableNames);
  }

  @SuppressWarnings("unchecked")
  public Map<String, VariableInstance> execute(CommandContext commandContext) {

    TaskEntity task = commandContext.getTaskManager()
      .findTaskById(resourceId);

    if(task == null) {
      throw new BadUserRequestException("Cannot find task with id '"+resourceId+"'.");
    }

    Map<String, PersistentVariableInstance> result = new HashMap<String, PersistentVariableInstance>();

    // first, evaluate form fields
    TaskFormData taskFormData = task.getTaskDefinition().getTaskFormHandler().createTaskForm(task);
    for (FormField formField : taskFormData.getFormFields()) {
      if(formVariableNames == null || formVariableNames.contains(formField.getId())) {
        result.put(formField.getId(), createVariable(formField));
      }
    }

    // collect remaining variables from task scope and parent scopes
    task.collectVariableInstances(result, formVariableNames);

    // ensure serialized values are fetched
    for (PersistentVariableInstance variableInstance : result.values()) {

      if(variableInstance.storesCustomObjects()) {
        try {
            variableInstance.getSerializedValue();
            variableInstance.getTypedValue();
        } catch(Exception t) {
          // do not fail if one of the variables fails to load
          log.log(Level.FINE, "Exception while getting value for variable", t);
        }
      }
    }

    return (Map) result;
  }

}
