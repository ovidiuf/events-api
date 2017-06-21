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

package io.novaordis.events.api.metric;

import io.novaordis.events.api.measure.MeasureUnit;
import io.novaordis.utilities.address.Address;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/4/16
 */
abstract class MockMetricDefinitionBase extends MetricDefinitionBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Class type;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param metricSourceAddress must always have a non-null source.
     */
    protected MockMetricDefinitionBase(Address metricSourceAddress) {

        this(null, metricSourceAddress);
    }

    /**
     * @param metricSourceAddress must always have a non-null source.
     */
    protected MockMetricDefinitionBase(String id, Address metricSourceAddress) {

        this(id, null, metricSourceAddress);
    }

    /**
     * @param metricSourceAddress must always have a non-null source.
     */
    protected MockMetricDefinitionBase(String id, Class type, Address metricSourceAddress) {

        super(metricSourceAddress);
        setId(id);
        this.type = type;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // MetricDefinition implementation ---------------------------------------------------------------------------------

    @Override
    public Class getType() {

        return type;
    }

    @Override
    public MeasureUnit getBaseUnit() {

        throw new RuntimeException("getBaseUnit() NOT YET IMPLEMENTED");
    }

    @Override
    public String getDescription() {

        throw new RuntimeException("getDescription() NOT YET IMPLEMENTED");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
