/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.events.api.metric.os;

import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.utilities.os.OSType;

import java.io.File;

/**
 * Represents the definition of a metric whose values can be obtained either by reading and parsing the content of
 * a file, or by executing an external OS command on the OSSourceBase metric source bound to this definition. Once the
 * file content is read, or the command is successfully executed and its stdout is collected, the content so produced
 * must be passed to the appropriate parseFileContent()/parseCommandOutput() method, which possesses the knowledge
 * to extract the associated property instance from the content.
 *
 * The metric definition instance does not perform the read I/O operations internally. It delegates this task instead to
 * to the calling layer. This is done for several reasons: a file needed by multiple metric definitions is read only
 * once, and the content can be shared among multiple metric definitions, instead of having each individual metric
 * definition perform identical I/O operations. This becomes important when the file resides on a remote metric source,
 * such as a RemoteOS. Another reason this approach is preferred is that multiple metrics that share a file content
 * generate values corresponding to the same precise timestamp - the moment the file was read - instead of generating
 * values corresponding to readings performed at moments in time that are slightly apart. This increases the precision
 * of readings.
 *
 * OSMetricDefinition works for metrics that can be collected from LocalOS and RemoteOS metric sources.
 *
 * The situation when a metric may be obtained from both a file and a command execution is valid. If this is the case,
 * the file should be preferred, for performance reasons, but ultimately is up to the calling layer to decide.
 *
 * Since 1.2.2, the OSMetricDefinition implementation comes with support for maintaining metric definition state across
 * successive collections. This is useful when the calculation of a value require access to previous state, as it is
 * the case for rates. More details in OSMetricDefinitionBase javadoc. TODO: consider migrating this mechanism to more
 * generic layers, such as MetricDefinitionBase. This way we have a built-in way of calculating rates.
 *
 * @see OSMetricDefinitionBase
 * @see OSSourceBase
 * @see LocalOS
 * @see RemoteOS
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/5/17
 */
public interface OSMetricDefinition extends MetricDefinition {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @param osType a valid OSType.
     *
     * @return a File instance that represents a file in the target's metric source filesystem. A null value means
     * that the metric cannot be read from a file.
     */
    File getSourceFile(OSType osType);

    /**
     * @param osType a valid OSType.
     *
     * @param sourceFileContent the content of the file designated by this metric as the source for readings, via
     *                          getSourceFile() command. Can be null, which means that the metric is not available
     *                          via this method on the target filesystem, In this case, the method must be prepared
     *                          this situation by manufacturing a null-value property.
     *
     * @return the metric value extracted from file content, assumed generated by reading the file returned by
     * getSourceFile(), possibly factoring in previous readings. If previous readings are necessary, the implementation
     * must maintain the required state, internally. If a value cannot be successfully produced because of invalid
     * content, or because the content is null, the method must return a valid Property instance containing a null
     * value. The method must not throw an exception in this case. The implementation should also log as WARN more
     * details on why collection failed, in case of a non-null argument.
     *
     * @exception IllegalArgumentException if the previous reading instance does not have the appropriate type.
     */
    Property parseSourceFileContent(OSType osType, byte[] sourceFileContent);

    /**
     * @param osType a valid OSType.
     *
     * @return the command to be executed to obtain the metric value, on the OS corresponding to the given osType.
     * The stdout generated by the command has to be parsed to extract the individual metric value. The metric
     * definition implementation contains the parsing logic. If the metric is not available on the specified OS, or if
     * it cannot be read via an external command, the method will return null.
     *
     * @see OSMetricDefinition#parseCommandOutput(OSType, String)
     */
    String getCommand(OSType osType);

    /**
     * @param osType a valid OSType.
     *
     * @param commandExecutionStdout the stdout generated by the execution of the command indicated by getCommand().
     *                               It may be null in case the metric is not available on the target system, and the
     *                               method must be prepared to handle that, by manufacturing a null-value property.
     *
     * @return the metric value extracted from the command stdout, assumed to be produced by the command returned by
     * getCommand(), possibly factoring in previous readings. If previous readings are necessary, the implementation
     * must maintain the required state, internally. If a value cannot be successfully extracted because of invalid
     * command output, or even if the command output is null, the method must return a valid Property instance
     * containing a null value. The method must not throw an exception in this case. The implementation should also log
     * as WARN more details on why collection failed, in case of a non-null argument.
     *
     * @exception IllegalArgumentException if the previous reading instance does not have the appropriate type.
     */
    Property parseCommandOutput(OSType osType, String commandExecutionStdout);

}
