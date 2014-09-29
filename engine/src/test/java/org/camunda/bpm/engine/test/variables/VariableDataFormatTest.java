/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.engine.test.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.SerializedObjectVariableValue;
import org.camunda.bpm.engine.delegate.SerializedVariableValueBuilder;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.db.sql.DbSqlSessionFactory;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;
import org.camunda.bpm.engine.impl.spin.SpinSerializationType;
import org.camunda.bpm.engine.impl.spin.SpinVariableTypeResolver;
import org.camunda.bpm.engine.impl.test.AbstractProcessEngineTestCase;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.api.runtime.DummySerializable;
import org.camunda.bpm.engine.variable.VariableType;
import org.camunda.spin.DataFormats;
import org.camunda.spin.impl.json.tree.JsonJacksonTreeDataFormat;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

public class VariableDataFormatTest extends AbstractProcessEngineTestCase {

  protected static final String ONE_TASK_PROCESS = "org/camunda/bpm/engine/test/variables/oneTaskProcess.bpmn20.xml";

  protected static final String JSON_FORMAT_NAME = DataFormats.jsonTree().getName();

  @Override
  protected void initializeProcessEngine() {
    ProcessEngineConfigurationImpl engineConfig =
        (ProcessEngineConfigurationImpl) ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("camunda.cfg.xml");

    engineConfig.setDefaultSerializationFormat(JSON_FORMAT_NAME);

    processEngine = engineConfig.buildProcessEngine();
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSerializationAsJson() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    SimpleBean bean = new SimpleBean("a String", 42, true);
    runtimeService.setVariable(instance.getId(), "simpleBean", bean);

    VariableInstance beanVariable = runtimeService.createVariableInstanceQuery().singleResult();
    assertNotNull(beanVariable);
    assertEquals(VariableType.SPIN.getName(), beanVariable.getSerializerName());

    SimpleBean returnedBean = (SimpleBean) beanVariable.getTypedValue();
    assertNotNull(returnedBean);
    assertBeansEqual(returnedBean, bean);

    // currently internal API
    VariableInstanceEntity variableEntity = (VariableInstanceEntity) beanVariable;
    assertEquals(JSON_FORMAT_NAME, variableEntity.getDataFormatId());

    String persistedValue = (String) variableEntity.getSerializedValue().getTypedValue();
    String expectedJson = bean.toExpectedJsonString();
    JSONAssert.assertEquals(expectedJson, persistedValue, true);

  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testListSerializationAsJson() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    List<SimpleBean> beans = new ArrayList<SimpleBean>();
    for (int i = 0; i < 20; i++) {
      beans.add(new SimpleBean("a String" + i, 42 + i, true));
    }

    runtimeService.setVariable(instance.getId(), "simpleBeans", beans);

    VariableInstance beansVariable = runtimeService.createVariableInstanceQuery().singleResult();
    assertNotNull(beansVariable);
    assertEquals(VariableType.SPIN.getName(), beansVariable.getSerializerName());

    List<SimpleBean> returnedBeans = (List<SimpleBean>) beansVariable.getTypedValue();
    assertNotNull(returnedBeans);
    assertTrue(returnedBeans instanceof ArrayList);
    assertListsEqual(beans, returnedBeans);

    // currently internal API
    VariableInstanceEntity variableEntity = (VariableInstanceEntity) beansVariable;
    assertEquals(JSON_FORMAT_NAME, variableEntity.getDataFormatId());

    String persistedValue = (String) variableEntity.getSerializedValue().getTypedValue();
    String expectedJson = toExpectedJsonArray(beans);
    JSONAssert.assertEquals(expectedJson, persistedValue, true);

  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testFailingSerialization() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    FailingSerializationBean failingBean = new FailingSerializationBean("a String", 42, true);

    try {
      runtimeService.setVariable(instance.getId(), "simpleBean", failingBean);
      fail("exception expected");
    } catch (ProcessEngineException e) {
      // happy path
    }
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testFailingDeserialization() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    FailingDeserializationBean failingBean = new FailingDeserializationBean("a String", 42, true);

    runtimeService.setVariable(instance.getId(), "simpleBean", failingBean);

    VariableInstance variableInstance = runtimeService.createVariableInstanceQuery().singleResult();

    assertNull(variableInstance.getTypedValue());
    assertNotNull(variableInstance.getErrorMessage());
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSettingVariableExceedingTextFieldLength() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    List<SimpleBean> lengthExceedingBeans = getListOfBeansExceedingFieldLength();
    runtimeService.setVariable(instance.getId(), "simpleBeans", lengthExceedingBeans);

    VariableInstance beansVariable = runtimeService.createVariableInstanceQuery().singleResult();
    assertNotNull(beansVariable);
    assertEquals(VariableType.SPIN.getName(), beansVariable.getSerializerName());

    List<SimpleBean> returnedBeans = (List<SimpleBean>) beansVariable.getTypedValue();
    assertNotNull(returnedBeans);
    assertTrue(returnedBeans instanceof ArrayList);
    assertListsEqual(lengthExceedingBeans, returnedBeans);

    // currently internal API
    VariableInstanceEntity variableEntity = (VariableInstanceEntity) beansVariable;
    String rawJson = (String) variableEntity.getSerializedValue().getTypedValue();
    JSONAssert.assertEquals(toExpectedJsonArray(lengthExceedingBeans), rawJson, true);
  }

  protected List<SimpleBean> getListOfBeansExceedingFieldLength() {
    // field TEXT is varchar(4000), i.e. 4000 byte
    int textFieldLength = DbSqlSessionFactory.ACT_RU_VARIABLE_TEXT_LENGTH;

    SimpleBean bean = new SimpleBean("a String", 42, true);
    String expectedJson = bean.toExpectedJsonString();

    int expectedBytesPerBean = expectedJson.getBytes().length;
    int beansToExceedFieldLength = (textFieldLength / expectedBytesPerBean) + 1;

    List<SimpleBean> lengthExceedingBeans = new ArrayList<SimpleBean>();
    for (int i = 0; i < beansToExceedFieldLength; i++) {
      lengthExceedingBeans.add(new SimpleBean("a String", 42, true));
    }

    return lengthExceedingBeans;
  }

  public void testFailForNonExistingSerializationFormat() {
    ProcessEngineConfigurationImpl engineConfig =
        (ProcessEngineConfigurationImpl) ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("camunda.cfg.xml");

    engineConfig.setDefaultSerializationFormat("an unknown data format");

    try {
      engineConfig.buildProcessEngine();
      fail("Exception expected");
    } catch (ProcessEngineException e) {
      // happy path
    }
  }

  public void testConfigureSerializableFormat() {
    ProcessEngineConfigurationImpl engineConfig =
        (ProcessEngineConfigurationImpl) ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("camunda.cfg.xml");

    ProcessEngine engine = engineConfig.setDefaultSerializationFormat("java serializable").buildProcessEngine();
    String deploymentId = engine.getRepositoryService().createDeployment()
        .addClasspathResource(ONE_TASK_PROCESS).deploy().getId();

    ProcessInstance instance = engine.getRuntimeService().startProcessInstanceByKey("oneTaskProcess");
    engine.getRuntimeService().setVariable(instance.getId(), "serializableVar", new DummySerializable());

    VariableInstance variable = engine.getRuntimeService().createVariableInstanceQuery().singleResult();

    assertEquals(VariableType.SERIALIZABLE.getName(), variable.getSerializerName());

    engine.getRepositoryService().deleteDeployment(deploymentId, true);
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testVariableValueCaching() {
    final ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    processEngineConfiguration.getCommandExecutorTxRequired().execute(new Command<Void>() {

      @Override
      public Void execute(CommandContext commandContext) {
        SimpleBean bean = new SimpleBean("a String", 42, true);
        runtimeService.setVariable(instance.getId(), "simpleBean", bean);

        Object returnedBean = runtimeService.getVariable(instance.getId(), "simpleBean");
        assertSame(bean, returnedBean);

        return null;
      }
    });

    VariableInstance variableInstance = runtimeService.createVariableInstanceQuery().singleResult();

    Object returnedBean = variableInstance.getTypedValue();
    Object theSameReturnedBean = variableInstance.getTypedValue();
    assertSame(returnedBean, theSameReturnedBean);
  }

  public void testApplicationOfGlobalConfiguration() throws JSONException {
    DataFormats.jsonTreeGlobal().mapper().config("aKey", "aValue");

    SpinVariableTypeResolver resolver = new SpinVariableTypeResolver();
    SpinSerializationType variableType = (SpinSerializationType) resolver.getTypeForSerializationFormat(JSON_FORMAT_NAME);

    DataFormats.jsonTreeGlobal().mapper().config("aKey", null);

    JsonJacksonTreeDataFormat dataFormat = (JsonJacksonTreeDataFormat) variableType.getDefaultDataFormat();
    assertNotSame("The variable type should not use the global data format instance",
        DataFormats.jsonTreeGlobal(), dataFormat);

    assertEquals("The global configuration should have been applied to the variable type's format",
        "aValue", dataFormat.mapper().getConfiguration().get("aKey"));
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testGetSerializedVariableValue() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    SimpleBean bean = new SimpleBean("a String", 42, true);
    runtimeService.setVariable(instance.getId(), "simpleBean", bean);

    VariableInstance beanVariable = runtimeService.createVariableInstanceQuery().singleResult();
    assertNotNull(beanVariable);
    assertEquals(VariableType.SPIN.getName(), beanVariable.getSerializerName());
    assertEquals(Object.class.getSimpleName(), beanVariable.getValueTypeName());
    assertTrue(beanVariable.storesCustomObjects());

    SerializedObjectVariableValue serializedVariable = beanVariable.getSerializedValue();

    Map<String, Object> config = serializedVariable.getConfig();
    assertEquals(2, config.size());
    assertEquals(JSON_FORMAT_NAME, config.get(VariableType.SPIN_TYPE_DATA_FORMAT_ID));
    assertEquals(bean.getClass().getCanonicalName(), config.get(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE));

    String variableAsJson = (String) serializedVariable.getTypedValue();
    JSONAssert.assertEquals(bean.toExpectedJsonString(), variableAsJson, true);
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSetSerializedVariableValue() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    SimpleBean bean = new SimpleBean("a String", 42, true);
    String beanAsJson = bean.toExpectedJsonString();

    Map<String, Object> variableConfig = new HashMap<String, Object>();
    variableConfig.put(VariableType.SPIN_TYPE_DATA_FORMAT_ID, JSON_FORMAT_NAME);
    variableConfig.put(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, bean.getClass().getCanonicalName());
    runtimeService.setVariableFromSerialized(instance.getId(), "simpleBean", beanAsJson,
        VariableType.SPIN.getName(), variableConfig);

    VariableInstance variableInstance = runtimeService.createVariableInstanceQuery().singleResult();

    SimpleBean returnedBean = (SimpleBean) variableInstance.getTypedValue();
    assertBeansEqual(bean, returnedBean);

    SerializedObjectVariableValue serializedVariable = variableInstance.getSerializedValue();
    JSONAssert.assertEquals(beanAsJson, (String) serializedVariable.getTypedValue(), true);
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSetSerializedVariableValueWithoutConfig() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    SimpleBean bean = new SimpleBean("a String", 42, true);
    String beanAsJson = bean.toExpectedJsonString();

    try {
      runtimeService.setVariableFromSerialized(instance.getId(), "simpleBean", beanAsJson,
          VariableType.SPIN.getName(), null);
      fail();
    } catch (BadUserRequestException e) {
      // expected
    }

    try {
      runtimeService.setVariableFromSerialized(instance.getId(), "simpleBean", beanAsJson,
          VariableType.SPIN.getName(), new HashMap<String, Object>());
      fail();
    } catch (BadUserRequestException e) {
      // expected
    }
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSetSerializedVariableValueWithMismatchingTypeConfig() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    SimpleBean bean = new SimpleBean("a String", 42, true);
    String beanAsJson = bean.toExpectedJsonString();

    Map<String, Object> variableConfig = new HashMap<String, Object>();
    variableConfig.put(VariableType.SPIN_TYPE_DATA_FORMAT_ID, JSON_FORMAT_NAME);
    variableConfig.put(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, "a non-sensical class name");

    runtimeService.setVariableFromSerialized(instance.getId(), "simpleBean", beanAsJson,
        VariableType.SPIN.getName(), variableConfig);

    assertCannotRetrieveVariable(instance.getId(), "simpleBean");
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSetSerializedVariableValueWithMismatchingDataFormatConfig() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    SimpleBean bean = new SimpleBean("a String", 42, true);
    String beanAsJson = bean.toExpectedJsonString();

    Map<String, Object> variableConfig = new HashMap<String, Object>();
    variableConfig.put(VariableType.SPIN_TYPE_DATA_FORMAT_ID, "a non-existing data format");
    variableConfig.put(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, bean.getClass().getCanonicalName());

    try {
      runtimeService.setVariableFromSerialized(instance.getId(), "simpleBean", beanAsJson,
          VariableType.SPIN.getName(), variableConfig);
      fail();
    } catch (BadUserRequestException e) {
      // expected
    }
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSetSerializedVariableValueExceedingFieldLength() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    List<SimpleBean> lengthExceedingBeans = getListOfBeansExceedingFieldLength();
    String beansAsJson = toExpectedJsonArray(lengthExceedingBeans);

    Map<String, Object> variableConfig = new HashMap<String, Object>();
    variableConfig.put(VariableType.SPIN_TYPE_DATA_FORMAT_ID, JSON_FORMAT_NAME);
    variableConfig.put(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, "java.util.ArrayList<" + SimpleBean.class.getCanonicalName() + ">");

    runtimeService.setVariableFromSerialized(instance.getId(), "simpleBeans", beansAsJson,
        VariableType.SPIN.getName(), variableConfig);

    List<SimpleBean> returnedBeans = (List<SimpleBean>) runtimeService.getVariable(instance.getId(), "simpleBeans");
    assertListsEqual(lengthExceedingBeans, returnedBeans);
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSetSerializedVariableValueWithConfigOfWrongType() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    SimpleBean bean = new SimpleBean("a String", 42, true);
    String beanAsJson = bean.toExpectedJsonString();

    Map<String, Object> configuration = new HashMap<String, Object>();
    configuration.put(VariableType.SPIN_TYPE_DATA_FORMAT_ID, 42);
    configuration.put(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, true);

    try {
      runtimeService.setVariableFromSerialized(instance.getId(), "simpleBean", beanAsJson,
          VariableType.SPIN.getName(), configuration);
      fail();
    } catch (BadUserRequestException e) {
      // expected
    }
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSetSerializedVariableNullValue() throws JSONException {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    // despite null value, we still require a consistent configuration map
    Map<String, Object> variableConfig = new HashMap<String, Object>();
    variableConfig.put(VariableType.SPIN_TYPE_DATA_FORMAT_ID, JSON_FORMAT_NAME);
    variableConfig.put(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, "java.lang.Object");

    runtimeService.setVariableFromSerialized(instance.getId(), "simpleBean", null,
        VariableType.SPIN.getName(), variableConfig);

    VariableInstance variable = runtimeService.createVariableInstanceQuery().singleResult();

    assertNotNull(variable);
    assertEquals("simpleBean", variable.getName());
    assertNull(variable.getTypedValue());

    SerializedObjectVariableValue serializedValue = variable.getSerializedValue();
    assertNotNull(serializedValue);
    assertNull(serializedValue.getTypedValue());
    assertEquals(2, serializedValue.getConfig().size());

    assertEquals(JSON_FORMAT_NAME, serializedValue.getConfig().get(VariableType.SPIN_TYPE_DATA_FORMAT_ID));
    assertEquals("java.lang.Object", serializedValue.getConfig().get(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE));

  }

  @Deployment(resources = ONE_TASK_PROCESS)
  public void testSerializedVarValue() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

    // given

    // the serialized value of a bean
    SimpleBean simpleBean = new SimpleBean("someString", 1, true);
    SerializedObjectVariableValue serializedValue = SerializedVariableValueBuilder.create()
      .value(simpleBean.toExpectedJsonString())
      .configValue(VariableType.SPIN_TYPE_DATA_FORMAT_ID, JSON_FORMAT_NAME)
      .configValue(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, SimpleBean.class.getCanonicalName())
    .done();

    // when
    runtimeService.setVariable(instance.getId(), "beanVariable", serializedValue);

    // then
    SimpleBean actualValue = (SimpleBean) runtimeService.getVariable(instance.getId(), "beanVariable");
    assertEquals(simpleBean, actualValue);
  }

  public void testDisabledDeserialization() {
    // given
    Task task = taskService.newTask();
    taskService.saveTask(task);

    Map<String, Object> variableConfiguration = new HashMap<String, Object>();
    variableConfiguration.put(VariableType.SPIN_TYPE_CONFIG_ROOT_TYPE, "an.unavailable.JavaClass");
    variableConfiguration.put(VariableType.SPIN_TYPE_DATA_FORMAT_ID, JSON_FORMAT_NAME);

    taskService.setVariableLocalFromSerialized(task.getId(), "variableWithoutClass",
        "{\"aKey\" : \"aValue\"}", VariableType.SPIN.getName(), variableConfiguration);

    // when
    VariableInstance instance =
        runtimeService.createVariableInstanceQuery().disableCustomObjectDeserialization().singleResult();

    // then
    assertNotNull(instance);
    assertNotNull(instance.getSerializedValue());
    assertNotNull(instance.getSerializedValue().getTypedValue());

    // delete task
    taskService.deleteTask(task.getId(), true);
  }

  protected void assertCannotRetrieveVariable(String scopeId, String variableName) {
    try {
      runtimeService.getVariable(scopeId, variableName);
      fail();
    } catch (ProcessEngineException e) {
      // expected
    }

    VariableInstance variableInstance = runtimeService.createVariableInstanceQuery().singleResult();

    assertNotNull(variableInstance);
    assertNotNull(variableInstance.getErrorMessage());
    assertNull(variableInstance.getTypedValue());
  }

  protected void assertListsEqual(List<SimpleBean> expectedBeans, List<SimpleBean> actualBeans) {
    assertEquals(expectedBeans.size(), actualBeans.size());

    for (int i = 0; i < actualBeans.size(); i++) {
      SimpleBean actualBean = actualBeans.get(i);
      SimpleBean expectedBean = expectedBeans.get(i);

      assertBeansEqual(expectedBean, actualBean);
    }
  }

  protected void assertBeansEqual(SimpleBean expectedBean, SimpleBean actualBean) {
    assertEquals(expectedBean.getStringProperty(), actualBean.getStringProperty());
    assertEquals(expectedBean.getIntProperty(), actualBean.getIntProperty());
    assertEquals(expectedBean.getBooleanProperty(), actualBean.getBooleanProperty());
  }

  protected String toExpectedJsonArray(List<SimpleBean> beans) {
    StringBuilder jsonBuilder = new StringBuilder();

    jsonBuilder.append("[");
    for (int i = 0; i < beans.size(); i++) {
      jsonBuilder.append(beans.get(i).toExpectedJsonString());

      if (i != beans.size() - 1)  {
        jsonBuilder.append(", ");
      }
    }
    jsonBuilder.append("]");

    return jsonBuilder.toString();
  }
}
