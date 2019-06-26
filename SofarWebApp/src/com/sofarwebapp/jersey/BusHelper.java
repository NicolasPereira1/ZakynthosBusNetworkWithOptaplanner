package com.sofarwebapp.jersey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class BusHelper {
    public static HashMap<String, Integer> travelTimes = new HashMap<String, Integer>();
    static {
    	if(travelTimes.size() < 1) {
	     	try {
	        	//Get the JSON
	    		String url = "http://ionian.safecontrol.gr/assets/components/travel/connector.php?action=legs/getlist&opta=true";
	    		URL u = new URL(url);
	    		HttpURLConnection con = (HttpURLConnection) u.openConnection();
	    		
	    		con.setRequestMethod("GET");
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    		String json = "";
	    		String line;
	    		while((line = reader.readLine()) != null)
	    			json += ("\n"+ line);
	    		JSONObject data = new JSONObject(json);
	    		JSONArray array = data.getJSONArray("results");
	    		for(int i=0; i<array.length(); i++) {
	    			JSONObject route = new JSONObject(array.get(i).toString());
	    			String key = route.get("a") + "-" + route.get("z");
	    			if(!travelTimes.containsKey(key))travelTimes.put(key, route.getInt("d"));
	    		}
	    		
	     	}catch (Exception e) {
	          		e.printStackTrace();
	       	}
    	}
    }
}
