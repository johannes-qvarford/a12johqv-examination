package org.jLOAF.Tetris;

import org.jLOAF.inputs.ComplexInput;
import org.jLOAF.inputs.Input;
import org.jLOAF.sim.SimilarityMetricStrategy;

public class TetrisInput extends ComplexInput {
	
	private static final long serialVersionUID = 1L;
	private static SimilarityMetricStrategy simMet;

	public TetrisInput(){
		super("Tetris Input");
	}

	@Override
	public double similarity(Input i) {
		//See if the user has defined similarity for each specific input, for all inputs
		//  of a specific type, of deferred to superclass
		if(this.simStrategy != null){
			return simStrategy.similarity(this, i);
		}else if(TetrisInput.isClassStrategySet()){
			return TetrisInput.similarity(this, i);
		}else{
			return super.similarity(i);
		}
	}

	private static double similarity(Input complexInput, Input i) {
		return TetrisInput.simMet.similarity(complexInput, i);
	}

	public static boolean isClassStrategySet(){
		if(TetrisInput.simMet == null){
			return false;
		}else{
			return true;
		}
	}

	public static void setClassSimilarityMetric(SimilarityMetricStrategy s){
		TetrisInput.simMet = s;
	}

}
