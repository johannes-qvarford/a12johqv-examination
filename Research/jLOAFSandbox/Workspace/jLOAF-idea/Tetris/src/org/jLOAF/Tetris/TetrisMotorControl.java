package org.jLOAF.Tetris;

import org.jLOAF.MotorControl;
import org.jLOAF.action.Action;
import org.jLOAF.action.AtomicAction;
import org.jLOAF.action.ComplexAction;

public class TetrisMotorControl extends MotorControl {

	@Override
	public String control(Action a) {
		if(a instanceof TetrisAction){
			ComplexAction cplx = (ComplexAction)a;
			Action f = cplx.get("TetrisFeatures");
			if(f == null){
				System.out.println("MotorControl: Something wrong...did not get a correct TetrisAction!");
				return null;
			}
			AtomicAction atom = (AtomicAction)f;
			String s = "";
			s += (int)atom.getFeature(0).getValue();
			s += ",";
			s += (int)atom.getFeature(1).getValue();
			s += ",";
			s += (int)atom.getFeature(2).getValue();
			s += ",";
			s += atom.getFeature(3).getValue();
			return s;
		}else{
			System.out.println("MotorControl: Something wrong...did not get a TetrisAction!");
			return null;
		}
		
	}

}
