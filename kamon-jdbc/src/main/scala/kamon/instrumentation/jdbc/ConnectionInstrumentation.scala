/* =========================================================================================
 * Copyright © 2013-2015 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.instrumentation.jdbc

import java.lang.instrument.Instrumentation
import java.sql.{ PreparedStatement, SQLException }
import java.util.concurrent.Callable

import kamon.util.instrumentation.KamonInstrumentation
import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.description.`type`.TypeDescription
import net.bytebuddy.implementation.MethodDelegation._
import net.bytebuddy.implementation.bind.annotation._
import net.bytebuddy.matcher.ElementMatcher.Junction
import net.bytebuddy.matcher.ElementMatchers._

class ConnectionInstrumentation extends KamonInstrumentation {

  private val Connection: Junction[TypeDescription] = isSubTypeOf(typePool.describe("java.sql.Connection").resolve())

  override def register(instrumentation: Instrumentation): Unit = {
    new AgentBuilder.Default()
      .`type`(Connection, is(ClassLoader.getSystemClassLoader))
      .transform(connectionTransformer)
      .installOn(instrumentation)
  }

  def connectionTransformer = withTransformer { (builder, typeDescription) ⇒
    builder
      .method(named("prepareStatement").and(TakesArguments))
      .intercept(to(ConnectionInterceptor).filter(NotDeclaredByObject))
  }
}

object ConnectionInterceptor {
  @RuntimeType
  @throws[SQLException]
  def prepareStatement(@SuperCall callable: Callable[PreparedStatement], @Argument(0) sql: String): Any = {
    val prepareStatement = callable.call().asInstanceOf[PreparedStatementExtension]
    prepareStatement.setSql(sql)
    prepareStatement
  }
}