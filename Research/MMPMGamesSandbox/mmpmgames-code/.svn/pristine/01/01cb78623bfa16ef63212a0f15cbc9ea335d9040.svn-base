/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Prafulla Mahindrakar
												Santi Ontanon
****************************************************************************/
package bc.objects;

public class BCOBoundaryUnit extends BCPhysicalEntity {
public boolean canBeDestroyedFlag = false;


	public BCOBoundaryUnit()
	{
		width = 16;
		length = 16;

	}
	public BCOBoundaryUnit( BCOBoundaryUnit incoming )
	{
		super(incoming);
	}
	public Object clone() {
		BCOBoundaryUnit e = new BCOBoundaryUnit(this);
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