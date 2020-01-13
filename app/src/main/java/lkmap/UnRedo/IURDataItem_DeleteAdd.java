package lkmap.UnRedo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class IURDataItem_DeleteAdd implements IURDataItem 
{
	public String LayerId = "";
	public List<Integer> ObjectIdList = new ArrayList<Integer>();
	
	public List<HashMap<String,String>> spilteIDs = new ArrayList<HashMap<String,String>>();
}
