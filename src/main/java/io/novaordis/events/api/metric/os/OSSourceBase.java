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
import io.novaordis.events.api.metric.MetricSourceBase;
import io.novaordis.events.api.metric.MetricSourceException;
import io.novaordis.utilities.address.Address;
import io.novaordis.utilities.address.OSAddress;
import io.novaordis.utilities.os.NativeExecutionResult;
import io.novaordis.utilities.os.NativeExecutor;
import io.novaordis.utilities.os.OSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A metric source for which all metrics can be obtained by reading and parsing content of files that are available to
 * the corresponding OS, or by executing OS commands and parsing the result.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/5/17
 */
public abstract class OSSourceBase extends MetricSourceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(OSSourceBase.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    //
    // the local OS instance or an OS running on a remote host
    //
    private NativeExecutor nativeExecutor;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected OSSourceBase(Address address) {

        super(address);
    }

    /**
     * Used by subclasses that build the Address instance internally
     */
    protected OSSourceBase() {

        super(null);
    }

    // MetricSourceBase overrides --------------------------------------------------------------------------------------

    @Override
    public OSAddress getAddress() {

        Address a = super.getAddress();
        return (OSAddress)a;
    }

    @Override
    protected List<Property> collect(List<MetricDefinition> metricDefinitions) throws MetricSourceException {

        if (!isStarted()) {

            throw new IllegalStateException(this + " not started");
        }

        if (log.isTraceEnabled()) {

            log.trace(this + " collecting " + metricDefinitions);
        }

        OSType thisOs = OSType.getCurrent();

        Map<String, String> commandOutputs = new HashMap<>();
        List<OSMetricDefinition> osMetricDefinitions = new ArrayList<>();

        for(MetricDefinition d: metricDefinitions) {

            OSMetricDefinition osmd = insureOSMetricDefinition(d);

            String command = osmd.getCommand(thisOs);

            //
            // command may be null, when we know the metric definition is not available for a specific O/S
            //

            if (command == null) {

                log.debug(d + " not available on " + OSType.getCurrent());
            }
            else {

                log.debug(osmd + " is collected on " + OSType.getCurrent() + " by executing \"" + command  + "\"") ;

                commandOutputs.put(command, null);
            }

            osMetricDefinitions.add(osmd);
        }

        //
        // execute all commands
        //

        if (log.isTraceEnabled()) {

            log.trace(this + " will execute the following commands: " + commandListToString(commandOutputs.keySet()));
        }

        //
        // TODO if more than one command, execute in parallel
        //

        for(String command: commandOutputs.keySet()) {

            //
            // execute the command and associate the command output with the command
            //

            String stdout = execute(command);

            commandOutputs.put(command, stdout);
        }

        //
        // process output
        //

        List<Property> results = new ArrayList<>();

        for(OSMetricDefinition osmd: osMetricDefinitions) {

            String commandKey = osmd.getCommand(thisOs);
            String commandOutput = commandOutputs.get(commandKey);

            //
            // the command output will be null in case the metric is not available on the current O/S so
            // parseCommandOutput() implementations must be able to handle that
            //
            Property p = osmd.parseCommandOutput(thisOs, commandOutput, null);

            results.add(p);
        }

        return results;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    void setNativeExecutor(NativeExecutor ne) {

        this.nativeExecutor = ne;
    }

    /**
     * Execute the command using the native executor and return the execution stdout. The stdout is in most cases a
     * multi-line string. If the execution fails for any reason (native executor unchecked command, native executor
     * checked command, non-zero exit code), or no stdout is returned, return null and log human interpretable
     * warnings. The method mustn't knowingly throw any unchecked exception.
     */
    String execute(String command) {

        if (log.isTraceEnabled()) {

            log.trace(this + " executing \"" + command + "\" on " + nativeExecutor);
        }

        String stdout = null;

        try {

            NativeExecutionResult r = nativeExecutor.execute(command);

            stdout = r.getStdout();
            String stderr = r.getStderr();

            if (r.isSuccess()) {

                if (stdout == null) {

                    log.warn("\"" + command + "\" succeeded but returned no stdout");
                }
            }
            else {

                int exitCode = r.getExitCode();

                String logStdout = stdout;
                stdout = null;

                String s = "\"" + command + "\" execution failed with exit code " + exitCode + ":";

                if (logStdout == null) {

                    s += "\n\nno stdout";
                }
                else {

                    s += "\n\nstdout:\n\n" + logStdout;
                }

                if (stderr == null) {

                    s += "\n\nno stderr";
                }
                else {

                    s += "\n\nstderr:\n\n" + stderr;
                }

                log.warn(s);
            }
        }
        catch (Exception e) {

            //
            // command fails in an unusual way
            //

            log.warn("\"" + command + "\" execution failed with exception (see below)", e);
        }

        return stdout;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    protected NativeExecutor getNativeExecutor() {

        return nativeExecutor;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    private String commandListToString(Set<String> commands) {

        if (commands.isEmpty()) {

            return "N/A";
        }

        String s = "";

        for(Iterator<String> i = commands.iterator(); i.hasNext(); ) {

            s += "\"" + i.next() + "\"";

            if (i.hasNext()) {

                s += ", ";
            }
        }

        return s;
    }

    /**
     * The method insures the instance is a OSMetricDefinition and performs the cast, or throws an
     * IllegalArgumentException otherwise.
     */
    private OSMetricDefinition insureOSMetricDefinition(MetricDefinition d) {

        if (!(d instanceof OSMetricDefinition)) {

            throw new IllegalArgumentException(this + " does not handle " + d + ", an " +
                    OSMetricDefinition.class.getSimpleName() + " is expected");
        }

        return (OSMetricDefinition)d;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
