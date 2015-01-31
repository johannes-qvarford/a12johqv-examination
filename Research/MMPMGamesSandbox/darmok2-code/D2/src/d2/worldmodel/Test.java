/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel;

import gatech.mmpm.ParseLmxTrace;
import gatech.mmpm.Trace;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


/*
 * This test tests the accuracy of both the action model and the opponents model
 * with a particular trace.
 */

public class Test {
	public static void main(String args[]) throws Exception {

		// Load the configuration file:
		Properties configuration = new Properties();
		String configuration_filename = "towers-default.cfg";
		if (args.length > 0)
			configuration_filename = args[0];
		System.out.println("Using configuration file: " + configuration_filename);
		FileInputStream f;
		f = new FileInputStream(configuration_filename);
		configuration.load(f);
		f.close();
		
		// Load the traces:
		List<String> files = new LinkedList<String>();
		{
			Scanner s = new Scanner(configuration.getProperty("learning_traces"));
			String file;
			s.useDelimiter(",( |\t|\n|\r)+");
			do {
				file = s.next();
				files.add(file);
			} while (s.hasNext());
		}
		List<Trace> traces = loadTraces(files, configuration.getProperty("domain"));
		// for(Trace t:traces) t.printTrace();

		// Learn the World model:
		WorldModel wm = new WorldModel();
		wm.config(configuration);
		wm.learnFromTraces(files);
		System.out.println("World Model Learned:");
		System.out.println(wm.toString());

		// Evaluate the learned model:
		for (Trace t : traces)
			wm.evaluateWithTrace(t);

		// Run the first trace from state 0 and visualize the result of the
		// simulator:
		Trace t = traces.get(0);
		wm.getActionsModel().visualizeWithTrace(t);

	}

	static List<Trace> loadTraces(List<String> files, String domain) throws Exception {
		List<Trace> traceCollection = new LinkedList<Trace>();
		ParseLmxTrace p = new ParseLmxTrace(domain);

		for (String fileName : files) {
			if (p.initializeDOMParser(fileName)) {
//				traceCollection.add(p.parse(ID++));
			}
		}
		return traceCollection;
	}
}
