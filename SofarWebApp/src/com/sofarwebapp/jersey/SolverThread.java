package com.sofarwebapp.jersey;

import java.io.File;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator;

public class SolverThread extends Thread {
	static Solver<CloudBalance> solver;
	static SolverFactory<CloudBalance> solverFactory;
	static CloudBalance solvedCloudBalance;
	static Problem problem;

	public SolverThread (Problem problem) {
		this.problem = problem;
	}
	
	public void run() {
		// Build the Solver

		File XML = new File("C:\\Users\\Nicolas\\Documents\\Sofar\\OptaConfig\\cloudBalancingSolverConfig.xml");
//		File XML = new File("/home/jim/Nicolas/OptaConfig/cloudBalancingSolverConfig.xml");

		solverFactory = SolverFactory.createFromXmlFile(XML);
	
	    solver 	= solverFactory.buildSolver();
	
	    // Load a problem
	    CloudBalance unsolvedCloudBalance = new CloudBalance(1, problem.getBusList(), problem.getRouteList());
	
	    // Solve the problem
	    solvedCloudBalance = solver.solve(unsolvedCloudBalance);
	
	    // Display the result
	    System.out.println("\nSolved cloudBalance with " + unsolvedCloudBalance.getBusList().size() + " Bus and " + unsolvedCloudBalance.getRouteList().size() + " Route:\n"
	            + toDisplayString(solvedCloudBalance));
	}
	
	private String toDisplayString(CloudBalance cloudBalance) {
	    StringBuilder displayString = new StringBuilder();
	    for (CloudRoute process : cloudBalance.getRouteList()) {
	        CloudBus computer = process.getBus();
	        displayString.append("  ").append(process.getLabel()).append(" -> ")
	                .append(computer == null ? null : computer.getLabel()).append("\n");
	    }
	    return displayString.toString();
	}
	
	public static void terminate() {
		solver.terminateEarly();
	}
	
	public static CloudBalance getBestSolution() {
		return solver.getBestSolution();
	}
}
