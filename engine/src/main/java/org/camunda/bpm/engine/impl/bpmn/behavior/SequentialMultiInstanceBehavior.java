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
package org.camunda.bpm.engine.impl.bpmn.behavior;

import java.util.List;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.EventSubscriptionDeclaration;
import org.camunda.bpm.engine.impl.core.variable.CoreVariableScope;
import org.camunda.bpm.engine.impl.jobexecutor.TimerDeclarationImpl;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;


/**
 * @author Joram Barrez
 * @author Falko Menge
 */
public class SequentialMultiInstanceBehavior extends MultiInstanceActivityBehavior {

  public SequentialMultiInstanceBehavior(ActivityImpl activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
    super(activity, innerActivityBehavior);
  }

  /**
   * Handles the sequential case of spawning the instances.
   * Will only create one instance, since at most one instance can be active.
   */
  protected void createInstances(ActivityExecution execution, int nrOfInstances) throws Exception {
    setLoopVariable(execution, NUMBER_OF_INSTANCES, nrOfInstances);
    setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, 0);
    setLoopVariable(execution, LOOP_COUNTER, 0);
    setLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES, 1);
    logLoopDetails(execution, "initialized", 0, 0, 1, nrOfInstances);

    executeIoMapping((CoreVariableScope<?>) execution);

    executeOriginalBehavior(execution, 0);
  }

  /**
   * Called when the wrapped {@link ActivityBehavior} calls the
   * {@link AbstractBpmnActivityBehavior#leave(ActivityExecution)} method.
   * Handles the completion of one instance, and executes the logic for the sequential behavior.
   */
  public void leave(ActivityExecution execution) {

    int loopCounter = getLoopVariable(execution, LOOP_COUNTER) + 1;
    int nrOfInstances = getLoopVariable(execution, NUMBER_OF_INSTANCES);
    int nrOfCompletedInstances = getLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES) + 1;
    int nrOfActiveInstances = getLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES);

    setLoopVariable(execution, LOOP_COUNTER, loopCounter);
    setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, nrOfCompletedInstances);
    logLoopDetails(execution, "instance completed", loopCounter, nrOfCompletedInstances, nrOfActiveInstances, nrOfInstances);

    if (loopCounter == nrOfInstances || completionConditionSatisfied(execution)) {
      super.leave(execution); // last instance
    } else {
      for (EventSubscriptionDeclaration declaration : EventSubscriptionDeclaration.getDeclarationsForScope(execution.getActivity())) {
        declaration.handleSequentialMultiInstanceLeave((ExecutionEntity) execution);
      }

      // delete all existing jobs
      List<JobEntity> jobs = ((ExecutionEntity)execution).getJobs();
      for (JobEntity jobEntity : jobs) {
        jobEntity.delete();
      }

      callActivityEndListeners(execution);

      executeIoMapping((CoreVariableScope<?>) execution);

      // create timer job for the current execution
      List<TimerDeclarationImpl> timerDeclarations = (List<TimerDeclarationImpl>) execution.getActivity().getProperty(BpmnParse.PROPERTYNAME_TIMER_DECLARATION);
      if (timerDeclarations!=null) {
        for (TimerDeclarationImpl timerDeclaration : timerDeclarations) {
          timerDeclaration.createTimerInstance((ExecutionEntity) execution);
        }
      }


      try {
        executeOriginalBehavior(execution, loopCounter);
      } catch (BpmnError error) {
        // re-throw business fault so that it can be caught by an Error Intermediate Event or Error Event Sub-Process in the process
        throw error;
      } catch (Exception e) {
        throw new ProcessEngineException("Could not execute inner activity behavior of multi instance behavior", e);
      }
    }
  }

  @Override
  public void execute(ActivityExecution execution) throws Exception {
    super.execute(execution);

    if(innerActivityBehavior instanceof SubProcessActivityBehavior) {
      // ACT-1185: end-event in subprocess may have inactivated execution
      if(!execution.isActive() && execution.isEnded() && (execution.getExecutions() == null || execution.getExecutions().size() == 0)) {
        execution.setActive(true);
      }
    }
  }

}
