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

package io.novaordis.events.api.metric.os.mdefs;

import io.novaordis.events.api.event.Property;
import io.novaordis.events.api.measure.Percentage;
import io.novaordis.events.api.metric.os.LocalOS;
import io.novaordis.events.api.metric.os.OSMetricDefinitionTest;
import io.novaordis.events.api.metric.os.OSType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/3/16
 */
public class CpuIdleTimeTest extends OSMetricDefinitionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // accessors -------------------------------------------------------------------------------------------------------

    @Test
    public void getId() throws Exception {

        CpuIdleTime md = new CpuIdleTime(new LocalOS());
        assertEquals("CpuIdleTime", md.getId());
    }

    @Test
    public void getType() throws Exception {

        CpuIdleTime md = new CpuIdleTime(new LocalOS());
        assertEquals(Float.class, md.getType());
    }

    @Test
    public void getBaseUnit() throws Exception {

        CpuIdleTime md = new CpuIdleTime(new LocalOS());
        assertEquals(Percentage.getInstance(), md.getBaseUnit());
    }

    @Test
    public void getSimpleLabel() throws Exception {

        CpuIdleTime m = new CpuIdleTime(new LocalOS());
        assertEquals("CPU Idle Time", m.getSimpleLabel());
    }

    @Test
    public void getDescription() throws Exception {

        CpuIdleTime m = new CpuIdleTime(new LocalOS());
        assertTrue(m.getDescription().toLowerCase().contains("percentage"));
        assertTrue(m.getDescription().toLowerCase().contains("cpu"));
        assertTrue(m.getDescription().toLowerCase().contains("idle"));
    }

    @Test
    public void getLinuxCommand() throws Exception {

        String expected = "/usr/bin/top -b -n 1 -p 0";

        CpuIdleTime m = new CpuIdleTime(new LocalOS());
        assertEquals(expected, m.getLinuxCommand());

        try {

            //
            // set the "current" OS to Mac
            //

            OSType.current = OSType.LINUX;

            String s = m.getCommand();
            assertEquals(expected, s);

        }
        finally {

            //
            // restore the "current" system
            //

            OSType.reset();
        }
    }

    @Test
    public void getMacCommand() throws Exception {

        String expected = "/usr/bin/top -l 1 -n 0";

        CpuIdleTime m = new CpuIdleTime(new LocalOS());
        assertEquals(expected, m.getMacCommand());

        try {

            //
            // set the "current" OS to Mac
            //

            OSType.current = OSType.MAC;

            String s = m.getCommand();
            assertEquals(expected, s);

        }
        finally {

            //
            // restore the "current" system
            //

            OSType.reset();
        }
    }

    @Test
    public void getWindowsCommand() throws Exception {

        CpuIdleTime m = new CpuIdleTime(new LocalOS());
        assertNull(m.getWindowsCommand());

        try {

            //
            // set the "current" OS to Mac
            //

            OSType.current = OSType.WINDOWS;

            String s = m.getCommand();
            assertNull(s);

        }
        finally {

            //
            // restore the "current" system
            //

            OSType.reset();
        }
    }

    // parseCommandOutput() --------------------------------------------------------------------------------------------

    //
    // invalid readings are tested in superclass
    //

    @Test
    @Override
    public void parseCommandOutput_ValidLinuxOutput() throws Exception {

        String output =

                "top - 11:10:11 up 0 min,  1 user,  load average: 0.15, 0.04, 0.02\n" +
                        "Tasks:   1 total,   1 running,   0 sleeping,   0 stopped,   0 zombie\n" +
                        "%Cpu(s):  2.8 us,  8.1 sy,  0.0 ni, 88.7 id,  0.4 wa,  0.0 hi,  0.1 si,  0.0 st\n" +
                        "KiB Mem :   999936 total,   735636 free,   117680 used,   146620 buff/cache\n" +
                        "KiB Swap:        0 total,        0 free,        0 used.   715840 avail Mem\n" +
                        "\n" +
                        "   PID USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND\n" +
                        "  1094 ansible   20   0  157440   1928   1484 R  0.0  0.2   0:00.00 top";

        CpuIdleTime d = getMetricDefinitionToTest();

        Property p;

        try {

            OSType.current = OSType.LINUX;

            p = d.parseCommandOutput(output);

        }
        finally {

            OSType.reset();
        }

        assertEquals(d.getId(), p.getName());
        assertEquals(d.getType(), p.getType());
        assertEquals(d.getBaseUnit(), p.getMeasureUnit());

        float expected = 88.7f;
        assertEquals(expected, ((Float) p.getValue()).floatValue(), 0.00001);
    }

    @Test
    @Override
    public void parseCommandOutput_ValidMacOutput() throws Exception {

        String output =

                "Processes: 263 total, 2 running, 5 stuck, 256 sleeping, 1421 threads \n" +
                        "2017/06/05 15:04:51\n" +
                        "Load Avg: 2.29, 2.02, 1.90 \n" +
                        "CPU usage: 2.73% user, 10.95% sys, 86.30% idle \n" +
                        "SharedLibs: 13M resident, 18M data, 0B linkedit.\n" +
                        "MemRegions: 59430 total, 5948M resident, 147M private, 4665M shared.\n" +
                        "PhysMem: 15G used (1447M wired), 1424M unused.\n" +
                        "VM: 699G vsize, 1064M framework vsize, 0(0) swapins, 0(0) swapouts.\n" +
                        "Networks: packets: 224778/181M in, 135478/17M out.\n" +
                        "Disks: 229646/5001M read, 113968/3311M written.\n" +
                        "\n" +
                        "\n";

        CpuIdleTime d = getMetricDefinitionToTest();

        Property p;

        try {

            OSType.current = OSType.MAC;

            p = d.parseCommandOutput(output);

        }
        finally {

            OSType.reset();
        }

        assertEquals(d.getId(), p.getName());
        assertEquals(d.getType(), p.getType());
        assertEquals(d.getBaseUnit(), p.getMeasureUnit());

        float expected = 86.3f;
        assertEquals(expected, ((Float) p.getValue()).floatValue(), 0.00001);
    }

    @Test
    @Override
    public void parseCommandOutput_ValidWindowsOutput() throws Exception {

        //
        // TODO noop for the time being, revisit when implementing Windows support
        //
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CpuIdleTime getMetricDefinitionToTest() throws Exception {

        return new CpuIdleTime(new LocalOS());
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
