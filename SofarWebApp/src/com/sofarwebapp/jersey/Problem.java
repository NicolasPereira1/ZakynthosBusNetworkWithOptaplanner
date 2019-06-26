package com.sofarwebapp.jersey;

import java.util.List;

import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public abstract class Problem {
	protected List<CloudBus> busList;
	protected List<CloudRoute> routeList;
	
	/**
	 * @return the busList
	 */
	public List<CloudBus> getBusList() {
		return busList;
	}
	/**
	 * @param busList the busList to set
	 */
	public void setbusList(List<CloudBus> busList) {
		this.busList = busList;
	}
	/**
	 * @return the routeList
	 */
	public List<CloudRoute> getRouteList() {
		return routeList;
	}
	/**
	 * @param routeList the processList to set
	 */
	public void setRouteList(List<CloudRoute> routeList) {
		this.routeList = routeList;
	}
}
