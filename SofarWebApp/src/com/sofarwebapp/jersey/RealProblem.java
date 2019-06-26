package com.sofarwebapp.jersey;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class RealProblem extends Problem {

	public RealProblem (List<CloudBus> busList, List<CloudRoute> routeList) {
		this.busList = busList;
		this.routeList = routeList;
	}
	
	public RealProblem (JSONObject routePr,JSONObject busPr) throws JSONException {
		this.busList = new ArrayList<CloudBus>();
		this.routeList = new ArrayList<CloudRoute>();
		
		JSONArray busJSONList = busPr.getJSONArray("results");
		
		for(int i=0; i<busJSONList.length(); i++) {
			JSONObject bus = busJSONList.getJSONObject(i);
			if(bus.get("capacity").getClass().equals(new Integer(0).getClass()) && bus.getInt("capacity")>0)
				busList.add(new CloudBus(		bus.getLong("id"),
												bus.getString("name"),
												bus.getInt("capacity"), 
												10,
												15,
												1000
											)
						);
		}
		
		JSONArray routeJSONList = routePr.getJSONArray("results");
	
		for(int i=0; i<routeJSONList.length(); i++) {
			boolean flag = false;
			int timeTravel = 0;
			JSONObject route = routeJSONList.getJSONObject(i);
			String [] locseq = ((String) route.get("locseq")).split(",");
			for(int j=0; j<locseq.length-1 && !flag; j++) {
				flag = (BusHelper.travelTimes.get(locseq[j] + "-" + locseq[j+1]) == null);
				if(!flag && BusHelper.travelTimes.get(locseq[j] + "-" + locseq[j+1])!=null) {
					timeTravel += BusHelper.travelTimes.get(locseq[j] + "-" + locseq[j+1]); 
					flag = (timeTravel == 0);
				}
			}
			//if(route.getInt("travelTime") > 0)
				routeList.add(new CloudRoute(	route.getLong("id"), 
												route.getInt("pax"), 
												route.getInt("travelTime"),
												route.getInt("startTimeMM"),
												locseq[0],
												locseq[locseq.length-1]
												)
						);
		}
	}

	
	
}
