package com.sofarwebapp.jersey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;
public class Main {
	public static void main(String[] args) throws IOException, JSONException {
		String url = "http://ionian.safecontrol.gr/assets/components/travel/connector.php?action=route/vuegetlist&opta=true&workingDate=20/5/2018&forcedb=ionian";
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		
		con.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String json = "";
		String line;
		while((line = reader.readLine()) != null)
			json += ("\n"+ line);
		JSONObject data = new JSONObject(json);
		JSONArray routeJSONList = data.getJSONArray("results");
		
		for(int i=0; i<routeJSONList.length(); i++) {
			int timeTravel = 0;
			JSONObject route = routeJSONList.getJSONObject(i);
			JSONArray locseq = route.getJSONArray("locseq");
			for(int j=0; j<locseq.length()-1; j++) { 
				if(BusHelper.travelTimes.get(locseq.get(j) + "-" + locseq.get(j+1))!=null)
					timeTravel += BusHelper.travelTimes.get(locseq.get(j) + "-" + locseq.get(j+1)); 
			}
			System.out.println(new CloudRoute(	route.getLong("id"), 
											route.getInt("pax"), 
											timeTravel,
											route.getInt("startTimeMM"),
											locseq.get(0).toString(),
											locseq.get(locseq.length()-1).toString()
											)
					);
		}
		
		CloudRoute routeA = new CloudRoute(0, 10, 50, 500, "41", "37");
		CloudRoute routeZ = new CloudRoute(0, 10, 50, 540, "41", "37");
		CloudBus bus = new CloudBus();
		System.out.println("test delay : \n"+bus.delayBetweenTrips(routeA, routeZ));
		System.out.println(BusHelper.travelTimes);
	}
}