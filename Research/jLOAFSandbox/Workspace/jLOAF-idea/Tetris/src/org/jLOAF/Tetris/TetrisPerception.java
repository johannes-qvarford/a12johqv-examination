package org.jLOAF.Tetris;

import org.jLOAF.Perception;
import org.jLOAF.inputs.Input;
import org.jLOAF.inputs.complex.Matrix;

public class TetrisPerception implements Perception {

	public Input sense(double[][] board, double[][] piece) {
		TetrisInput ti = new TetrisInput();
		Matrix brd = new Matrix("TetrisBoard", board);
		Matrix pce = new Matrix("TetrisPiece", piece);
		ti.add(brd);
		ti.add(pce);
		
		return ti;
	}

}
