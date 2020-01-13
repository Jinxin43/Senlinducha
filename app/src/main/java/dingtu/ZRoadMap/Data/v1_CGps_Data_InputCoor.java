package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.ListView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkPartType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Tools.Tools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;

public class v1_CGps_Data_InputCoor {

	private v1_FormTemplate _Dialog = null;

	public v1_CGps_Data_InputCoor() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_cgps_data_inputcoor);
		_Dialog.ReSetSize(0.5f, 0.8f);
		_Dialog.SetCaption("输入坐标序列");

		_Dialog.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("确定") + "  ,确定", pCallback);
		_Dialog.SetButtonInfo("2," + R.drawable.v1_addcheckpoint + "," + Tools.ToLocale("增加坐标") + "  ,增加坐标", pCallback);
	}

	// 上部按钮事件
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("确定")) {
				// 提取坐标串
				List<Coordinate> CoorList = new ArrayList<Coordinate>();
				for (HashMap<String, Object> ho : m_CoorList) {
					Coordinate Coor = new Coordinate();
					Coor.setX(Double.parseDouble(ho.get("XCoor") + ""));
					Coor.setY(Double.parseDouble(ho.get("YCoor") + ""));
					CoorList.add(Coor);
				}

				List<Geometry> pGeometryList = new ArrayList<Geometry>();
				if (CoorList.size() == 0) {
					_Dialog.dismiss();
					return;
				}
				if (m_Dataset.getType() == lkGeoLayerType.enPolyline && CoorList.size() > 1) {
					Polyline PL = new Polyline();
					Part part = new Part();
					part.setVertext(CoorList);
					PL.AddPart(part);
					pGeometryList.add(PL);
				}
				if (m_Dataset.getType() == lkGeoLayerType.enPolygon && CoorList.size() > 2) {
					Polygon PLY = new Polygon();
					Part part = new Part();
					CoorList.add(CoorList.get(0).Clone());
					part.setVertext(CoorList);
					part.SetPartType(lkPartType.enPoly);
					PLY.AddPart(part);
					pGeometryList.add(PLY);
				}
				if (m_Dataset.getType() == lkGeoLayerType.enPoint && CoorList.size() > 0) {
					for (Coordinate Coor : CoorList) {

						Point Pt = new Point();
						Part part = new Part();
						part.getVertexList().add(Coor);
						Pt.AddPart(part);
						pGeometryList.add(Pt);
					}
				}

				if (pGeometryList.size() == 0) {
					Tools.ShowMessageBox(_Dialog.getContext(), "所输坐标无法构建图形，请检查！");
					return;
				}

				// 回退区
				UnRedoParaStru UnRedoPara = new UnRedoParaStru();
				UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;

				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDelete.LayerId = m_Dataset.getId();

				// 保存操作
				v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
				gpsDataObj.SetDataset(m_Dataset);
				for (Geometry pGeometry : pGeometryList) {
					gpsDataObj.SetSYS_ID(-1);
					int newId = -1;
					if (m_Dataset.getType() == lkGeoLayerType.enPoint)
						newId = gpsDataObj.SaveGeoToDb(pGeometry, -1, -1);
					if (m_Dataset.getType() == lkGeoLayerType.enPolyline)
						newId = gpsDataObj.SaveGeoToDb(pGeometry, ((Polyline) pGeometry).getLength(true), -1);
					if (m_Dataset.getType() == lkGeoLayerType.enPolygon)
						newId = gpsDataObj.SaveGeoToDb(pGeometry, ((Polygon) pGeometry).getLength(true),
								((Polygon) pGeometry).getArea(true));
					if (newId >= 0) {
						urDelete.ObjectIdList.add(newId);
					}
				}
				urDataItem.DataList.add(urDelete);
				UnRedoPara.DataItemList.add(urDataItem);
				IUnRedo.AddHistory(UnRedoPara);
				PubVar.m_Map.Refresh();
				_Dialog.dismiss();
			}

			if (Str.equals("编辑")) {
				HashMap<String, Object> ho = (HashMap<String, Object>) ExtraStr;
				v1_CGps_Data_InputCoor_Input cdii = new v1_CGps_Data_InputCoor_Input();
				cdii.SetEditItem(ho);
				cdii.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_List_Adpter.notifyDataSetChanged();
					}
				});

				cdii.ShowDialog();
			}

			if (Str.equals("删除")) {
				final HashMap<String, Object> ho = (HashMap<String, Object>) ExtraStr;
				Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除【" + ho.get("XH") + "】号点？", new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_CoorList.remove(ho);
						for (int i = 0; i < m_CoorList.size(); i++) {
							HashMap<String, Object> ho = m_CoorList.get(i);
							ho.put("XH", i + 1);
						}

						m_List_Adpter.notifyDataSetChanged();
					}
				});
			}

			if (Str.equals("增加坐标")) {
				v1_CGps_Data_InputCoor_Input cdii = new v1_CGps_Data_InputCoor_Input();
				cdii.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						Coordinate Coor = (Coordinate) ExtraStr;
						HashMap<String, Object> ho = new HashMap<String, Object>();
						ho.put("XH", m_CoorList.size() + 1);
						ho.put("XCoor", Coor.getX());
						ho.put("YCoor", Coor.getY());
						m_CoorList.add(ho);
						m_List_Adpter.notifyDataSetChanged();
					}
				});
				cdii.ShowDialog();
			}
		}
	};

	private Dataset m_Dataset = null;

	public void SetDataset(Dataset pDataset) {
		this.m_Dataset = pDataset;
	}

	private List<HashMap<String, Object>> m_CoorList = new ArrayList<HashMap<String, Object>>();
	private v1_LayerList_Adpter m_List_Adpter = null;

	/**
	 * 加载图层列表信息
	 */
	private void LoadLayerInfo() {
		if (this.m_List_Adpter == null) {
			this.m_List_Adpter = new v1_LayerList_Adpter(_Dialog.getContext(), this.m_CoorList,
					R.layout.v1_bk_cgps_data_inputcoor_item, new String[] { "XH", "XCoor", "YCoor" },
					new int[] { R.id.tv_xh, R.id.tv_xcoor, R.id.tv_ycoor, R.id.bt_edit, R.id.bt_delete });

		}
		ListView lvList = (ListView) _Dialog.findViewById(R.id.lvList);
		lvList.setAdapter(this.m_List_Adpter);
		this.m_List_Adpter.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				pCallback.OnClick(Str, ExtraStr);
			}
		});

		// lvList.setOnItemClickListener(new OnItemClickListener(){
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, final int
		// arg2,long arg3) {
		// ListView lvList = (ListView)arg0;
		// v1_LayerList_Adpter la = (v1_LayerList_Adpter)lvList.getAdapter();
		// la.SetSelectItemIndex(arg2);
		// la.notifyDataSetChanged();
		// }});
	}

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerInfo();
			}
		});
		_Dialog.show();
	}
}
