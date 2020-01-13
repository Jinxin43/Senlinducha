package lkmap.UnRedo;

import java.util.ArrayList;
import java.util.List;
public class IURDataItem_Move implements IURDataItem 
{
	public String LayerId = "";
	public List<Integer> ObjectIdList = new ArrayList<Integer>();
	public double OffsetX = 0;
	public double OffsetY = 0;
}
