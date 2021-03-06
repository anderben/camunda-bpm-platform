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
package org.camunda.bpm.engine.impl.cmmn.transformer;

import java.util.List;

import org.camunda.bpm.engine.impl.cmmn.handler.DefaultCmmnElementHandlerRegistry;
import org.camunda.bpm.engine.impl.core.transformer.Transformer;
import org.camunda.bpm.engine.impl.el.ExpressionManager;

/**
 * @author Roman Smirnov
 *
 */
public class CmmnTransformer implements Transformer<CmmnTransform> {

  protected ExpressionManager expressionManager;
  protected CmmnTransformFactory factory;
  protected List<CmmnTransformListener> transformListener;
  protected DefaultCmmnElementHandlerRegistry cmmnElementHandlerRegistry;

  public CmmnTransformer(ExpressionManager expressionManager, DefaultCmmnElementHandlerRegistry handlerRegistry, CmmnTransformFactory factory) {
    this.expressionManager = expressionManager;
    this.factory = factory;
    this.cmmnElementHandlerRegistry = handlerRegistry;
  }

  public CmmnTransform createTransform() {
    return factory.createTransform(this);
  }

  public ExpressionManager getExpressionManager() {
    return expressionManager;
  }

  public void setExpressionManager(ExpressionManager expressionManager) {
    this.expressionManager = expressionManager;
  }

  public CmmnTransformFactory getFactory() {
    return factory;
  }

  public void setFactory(CmmnTransformFactory factory) {
    this.factory = factory;
  }

  public List<CmmnTransformListener> getTransformListener() {
    return transformListener;
  }

  public void setTransformListener(List<CmmnTransformListener> transformListener) {
    this.transformListener = transformListener;
  }

  public DefaultCmmnElementHandlerRegistry getCmmnElementHandlerRegistry() {
    return cmmnElementHandlerRegistry;
  }

  public void setCmmnElementHandlerRegistry(DefaultCmmnElementHandlerRegistry registry) {
    this.cmmnElementHandlerRegistry = registry;
  }

}
