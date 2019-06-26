package com.sofarwebapp.jersey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@Path("/RouteBalancing")
public class StartAndStop {
	@Context
    public UriInfo uriInfo;
	
	private SolverThread solver;
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public String accueil() {
		String page = "<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"	<title>Accueil</title>\r\n" + 
				"	<meta charset=\"UTF-8\">\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"	<h1>Sofar X Opta Planner :</h1>\r\n" + 
				"	<ul>\r\n" + 
				"		<li><a href=\"http://192.168.1.200:8027/SofarWebApp/RouteBalancing/problemTester\">Testing problems (from kriton.safecontrol.gr)</a></li>\r\n" +
				"		<li><a href=\"http://kritondev.safecontrol.gr/daily-routes.html/\">Real problem (from ionian.safecontrol.gr)</a></li>\r\n" + 
				"	</ul>\r\n" + 
				"</body>\r\n" + 
				"</html>";
		return page;	
	}
	
	@GET
	@Path("/problemTester")
	@Produces(MediaType.TEXT_HTML)
	public String problem() {
		String page = "<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"	<title>Accueil</title>\r\n" + 
				"	<meta charset=\"UTF-8\">\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"	<h1>Sofar X Opta Planner :</h1>\r\n" + 
				"	<ul>\r\n" + 
				"		<li><a href=\"http://192.168.1.200:8027/SofarWebApp/RouteBalancing/problemTester/start\">Start</a></li>\r\n" +
				"		<li><a href=\"http://192.168.1.200:8027/SofarWebApp/RouteBalancing/getBestSolution\">GetTheBestSolution</a></li>\r\n" +
				"		<li><a href=\"http://192.168.1.200:8027/SofarWebApp/RouteBalancing/stop\">Stop</a></li>\r\n" +
				"	</ul>\r\n" + 
				"</body>\r\n" + 
				"</html>";
		return page;	
	}
	
	@GET
	@Path("/problemTester/start")
	@Produces(MediaType.TEXT_HTML)
	public Response problemStart() throws IOException, JSONException {
		//Get the JSON
//		String url = "http://kriton.safecontrol.gr/testterminateearly.json";
		String url = "http://kriton.safecontrol.gr/plannertestconflictingtime.json";
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		
		con.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String data = "";
		String line;
		while((line = reader.readLine()) != null)
			data += ("\n"+ line);
		JSONObject json = new JSONObject(data);
		
		solver = new SolverThread(new ProblemTester(json));
		solver.start();
		
		Response response = Response.ok("Solving Started").build();
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		return response;
	}
	
	@GET
	@Path("/realProblem/start")
	@Produces(MediaType.TEXT_HTML)
	public Response realProblemStart(@QueryParam("dater") String dater) throws IOException, JSONException {
		//Get the JSON
		String urlRoute = "http://kritondev.safecontrol.gr/assets/components/travel/connector.php?action=route/getlist&plain=visj&limit=0&workingDate=" + dater + "&ctx=web&forcedb=ionian";
		String urlBus = "http://kritondev.safecontrol.gr/assets/components/travel/connector.php?action=assets/getlist&forcedb=ionian&ctx=web";
		URL uR = new URL(urlRoute);
		URL uB = new URL(urlBus);

		HttpURLConnection conR = (HttpURLConnection) uR.openConnection();
		HttpURLConnection conB = (HttpURLConnection) uB.openConnection();
		
		conR.setRequestMethod("GET");	
		BufferedReader reader = new BufferedReader(new InputStreamReader(conR.getInputStream()));
		String json = "";
		String line;
		//Read Route
		while((line = reader.readLine()) != null)
			json += ("\n"+ line);
		reader = new BufferedReader(new InputStreamReader(conB.getInputStream()));
		JSONObject routeJSON = new JSONObject(json);
		//Read Bus
		json = "";
		while((line = reader.readLine()) != null)
			json += ("\n"+ line);
		JSONObject busJSON = new JSONObject(json);
		
		solver = new SolverThread(new RealProblem(routeJSON, busJSON));
		solver.start();
		
		Response response =  Response.ok("Solving Started").build();
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		return response;
	}
	
	@GET
	@Path("/stop")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stop() throws InterruptedException {
		solver.terminate();
		Response response =  Response.ok(solver.getBestSolution().getRouteList()).build();
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		return response;
	}
	
	@GET
	@Path("/getBestSolution")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBestSolution() throws InterruptedException {
		Response response =  Response.ok(solver.getBestSolution().getRouteList()).build();
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		return response;
	}

	@GET
	@Path("/getHardSoftScore")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHardSoftScore() throws InterruptedException {
		HardSoftScore score;
		try {
			score = solver.getBestSolution().getScore();
		} catch (NullPointerException npe) {
			score = HardSoftScore.ZERO;
		}
		Response response =  Response.ok(score).build();
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		return response;
	}
	
	@POST
	@Path("/problemTester")
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProblem(String xml) {
		System.out.println(xml);
//		solverFactory = SolverFactory.createFromXmlResource(problemTester);

		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		builder.path(Integer.toString(5));
		URI uri = builder.build();
		return Response.created(uri).build();
	}

}
