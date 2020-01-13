package lkmap.ToolBar;

import lkmap.Edit.DeleteAddObject;
import lkmap.Edit.MoveObject;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.MapControl.Tools;

import com.dingtu.senlinducha.R;

import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;

public class v1_Agent_MoveDelete implements v1_IToolsBarCommand {

	@Override
	public void OnDispose() {
	}
	
	@Override
	public void OnChange() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void OnPrepare() {
		if (this.m_MoveObject==null)this.m_MoveObject = new MoveObject(PubVar.m_MapControl);
		if (this.m_Delete==null)this.m_Delete = new DeleteAddObject();
	}
	
	private MoveObject m_MoveObject = null;		//移动实体类
	private DeleteAddObject m_Delete = null;		//删除实体类
	
	private View m_View = null;
	public void SetView(View view)
	{
		this.m_View = view;
		this.m_View.findViewById(R.id.bt_move).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_moveoffset).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_delete).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_undo).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_redo).setOnClickListener(new ViewClick());
	}
	
    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.equals("移动"))Move();

    		if (Tag.equals("删除"))Delete();

    		if (Tag.equals("回退"))
    		{
    			PubVar.m_DoEvent.DoCommand(Tag);
    		}
    		if (Tag.equals("重做"))
    		{
    			PubVar.m_DoEvent.DoCommand(Tag);
    		}
    	}
    }
    
    //开始移动实体
    private void Move()
    {
		if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
		
		//验证是否有选中的可移动实体
		int SelectObectCount = 0;
        for  (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
        {
            if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount()>0)
            {
            	SelectObectCount += pGeoLayer.getSelSelection().getCount();
            }
        }
        if (SelectObectCount>0)
        {
        	PubVar.m_MapControl.setActiveTools(Tools.MoveObject, this.m_MoveObject, this.m_MoveObject);
        } else
        {
        	lkmap.Tools.Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "请在可编辑图层中选择需要移动的实体！");
        	return;
        }
    }
    
    //删除实体
    private void Delete()
    {
    	if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
        this.m_Delete.Delete();
    }

}
