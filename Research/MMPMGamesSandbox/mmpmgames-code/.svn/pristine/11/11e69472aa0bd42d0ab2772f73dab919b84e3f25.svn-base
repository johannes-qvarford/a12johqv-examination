/*********************************************************************************
Organization 					: 				Georgia Institute of Technology
												Cognitive Computing Lab (CCL)
Authors							: 				Jai Rad
												Prafulla Mahindrakar
												Santi Ontanon
****************************************************************************/
package bc.objects;

public class BCOBlock extends BCOBoundaryUnit {
public boolean canBeDestroyedFlag = true;


	public BCOBlock()
	{
	}
	public BCOBlock( BCOBlock incoming )
	{
		super(incoming);
	}
	public Object clone() {
		BCOBlock e = new BCOBlock(this);
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