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
package org.camunda.bpm.engine.test.bpmn.iomapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.camunda.bpm.engine.impl.test.PluggableProcessEngineTestCase;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.test.Deployment;

/**
 * Testcase for camunda input / output in BPMN
 *
 * @author Daniel Meyer
 *
 */
public class InputOutputTest extends PluggableProcessEngineTestCase {

  // Input parameters /////////////////////////////////////////

  @Deployment
  public void testInputNullValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals("null", variable.getSerializerName());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputStringConstantValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals("stringValue", variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }


  @Deployment
  public void testInputElValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2l, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputScriptValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptSource", "return 1 + 1");
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputExternalScriptValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputExternalScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptPath", "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy");
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputExternalScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputExternalClasspathScriptValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputExternalClasspathScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptPath", "classpath://org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy");
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testInputExternalClasspathScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment(resources = {
    "org/camunda/bpm/engine/test/bpmn/iomapping/InputOutputTest.testInputExternalDeploymentScriptValue.bpmn",
    "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy"
  })
  public void testInputExternalDeploymentScriptValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment(resources = {
    "org/camunda/bpm/engine/test/bpmn/iomapping/InputOutputTest.testInputExternalDeploymentScriptValueAsVariable.bpmn",
    "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy"
  })
  public void testInputExternalDeploymentScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptPath", "deployment://org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy");
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment(resources = {
    "org/camunda/bpm/engine/test/bpmn/iomapping/InputOutputTest.testInputExternalDeploymentScriptValueAsBean.bpmn",
    "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy"
  })
  public void testInputExternalDeploymentScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    runtimeService.startProcessInstanceByKey("testProcess", variables);
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(execution.getId(), variable.getExecutionId());
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testInputListElValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    List<Object> value = (List<Object>) variable.getTypedValue();
    assertEquals(2l, value.get(0));
    assertEquals(3l, value.get(1));
    assertEquals(4l, value.get(2));
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testInputListMixedValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    List<Object> value = (List<Object>) variable.getTypedValue();
    assertEquals("constantStringValue", value.get(0));
    assertEquals("elValue", value.get(1));
    assertEquals("scriptValue", value.get(2));
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testInputMapElValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    TreeMap<String, Object> value = (TreeMap) variable.getTypedValue();
    assertEquals(2l, value.get("a"));
    assertEquals(3l, value.get("b"));
    assertEquals(4l, value.get("c"));

  }

  @Deployment
  public void testInputMultipleElValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance var1 = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(var1);
    assertEquals(2l, var1.getTypedValue());
    assertEquals(execution.getId(), var1.getExecutionId());

    VariableInstance var2 = runtimeService.createVariableInstanceQuery().variableName("var2").singleResult();
    assertNotNull(var2);
    assertEquals(3l, var2.getTypedValue());
    assertEquals(execution.getId(), var2.getExecutionId());
  }

  @Deployment
  public void testInputMultipleMixedValue() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance var1 = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(var1);
    assertEquals(2l, var1.getTypedValue());
    assertEquals(execution.getId(), var1.getExecutionId());

    VariableInstance var2 = runtimeService.createVariableInstanceQuery().variableName("var2").singleResult();
    assertNotNull(var2);
    assertEquals("stringConstantValue", var2.getTypedValue());
    assertEquals(execution.getId(), var2.getExecutionId());
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testInputNested() {
    runtimeService.startProcessInstanceByKey("testProcess");
    Execution execution = runtimeService.createExecutionQuery().activityId("wait").singleResult();

    VariableInstance var1 = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    TreeMap<String, Object> value = (TreeMap) var1.getTypedValue();
    List<Object> nestedList = (List<Object>) value.get("a");
    assertEquals("stringInListNestedInMap", nestedList.get(0));
    assertEquals("b", nestedList.get(1));

    VariableInstance var2 = runtimeService.createVariableInstanceQuery().variableName("var2").singleResult();
    assertNotNull(var2);
    assertEquals("stringConstantValue", var2.getTypedValue());
    assertEquals(execution.getId(), var2.getExecutionId());
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testInputNestedListValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    List<Object> value = (List<Object>) variable.getTypedValue();
    assertEquals("constantStringValue", value.get(0));
    assertEquals("elValue", value.get(1));
    assertEquals("scriptValue", value.get(2));

    List<Object> nestedList = (List<Object>) value.get(3);
    List<Object> nestedNestedList = (List<Object>) nestedList.get(0);
    assertEquals("a", nestedNestedList.get(0));
    assertEquals("b", nestedNestedList.get(1));
    assertEquals("c", nestedNestedList.get(2));
    assertEquals("d", nestedList.get(1));

    TreeMap<String, Object> nestedMap = (TreeMap<String, Object>) value.get(4);
    assertEquals("bar", nestedMap.get("foo"));
    assertEquals("world", nestedMap.get("hello"));
  }

  // output parameter ///////////////////////////////////////////////////////

  @Deployment
  public void testOutputNullValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals("null", variable.getSerializerName());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputStringConstantValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals("stringValue", variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }


  @Deployment
  public void testOutputElValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2l, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputScriptValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptSource", "return 1 + 1");
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputExternalScriptValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputExternalScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptPath", "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy");
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputExternalScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputExternalClasspathScriptValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputExternalClasspathScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptPath", "classpath://org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy");
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  public void testOutputExternalClasspathScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment(resources = {
    "org/camunda/bpm/engine/test/bpmn/iomapping/InputOutputTest.testOutputExternalDeploymentScriptValue.bpmn",
    "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy"
  })
  public void testOutputExternalDeploymentScriptValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment(resources = {
    "org/camunda/bpm/engine/test/bpmn/iomapping/InputOutputTest.testOutputExternalDeploymentScriptValueAsVariable.bpmn",
    "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy"
  })
  public void testOutputExternalDeploymentScriptValueAsVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("scriptPath", "deployment://org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy");
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment(resources = {
    "org/camunda/bpm/engine/test/bpmn/iomapping/InputOutputTest.testOutputExternalDeploymentScriptValueAsBean.bpmn",
    "org/camunda/bpm/engine/test/bpmn/iomapping/oneplusone.groovy"
  })
  public void testOutputExternalDeploymentScriptValueAsBean() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("onePlusOneBean", new OnePlusOneBean());
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess", variables);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals(2, variable.getTypedValue());
    assertEquals(pi.getId(), variable.getExecutionId());
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testOutputListElValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    List<Object> value = (List<Object>) variable.getTypedValue();
    assertEquals(2l, value.get(0));
    assertEquals(3l, value.get(1));
    assertEquals(4l, value.get(2));
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testOutputListMixedValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    List<Object> value = (List<Object>) variable.getTypedValue();
    assertEquals("constantStringValue", value.get(0));
    assertEquals("elValue", value.get(1));
    assertEquals("scriptValue", value.get(2));
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testOutputMapElValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    TreeMap<String, Object> value = (TreeMap) variable.getTypedValue();
    assertEquals(2l, value.get("a"));
    assertEquals(3l, value.get("b"));
    assertEquals(4l, value.get("c"));

  }

  @Deployment
  public void testOutputMultipleElValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance var1 = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(var1);
    assertEquals(2l, var1.getTypedValue());
    assertEquals(pi.getId(), var1.getExecutionId());

    VariableInstance var2 = runtimeService.createVariableInstanceQuery().variableName("var2").singleResult();
    assertNotNull(var2);
    assertEquals(3l, var2.getTypedValue());
    assertEquals(pi.getId(), var2.getExecutionId());
  }

  @Deployment
  public void testOutputMultipleMixedValue() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance var1 = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(var1);
    assertEquals(2l, var1.getTypedValue());
    assertEquals(pi.getId(), var1.getExecutionId());

    VariableInstance var2 = runtimeService.createVariableInstanceQuery().variableName("var2").singleResult();
    assertNotNull(var2);
    assertEquals("stringConstantValue", var2.getTypedValue());
    assertEquals(pi.getId(), var2.getExecutionId());
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testOutputNested() {
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance var1 = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    TreeMap<String, Object> value = (TreeMap) var1.getTypedValue();
    List<Object> nestedList = (List<Object>) value.get("a");
    assertEquals("stringInListNestedInMap", nestedList.get(0));
    assertEquals("b", nestedList.get(1));
    assertEquals(pi.getId(), var1.getExecutionId());

    VariableInstance var2 = runtimeService.createVariableInstanceQuery().variableName("var2").singleResult();
    assertNotNull(var2);
    assertEquals("stringConstantValue", var2.getTypedValue());
    assertEquals(pi.getId(), var2.getExecutionId());
  }

  @Deployment
  @SuppressWarnings("unchecked")
  public void testOutputListNestedValues() {
    runtimeService.startProcessInstanceByKey("testProcess");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    List<Object> value = (List<Object>) variable.getTypedValue();
    assertEquals("constantStringValue", value.get(0));
    assertEquals("elValue", value.get(1));
    assertEquals("scriptValue", value.get(2));

    List<Object> nestedList = (List<Object>) value.get(3);
    List<Object> nestedNestedList = (List<Object>) nestedList.get(0);
    assertEquals("a", nestedNestedList.get(0));
    assertEquals("b", nestedNestedList.get(1));
    assertEquals("c", nestedNestedList.get(2));
    assertEquals("d", nestedList.get(1));

    TreeMap<String, Object> nestedMap = (TreeMap<String, Object>) value.get(4);
    assertEquals("bar", nestedMap.get("foo"));
    assertEquals("world", nestedMap.get("hello"));
  }

  // ensure Io supported on event subprocess /////////////////////////////////

  @Deployment
  public void testNonInterruptingEventSubprocessIoSupport() {
    runtimeService.startProcessInstanceByKey("testProcess");
    runtimeService.correlateMessage("msg");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals("stringValue", variable.getTypedValue());
  }

  @Deployment
  public void testInterruptingEventSubprocessIoSupport() {
    runtimeService.startProcessInstanceByKey("testProcess");
    runtimeService.correlateMessage("msg");

    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("var1").singleResult();
    assertNotNull(variable);
    assertEquals("stringValue", variable.getTypedValue());
  }

}
