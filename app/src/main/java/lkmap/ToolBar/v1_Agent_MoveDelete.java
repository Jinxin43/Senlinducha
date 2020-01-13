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
	
	private MoveObject m_MoveObject = null;		//�ƶ�ʵ����
	private DeleteAddObject m_Delete = null;		//ɾ��ʵ����
	
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
    		if (Tag.equals("�ƶ�"))Move();

    		if (Tag.equals("ɾ��"))Delete();

    		if (Tag.equals("����"))
    		{
    			PubVar.m_DoEvent.DoCommand(Tag);
    		}
    		if (Tag.equals("����"))
    		{
    			PubVar.m_DoEvent.DoCommand(Tag);
    		}
    	}
    }
    
    //��ʼ�ƶ�ʵ��
    private void Move()
    {
		if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
		
		//��֤�Ƿ���ѡ�еĿ��ƶ�ʵ��
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
        	lkmap.Tools.Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "���ڿɱ༭ͼ����ѡ����Ҫ�ƶ���ʵ�壡");
        	return;
        }
    }
    
    //ɾ��ʵ��
    private void Delete()
    {
    	if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
        this.m_Delete.Delete();
    }

}
