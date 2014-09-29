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

package org.camunda.bpm.engine.impl.persistence.entity;

import java.util.Date;

import org.camunda.bpm.engine.history.HistoricVariableUpdate;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.entitymanager.DbEntityManager;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.variable.serializer.ValueFields;
import org.camunda.bpm.engine.impl.variable.serializer.ValueSerializer;
import org.camunda.bpm.engine.variable.value.VariableValue;


/**
 * @author Tom Baeyens
 */
public class HistoricDetailVariableInstanceUpdateEntity extends HistoricVariableUpdateEventEntity implements ValueFields, HistoricVariableUpdate {

  private static final long serialVersionUID = 1L;

  protected ValueSerializer valueSerializer;
  protected ByteArrayEntity byteArrayValue;

  protected VariableValue cachedValue;

  protected String errorMessage;

  public Object getValue() {
    VariableValue variableValue = getVariableValue();
    if(variableValue != null) {
      return variableValue.getValue();
    } else {
      return null;
    }
  }

  public VariableValue getVariableValue() {
    if (errorMessage == null && (!valueSerializer.isCachable() || cachedValue==null)) {
      try {
        cachedValue = valueSerializer.getValue(this);

      } catch(RuntimeException e) {
        // catch error message
        errorMessage = e.getMessage();

        //re-throw the exception
        throw e;
      }
    }
    return cachedValue;
  }

  public void delete() {

    DbEntityManager dbEntityManger = Context
        .getCommandContext()
        .getDbEntityManager();

    dbEntityManger.delete(this);

    if (byteArrayId != null) {
      // the next apparently useless line is probably to ensure consistency in the DbSqlSession
      // cache, but should be checked and docced here (or removed if it turns out to be unnecessary)
      // @see also HistoricVariableInstanceEntity
      getByteArrayValue();
      Context
        .getCommandContext()
        .getByteArrayManager()
        .deleteByteArrayById(byteArrayId);
    }
  }

  public String getVariableTypeName() {
    return (valueSerializer!=null ? valueSerializer.getSerializerName() : null);
  }

  public String getValueTypeName() {
    return (valueSerializer != null ? valueSerializer.getSerializerName() : null);
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  // byte array value /////////////////////////////////////////////////////////

  // i couldn't find a easy readable way to extract the common byte array value logic
  // into a common class.  therefor it's duplicated in VariableInstanceEntity,
  // HistoricVariableInstance and HistoricDetailVariableInstanceUpdateEntity

  public String getByteArrayValueId() {
    return byteArrayId;
  }

  public ByteArrayEntity getByteArrayValue() {
    if ((byteArrayValue == null) && (byteArrayId != null)) {
      // no lazy fetching outside of command context
      if(Context.getCommandContext() != null) {
        byteArrayValue = Context
          .getCommandContext()
          .getDbEntityManager()
          .selectById(ByteArrayEntity.class, byteArrayId);
      }
    }
    return byteArrayValue;
  }

  public void setByteArrayValue(byte[] bytes) {
    ByteArrayEntity byteArrayValue = null;
    if (this.byteArrayId!=null) {
      getByteArrayValue();
      Context
        .getCommandContext()
        .getByteArrayManager()
       .deleteByteArrayById(this.byteArrayId);
    }
    if (bytes!=null) {
      byteArrayValue = new ByteArrayEntity(bytes);
      Context
        .getCommandContext()
        .getDbEntityManager()
        .insert(byteArrayValue);
    }
    this.byteArrayValue = byteArrayValue;
    if (byteArrayValue != null) {
      this.byteArrayId = byteArrayValue.getId();
    } else {
      this.byteArrayId = null;
    }
  }

  protected void deleteByteArrayValue() {
    if (byteArrayId != null) {
      // the next apparently useless line is probably to ensure consistency in the DbSqlSession
      // cache, but should be checked and docced here (or removed if it turns out to be unnecessary)
      getByteArrayValue();
      Context
        .getCommandContext()
        .getByteArrayManager()
        .deleteByteArrayById(this.byteArrayId);
    }
  }

  public String getName() {
    return getVariableName();
  }

  // getters and setters //////////////////////////////////////////////////////

  public Date getTime() {
    return timestamp;
  }

  public ValueSerializer getVariableType() {
    return valueSerializer;
  }

  public VariableValue getCachedValue() {
    return cachedValue;
  }

  public void setCachedValue(VariableValue cachedValue) {
    this.cachedValue = cachedValue;
  }

  public void setVariableType(ValueSerializer valueSerializer) {
    this.valueSerializer = valueSerializer;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()
           + "[byteArrayValue=" + byteArrayValue
           + ", variableName=" + variableName
           + ", variableInstanceId=" + variableInstanceId
           + ", revision=" + revision
           + ", variableTypeName=" + variableTypeName
           + ", longValue=" + longValue
           + ", doubleValue=" + doubleValue
           + ", textValue=" + textValue
           + ", textValue2=" + textValue2
           + ", byteArrayId=" + byteArrayId
           + ", activityInstanceId=" + activityInstanceId
           + ", eventType=" + eventType
           + ", executionId=" + executionId
           + ", id=" + id
           + ", processDefinitionId=" + processInstanceId
           + ", processInstanceId=" + processInstanceId
           + ", taskId=" + taskId
           + ", timestamp=" + timestamp
           + "]";
  }

}
