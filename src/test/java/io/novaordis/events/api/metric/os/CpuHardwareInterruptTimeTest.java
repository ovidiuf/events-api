/*
 * Copyright (c) 2016 Nova Ordis LLC
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


import io.novaordis.events.api.measure.MeasureUnit;
import io.novaordis.events.api.measure.Percentage;
import io.novaordis.events.api.metric.MetricDefinition;
import io.novaordis.events.api.metric.MetricDefinitionTest;
import io.novaordis.events.api.metric.MetricSource;
import io.novaordis.events.api.metric.source.OSCommand;
import io.novaordis.utilities.os.OS;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/3/16
 */
public class CpuHardwareInterruptTimeTest extends MetricDefinitionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // getMeasureUnit() ------------------------------------------------------------------------------------------------

    @Test
    public void measureUnitIsPercentage() throws Exception {

        CpuHardwareInterruptTime m = getMetricDefinitionToTest();

        MeasureUnit mu = m.getMeasureUnit();

        assertEquals(Percentage.getInstance(), mu);
    }

    // getType() -------------------------------------------------------------------------------------------------------

    @Test
    public void typeIsFloat() throws Exception {

        CpuHardwareInterruptTime m = getMetricDefinitionToTest();

        Class t = m.getType();

        assertEquals(Float.class, t);
    }

    // getInstance() ---------------------------------------------------------------------------------------------------

    @Test
    public void getInstance() throws Exception {

        CpuHardwareInterruptTime m = (CpuHardwareInterruptTime) MetricDefinition.getInstance("CpuHardwareInterruptTime");
        assertNotNull(m);
    }

    @Test
    public void getSimpleLabel() throws Exception {

        CpuHardwareInterruptTime m = new CpuHardwareInterruptTime();
        assertEquals("CPU Hardware Interrupt Time", m.getSimpleLabel());
    }

    // sources ---------------------------------------------------------------------------------------------------------

    @Test
    public void sourcesLinux() throws Exception {

        CpuHardwareInterruptTime m = getMetricDefinitionToTest();

        List<MetricSource> linuxSources = m.getSources(OS.Linux);
        assertEquals(1, linuxSources.size());
        OSCommand c = (OSCommand) linuxSources.get(0);
        assertEquals("top", c.getCommand());
    }

    @Test
    public void sourcesMac() throws Exception {

        CpuHardwareInterruptTime m = getMetricDefinitionToTest();

        List<MetricSource> macSources = m.getSources(OS.MacOS);
        // TODO this will probably change
        assertEquals(0, macSources.size());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CpuHardwareInterruptTime getMetricDefinitionToTest() throws Exception {

        return new CpuHardwareInterruptTime();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
