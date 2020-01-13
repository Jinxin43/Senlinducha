package lkmap.UnRedo;

import java.util.ArrayList;
import java.util.List;
import lkmap.Enum.lkReUndoFlag;

public class UnRedoDataItem 
{
	public List<IURDataItem> DataList = new ArrayList<IURDataItem>();;
    public lkReUndoFlag Type;       //ReUndo±Í ∂
}
