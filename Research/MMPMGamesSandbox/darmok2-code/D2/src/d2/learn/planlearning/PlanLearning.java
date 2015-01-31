/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.learn.planlearning;

import d2.core.D2;
import d2.core.D2Module;
import java.util.List;

import d2.execution.planbase.PlanBase;
import d2.execution.planbase.PlanBaseEntry;
import d2.worldmodel.WorldModel;
import gatech.mmpm.IDomain;

import gatech.mmpm.Trace;
import java.util.LinkedList;

public abstract class PlanLearning extends D2Module {

    protected IDomain m_domain;

    public PlanLearning(IDomain domain) {
        this.m_domain = domain;
    }

    public abstract List<PlanBaseEntry> learnFromTrace(Trace t, String playerName, WorldModel wm) throws Exception;

    public List<PlanBaseEntry> learnFromTraces(List<String> trace_file_names, List<String> playerNames, WorldModel wm) throws Exception {
        int count = 0;
        List<PlanBaseEntry> l = new LinkedList<PlanBaseEntry>();

        System.out.println("PlanLearning.learnFromTraces: " + trace_file_names.size() + " traces");

        for (count = 0; count < trace_file_names.size(); count++) {
            String filename = trace_file_names.get(count);
            String playerName = playerNames.get(count);

            System.out.println("PlanLearning.learnFromTraces: " + filename + " -> " + playerName);

            Trace trace = gatech.mmpm.tracer.TraceParser.parse(filename, d2.core.Config.getDomain());
            if (trace != null) {
                trace.setTraceID(count);
                l.addAll(learnFromTrace(trace, playerName, wm));
            } else {
                System.err.println("Failed to load trace in " + filename);
            }
        }
        
        System.out.println("Completed learning from " + trace_file_names.size() + " traces. " + l.size() + " cases learned.");

        return l;
    }
}
