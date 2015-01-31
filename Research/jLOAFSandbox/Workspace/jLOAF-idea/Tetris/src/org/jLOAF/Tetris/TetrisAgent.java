package org.jLOAF.Tetris;

import org.jLOAF.Agent;
import org.jLOAF.action.Action;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.Input;
import org.jLOAF.inputs.atomic.MatrixCell;
import org.jLOAF.inputs.complex.Matrix;
import org.jLOAF.reasoning.SimpleKNN;
import org.jLOAF.sim.atomic.Equality;
import org.jLOAF.sim.complex.Mean;


public class TetrisAgent extends Agent {

	public TetrisAgent(CaseBase cb){
		super(null,null,null,cb);
		//sets the similarity metrics that are used
		MatrixCell.setClassSimilarityMetric(new Equality());
		Matrix.setClassSimilarityMetric(new Mean());
		TetrisInput.setClassSimilarityMetric(new Mean());
		
		//create the Tetris-specific motor control and perception
		this.mc = new TetrisMotorControl();
		this.p = new TetrisPerception();
		
		//use a general reasoning module (1-NN)
		this.r = new SimpleKNN(1, cb);
		
		this.cb = cb;
	}

	public String go(double[][] board, double[][] piece) {
		Input in = ((TetrisPerception)this.p).sense(board,piece);
		Action act = this.r.selectAction(in);
		return this.mc.control(act);
		
	}
}
