package com.sofarwebapp.jersey;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class ProblemTester extends Problem {

	public ProblemTester (List<CloudBus> busList, List<CloudRoute> routeList) {
		this.busList = busList;
		this.routeList = routeList;
	}
	
	public ProblemTester (JSONObject problem) throws JSONException {
		this.busList = new ArrayList<CloudBus>();
		this.routeList = new ArrayList<CloudRoute>();
		
		JSONArray route = problem.getJSONArray("routeList");
		JSONArray bus = problem.getJSONArray("busList");

		for(int i=0; i<bus.length(); i++) {
			JSONObject p = bus.getJSONObject(i);
			busList.add(new CloudBus(
												p.getLong("id"), 
												p.getInt("capacity"), 
												p.getInt("transfers"), 
												p.getInt("timeFinished"), 
												p.getInt("cost"))
			);
		}
		for(int i=0; i<route.length(); i++) {
			JSONObject p = route.getJSONObject(i);
			int timeTravel = 0;
			if(BusHelper.travelTimes.get(p.getString("startPoint")+"-"+p.getString("endPoint")) != null)
				timeTravel+=BusHelper.travelTimes.get(p.getString("startPoint")+"-"+p.getString("endPoint"));
	
			routeList.add(new CloudRoute(	p.getLong("id"), 
											p.getInt("demand"), 
											timeTravel,
											p.getInt("startTime"),
											p.getString("startPoint"),
											p.getString("endPoint"))	
			);
		}
	}
}
