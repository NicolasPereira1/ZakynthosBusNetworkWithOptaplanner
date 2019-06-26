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

import com.sofarwebapp.jersey.BusHelper;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.HashMap;

import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CloudBus")
public class CloudBus extends AbstractPersistable{

	private String name;
    private int capacity; // in number of person
    private int transfers; // in unity
    private int timeFinished; // in minutes
    private int cost; // in euro per month

    public CloudBus() {
    }

    public CloudBus(long id, int capacity, int transfers, int timeFinished, int cost) {
    	this(id, "no name", capacity, transfers, timeFinished, cost);
    }

    
    public CloudBus(long id, String name, int capacity, int transfers, int timeFinished, int cost) {
        super(id);
        this.name = name;
        this.capacity = capacity;
        this.transfers = transfers;
        this.timeFinished = timeFinished;
        this.cost = cost;
    }

    public int delayBetweenTrips(CloudRoute routeA,CloudRoute routeZ){
        return this.getTravelTime(routeA.getEndPoint(),routeZ.getStartPoint());
    }
    
    public int getTravelTime (String depart, String arrival){
    	String key = depart + "-" + arrival;
    	int time = 90;
    	if(BusHelper.travelTimes.get(key) != null)
    		time = BusHelper.travelTimes.get(key);
    	return time;
    }
    
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getTransfers() {
        return transfers;
    }

    public void setTransfers(int transfers) {
        this.transfers = transfers;
    }

    public int getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(int timeFinished) {
        this.timeFinished = timeFinished;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    

    // ************************************************************************
    // Complex methods
    // ************************************************************************

	
	
	public int getMultiplicand() {
        return capacity * cost;
    }

    public String getLabel() {
        return "Bus " + id;
    }

}
