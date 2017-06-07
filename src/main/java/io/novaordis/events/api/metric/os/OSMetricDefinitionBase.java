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

import io.novaordis.events.api.event.DoubleProperty;
import io.novaordis.events.api.event.LongProperty;
import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.measure.MeasureUnit;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.events.api.metric.MetricDefinitionBase;

import java.util.regex.Pattern;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/6/17
 */
public abstract class OSMetricDefinitionBase extends MetricDefinitionBase implements OSMetricDefinition {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    protected static Class TYPE;
    protected static String DESCRIPTION;
    protected static MeasureUnit BASE_UNIT;
    protected static String LABEL;
    protected static String LINUX_COMMAND;
    protected static Pattern LINUX_PATTERN;
    protected static String MAC_COMMAND;
    protected static Pattern MAC_PATTERN;
    protected static String WINDOWS_COMMAND;
    protected static Pattern WINDOWS_PATTERN;

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    protected OSMetricDefinitionBase(OSSource source) {

        super(source);
    }

    // MetricDefinition implementation ---------------------------------------------------------------------------------

    /**
     * For all OS metrics, the ID is conventionally the simple name of the class implementing the metric definition.
     */
    @Override
    public String getId() {

        return getClass().getSimpleName();
    }

    @Override
    public Class getType() {

        return TYPE;
    }

    @Override
    public MeasureUnit getBaseUnit() {

        return BASE_UNIT;
    }

    @Override
    public String getDescription() {

        return DESCRIPTION;
    }

    // OSMetricDefinition implementation -------------------------------------------------------------------------------

    @Override
    public String getCommand() {

        OSType t = OSType.getCurrent();

        if (OSType.LINUX.equals(t)) {

            return LINUX_COMMAND;
        }
        else if (OSType.MAC.equals(t)) {

            return MAC_COMMAND;
        }
        else if (OSType.WINDOWS.equals(t)) {

            return WINDOWS_COMMAND;
        }
        else {

            throw new IllegalStateException(t + " not supported yet");
        }
    }

    @Override
    public Property parseCommandOutput(String commandExecutionStdout) {

        Property result = getPropertyInstance(getId(), getType(), getBaseUnit());

        String methodName = null;
        Object value = null;
        boolean knownOS = true;

        OSType t = OSType.getCurrent();

        try {

            if (OSType.LINUX.equals(t)) {

                methodName = "parseLinuxCommandOutput";
                value = parseLinuxCommandOutput(commandExecutionStdout);
            }
            else if (OSType.MAC.equals(t)) {

                methodName = "parseMacCommandOutput";
                value = parseMacCommandOutput(commandExecutionStdout);
            }
            else if (OSType.WINDOWS.equals(t)) {

                methodName = "parseWindowsCommandOutput";
                value = parseWindowsCommandOutput(commandExecutionStdout);
            }
            else {

                knownOS = false;
                log.warn("OS type " + t + " not supported yet");
            }

            //
            // the method must always return a non-null value, if null is seen here, it is an implementation error
            //

            if (knownOS && value == null) {

                log.warn(getClass().getName() + "." + methodName + "(...) incorrectly implemented, returning null");
            }

        }
        catch(Exception e) {

            log.warn("failed to parse \"" + getCommand() + "\" output: \n\n" + commandExecutionStdout + "\n", e);
        }

        return result;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected String getSimpleLabel() {

        return LABEL;
    }

    //
    // Command output parsing for various OSes -------------------------------------------------------------------------
    //

    /**
     * @return the value corresponding to this metric definition, as extracted from the output of the associated
     * MacOS command. The type of the value must match TYPE for this class, as declared above, otherwise the calling
     * layer may throw an IllegalStateException. The value must be expressed in the BASE_UNIT declared above,
     * otherwise the calling layer may throw an IllegalStateException.
     *
     * The method must ALWAYS return a non-null value. If the value cannot be successfully extracted because of invalid
     * command output, the method must throw an exception containing a human readable message. Any exceptions, checked
     * or unchecked, should be thrown immediately - the calling layer will log appropriately.
     *
     * @see MetricDefinition#getBaseUnit()
     * @see MetricDefinition#getBaseUnit()
     */
    protected abstract Object parseMacCommandOutput(String commandOutput) throws Exception;

    /**
     * @return the value corresponding to this metric definition, as extracted from the output of the associated
     * MacOS command. The type of the value must match TYPE for this class, as declared above, otherwise the calling
     * layer may throw an IllegalStateException. The value must be expressed in the BASE_UNIT declared above,
     * otherwise the calling layer may throw an IllegalStateException.
     *
     * The method must ALWAYS return a non-null value. If the value cannot be successfully extracted because of invalid
     * command output, the method must throw an exception containing a human readable message. Any exceptions, checked
     * or unchecked, should be thrown immediately - the calling layer will log appropriately.
     *
     * @see MetricDefinition#getBaseUnit()
     * @see MetricDefinition#getBaseUnit()
     */
    protected abstract Object parseLinuxCommandOutput(String commandOutput) throws Exception;

    /**
     * @return the value corresponding to this metric definition, as extracted from the output of the associated
     * MacOS command. The type of the value must match TYPE for this class, as declared above, otherwise the calling
     * layer may throw an IllegalStateException. The value must be expressed in the BASE_UNIT declared above,
     * otherwise the calling layer may throw an IllegalStateException.
     *
     * The method must ALWAYS return a non-null value. If the value cannot be successfully extracted because of invalid
     * command output, the method must throw an exception containing a human readable message. Any exceptions, checked
     * or unchecked, should be thrown immediately - the calling layer will log appropriately.
     *
     * @see MetricDefinition#getBaseUnit()
     * @see MetricDefinition#getBaseUnit()
     */
    protected abstract Object parseWindowsCommandOutput(String commandOutput) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    private Property getPropertyInstance(String id, Class c, MeasureUnit u) {

        //
        // TODO if I am ever in the situation to modify this, add a static Property.getInstance() to
        // io.novaordis.events.api.event.Property and fully implement there
        //

        if (Long.class.equals(c)) {

            LongProperty p = new LongProperty(id);
            p.setMeasureUnit(u);
            return p;
        }
        else if (Double.class.equals(c)) {

            DoubleProperty p = new DoubleProperty(id);
            p.setMeasureUnit(u);
            return p;
        }

        throw new RuntimeException("NOT YET IMPLEMENTED: getPropertyInstance(...) for " + c);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}