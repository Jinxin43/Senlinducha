package dingtu.ZRoadMap.Data;

import com.dingtu.senlinducha.R;

import android.widget.DatePicker;
import dingtu.ZRoadMap.PubVar;

public class v1_Data_Template_DateTime
{

	private v1_FormTemplate _Dialog = null; 
    public v1_Data_Template_DateTime()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_template_datetime);
    	//_Dialog.ReSetSize(0.4f,0.5f);
    	
    	//�ϲ����ܰ�ť�¼���
    	_Dialog.SetCaption("��������");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_clear_queryfilter+",ȷ��  ,ȷ��", pCallback);
    }
    
    
    private ICallback _Callback = null;
    public void SetCallabck(ICallback cb){this._Callback = cb;}
    
    private ICallback pCallback = new ICallback(){

		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("ȷ��"))
			{
				DatePicker DT = (DatePicker)_Dialog.findViewById(R.id.dp_date);
				String DTString = DT.getYear()+"-"+(DT.getMonth()+1)+"-"+DT.getDayOfMonth();
				if (_Callback!=null) _Callback.OnClick("����", DTString);
				_Dialog.dismiss();
			}
			
		}};
    public void ShowDialog()
    {
    	_Dialog.show();
    }
    

}
