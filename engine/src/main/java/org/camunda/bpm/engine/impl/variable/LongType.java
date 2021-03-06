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
package org.camunda.bpm.engine.impl.variable;

import org.camunda.bpm.engine.delegate.ProcessEngineVariableType;




/**
 * @author Tom Baeyens
 */
public class LongType extends PrimitiveVariableType {

  public String getTypeName() {
    return ProcessEngineVariableType.LONG.getName();
  }

  public boolean isCachable() {
    return true;
  }

  public Object getValue(ValueFields valueFields) {
    return valueFields.getLongValue();
  }

  public void setValue(Object value, ValueFields valueFields) {
    valueFields.setLongValue((Long) value);
    if (value!=null) {
      valueFields.setTextValue(value.toString());
    } else {
      valueFields.setTextValue(null);
    }
  }

  public boolean isAbleToStore(Object value) {
    if (value==null) {
      return true;
    }
    return Long.class.isAssignableFrom(value.getClass())
           || long.class.isAssignableFrom(value.getClass());
  }

  public String getTypeNameForValue(ValueFields valueFields) {
    // typename independent of value
    return Long.class.getSimpleName();
  }

}
