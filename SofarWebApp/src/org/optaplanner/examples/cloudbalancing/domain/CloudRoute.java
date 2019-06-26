/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.optaplanner.examples.cloudbalancing.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.cloudbalancing.optional.domain.CloudBusStrengthComparator;
import org.optaplanner.examples.cloudbalancing.optional.domain.CloudRouteDifficultyComparator;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity(difficultyComparatorClass = CloudRouteDifficultyComparator.class)
@XStreamAlias("CloudRoute")
public class CloudRoute extends AbstractPersistable {

    private int demand; // in gigahertz
    private int travelTime; // in gigabyte RAM
    private int startTime; // in gigabyte per hour
    private int endTime;
    private String startPoint;
    private String endPoint;

    // Planning variables: changes during planning, between score calculations.
    private CloudBus bus;

    public CloudRoute() {
    }

    public CloudRoute(long id, int demand, int travelTime, int startTime, String startPoint, String endPoint) {
        super(id);
        this.demand = demand;
        this.travelTime = travelTime;
        this.startTime = startTime;
        this.endTime = startTime+travelTime;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public int getDemand() {
        return demand;
    }

    public void seDemand(int demand) {
        this.demand = demand;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}
	
	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	@PlanningVariable(valueRangeProviderRefs = {"busRange"},
            strengthComparatorClass = CloudBusStrengthComparator.class)
    public CloudBus getBus() {
        return bus;
    }

    public void setBus(CloudBus bus) {
        this.bus = bus;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getRequiredMultiplicand() {
        return demand;
    }

    public String getLabel() {
        return "Route " + id;
    }

}
