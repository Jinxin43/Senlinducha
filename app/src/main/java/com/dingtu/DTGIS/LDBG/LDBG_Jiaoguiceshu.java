package com.dingtu.DTGIS.LDBG;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;

public class LDBG_Jiaoguiceshu {
	private v1_FormTemplate dialogView = null;

	public LDBG_Jiaoguiceshu() {
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.lindibiangeng_jiaoguiraoce);
		dialogView.ReSetSize(0.8f, 0.7f);
		dialogView.SetCaption("小班蓄积量调查表");
		dialogView.SetButtonInfo("1," + R.drawable.v1_ok + ",确定  ,确定", pCallback);
	}

	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("确定")) {

			}
		}
	};

	private void getCaijiDiao() {

	}

	public void ShowDialog() {
		dialogView.show();
	}
}
