/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Prafulla Mahindrakar
												Santi Ontanon
****************************************************************************/
package bc.objects;

public class BCOWater extends BCPhysicalEntity {
public boolean canBePassedFlag = true;


	public BCOWater()
	{
		width = 16;
		length = 16;

	}
	public BCOWater( BCOWater incoming )
	{
		super(incoming);
	}
	public Object clone() {
		BCOWater e = new BCOWater(this);
		return e;
	}


	public static boolean isActive() 
	{
		return false;
	}

	public boolean getcanBePassedFlag()
	{

		return canBePassedFlag;

	}

	public void setcanBePassedFlag( boolean incoming )
	{
		this.canBePassedFlag = incoming;
	}

	public void setcanBePassedFlag( String incoming )
	{
		this.canBePassedFlag = Boolean.parseBoolean(incoming);
	}
}