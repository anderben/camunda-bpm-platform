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
package org.camunda.bpm.engine.impl.form;

import java.util.Map;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.form.handler.StartFormHandler;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.impl.history.producer.HistoryEventProducer;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.runtime.PvmExecutionImpl;

/**
 * @author Thorben Lindhauer
 *
 */
public class StartFormHelper {

 public static void initFormPropertiesOnScope(Map<String, Object> properties, PvmExecutionImpl execution) {
   final ProcessEngineConfigurationImpl processEngineConfiguration = Context.getProcessEngineConfiguration();
   HistoryLevel historyLevel = processEngineConfiguration.getHistoryLevel();

   if(historyLevel.isHistoryEventProduced(HistoryEventTypes.FORM_PROPERTY_UPDATE, execution)) {
     final HistoryEventProducer eventProducer = processEngineConfiguration.getHistoryEventProducer();
     final HistoryEventHandler eventHandler = processEngineConfiguration.getHistoryEventHandler();

     for (String propertyId : properties.keySet()) {
       Object propertyValue = properties.get(propertyId);
       HistoryEvent evt = eventProducer.createFormPropertyUpdateEvt((ExecutionEntity) execution, propertyId, propertyValue, null);
       eventHandler.handleEvent(evt);
     }
   }


   ProcessDefinitionEntity pd = (ProcessDefinitionEntity) execution.getProcessDefinition();
   StartFormHandler startFormHandler = pd.getStartFormHandler();
   startFormHandler.submitFormProperties(properties, (ExecutionEntity) execution);
 }
}
