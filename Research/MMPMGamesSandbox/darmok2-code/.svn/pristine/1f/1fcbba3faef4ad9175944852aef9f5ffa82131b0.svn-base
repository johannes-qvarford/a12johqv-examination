/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.execution.planner;

import gatech.mmpm.GameState;

import java.util.ArrayList;
import java.util.List;

import d2.plans.Plan;


public class MinMaxElement {
	protected GameState returnedGameState;
	protected Plan chosenPlan;
	protected double value;
	protected List <MinMaxElement> nextElements;
	
	public MinMaxElement() {
		nextElements = new ArrayList <MinMaxElement> ();
	}
	
	public MinMaxElement(Plan p, GameState predictedGS) {
		chosenPlan = p;
		returnedGameState = predictedGS;
		nextElements = new ArrayList <MinMaxElement> ();
	}

	public MinMaxElement(Plan p) {
		chosenPlan = p;
		nextElements = new ArrayList <MinMaxElement> ();
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
	
	public void addNextElement(MinMaxElement element) {
		nextElements.add(element);
	}

	public List<MinMaxElement> getNextElements() {
		return nextElements;
	}

	public GameState getReturnedGameState() {
		return returnedGameState;
	}

	public void setReturnedGameState(GameState returnedGameState) {
		this.returnedGameState = returnedGameState;
	}

	public Plan getChosenPlan() {
		return chosenPlan;
	}

	public void setChosenPlan(Plan chosenPlan) {
		this.chosenPlan = chosenPlan;
	}

}
