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
package org.camunda.bpm.engine.test.bpmn.servicetask.util;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.connect.impl.AbstractConnector;

/**
 * @author Daniel Meyer
 *
 */
public class TestConnector extends AbstractConnector<TestConnectorRequest, TestConnectorRespose> {

  public static Map<String, Object> requestParameters;
  public static Map<String, Object> responseParameters = new HashMap<String, Object>();

  public TestConnectorRequest createRequest() {
    return new TestConnectorRequest(this);
  }

  public String getId() {
    return "testConnector";
  }

  public TestConnectorRespose execute(TestConnectorRequest req) {
    // capture request parameters
    requestParameters = req.getRequestParameters();

    TestRequestInvocation testRequestInvocation = new TestRequestInvocation(null, req, requestInterceptors);

    try {
      testRequestInvocation.proceed();
      // use response parameters
      return new TestConnectorRespose(responseParameters);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
