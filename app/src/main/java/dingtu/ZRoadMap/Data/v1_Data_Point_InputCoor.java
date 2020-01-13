package dingtu.ZRoadMap.Data;

import com.dingtu.senlinducha.R;

import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Tools.Tools;

public class v1_Data_Point_InputCoor {
	private v1_FormTemplate _Dialog = null;

	public v1_Data_Point_InputCoor() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_data_point_inputcoor);
		_Dialog.SetCaption(Tools.ToLocale("������"));

		_Dialog.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("ȷ��") + "  ,ȷ��", pCallback);

		// ��������ϵͳȷ��������������
		if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals("WGS-84����")) {
			((TextView) _Dialog.findViewById(R.id.tvCoorX)).setText("����(��)��");
			((TextView) _Dialog.findViewById(R.id.tvCoorY)).setText("γ��(��)��");
		}

		// ������֧��
		Tools.ToLocale(_Dialog.findViewById(R.id.tvCoorX));
		Tools.ToLocale(_Dialog.findViewById(R.id.tvCoorY));
		Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));

	}

	// ��ť�¼�
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("ȷ��")) {
				if (m_Callback != null) {
					String X = Tools.GetTextValueOnID(_Dialog, R.id.et_X);
					String Y = Tools.GetTextValueOnID(_Dialog, R.id.et_Y);
					if (!Tools.IsFloat(X) || !Tools.IsFloat(Y)) {
						Tools.ShowMessageBox("������ȷ��������Ϣ��");
						return;
					}

					double Coor_X = Double.parseDouble(X);
					double Coor_Y = Double.parseDouble(Y);
					Coordinate Coor = new Coordinate(Coor_X, Coor_Y);
					CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
					if (CS.GetName().equals("WGS-84����")) {
						Coordinate Coor1 = Project_Web.Web_BLToXY(Coor_X, Coor_Y);
						Coor = new Coordinate(Coor1.getX(), Coor1.getY());
						// Coor.setGeoX(Coor_X);Coor.setGeoY(Coor_Y);
					} else {
						// Coordinate lb
						// =Project_GK.GK_XYToBL(Coor_X,Coor_Y,CS);
						// Coor.setGeoX(lb.getX());Coor.setGeoY(lb.getY());
					}

					m_Callback.OnClick("", Coor);
					_Dialog.dismiss();
				}
			}
		};
	};

	// �ص�
	private ICallback m_Callback = null;

	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	public void ShowDialog() {
		_Dialog.show();
	}
}
