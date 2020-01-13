package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ListView;
import android.widget.ScrollView;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkMapFileType;
import lkmap.Enum.lkRenderType;
import lkmap.Layer.GeoLayer;
import lkmap.Render.SimpleRender;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.DataImport.DataImport_Photo;
import lkmap.ZRoadMap.DataImport.DataImport_Shp;
import lkmap.ZRoadMap.DataImport.DataImport_VMX;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_VectorSet_Adpter;

public class LayerManger {

	private v1_FormTemplate dialogView = null;
	private List<v1_Layer> copiedLayers = null;// ������ͼ�㣬���ڱ༭
	private List<HashMap<String, Object>> layerItemList = null;
	private v1_Layer m_SelectLayer = null;// ��ǰ���б����ѡ�е�ͼ��
	private v1_Layer mBGLayer;
	private v1_LayerList_Adpter m_LayerList_Adpter = null;
	private v1_Project_Layer_BKMap_VectorSet_Adpter bkLayer_Adpter = null;
	private List<HashMap<String, Object>> gridBKLayerList = null;
	private List<HashMap<String, Object>> vectorBKLayerList = null;

	public LayerManger() {

		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.d_layermanager);
		dialogView.ReSetSize(0.76f, 0.86f);

		dialogView.SetCaption(Tools.ToLocale("ͼ�����"));
		dialogView.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("ȷ��") + "  ,ȷ��",
				btnCallback);
		// dialogView.SetButtonInfo("2,"+R.drawable.v1_newlayer_shp+","+Tools.ToLocale("����ͼ��")+"
		// ,����ͼ��", btnCallback);
		// dialogView.SetButtonInfo("3,"+R.drawable.v1_newlayer+","+Tools.ToLocale("�½�ͼ��")+"
		// ,�½�ͼ��", btnCallback);

		dialogView.findViewById(R.id.btn_createlayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_importlayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_vectormanger).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_gridmanger).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_createTemplate).setOnClickListener(new ViewClick());

		dialogView.findViewById(R.id.btn_hiddendatalayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_showdatalayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_showvectorlayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_hiddenvectorlayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_hiddengridlayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_showgridlayer).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_ImageEnhance).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.btn_importphotos).setOnClickListener(new ViewClick());
	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			DoCommand(Tag);
		}
	}

	private void DoCommand(String strCommond) {
		if (strCommond.equals("���زɼ�ͼ��")) {
			dialogView.findViewById(R.id.btn_showdatalayer).setVisibility(View.VISIBLE);
			dialogView.findViewById(R.id.btn_hiddendatalayer).setVisibility(View.GONE);
			dialogView.findViewById(R.id.lvList).setVisibility(View.GONE);
		}

		if (strCommond.equals("��ʾ�ɼ�ͼ��")) {
			dialogView.findViewById(R.id.btn_hiddendatalayer).setVisibility(View.VISIBLE);
			dialogView.findViewById(R.id.btn_showdatalayer).setVisibility(View.GONE);
			dialogView.findViewById(R.id.lvList).setVisibility(View.VISIBLE);
		}

		if (strCommond.equals("����ʸ����ͼ")) {
			dialogView.findViewById(R.id.btn_showvectorlayer).setVisibility(View.VISIBLE);
			dialogView.findViewById(R.id.btn_hiddenvectorlayer).setVisibility(View.GONE);
			dialogView.findViewById(R.id.lvVectorBKBKList).setVisibility(View.GONE);
		}

		if (strCommond.equals("��ʾʸ����ͼ")) {
			dialogView.findViewById(R.id.btn_hiddenvectorlayer).setVisibility(View.VISIBLE);
			dialogView.findViewById(R.id.btn_showvectorlayer).setVisibility(View.GONE);
			dialogView.findViewById(R.id.lvVectorBKBKList).setVisibility(View.VISIBLE);
		}

		if (strCommond.equals("����դ���ͼ")) {
			dialogView.findViewById(R.id.btn_hiddengridlayer).setVisibility(View.GONE);
			dialogView.findViewById(R.id.btn_showgridlayer).setVisibility(View.VISIBLE);
			dialogView.findViewById(R.id.lvGridBKList).setVisibility(View.GONE);
		}

		if (strCommond.equals("��ʾդ���ͼ")) {
			dialogView.findViewById(R.id.btn_hiddengridlayer).setVisibility(View.VISIBLE);
			dialogView.findViewById(R.id.btn_showgridlayer).setVisibility(View.GONE);
			dialogView.findViewById(R.id.lvGridBKList).setVisibility(View.VISIBLE);
		}

		if (strCommond.equals("���뺽����Ƭ")) {
			lkmap.ZRoadMap.ToolsBox.v1_selectdictionary sd = new lkmap.ZRoadMap.ToolsBox.v1_selectdictionary();
			sd.isSelectFolder = true;
			sd.SetCallback(new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					DataImport_Photo dataImport_Photo = new DataImport_Photo();
					for (String path : (List<String>) ExtraStr) {
						v1_Layer pLayer = dataImport_Photo.CreateLayerByPhoto(path);
						copiedLayers.add(pLayer);
						LoadLayerInfo();
						m_LayerList_Adpter.notifyDataSetChanged();
					}

				}
			});
			sd.ShowDialog("");

		}
		
		// ����ͼ��
		if (strCommond.equals("����ͼ��")) {
			lkmap.ZRoadMap.ToolsBox.v1_selectdictionary sd = new lkmap.ZRoadMap.ToolsBox.v1_selectdictionary();

			sd.SetFileFilter(new String[] { "SHP", "DTV", "VMX" });
			sd.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, final Object ExtraStrT) {

					String StartTime = Tools.GetSystemDate();
					List<String> importFileList = (List<String>) ExtraStrT;
					for (String importFile : importFileList) {
						// �����ļ����Ͳ�ͬ���е�������
						String FileType = importFile.substring(importFile.length() - 3, importFile.length());
						if (FileType.toUpperCase().equals("SHP")) {
							DataImport_Shp diSHP = new DataImport_Shp();
							v1_Layer pLayer = diSHP.CreateLayerByShp(importFile);
							if (pLayer != null)
								copiedLayers.add(pLayer);
						}
						if (FileType.toUpperCase().equals("VMX") || FileType.toUpperCase().equals("DTV")) {
							DataImport_VMX diVMX = new DataImport_VMX();
							List<v1_Layer> pLayerList = diVMX.CreateLayerByVMX(importFile);

							for (v1_Layer pLayer : pLayerList)
								copiedLayers.add(pLayer);
						}
					}
					String EndTime = Tools.GetSystemDate();
					// Tools.ShowMessageBox(dialogView.getContext(),"��ʼʱ�䣺"+StartTime+"\r\n����ʱ�䣺"+EndTime);
					LoadLayerInfo(); // ���¼���ͼ���б�

					// Tools.OpenDialog("���ڵ���ͼ��...", new ICallback(){
					// @Override
					// public void OnClick(String Str, Object ExtraStr) {
					// String StartTime = Tools.GetSystemDate();
					// List<String> importFileList = (List<String>)ExtraStrT;
					// for(String importFile:importFileList)
					// {
					// //�����ļ����Ͳ�ͬ���е�������
					// String FileType =
					// importFile.substring(importFile.length()-3,
					// importFile.length());
					// if (FileType.toUpperCase().equals("SHP"))
					// {
					// DataImport_Shp diSHP = new DataImport_Shp();
					// v1_Layer pLayer = diSHP.CreateLayerByShp(importFile);
					// if (pLayer!=null)copiedLayers.add(pLayer);
					// }
					// if (FileType.toUpperCase().equals("VMX"))
					// {
					// DataImport_VMX diVMX = new DataImport_VMX();
					// List<v1_Layer> pLayerList =
					// diVMX.CreateLayerByVMX(importFile);
					// for(v1_Layer pLayer:pLayerList)
					// copiedLayers.add(pLayer);
					// }
					// }
					// String EndTime = Tools.GetSystemDate();
					// Tools.ShowMessageBox(dialogView.getContext(),"��ʼʱ�䣺"+StartTime+"\r\n����ʱ�䣺"+EndTime);
					// //LoadLayerInfo(); //���¼���ͼ���б�
					//
					// }});

				}
			});
			sd.ShowDialog("");
		}

		// �½�ͼ��
		if (strCommond.equals("�½�ͼ��")) {
			LayerManagerCreateDataLayer pln = new LayerManagerCreateDataLayer();
			pln.SetHaveLayerList(copiedLayers);
			pln.SetCallback(new ICallback() { // �½�ͼ�㣬�ص���־��ͼ�㣬Obj=��ͼ����
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					v1_Layer newLayer = (v1_Layer) ExtraStr;
					if (createOrUpdateLayer(newLayer)) {
						copiedLayers.add(newLayer);
						PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList().add(newLayer);
						// saveLayerInfo();
						LoadLayerInfo(); // ���¼���ͼ���б�
					}

				}
			});

			pln.ShowDialog();
			return;
		}

		// �½�ͼ��
		if (strCommond.equals("ͼ�����")) {
			ImageEnhance imageEnhance = new ImageEnhance();

			imageEnhance.ShowDialog();
			return;
		}

		// vectorlayer setting
		if (strCommond.equals("դ���ͼ����")) {
			v1_project_layer_bkmap plb = new v1_project_layer_bkmap();
			plb.SetBKMapType(lkMapFileType.enGrid);
			plb.SetMapFileList(
					PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList());
			plb.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					loadGridBKLayer();
				}
			});
			plb.ShowDialog();
		}

		// gridlayer setting
		if (strCommond.equals("ʸ����ͼ����")) {
			v1_project_layer_bkmap plb = new v1_project_layer_bkmap();
			plb.SetBKMapType(lkMapFileType.enVector);
			plb.SetMapFileList(
					PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList());
			plb.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					loadVectorBKLayer();
				}
			});
			plb.ShowDialog();
		}

		if (strCommond.equals("ʸ��ͼ������")) {
			LayerManager_VectorLayerSetting vectorLayerSetting = new LayerManager_VectorLayerSetting();
			vectorLayerSetting.SetEditLayer(mBGLayer);
			vectorLayerSetting.ShowDialog();
		}

		if (strCommond.equals("����")) {
			v1_project_layer_new pln = new v1_project_layer_new();
			pln.SetHaveLayerList(this.copiedLayers);
			// if(m_SelectLayer.GetLayerProjecType().isEmpty())
			// {
			// m_SelectLayer.SetLayerProjectType("�Զ��幤��");
			// }
			pln.SetEditLayer(this.m_SelectLayer);
			pln.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					((v1_Layer) ExtraStr).CopyTo(m_SelectLayer);
					// ������ʾ�б�
					for (HashMap<String, Object> lyr : layerItemList) {
						if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID())) {
							lyr.put("D3", m_SelectLayer.GetLayerAliasName()); // ͼ������
						}
					}
					m_LayerList_Adpter.notifyDataSetChanged();
					m_SelectLayer.SetEditMode(lkEditMode.enEdit);

				}
			});
			pln.ShowDialog();
			return;
		}

		if (strCommond.equals("����")) {
			// ����ֱ������ͼ����ŶԻ���
			if (m_SelectLayer.GetRenderType() == lkRenderType.enSimple) {
				v1_project_layer_render_symbolexplorer plrs = new v1_project_layer_render_symbolexplorer();
				plrs.SetGeoLayerType(m_SelectLayer.GetLayerType());
				plrs.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_SelectLayer.SetSimpleSymbol(((v1_SymbolObject) ExtraStr).SymbolBase64Str);

						// ��ȡ�����̵�ͼ���б�
						for (HashMap<String, Object> lyr : layerItemList) {
							if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID())) {
								lyr.put("D4", m_SelectLayer.GetSymbolFigure()); // ͼ�����ָʾͼ
							}
						}
						m_LayerList_Adpter.notifyDataSetChanged();
						if (m_SelectLayer.GetEditMode() != lkEditMode.enNew)
							m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					}
				});
				v1_SymbolObject SO = new v1_SymbolObject();
				SO.SymbolBase64Str = m_SelectLayer.GetSimpleSymbol();
				SO.SymbolFigure = m_SelectLayer.GetSymbolFigure();
				plrs.SetDefaultSymbolObject(SO);
				plrs.ShowDialog();
			}
			if (m_SelectLayer.GetRenderType() == lkRenderType.enUniqueValue) {
				v1_project_layer_render_uniquevalue plru = new v1_project_layer_render_uniquevalue();
				plru.SetEditLayer(m_SelectLayer);
				plru.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (m_SelectLayer.GetEditMode() != lkEditMode.enNew)
							m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					}
				});
				plru.ShowDialog();
			}
		}

		if (strCommond.equals("��Ⱦ")) {
			v1_project_layer_render plr = new v1_project_layer_render();
			plr.SetEditLayer(this.m_SelectLayer);
			plr.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					// ��ȡ�����̵�ͼ���б�
					for (HashMap<String, Object> lyr : layerItemList) {
						if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID())) {
							lyr.put("D4", m_SelectLayer.GetSymbolFigure()); // ͼ�����ָʾͼ
						}
					}
					m_LayerList_Adpter.notifyDataSetChanged();
					if (m_SelectLayer.GetEditMode() != lkEditMode.enNew)
						m_SelectLayer.SetEditMode(lkEditMode.enEdit);
				}
			});
			plr.ShowDialog();
			return;
		}

		if (strCommond.equals("����") || strCommond.equals("����")) {
			// ��ͼ���б��л�ȡ��ǰѡ�еİ���Ϣ
			int idx = 0;
			HashMap<String, Object> moveObj = null;
			for (int i = 0; i < this.layerItemList.size(); i++) {
				HashMap<String, Object> lyr = this.layerItemList.get(i);
				if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID())) {
					moveObj = lyr;
					idx = i;
				}
			}

			// ���ϡ�������
			if (idx == 0 && strCommond.toString().equals("����")) {
				Tools.ShowToast(dialogView.getContext(), "�Ѿ������ϲ㣡");
				return;
			}
			if (idx == this.layerItemList.size() - 1 && strCommond.toString().equals("����")) {
				Tools.ShowToast(dialogView.getContext(), "�Ѿ������²㣡");
				return;
			}
			if (strCommond.toString().equals("����"))
				idx--;
			if (strCommond.toString().equals("����"))
				idx++;

			this.layerItemList.remove(moveObj);
			this.layerItemList.add(idx, moveObj);

			this.copiedLayers.remove(this.m_SelectLayer);
			this.copiedLayers.add(idx, this.m_SelectLayer);

			this.m_LayerList_Adpter.notifyDataSetChanged();
			this.GetSelectLayerByHO(moveObj);
		}

		if (strCommond.equals("ɾ��")) {
			Tools.ShowYesNoMessage(dialogView.getContext(),
					Tools.ToLocale("�Ƿ�ɾ��ͼ��") + "��" + this.m_SelectLayer.GetLayerAliasName() + "����", new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								for (int i = 0; i < layerItemList.size(); i++) {
									HashMap<String, Object> lyr = layerItemList.get(i);
									if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID())) {
										layerItemList.remove(lyr);
										m_LayerList_Adpter.notifyDataSetChanged();
										m_SelectLayer.SetEditMode(lkEditMode.enDelete);
										return;
									}
								}
							}

						}
					});
		}

		// ����ͼ��ģ��
		if (strCommond.equals("��ģ��")) {
			List<v1_Layer> pLayerList = new ArrayList<v1_Layer>();
			for (v1_Layer pLayer : this.copiedLayers) {
				if (pLayer.GetEditMode() != lkEditMode.enDelete)
					pLayerList.add(pLayer);
			}
			if (pLayerList.size() == 0) {
				Tools.ShowMessageBox(dialogView.getContext(), "ͼ���б�����Ϊ0���޷�����ģ�壡");
				return;
			}
			v1_project_layer_savetemplate pls = new v1_project_layer_savetemplate();
			pls.SetLayerList(pLayerList);
			pls.ShowDialog();
		}

		// ��ȡͼ��ģ��
		if (strCommond.equals("��ģ��")) {
			v1_project_layer_loadtemplate pls = new v1_project_layer_loadtemplate();
			pls.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					// ͼ��ģ��ѡ�к�Ļص�
					if (Str.equals("ģ���б�")) {
						final List<v1_Layer> templateLayerList = (List<v1_Layer>) ExtraStr;
						if (copiedLayers.size() > 0) {
							Tools.ShowYesNoMessage(dialogView.getContext(), "�Ƿ�Ҫ�������ͼ���б�����ģ��ͼ�㣿", new ICallback() {
								@Override
								public void OnClick(String Str, Object ExtraStr) {
									if (Str.equals("YES"))
										UpdateLayerListByTemplate(templateLayerList);
								}
							});
						} else
							UpdateLayerListByTemplate(templateLayerList);

					}

				}
			});
			pls.ShowDialog();
		}

	}

	private void UpdateLayerListByTemplate(List<v1_Layer> templateLayerList) {
		// ������ͼ��ȫ����Ϊ"delete"
		for (v1_Layer vLayer : copiedLayers)
			vLayer.SetEditMode(lkEditMode.enDelete);

		// ��ģ���б��ͼ����Ϊ"new"
		for (v1_Layer vLayer : templateLayerList) {
			vLayer.SetEditMode(lkEditMode.enNew);
			copiedLayers.add(vLayer);
		}
		// ���¼���ͼ���б�
		LoadLayerInfo();
	}

	private ICallback m_Callback = null;

	public void SetCallback(ICallback callBack) {
		this.m_Callback = callBack;
	}

	private void loadGridBKLayer() {
		if (this.gridBKLayerList == null) {
			this.gridBKLayerList = new ArrayList<HashMap<String, Object>>();
		}
		this.gridBKLayerList.clear();

		List<HashMap<String, Object>> bkGridList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer()
				.GetGridLayerExplorer().GetBKFileList();
		Tools.SetTextViewValueOnID(this.dialogView, R.id.tx_bkgridlayercount, bkGridList.size() + "");

		for (HashMap<String, Object> ho : bkGridList) {
			HashMap<String, Object> feature = new HashMap<String, Object>();
			feature.put("UUID", UUID.randomUUID().toString());
			feature.put("BKMapFile", ho.get("BKMapFile"));
			feature.put("D1", ho.get("Visible")); // �ɼ���
			feature.put("D2", ho.get("BKMapFile"));
			feature.put("D3", Integer.parseInt(ho.get("Transparent") + ""));

			this.gridBKLayerList.add(feature);
		}
		if (this.bkLayer_Adpter == null)
			this.bkLayer_Adpter = new v1_Project_Layer_BKMap_VectorSet_Adpter(dialogView.getContext(),
					this.gridBKLayerList, R.layout.c_bkmap_griditem, new String[] { "D1", "D2", "D3" },
					new int[] { R.id.rp_itemlayout1, R.id.rp_itemtext, R.id.rp_itemlayout2, R.id.bt_moveup,
							R.id.bt_movedown });

		this.bkLayer_Adpter.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				HashMap<String, Object> ho = (HashMap<String, Object>) ExtraStr;
				if (Str.equals("����"))
					move(ho, -1);
				if (Str.equals("����"))
					move(ho, 1);
			}
		});

		(((ListView) dialogView.findViewById(R.id.lvGridBKList))).setAdapter(this.bkLayer_Adpter);
		this.bkLayer_Adpter.notifyDataSetChanged();
	}

	// ���£����ϣ��ƶ�һ��λ��
	private void move(HashMap<String, Object> HO, int Step) {
		// ����ָ��ʵ���λ��
		int Pos = -1;
		for (int i = 0; i < this.gridBKLayerList.size(); i++) {
			HashMap<String, Object> ho = this.gridBKLayerList.get(i);
			if (ho.get("UUID").equals(HO.get("UUID")))
				Pos = i;
		}
		Pos += Step;

		// ���ϡ�������
		if (Pos < 0) {
			Tools.ShowToast(dialogView.getContext(), "�Ѿ������ϲ㣡");
			return;
		}
		if (Pos > this.gridBKLayerList.size() - 1) {
			Tools.ShowToast(dialogView.getContext(), "�Ѿ������²㣡");
			return;
		}

		this.gridBKLayerList.remove(HO);
		this.gridBKLayerList.add(Pos, HO);
		this.bkLayer_Adpter.notifyDataSetChanged();
		this.bkLayer_Adpter.SetSelectItemIndex(Pos);
	}

	private void loadVectorBKLayer() {
		if (this.vectorBKLayerList == null) {
			this.vectorBKLayerList = new ArrayList<HashMap<String, Object>>();
		}
		this.vectorBKLayerList.clear();

		// //�������͸��������
		// List<HashMap<String,Object>> vectorBKLayerList =
		// PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList();
		// Tools.SetTextViewValueOnID(this.dialogView,
		// R.id.tx_bkvertorlayercount, vectorBKLayerList.size()+"");
		// for(HashMap<String,Object> ho :vectorBKLayerList)
		// {
		// HashMap<String,Object> feature = new HashMap<String,Object>();
		// feature.put("UUID", UUID.randomUUID().toString());
		// feature.put("D1", ho.get("Visible"));
		// feature.put("D2", ho.get("BKMapFile"));
		// feature.put("D3", Integer.parseInt(ho.get("Transparent")+""));
		// this.vectorBKLayerList.add(feature);
		// }

		List<v1_Layer> layerList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
				.GetLayerList();
		Tools.SetTextViewValueOnID(this.dialogView, R.id.tx_bkvertorlayercount, layerList.size() + "");
		for (v1_Layer pLayer : layerList) {
			// if (pLayer.GetLayerType()==lkGeoLayerType.enPolygon)
			// {
			HashMap<String, Object> feature = new HashMap<String, Object>();
			feature.put("LayerID", pLayer.GetLayerID());
			feature.put("D1", pLayer.GetVisible());
			feature.put("D2", pLayer.GetLayerAliasName());
			feature.put("D3", pLayer.GetSelectable());
			feature.put("D4", pLayer.GetTransparet());
			this.vectorBKLayerList.add(feature);
			// }
		}

		LayerManagerVectorSetAdapter adapter = new LayerManagerVectorSetAdapter(dialogView.getContext(),
				this.vectorBKLayerList, R.layout.c_bkmap_vectoritem, new String[] { "D1", "D2", "D3", "D4" },
				new int[] { R.id.rp_itemlayout1, R.id.rp_itemtext, R.id.ck_selectable, R.id.rp_itemlayout2,
						R.id.iv_feature });

		adapter.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				String layerName = ((HashMap<String, Object>) ExtraStr).get("LayerID") + "";
				mBGLayer = null;
				for (v1_Layer layer : PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
						.GetLayerList()) {
					if (layer.GetLayerID().equals(layerName)) {
						mBGLayer = layer;
					}
				}
				DoCommand(Str);
			}
		});

		ListView listView = (((ListView) dialogView.findViewById(R.id.lvVectorBKBKList)));
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		for (v1_Layer pLayer : layerList) {
			GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground)
					.GetLayerById(pLayer.GetLayerID());
			pGeoLayer.setVisible(pLayer.GetVisible());

			if (pGeoLayer.getRender().getType() == lkRenderType.enSimple) {
				((SimpleRender) pGeoLayer.getRender()).SetSymbolTransparent(pLayer.GetTransparet());
			}
			if (pGeoLayer.getRender().getType() == lkRenderType.enUniqueValue) {
				((UniqueValueRender) pGeoLayer.getRender()).SetSymbolTransparent(pLayer.GetTransparet());
			}
		}

		if (PubVar.VectorBGEditable) {
			// ѡ��ʸ����ͼ
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
					ListView lvList = (ListView) arg0;
					LayerManagerVectorSetAdapter la = (LayerManagerVectorSetAdapter) lvList.getAdapter();
					la.SetSelectItemIndex(arg2);
					la.notifyDataSetChanged();

					// ��ȡ��ǰѡ�е�ͼ��
					HashMap<String, Object> selectObj = (HashMap<String, Object>) la.getItem(arg2);
					GetSelectLayerByHO(selectObj);
					v1_Layer layer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
							.GetLayerByID(selectObj.get("LayerID") + "");

					if (m_Callback != null)
						m_Callback.OnClick("Vector", layer);
					dialogView.dismiss();
				}
			});
		}

		// ����ƫ��������
		// Tools.SetTextViewValueOnID(dialogView, R.id.etOffsetX,
		// PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetX()+"");
		// Tools.SetTextViewValueOnID(dialogView, R.id.etOffsetY,
		// PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetY()+"");
	}

	private void LoadLayerInfo() {
		// ���õ�ͼͼ������
		// int VectorCount =
		// PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList().size();
		// Tools.SetTextViewValueOnID(this.dialogView, R.id.bt_vectorcount,
		// VectorCount+"");

		// int GridCount =
		// PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList().size();
		// Tools.SetTextViewValueOnID(this.dialogView, R.id.bt_gridcount,
		// GridCount+"");
		loadGridBKLayer();
		loadVectorBKLayer();

		// ��ȡ�����̵�ͼ���б�
		if (this.layerItemList == null)
			this.layerItemList = new ArrayList<HashMap<String, Object>>();
		this.layerItemList.clear();

		for (v1_Layer lyr : this.copiedLayers) {
			Tools.SetTextViewValueOnID(this.dialogView, R.id.tx_datalayercount, this.copiedLayers.size() + "");

			if (lyr.GetEditMode() == lkEditMode.enDelete)
				continue;
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("UUID", UUID.randomUUID().toString()); // ΨһIdֵ
			hm.put("LayerID", lyr.GetLayerID()); // ͼ��ID�����ڱ�ʶΨһͼ��
			hm.put("D1", lyr.GetVisible()); // �ɼ���

			// ͼ�����͵�ͼƬ��ʽ
			int resourceId = R.drawable.v1_layertype_point;
			if (lyr.GetLayerTypeName().equals("��"))
				resourceId = R.drawable.v1_layertype_point;
			if (lyr.GetLayerTypeName().equals("��"))
				resourceId = R.drawable.v1_layertype_line;
			if (lyr.GetLayerTypeName().equals("��"))
				resourceId = R.drawable.v1_layertype_poly;
			hm.put("D2", Tools.GetBitmapByResources(resourceId)); // ͼ��������ʽͼƬ
			hm.put("D3", lyr.GetLayerAliasName()); // ͼ������
			hm.put("D4", lyr.GetSymbolFigure()); // ͼ�����ָʾͼ
			hm.put("D5", lyr.GetSelectable()); // �Ƿ��ѡ��
			this.layerItemList.add(hm);
		}

		if (this.m_LayerList_Adpter == null) {
			this.m_LayerList_Adpter = new v1_LayerList_Adpter(dialogView.getContext(), this.layerItemList,
					R.layout.c_datalayer_selectitem, new String[] { "D1", "D2", "D3", "D4", "D5" },
					new int[] { R.id.tb_visible, R.id.iv_layertype, R.id.tv_layername, R.id.iv_symbol,
							R.id.ck_selectable, R.id.iv_render, R.id.iv_feature, R.id.iv_moveup, R.id.iv_movedown,
							R.id.iv_delete });
		}
		ListView lvList = (ListView) dialogView.findViewById(R.id.lvList);
		lvList.setAdapter(this.m_LayerList_Adpter);
		this.m_LayerList_Adpter.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				GetSelectLayerByHO((HashMap<String, Object>) ExtraStr);
				DoCommand(Str);
			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				ListView lvList = (ListView) arg0;
				v1_LayerList_Adpter la = (v1_LayerList_Adpter) lvList.getAdapter();
				la.SetSelectItemIndex(arg2);
				la.notifyDataSetChanged();

				// ��ȡ��ǰѡ�е�ͼ��
				HashMap<String, Object> selectObj = (HashMap<String, Object>) la.getItem(arg2);
				GetSelectLayerByHO(selectObj);

				if (m_Callback != null)
					m_Callback.OnClick("", m_SelectLayer);
				dialogView.dismiss();
			}
		});

		// ѡ��Ĭ��ֵ
		for (HashMap<String, Object> ho : this.layerItemList) {
			if (ho.get("LayerID").equals(PubVar.m_DoEvent.m_GpsInfoManage.GetCurrentLayerId())) {
				GetSelectLayerByHO(ho);
			}
		}
	}

	private void GetSelectLayerByHO(HashMap<String, Object> ho) {
		// ��ȡ��ǰѡ�е�ͼ��
		this.m_SelectLayer = null;
		HashMap<String, Object> selectObj = (HashMap<String, Object>) ho;
		String LayerID = selectObj.get("LayerID").toString();
		for (v1_Layer lyr : this.copiedLayers) {
			if (lyr.GetLayerID().equals(LayerID)) {
				this.m_SelectLayer = lyr;
			}
		}

		for (int idx = 0; idx < this.layerItemList.size(); idx++) {
			HashMap<String, Object> hox = this.layerItemList.get(idx);
			if (hox.get("UUID").equals(ho.get("UUID"))) {
				ListView lvList = (ListView) dialogView.findViewById(R.id.lvList);
				v1_LayerList_Adpter la = (v1_LayerList_Adpter) lvList.getAdapter();
				la.SetSelectItemIndex(idx);
				la.notifyDataSetChanged();
			}
		}
	}

	// ������ť�¼�
	private ICallback btnCallback = new ICallback() {

		@Override
		public void OnClick(String Str, Object ExtraStr) {
			// TODO Auto-generated method stub

			if (Str.equals("ȷ��")) {
				Tools.OpenDialog("���ڱ���ͼ������...", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						// TODO Auto-generated method stu
						if (saveLayerInfo()) {
							dialogView.dismiss();
						}
						if (m_Callback != null) {
							m_Callback.OnClick("", m_SelectLayer);
							PubVar.m_DoEvent.m_MainBottomToolBar.ClearButtonSelect();
							PubVar.m_MapControl.setActiveTool(lkmap.MapControl.Tools.ZoomInOutPan);
						}
					}
				});
			}

		}
	};

	private boolean saveLayerInfo() {
		// �ȴ���ɾ����ͼ��
		int layerCount = this.copiedLayers.size();
		for (int i = layerCount - 1; i >= 0; i--) {
			v1_Layer layer = this.copiedLayers.get(i);
			if (layer.GetEditMode() == lkEditMode.enDelete) {
				String delSql = "delete from T_Layer where LayerId ='%1$s'";
				delSql = String.format(delSql, layer.GetLayerID());
				if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(delSql)) {
					layer.SetEditMode(lkEditMode.enUnkonw);

					PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).Remove(layer.GetLayerID());
					boolean okay = PubVar.m_Workspace.GetDataSourceByEditing().RemoveDataset(layer.GetLayerID());
					if (okay) {
						this.copiedLayers.remove(i);
					}
				} else {
					Tools.ShowMessageBox(this.dialogView.getContext(), "ɾ��ͼ��ʧ�ܣ�");
				}
			}
		}

		// Ȼ�����������޸�
		List<String> sortLayerList = new ArrayList<String>();
		int sort = 0;

		// save backgroud layer
		// PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().SaveLayerFormLayerList(this.copiedLayers);

		for (v1_Layer layer : copiedLayers) {
			sortLayerList.add("when LayerID='" + layer.GetLayerID() + "' then '" + sort + "'");
			sort++;

			for (HashMap<String, Object> hashObj : this.layerItemList) {
				if (hashObj.get("LayerID").toString().equals(layer.GetLayerID())) {
					boolean visible = Boolean.parseBoolean(hashObj.get("D1").toString());
					layer.SetVisible(visible);
					boolean isSelect = Boolean.parseBoolean(hashObj.get("D5").toString());
					layer.SetSelectable(isSelect);
					PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerById(layer.GetLayerID())
							.setSelectable(isSelect);
					if (layer.GetEditMode() != lkEditMode.enNew) {
						layer.SetEditMode(lkEditMode.enEdit);
					}

				}
			}
			createOrUpdateLayer(layer);
		}

		PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().LoadLayer();

		// save sort of layers
		if(copiedLayers.size()>0)
		{
			String sortSql = "Update T_Layer Set SortId = case " + Tools.JoinT(" ", sortLayerList) + " end";
			PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sortSql);
		}
		

		for (int idx = 0; idx < this.copiedLayers.size(); idx++) {
			v1_Layer layer = this.copiedLayers.get(idx);
			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).MoveTo(layer.GetLayerID(), idx);
			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerById(layer.GetLayerID())
					.setVisible(layer.GetVisible());
		}

		saveVectorBKLayer();
		saveBKGridLayer();

		// refersh view
		PubVar.m_Map.Refresh();
		return true;
	}

	private boolean saveBKGridLayer() {
		for (int i = 0; i < this.gridBKLayerList.size(); i++) {
			HashMap<String, Object> hashMap = this.gridBKLayerList.get(i);
			// ͸����
			int transparent = Integer.parseInt(hashMap.get("D3").toString());

			// �ɼ���
			boolean visible = Boolean.parseBoolean(hashMap.get("D1").toString());

			String MapFileName = hashMap.get("D2") + "";

			List<HashMap<String, Object>> hoList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer()
					.GetGridLayerExplorer().GetBKFileList();
			for (HashMap<String, Object> ho : hoList) {
				if (ho.get("BKMapFile").equals(MapFileName)) {
					ho.put("Transparent", transparent);
					ho.put("Visible", visible);
					ho.put("Sort", i);
				}
			}
		}

		if (PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().SaveBKLayer()) {
			PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().LoadBKLayer();
			PubVar.m_Map.GetGridLayers().GetList().clear();
			PubVar.m_Map.GetGridLayers().SetMapFileList(
					PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList());
			PubVar.m_Map.FastRefresh();
			return true;
		}

		return false;
	}

	private boolean saveVectorBKLayer() {
		List<HashMap<String, Object>> hoList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer()
				.GetVectorLayerExplorer().GetBKFileList();
		List<v1_Layer> layerList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
				.GetLayerList();

		for (int i = 0; i < this.vectorBKLayerList.size(); i++) {
			HashMap<String, Object> hashMap = this.vectorBKLayerList.get(i);
			// ͸����
			int transparent = Integer.parseInt(hashMap.get("D4").toString());

			// �ɼ���
			boolean visible = Boolean.parseBoolean(hashMap.get("D1").toString());
			boolean selectable = Boolean.parseBoolean(hashMap.get("D3").toString());

			for (v1_Layer pLayer : layerList) {
				if (pLayer.GetLayerID().equals(hashMap.get("LayerID"))) {
					// ����ͼ���ڵ�ʵ�����
					GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground)
							.GetLayerById(pLayer.GetLayerID());
					pGeoLayer.setVisible(visible);
					pGeoLayer.setSelectable(selectable);
					pLayer.SetTransparent(transparent);
					pLayer.SetVisible(visible);
					pLayer.SetSelectable(selectable);
					if (pGeoLayer.getRender().getType() == lkRenderType.enSimple) {
						((SimpleRender) pGeoLayer.getRender()).SetSymbolTransparent(transparent);
					}
					if (pGeoLayer.getRender().getType() == lkRenderType.enUniqueValue) {
						((UniqueValueRender) pGeoLayer.getRender()).SetSymbolTransparent(transparent);
					}

					// ��������
					PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
							.SaveVectorLayerInfo(pLayer);
				}
			}

		}

		// PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SetBKFileList(hoList);

		// if(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SaveVectorBKLayer())
		// {
		// //PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().ClearVectorLayer();
		// //PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().OpenVectorDataSource();
		// return true;
		// }

		return false;

		// //����ƫ��������
		// double offsetX = 0,offsetY = 0;
		// String offX = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetX);
		// String offY = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetY);
		// if (Tools.IsDouble(offX))offsetX = Double.parseDouble(offX);
		// if (Tools.IsDouble(offY))offsetY = Double.parseDouble(offY);
		// PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SaveVectorOffset(offsetX,
		// offsetY);
	}

	public static boolean createOrUpdateLayer(v1_Layer lyr) {
		String SQL = "1=1";
		if (lyr.GetEditMode() == lkEditMode.enNew) {
			// ����
			SQL = "insert into T_Layer (Name,LayerId,Type,Visible,Transparent,VisibleScaleMin,VisibleScaleMax,LabelScaleMin,LabelScaleMax,MinX,MinY,MaxX,MaxY,IfLabel,LabelField,LabelFont,FieldList,"
					+ "Selectable,Editable,Snapable,RenderType,SimpleRender,UniqueValueField,UniqueValueList,UniqueSymbolList,UniqueDefaultSymbol,F1,F2,F3,F4,F5,F6,F7) values "
					+ "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s','%9$s','%10$s','%11$s','%12$s','%13$s','%14$s','%15$s','%16$s','%17$s','%18$s','%19$s','%20$s',"
					+ "'%21$s','%22$s','%23$s','%24$s','%25$s','%26$s','%27$s','%28$s','%29$s','%30$s','%31$s','%32$s','%33$s')";

			SQL = String.format(SQL, lyr.GetLayerAliasName(), lyr.GetLayerID(), lyr.GetLayerTypeName(),
					lyr.GetVisible(), lyr.GetTransparet(), lyr.GetVisibleScaleMin(), lyr.GetVisibleScaleMax(),
					lyr.GetLabelScaleMin(), lyr.GetLabelScaleMax(), lyr.GetMinX(), lyr.GetMinY(), lyr.GetMaxX(),
					lyr.GetMaxY(), lyr.GetIfLabel(), lyr.GetLabelDataFieldStr(), lyr.GetLabelFont(),
					lyr.GetFieldListJsonStr(), lyr.GetSelectable(), lyr.GetEditable(), lyr.GetSnapable(),
					lyr.GetRenderTypeInt(), lyr.GetSimpleSymbol(),
					Tools.ListToJSONStr((List<String>) lyr.GetUniqueSymbolInfoList().get("UniqueValueField")),
					Tools.ListToJSONStr((List<String>) lyr.GetUniqueSymbolInfoList().get("UniqueValueList")),
					Tools.ListToJSONStr((List<String>) lyr.GetUniqueSymbolInfoList().get("UniqueSymbolList")),
					lyr.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"), lyr.GetLayerProjecType(), lyr.getCity(),
					lyr.getCounty(), lyr.getYear(), lyr.getWeiPianDataLayer(), lyr.GetShowWaterMark(),
					lyr.GetWaterMarkDataFieldStr());
			if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL)) {
				lyr.SetEditMode(lkEditMode.enUnkonw);

				// �����µ����ݱ�
				if (PubVar.m_Workspace.GetDataSourceByEditing().CreateDataset(lyr.GetLayerID())) {
					Dataset pDataset = new Dataset(PubVar.m_Workspace.GetDataSourceByEditing());
					pDataset.setSourceType(lkDatasetSourceType.enEditingData);
					pDataset.setId(lyr.GetLayerID());
					pDataset.setType(lyr.GetLayerType());
					pDataset.GetMapCellIndex().setEnvelope(PubVar.m_Map.getFullExtend());
					pDataset.setPorjectType(lyr.GetLayerProjecType());
					PubVar.m_Workspace.GetDataSourceByEditing().getDatasets().add(pDataset);
					return PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(lyr);
				} else {
					Tools.ShowMessageBox("Create dataset faield");
				}

			} else {
				Tools.ShowMessageBox("save layer sql faield");
			}
		}
		if (lyr.GetEditMode() == lkEditMode.enEdit) {
			// �༭
			SQL = "update T_Layer set Name='%1$s',LayerId='%2$s',Type='%3$s',Visible='%4$s',Transparent='%5$s',VisibleScaleMin='%6$s',VisibleScaleMax='%7$s',"
					+ "IfLabel='%8$s',LabelField='%9$s',LabelFont='%10$s',FieldList='%11$s',Selectable='%12$s',Editable='%13$s',Snapable='%14$s',"
					+ "RenderType='%15$s',SimpleRender='%16$s',UniqueValueField='%17$s',UniqueValueList='%18$s',UniqueSymbolList='%19$s',UniqueDefaultSymbol='%20$s', F1='%21$s', F2='%22$s',F3='%23$s',F4='%24$s',F5='%25$s',F6='%26$s',F7='%27$s'  where LayerId = '"
					+ lyr.GetLayerID() + "'";
			SQL = String.format(SQL, lyr.GetLayerAliasName(), lyr.GetLayerID(), lyr.GetLayerTypeName(),
					lyr.GetVisible(), lyr.GetTransparet(), lyr.GetVisibleScaleMin(), lyr.GetVisibleScaleMax(),
					lyr.GetIfLabel(), lyr.GetLabelDataFieldStr(), lyr.GetLabelFont(), lyr.GetFieldListJsonStr(),
					lyr.GetSelectable(), lyr.GetEditable(), lyr.GetSnapable(), lyr.GetRenderTypeInt(),
					lyr.GetSimpleSymbol(),
					Tools.ListToJSONStr((List<String>) lyr.GetUniqueSymbolInfoList().get("UniqueValueField")),
					Tools.ListToJSONStr((List<String>) lyr.GetUniqueSymbolInfoList().get("UniqueValueList")),
					Tools.ListToJSONStr((List<String>) lyr.GetUniqueSymbolInfoList().get("UniqueSymbolList")),
					lyr.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"), lyr.GetLayerProjecType(), lyr.getCity(),
					lyr.getCounty(), lyr.getYear(), lyr.getWeiPianDataLayer(), lyr.GetShowWaterMark(),
					lyr.GetWaterMarkDataFieldStr());
			if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL)) {
				lyr.SetEditMode(lkEditMode.enUnkonw);

				// ������Ⱦͼ��
				return PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForUpdate(lyr);
			}
		}
		return false;
	}

	public void ShowDialog() {
		// �ȿ�����ͼ���б����ڱ༭����
		this.copiedLayers = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CopyLayerList();

		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerInfo();
			}
		});

		try {
			Window window = dialogView.getWindow();
			window.setWindowAnimations(R.style.DialogAnimation);
			dialogView.show();
			ScrollView sv = (ScrollView) dialogView.findViewById(R.id.allsv);
			sv.smoothScrollTo(0, 0);
		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		}
	}

}
