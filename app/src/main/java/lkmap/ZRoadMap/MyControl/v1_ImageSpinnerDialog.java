package lkmap.ZRoadMap.MyControl;

import java.util.ArrayList;
import java.util.List;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import dingtu.ZRoadMap.Data.ICallback;

public class v1_ImageSpinnerDialog extends Spinner
{
	private v1_ImageSpinnerDialogAdpter m_Adpter = null;
	public v1_ImageSpinnerDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);this.Inti(context);
	}
	public v1_ImageSpinnerDialog(Context context)
	{
		super(context);this.Inti(context);
	}
    public v1_ImageSpinnerDialog(Context context, AttributeSet attrs) 
    {
		super(context, attrs);this.Inti(context);
	}
    
    private void Inti(Context context)
    {
    	this.m_Adpter = new v1_ImageSpinnerDialogAdpter(context);
    	List<v1_SymbolObject> itemList = new ArrayList<v1_SymbolObject>();
    	itemList.add(null);
    	this.m_Adpter.SetDataList(itemList);
    	this.setAdapter(this.m_Adpter);
    }
    
	@Override
	public boolean performClick() 
	{
		if (this.m_Callback!=null) this.m_Callback.OnClick("ImageSpinnerCallback", null);
		return false;
	}
	    
    //回调函数
    private ICallback m_Callback = null;
    /**
     * 设置回调函数
     * @param cb
     */
    public void SetCallback(ICallback cb){this.m_Callback = cb;}
    
    
    //选择项列表
    private List<v1_SymbolObject> m_ItemList = new ArrayList<v1_SymbolObject>();
    
    /**
     * 设置选择项列表
     * @param itemList
     */
    public void SetImageItemList(List<v1_SymbolObject> itemList)
    {
    	this.m_ItemList = itemList;
    	this.m_Adpter.SetDataList(itemList);
    	this.setAdapter(this.m_Adpter);
    }
    
    /**
     * 返回符号实体
     * @return
     */
    public v1_SymbolObject GetSelectSymbolObject()
    {
    	if (this.m_ItemList.size()==0) return null;
    	else return this.m_ItemList.get(0);
    }

}
