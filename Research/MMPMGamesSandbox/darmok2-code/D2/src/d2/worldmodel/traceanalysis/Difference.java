/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.traceanalysis;

import gatech.mmpm.GameState;

public abstract class Difference {

	public abstract void apply(GameState gs);
	
	public abstract String getType();
}
