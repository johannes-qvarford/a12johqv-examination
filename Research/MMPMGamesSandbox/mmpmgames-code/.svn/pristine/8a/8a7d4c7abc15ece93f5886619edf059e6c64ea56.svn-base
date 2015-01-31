/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Prafulla Mahindrakar
												Santi Ontanon
****************************************************************************/
package bc.objects;

public class BCOWall extends BCOBoundaryUnit {
public boolean canBeDestroyedFlag = false;


	public BCOWall()
	{
	}
	public BCOWall( BCOWall incoming )
	{
		super(incoming);
	}
	public Object clone() {
		BCOWall e = new BCOWall(this);
		return e;
	}


	public static boolean isActive() 
	{
		return false;
	}

	public boolean getcanBeDestroyedFlag()
	{

		return canBeDestroyedFlag;

	}

	public void setcanBeDestroyedFlag( boolean incoming )
	{
		this.canBeDestroyedFlag = incoming;
	}

	public void setcanBeDestroyedFlag( String incoming )
	{
		this.canBeDestroyedFlag = Boolean.parseBoolean(incoming);
	}
}