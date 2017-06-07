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
import io.novaordis.events.api.measure.MeasureUnit;
import io.novaordis.events.api.measure.MemoryMeasureUnit;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.events.api.metric.MetricDefinitionTest;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/6/17
 */
public abstract class OSMetricDefinitionTest extends MetricDefinitionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    /**
     * These tests will have different results depending on the operating system the test suite is executed on, but
     * they all must pass.
     */
    @Test
    public void collectMetricOnTheLocalSystem() throws Exception {

        MetricDefinition md = getMetricDefinitionToTest();

        LocalOS localOs = (LocalOS)md.getSource();

        List<Property> measurements = localOs.collectMetrics(Collections.singletonList(md));

        assertEquals(1, measurements.size());

        Property p = measurements.get(0);

        assertEquals(md.getId(), p.getName());
        assertEquals(MemoryMeasureUnit.BYTE, p.getMeasureUnit());
        assertEquals(Long.class, p.getType());

        Long value = (Long)p.getValue();
        assertNotNull(value);
    }

    // accessors -------------------------------------------------------------------------------------------------------

    @Test
    public void getId() throws Exception {

        //
        // For all OS metrics, the ID is conventionally the simple name of the class implementing the metric definition.
        //

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        String id = d.getId();
        String expected = d.getClass().getSimpleName();
        assertEquals(expected, id);
    }

    @Test
    public void getType_NotNull() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        Class c = d.getType();

        assertNotNull(c);
    }

    @Test
    public void getType_KnownType() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        Class c = d.getType();

        if (Integer.class.equals(c) || Long.class.equals(c) || Double.class.equals(c))  {

            return;
        }

        fail("invalid type " + c);
    }

    @Test
    public void getBaseUnit_NotNull() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        MeasureUnit u = d.getBaseUnit();

        assertNotNull(u);
    }

    @Test
    public void getSimpleLabel_NotEmpty() throws Exception {

        OSMetricDefinitionBase d = (OSMetricDefinitionBase)getMetricDefinitionToTest();

        String label = d.getSimpleLabel();

        label = label.trim();

        assertFalse(label.isEmpty());
    }

    @Test
    public void getDescription_NotEmpty() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        String description = d.getDescription();

        description = description.trim();

        assertFalse(description.isEmpty());
    }

    // getCommand() ----------------------------------------------------------------------------------------------------

    @Test
    public void getCommand_Linux() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        try {

            //
            // set the "current" OS to Linux
            //

            OSType.current = OSType.LINUX;

            String s = d.getCommand();
            assertEquals(OSMetricDefinitionBase.LINUX_COMMAND, s);

        }
        finally {

            //
            // restore the "current" system
            //

            OSType.reset();
        }
    }

    @Test
    public void getCommand_Mac() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        try {

            //
            // set the "current" OS to Mac
            //

            OSType.current = OSType.MAC;

            String s = d.getCommand();
            assertEquals(OSMetricDefinitionBase.MAC_COMMAND, s);

        }
        finally {

            //
            // restore the "current" system
            //

            OSType.reset();
        }
    }

    @Test
    public void getCommand_Windows() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        try {

            //
            // set the "current" OS to Windows
            //

            OSType.current = OSType.WINDOWS;

            String s = d.getCommand();
            assertEquals(OSMetricDefinitionBase.WINDOWS_COMMAND, s);

        }
        finally {

            //
            // restore the "current" system
            //

            OSType.reset();
        }
    }

    @Test
    public void getCommand_UnsupportedOS() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        try {

            //
            // set the "current" OS to something that is not supported
            //

            OSType.current = OSType.TEST;

            d.getCommand();

            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("not supported yet"));
        }
        finally {

            //
            // restore the "current" system
            //

            OSType.reset();
        }
    }

    // parseCommandOutput() --------------------------------------------------------------------------------------------

    @Test
    public void parseCommandOutput() throws Exception {

        OSMetricDefinition d = (OSMetricDefinition)getMetricDefinitionToTest();

        d.parseCommandOutput("something");

        throw new RuntimeException("NYE");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}