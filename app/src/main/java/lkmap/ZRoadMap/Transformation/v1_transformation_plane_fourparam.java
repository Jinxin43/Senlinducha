package lkmap.ZRoadMap.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.ConfigDB;
import com.dingtu.DTGIS.DataService.DaDiPoint;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_Data_Gps_AveragePoint;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorParamTools;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_GK;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkCoorTransMethod;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;

public class v1_transformation_plane_fourparam {
	private v1_FormTemplate _Dialog = null;
	private boolean isAutoCalc = false;// 是否是自动计算参数

	public v1_transformation_plane_fourparam() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_transformation_plane_fourparam);
		_Dialog.SetCaption("平面转换");
		_Dialog.ReSetSize(0.5f, -1f);
		_Dialog.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("确定") + " ,确定", pCallback);
		_Dialog.SetButtonInfo("2," + R.drawable.v1_clearscreen + "," + Tools.ToLocale("清空") + ",清空", pCallback);

		// 提取当前工程的平面转换参数
		CoorSystem CS = (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
		isAutoCalc = CS.GetIsAutoCalc();
		if (CS.GetPMTransMethod() == lkCoorTransMethod.enFourPara) {
			takeParamShow();
			_Dialog.SetCaption("平面转换-【" + CS.GetPMTransMethodName() + "】");
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX, Tools.ConvertToDigi(CS.GetTransToP41()));
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY, Tools.ConvertToDigi(CS.GetTransToP42()));
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_Rotate, Tools.ConvertToDigi(CS.GetTransToP43()));
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_Scale, Tools.ConvertToDigi(CS.GetTransToP44()));
		}
		if (CS.GetPMTransMethod() == lkCoorTransMethod.enNull) {
			_Dialog.SetCaption("平面转换-【无】");
		}

		_Dialog.findViewById(R.id.bt_onepoint).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pCallback.OnClick("单点校正", "");
			}
		});

		_Dialog.findViewById(R.id.bt_calfourparam).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pCallback.OnClick("四参数", "");
			}
		});

		_Dialog.findViewById(R.id.bt_paramset).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pCallback.OnClick("参数管理", "");
			}
		});

		_Dialog.findViewById(R.id.btnAutoCalcFourParam).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pCallback.OnClick("自动计算四参数", "");
			}
		});
		_Dialog.findViewById(R.id.btnSmartCalcFourParam).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pCallback.OnClick("快速校准", "");
			}
		});
	}

	private Double oldDelX = 0d;
	private Double oldDelY = 0d;
	private Double oldRoute = 0d;
	private Double oldScale = 1.0d;

	// 按钮事件
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {

			if (Str.equals("清空")) {

				lkmap.Tools.Tools.ShowYesNoMessage(_Dialog.getContext(), "是否清空当前转换参数？\n", new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						String SQL = "update T_Project set %1$s where Id=2";
						SQL = String.format(SQL, "P41='0.0'," + "P42='0.0'," + "P43='0.0'," + "P44='1.0'," + "F1='',"
								+ "PMTransMethod='四参转换'");

						if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL)) {
							isAutoCalc = false;
							CoorSystem CS = (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
							CS.SetPMTransMethodName("四参转换");
							CS.SetTransToP41("0.0");
							CS.SetTransToP42("0.0");
							CS.SetTransToP43("0.0");
							// double s = (1/oldScale)
							// *Double.parseDouble(Scale) ;
							CS.SetTransToP44("1.0");
							CS.SetIsAutoCalc("");

							// _Dialog.dismiss();
							takeParamShow();
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX, "0.0");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY, "0.0");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_Rotate, "0.0");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_Scale, "1.0");
						} else {
							lkmap.Tools.Tools.ShowMessageBox("保存转换参数失败！");
							return;
						}
					}
				});
			}

			if (Str.equals("确定")) {

				// 保存到工程配置数据库
				final String DelX = Tools.GetTextValueOnID(_Dialog, R.id.et_DelX); // X平移
				final String DelY = Tools.GetTextValueOnID(_Dialog, R.id.et_DelY); // Y平移
				final String Rotate = Tools.GetTextValueOnID(_Dialog, R.id.et_Rotate); // 旋转
				final String Scale = Tools.GetTextValueOnID(_Dialog, R.id.et_Scale); // 尺度

				String SQL = "Select * from T_Project where Id=2";
				SQLiteDataReader DR = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
				if (DR.Read()) {
					if (DR.GetString("P41") != null && !DR.GetString("P41").isEmpty()) {
						oldDelX = Double.parseDouble(DR.GetString("P41"));
					} else {
						oldDelX = 0d;
					}

					if (DR.GetString("P42") != null && !DR.GetString("P42").isEmpty()) {
						oldDelY = Double.parseDouble(DR.GetString("P42"));
					}

					if (DR.GetString("P43") != null && !DR.GetString("P43").isEmpty()) {
						oldRoute = Double.parseDouble(DR.GetString("P43"));
					}

					if (DR.GetString("P44") != null && !DR.GetString("P44").isEmpty()) {
						oldScale = Double.parseDouble(DR.GetString("P44"));
					} else {
						oldScale = 1.0d;
					}
				}

				if (isAutoCalc) {
					String SQL1 = "update T_Project set %1$s where Id=2";
					SQL1 = String.format(SQL1,
							"P41='" + (Double.parseDouble(DelX)) + "'," + "P42='" + (Double.parseDouble(DelY)) + "',"
									+ "P43='" + (Double.parseDouble(Rotate)) + "'," + "P44='"
									+ (1 / Double.parseDouble(Scale)) + "'," + "F1='" + 1 + "',"
									+ "PMTransMethod='四参转换'");

					if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL1)) {

						CoorSystem CS = (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
						CS.SetPMTransMethodName("四参转换");
						CS.SetTransToP41(Double.parseDouble(DelX) + "");
						CS.SetTransToP42(Double.parseDouble(DelY) + "");
						CS.SetTransToP43(Double.parseDouble(Rotate) + "");
						double s = 1 / Double.parseDouble(Scale);
						CS.SetTransToP44(s + "");
						CS.SetIsAutoCalc("1");
						_Dialog.dismiss();
					} else {
						lkmap.Tools.Tools.ShowMessageBox("保存转换参数失败！");
						return;
					}
				} else {

					// if (Math.abs(Double.parseDouble(DelX)) > 300 ||
					// Math.abs(Double.parseDouble(DelY)) > 300) {
					// lkmap.Tools.Tools.ShowMessageBox("偏移量超过了300，您需要将红色十字移到您站立点，重新校正！");
					// return;
					// }
					String SQL1 = "update T_Project set %1$s where Id=2";
					SQL1 = String.format(SQL1,
							"P41='" + (Double.parseDouble(DelX)) + "'," + "P42='" + (Double.parseDouble(DelY)) + "',"
									+ "P43='" + (Double.parseDouble(Rotate)) + "'," + "P44='"
									+ (1 / Double.parseDouble(Scale)) + "'," + "F1=''," + "PMTransMethod='四参转换'");

					if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL1)) {

						CoorSystem CS = (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
						CS.SetPMTransMethodName("四参转换");
						CS.SetTransToP41(Double.parseDouble(DelX) + "");
						CS.SetTransToP42(Double.parseDouble(DelY) + "");
						CS.SetTransToP43(Double.parseDouble(Rotate) + "");
						double s = 1 / Double.parseDouble(Scale);
						CS.SetTransToP44(s + "");

						_Dialog.dismiss();
					} else {
						lkmap.Tools.Tools.ShowMessageBox("保存转换参数失败！");
						return;
					}
				}

				// String Message = "X平移（米）："+DelX+"\nY平移（米）："+DelY+"\n
				// 旋转（秒）："+Rotate+"\n 尺度："+Scale;
				//
				// lkmap.Tools.Tools.ShowYesNoMessage(_Dialog.getContext(),
				// "是否按如下参数进行平面转换？\n"+Message, new ICallback(){
				// @Override
				// public void OnClick(String Str, Object ExtraStr) {
				// String SQL = "update T_Project set %1$s where Id=2";
				// SQL = String.format(SQL,
				// "P41='"+(Double.parseDouble(DelX))+"',"+
				// "P42='"+(Double.parseDouble(DelY))+"',"+
				// "P43='"+(Double.parseDouble(Rotate))+"',"+
				// "P44='"+(1/Double.parseDouble(Scale))+"',"+
				// "PMTransMethod='四参转换'");
				//
				// if
				// (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
				// {
				//
				// CoorSystem CS =
				// (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
				// CS.SetPMTransMethodName("四参转换");
				// CS.SetTransToP41(Double.parseDouble(DelX)+"");
				// CS.SetTransToP42(Double.parseDouble(DelY)+"");
				// CS.SetTransToP43(Double.parseDouble(Rotate)+"");
				// double s = 1/Double.parseDouble(Scale) ;
				// CS.SetTransToP44(s+"");
				//
				// _Dialog.dismiss();
				// }
				// else
				// {
				// lkmap.Tools.Tools.ShowMessageBox("保存转换参数失败！");return;
				// }
				// }});
			}

			if (Str.equals("单点校正")) {

				// v1_transformation_addmatchpoint pnca = new
				// v1_transformation_addmatchpoint();
				// pnca.SetMatchPointType(Str);
				// pnca.SetCallback(new ICallback(){
				// @Override
				// public void OnClick(String Str, Object ExtraStr) {
				// isAutoCalc = false;
				// takeParamShow();
				// oldDelX = Double.valueOf(Tools.GetTextValueOnID(_Dialog,
				// R.id.et_DelX)); //X平移
				// oldDelY = Double.valueOf(Tools.GetTextValueOnID(_Dialog,
				// R.id.et_DelY)); //Y平移
				// oldRoute = Double.valueOf(Tools.GetTextValueOnID(_Dialog,
				// R.id.et_Rotate)); //旋转
				// oldScale = Double.valueOf(Tools.GetTextValueOnID(_Dialog,
				// R.id.et_Scale)); //尺度
				//
				// HashMap<String,Object> result =
				// (HashMap<String,Object>)ExtraStr;
				// lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX,
				// result.get("DX")+"");
				// lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY,
				// result.get("DY")+"");
				// lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog,
				// R.id.et_Rotate, result.get("R")+"");
				// lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog,
				// R.id.et_Scale, result.get("K")+"");
				// }});
				// pnca.ShowDialog();

				if (PubVar.CenterCrossShow) {
					v1_transformation_quickmatchpoint quickmatchpoint = new v1_transformation_quickmatchpoint();
					quickmatchpoint.SetMatchPointType("单点校正");
					quickmatchpoint.SetCallback(new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							isAutoCalc = false;
							takeParamShow();
							oldDelX = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_DelX)); // X平移
							oldDelY = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_DelY)); // Y平移
							oldRoute = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_Rotate)); // 旋转
							oldScale = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_Scale)); // 尺度

							HashMap<String, Object> result = (HashMap<String, Object>) ExtraStr;
							lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX, result.get("DX") + "");
							lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY, result.get("DY") + "");
							lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Rotate, result.get("R") + "");
							lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Scale, result.get("K") + "");

						}
					});
					quickmatchpoint.ShowDialog();
				} else {
					Tools.ShowMessageBox("请在系统设置中开启屏幕中心点十字，然后将当前所站位置移动到屏幕中心点！");
				}

			}

			if (Str.equals("四参数")) {

				v1_transformation_calparam tc = new v1_transformation_calparam();
				tc.SetCallParamType("计算四参数");
				tc.SetCallback(new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						isAutoCalc = false;
						takeParamShow();
						HashMap<String, Object> result = (HashMap<String, Object>) ExtraStr;
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX, result.get("DX") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY, result.get("DY") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Rotate, result.get("R") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Scale, result.get("K") + "");
					}
				});
				tc.ShowDialog();
			}

			if (Str.equals("自动计算四参数")) {

				if (!lkmap.Tools.Tools.ReadyGPS(true))
					return;
				v1_Data_Gps_AveragePoint dsap = new v1_Data_Gps_AveragePoint();
				dsap.SetGpsPointCount(5);
				// 镇巴国土资源局：107.901882,32.543125 -114.587,10.020,-0.174
				// 镇巴县高桥乡：107.943145,32.555728
				// 镇巴县凉桥乡：107.724056,32.601089
				// final Coordinate c =
				// StaticObject.soProjectSystem.WGS84ToXY(107.724056,32.601089,0);
				// autoCalFourParam(c);
				dsap.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						Coordinate Coor = (Coordinate) ExtraStr;
						isAutoCalc = true;
						takeParamShow();
						// 获取最近的两个点,计算四参数
						autoCalFourParam(Coor);
					}
				});
				dsap.ShowDialog();

			}

			if (Str.equals("快速校准")) {
				v1_transformation_quickmatchpoint quickmatchpoint = new v1_transformation_quickmatchpoint();
				quickmatchpoint.SetMatchPointType("单点校正");
				quickmatchpoint.SetCallback(new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						isAutoCalc = false;
						takeParamShow();
						oldDelX = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_DelX)); // X平移
						oldDelY = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_DelY)); // Y平移
						oldRoute = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_Rotate)); // 旋转
						oldScale = Double.valueOf(Tools.GetTextValueOnID(_Dialog, R.id.et_Scale)); // 尺度

						HashMap<String, Object> result = (HashMap<String, Object>) ExtraStr;
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX, result.get("DX") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY, result.get("DY") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Rotate, result.get("R") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Scale, result.get("K") + "");

					}
				});
				quickmatchpoint.ShowDialog();
			}

			if (Str.equals("参数管理")) {

				v1_transformation_paramanage tp = new v1_transformation_paramanage();
				tp.SetCallParamType("四参数");
				tp.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						isAutoCalc = false;
						takeParamShow();
						HashMap<String, Object> result = (HashMap<String, Object>) ExtraStr;
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX, result.get("P1") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY, result.get("P2") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Rotate, result.get("P3") + "");
						lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Scale, result.get("P4") + "");
					}
				});
				tp.ShowDialog();
			}

		}
	};

	private void autoCalFourParam(Coordinate coor) {
		// Coordinate gpsPoint = new Coordinate(583540.429, 3793030.552);
		// StaticObject.soProjectSystem.GetCoorSystem().SetCenterMeridian(ProjectSystem.AutoCalCenterJX(108.9058849,34.2600676));
		// Coordinate gpsPoint = Project_GK.GK_BLToXY(108.9058849,34.2600676,
		// StaticObject.soProjectSystem.GetCoorSystem());

		HashMap<String, Object> paramer = new HashMap<String, Object>();
		ConfigDB db = new ConfigDB();
		List<DaDiPoint> allPoint = db.getAllPoint();
		DaDiPoint point1 = null;
		DaDiPoint point2 = null;

		Coordinate coor1 = new Coordinate(0, 0);
		Coordinate coor3 = new Coordinate(0, 0);

		if (StaticObject.soProjectSystem.GetCoorSystem().GetCenterMeridian() == Float.parseFloat("108")) {
			double mixDistant = Double.MAX_VALUE;
			for (DaDiPoint p : allPoint) {
				Coordinate ddPoint = new Coordinate(p.getX108(), p.getY108());
				double dis = Tools.GetTwoPointDistance(coor, ddPoint);
				if (dis <= mixDistant) {
					point1 = p;
					mixDistant = dis;
				}

			}

			if (point1 != null) {
				coor1 = new Coordinate(point1.getX108(), point1.getY108());

				allPoint.remove(point1);

				double mixDistant2 = Double.MAX_VALUE;
				for (DaDiPoint p : allPoint) {
					Coordinate ddPoint = new Coordinate(p.getX108(), p.getY108());
					double dis = Tools.GetTwoPointDistance(coor, ddPoint);
					if (dis <= mixDistant2) {
						point2 = p;
						mixDistant2 = dis;
					}
				}

				coor3 = new Coordinate(point2.getX108(), point2.getY108());
			}
		} else if (StaticObject.soProjectSystem.GetCoorSystem().GetCenterMeridian() == Float.parseFloat("111")) {
			double mixDistant = Double.MAX_VALUE;
			for (DaDiPoint p : allPoint) {
				Coordinate ddPoint = new Coordinate(p.getX111(), p.getY111());
				double dis = Tools.GetTwoPointDistance(coor, ddPoint);
				if (dis <= mixDistant) {
					point1 = p;
					mixDistant = dis;
				}

			}

			if (point1 != null) {
				coor1 = new Coordinate(point1.getX111(), point1.getY111());
				allPoint.remove(point1);

				double mixDistant2 = Double.MAX_VALUE;
				for (DaDiPoint p : allPoint) {
					Coordinate ddPoint = new Coordinate(p.getX111(), p.getY111());
					double dis = Tools.GetTwoPointDistance(coor, ddPoint);
					if (dis <= mixDistant2) {
						point2 = p;
						mixDistant2 = dis;
					}

				}

				coor3 = new Coordinate(point2.getX111(), point2.getY111());

			}

		} else if (StaticObject.soProjectSystem.GetCoorSystem().GetCenterMeridian() == Float.parseFloat("105")) {
			double mixDistant = Double.MAX_VALUE;
			for (DaDiPoint p : allPoint) {
				Coordinate ddPoint = new Coordinate(p.getX105(), p.getY105());
				double dis = Tools.GetTwoPointDistance(coor, ddPoint);
				if (dis <= mixDistant) {
					point1 = p;
					mixDistant = dis;
				}

			}

			if (point1 != null) {
				coor1 = new Coordinate(point1.getX105(), point1.getY105());
				allPoint.remove(point1);

				double mixDistant2 = Double.MAX_VALUE;
				for (DaDiPoint p : allPoint) {
					Coordinate ddPoint = new Coordinate(p.getX105(), p.getY105());
					double dis = Tools.GetTwoPointDistance(coor, ddPoint);
					if (dis <= mixDistant2) {
						point2 = p;
						mixDistant2 = dis;
					}

				}

				coor3 = new Coordinate(point2.getX105(), point2.getY105());

			}
		}

		List<Coordinate> CoorList = new ArrayList<Coordinate>();

		// Double B1 = 34.30537932;
		// Double L1 = 108.9379529;
		// Coordinate coor2 =
		// Project_GK.GK_BLToXY2(L1,B1,StaticObject.soProjectSystem.GetCoorSystem());

		Double L1 = point1.getLonDu() + (point1.getLonFen() + point1.getLonMiao() / 60) / 60;
		Double B1 = point1.getLatDu() + (point1.getLatFen() + point1.getLatMiao() / 60) / 60;
		Coordinate coor2 = Project_GK.GK_BLToXY(L1, B1, StaticObject.soProjectSystem.GetCoorSystem());

		CoorList.add(coor2);

		CoorList.add(coor1);

		Double L2 = point2.getLonDu() + (point2.getLonFen() + point2.getLonMiao() / 60) / 60;
		Double B2 = point2.getLatDu() + (point2.getLatFen() + point2.getLatMiao() / 60) / 60;
		Coordinate coor4 = Project_GK.GK_BLToXY(L2, B2, StaticObject.soProjectSystem.GetCoorSystem());
		CoorList.add(coor4);
		CoorList.add(coor3);
		// lkmap.Tools.Tools.ShowMessageBox("point2 "+point2.getDianhao()+"
		// coor4 x="+coor4.getX()+"/r/n y="+coor4.getY());
		// lkmap.Tools.Tools.ShowMessageBox(" coor3 x="+coor3.getX()+"/r
		// y="+coor3.getY());
		// lkmap.Tools.Tools.ShowMessageBox(" coor4 x="+coor3.getX()+"/r
		// y="+coor3.getY());
		HashMap<String, Object> result = CoorParamTools.CalFourPara(CoorList);

		lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelX, result.get("DX") + "");
		lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_DelY, result.get("DY") + "");
		lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Rotate, result.get("R") + "");
		lkmap.Tools.Tools.SetTextViewValueOnID(_Dialog, R.id.et_Scale, result.get("K") + "");
	}

	private void takeParamShow() {
		EditText delx = (EditText) _Dialog.findViewById(R.id.et_DelX);
		EditText delY = (EditText) _Dialog.findViewById(R.id.et_DelY);
		EditText editRotate = (EditText) _Dialog.findViewById(R.id.et_Rotate);
		EditText editScale = (EditText) _Dialog.findViewById(R.id.et_Scale);

		if (isAutoCalc) {
			delx.setTransformationMethod(PasswordTransformationMethod.getInstance());
			delY.setTransformationMethod(PasswordTransformationMethod.getInstance());
			editRotate.setTransformationMethod(PasswordTransformationMethod.getInstance());
			editScale.setTransformationMethod(PasswordTransformationMethod.getInstance());
		} else {
			delx.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			delY.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			editRotate.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			editScale.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		}
	}

	// 打开工程后的回调
	private ICallback m_Callback = null;

	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// RefreshCheckPointList();
			}
		});
		_Dialog.show();
	}
}
