/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.learn.planlearning;

import java.util.ArrayList;

import d2.plans.Plan;


public class PlanFilteredParameter {

	String actionName;
	String performingEntityType;
	ArrayList<String> actionParameters;
	Plan linkToActualPlan;
	public PlanFilteredParameter(String actionName_,String performingEntityType_,Plan plan)
	{
		actionName=actionName_;
		performingEntityType=performingEntityType_;
		actionParameters=new ArrayList<String>();
		linkToActualPlan=plan;
	}
	public void addParameter(String parameter_)
	{
		actionParameters.add(parameter_);
	}
	public String getPlanName() {
		return actionName;
	}
	public void setPlanName(String actionName) {
		this.actionName = actionName;
	}
	public String getPerformingEntityType() {
		return performingEntityType;
	}
	public void setPerformingEntityType(String performingEntityType) {
		this.performingEntityType = performingEntityType;
	}
	public ArrayList<String> getParameters() {
		return actionParameters;
	}
	public void setParameters(ArrayList<String> actionParameters) {
		this.actionParameters = actionParameters;
	}
    public String toString()
    {
    	String afpString=actionName+" "+performingEntityType;
    	for(String acParam : actionParameters)
    	afpString+=" "+acParam;
    	return afpString;
    }
	
    public boolean equals(PlanFilteredParameter afp)
    {
    	boolean flag=false;
    	if(afp == null|| this.actionParameters.size() != afp.actionParameters.size())
    		return flag;
    	if(this.actionName.equals(afp.actionName)&&this.performingEntityType.equals(afp.performingEntityType))
    	{
    		flag=true;
    		for(int index=0;index < actionParameters.size();index++)
    			if(!this.getParameters().get(index).equals(afp.getParameters().get(index)))
    			{
    				flag=false;
    				break;
    			}
    	}
    	return flag;
    	
    }
    
}
