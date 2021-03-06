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
package org.camunda.bpm.engine.runtime;

import org.camunda.bpm.engine.query.Query;

/**
 * @author Roman Smirnov
 *
 */
public interface CaseSentryPartQuery extends Query<CaseSentryPartQuery, CaseSentryPart> {

  CaseSentryPartQuery caseSentryPartId(String caseSentryPartId);

  CaseSentryPartQuery caseInstanceId(String caseInstanceId);

  CaseSentryPartQuery caseExecutionId(String caseExecutionId);

  CaseSentryPartQuery sentryId(String sentryId);

  CaseSentryPartQuery type(String type);

  CaseSentryPartQuery sourceCaseExecutionId(String sourceCaseExecutionId);

  CaseSentryPartQuery standardEvent(String standardEvent);

  CaseSentryPartQuery satisfied();

  CaseSentryPartQuery orderByCaseSentryId();

  CaseSentryPartQuery orderByCaseInstanceId();

  CaseSentryPartQuery orderByCaseExecutionId();

  CaseSentryPartQuery orderBySentryId();

  CaseSentryPartQuery orderBySource();

}
