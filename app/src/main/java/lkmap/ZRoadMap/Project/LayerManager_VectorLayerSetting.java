package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.Controls.YearPicker;
import com.dingtu.DTGIS.DataService.DictXZQH;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.Spinner;
import android.widget.TextView;
import lkmap.Enum.ForestryLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.ToolsBox.v1_UserConfigDB_PolyAnalysisOption;

public class LayerManager_VectorLayerSetting {

	private v1_FormTemplate _Dialog = null;
	private String _ProjectName = "";

	public LayerManager_VectorLayerSetting() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_project_layer_new);

		// _Dialog.ReSetSize(1f, 0.95f);
		_Dialog.ReSetSize(0.6f, 0.8f);

		// ��ʼ��ʾ����ť
		_Dialog.SetCaption("��" + this._ProjectName + "��" + Tools.ToLocale("ͼ��"));
		_Dialog.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("ȷ��") + "  ,ȷ��", pCallback);

		// ��ͼ�������б�
		v1_DataBind.SetBindListSpinner(_Dialog, "ͼ������", Tools.StrArrayToList(new String[] { "��", "��", "��" }),
				R.id.sp_type);
		_Dialog.findViewById(R.id.ll_pipei).setVisibility(View.INVISIBLE);

		List<String> listProjecTypes = new ArrayList<String>();
		listProjecTypes = Tools.StrArrayToList(new String[] { "", "�ֵر��", "ɭ����Դ�������" });
		v1_DataBind.SetBindListSpinner(_Dialog, "��������", listProjecTypes, R.id.sp_projecttype);
		((Spinner) _Dialog.findViewById(R.id.sp_projecttype)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String value = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype);
				if (m_EditLayer != null) {
					m_EditLayer.SetLayerProjectType(value);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		_Dialog.findViewById(R.id.pln_add).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_edit).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_delete).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_mactchDict).setOnClickListener(new ViewClick());

		
	}
	
	private void initAnalysisLayer()
	{
		v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		List<HashMap<String, Object>> OptList = m_PAO.GetPolyAnalysisOption();

		// 2����ȡ��Ҫ���������
		List<HashMap<String, Object>> polyDatasetList = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> Opt : OptList) {
			if(m_EditLayer.GetLayerID().equals(Opt.get("LayerId")))
			{
				((CheckBox) _Dialog.findViewById(R.id.cbSettingDuChaLayer)).setChecked(true); 
			}
		}
		
		CheckBox cbSettingDuChaLayer = (CheckBox) _Dialog.findViewById(R.id.cbSettingDuChaLayer);
		cbSettingDuChaLayer.setVisibility(View.VISIBLE);
	
		cbSettingDuChaLayer.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {

					setAsDuChaLayer();

				} else {
//					final CompoundButton cb = buttonView;
					Tools.ShowYesNoMessage(_Dialog.getContext(), "ȷ��Ҫȡ��ɭ�ֶ�����״����ͼ�㣿", new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().deleteUserPara("Poly_Analysis_Option");
							}
//							else
//							{
//								cb.setChecked(true);
//							}
							

						}
					});

				}

			}
		});
	}

	private void setAsDuChaLayer() {
//		String[] calcField = "LD_QS,DI_LEI,SEN_LIN_LB,SHI_QUAN_D,GJGYL_BHDJ,BH_DJ,QI_YUAN,HUO_LMGQXJ,YOU_SHI_SZ".split(",");
//		String calcFieldCopy = "LD_QS,DI_LEI,SEN_LIN_LB,SHI_QUAN_D,GJGYL_BHDJ,BH_DJ,QI_YUAN,HUO_LMGQXJ,YOU_SHI_SZ";
		String[] calcField = "DI_LEI,SEN_LIN_LB,SHI_QUAN_D,GJGYL_BHDJ,BH_DJ,HUO_LMGQXJ".split(",");
		String calcFieldCopy = "DI_LEI,SEN_LIN_LB,SHI_QUAN_D,GJGYL_BHDJ,BH_DJ,HUO_LMGQXJ";
		
		ArrayList<String> fileNameList = new ArrayList<String>();
		ArrayList<String> fileCaptionList = new ArrayList<String>();
		for (String tagert : calcField) {
			for (v1_LayerField field : m_EditLayer.GetFieldList()) {
				if (field.GetFieldName().equals(tagert)) {
					fileNameList.add(field.GetDataFieldName());
					fileCaptionList.add(field.GetFieldName());
					calcFieldCopy = calcFieldCopy.replace(tagert + ",", "").replace(tagert, "");
					break;
				}
			}
		}

		if (calcFieldCopy.length() > 0) {
			Tools.ShowMessageBox("��ͼ��ȱ�ٱ�Ҫ�ֶΣ�" + calcFieldCopy + "�����Բ�����Ϊɭ�ֶ�����״����ͼ�㣡");
			return;
		}

		HashMap<String, Object> OptItem = new HashMap<String, Object>();

		OptItem.put("LayerName", m_EditLayer.GetLayerAliasName());
		OptItem.put("LayerId", m_EditLayer.GetLayerID());
		OptItem.put("Select", true);
		OptItem.put("FieldNameList", fileNameList);
		OptItem.put("FieldCaptionList", fileCaptionList);
		OptItem.put("FieldCaptionListStr", Tools.JoinT(",", fileCaptionList));

		List<HashMap<String, Object>> m_PolyAnalysisOption = new ArrayList<HashMap<String, Object>>();
		m_PolyAnalysisOption.add(OptItem);
		if (new v1_UserConfigDB_PolyAnalysisOption().SavePolyAnalysisOption(m_PolyAnalysisOption)) {
			Tools.ShowMessageBox("�Ѿ�����ͼ������Ϊɭ�ֶ�����鿨Ƭ�����׼ͼ�㣡");
		}

	}

	// ��ť�¼�
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("ȷ��")) {

				if (SaveLayerInfo()) {
					m_OldEditLayer = m_EditLayer;

					if (m_Callback != null)
						m_Callback.OnClick("ͼ��", m_OldEditLayer);
					_Dialog.dismiss();
				}
			}

			if (Str.equals("�ֶ�")) // ��ʾ�ֶα༭��ɺ�Ļص�
			{
				LoadLayerInfo(); // ����ˢ��ͼ���б�
			}

			// ѡ���ֶ��б��Ļص�
			if (Str.equals("�б�ѡ��")) {
				HashMap<String, Object> obj = (HashMap<String, Object>) ExtraStr;
				String FieldName = obj.get("D2").toString();
				List<v1_LayerField> lyrFieldList = m_EditLayer.GetFieldList();
				for (v1_LayerField Field : lyrFieldList) {
					if (Field.GetFieldName().equals(FieldName))
						m_SelectField = Field;
				}
				SetButtonEnable(true);
			}

			if (Str.equals("�ֶ�ƥ��")) {
				if (m_EditLayer != null) {
					m_EditLayer.SetFieldList((List<v1_LayerField>) ExtraStr);
					LoadLayerInfo();
				}
			}

		}
	};

	private boolean SaveLayerInfo() {
		// ��ȡͼ����Ϣ
		String LayerName = Tools.GetTextValueOnID(_Dialog, R.id.et_name); // ͼ������
		String LayerType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_type); // ͼ������

		// ��֤ͼ����Ϣ
		String ErrorInfo = "";
		if (LayerName.equals(""))
			ErrorInfo += "��ͼ�����ơ�������Ϊ��ֵ��\r\n";
		// for (v1_Layer lyr : this.m_HaveLayerList) {
		// if (lyr.GetLayerAliasName().equals(LayerName) &&
		// (!lyr.GetLayerID().equals(this.m_EditLayer.GetLayerID())))
		// ErrorInfo += "��ͼ�����ơ��������ظ���\r\n";
		// }
		if (this.m_EditLayer.GetFieldList().size() == 0) {
			ErrorInfo += "���ֶΡ�����������Ϊ0����\r\n";
		} else {
			try {
				int i = 0;
				for (v1_LayerField field : this.m_EditLayer.GetFieldList()) {
					field.SetIsSelect(Boolean.parseBoolean(dataList.get(i).get("D1") + ""));
					i++;
				}
			} catch (Exception ex) {
				Tools.ShowMessageBox("�����ֶ��Ƿ���ʾ����" + ex.getMessage());
			}
		}

		if (!ErrorInfo.equals("")) {
			Tools.ShowMessageBox(_Dialog.getContext(), ErrorInfo);
			return false;
		}

		// if(this.m_EditLayer.GetLayerProjecType().contains("�˸�����"))
		// {
		m_EditLayer.setCity(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds));
		m_EditLayer.setCounty(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgqx));
		// DatePicker datepicker =
		// (DatePicker)_Dialog.findViewById(R.id.dp_StartDate);
		YearPicker year = (YearPicker) _Dialog.findViewById(R.id.yearPicker);
		m_EditLayer.setYear(year.getYear());
		// }

		// ����
		this.m_EditLayer.SetLayerAliasName(LayerName);
		this.m_EditLayer.SetLayerTypeName(LayerType);

		return true;
	}

	// ����ͼ���Ļص�
	private ICallback m_Callback = null;

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			DoCommand(Tag);
		}
	}

	private void DoCommand(String StrCommand) {
		if (StrCommand.equals("�����ֶ�")) {
			v1_project_layer_field plf = new v1_project_layer_field();
			plf.SetEditLayer(this.m_EditLayer);
			plf.SetCallback(pCallback); // �ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
			plf.ShowDialog();
		}
		if (StrCommand.equals("�ֶ�����")) {
			v1_project_layer_field plf = new v1_project_layer_field();
			plf.SetEditLayer(this.m_EditLayer);
			plf.SetEditField(this.m_SelectField);
			plf.SetCallback(pCallback); // �ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
			plf.ShowDialog();
		}
		if (StrCommand.equals("ɾ���ֶ�")) {

			if (this.m_SelectField == null) {
				Tools.ShowToast(_Dialog.getContext(), "����ѡ��Ҫɾ�����ֶΣ�");
				return;
			}

			Tools.ShowYesNoMessage(_Dialog.getContext(),
					Tools.ToLocale("�Ƿ�ɾ���ֶ�") + "��" + this.m_SelectField.GetFieldName() + "����", new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								List<v1_LayerField> lyrFieldList = m_EditLayer.GetFieldList();
								for (v1_LayerField Field : lyrFieldList) {
									if (Field.GetFieldName().equals(m_SelectField.GetFieldName())) {
										lyrFieldList.remove(Field);
										LoadLayerInfo();
										return;
									}
								}
							}

						}
					});

		}
		if (StrCommand.equals("ƥ���ֵ�")) {
			autoMatchDict();
		}

	}

	private void autoMatchDict() {
		String strProjectType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype);
		if (!(strProjectType.equals("ɭ����Դ�������") || strProjectType.equals("�ֵر��"))) {
			Tools.ShowMessageBox("��ʱ�޷�Ϊ��ǰ�Ĺ�������ƥ�������ֵ䣡");
			return;
		}

		if (strProjectType.equals("ɭ����Դ�������")) {
			for (v1_LayerField field : m_EditLayer.GetFieldList()) {

				if (field.GetFieldName().equals("��ò") || field.GetFieldName().toUpperCase().equals("DI_MAO")) {
					field.SetFieldEnumCode("��ò");
					continue;
				}
				if (field.GetFieldName().equals("��λ") || field.GetFieldName().toUpperCase().equals("PO_WEI")) {
					field.SetFieldEnumCode("��λ");
					continue;
				}
				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("PO_XIANG")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("�¶�") || field.GetFieldName().toUpperCase().equals("PO_DU")) {
					field.SetFieldEnumCode("�¶�");
					continue;
				}
				if (field.GetFieldName().equals("��ͨ��λ") || field.GetFieldName().toUpperCase().equals("KE_JI_DU")) {
					field.SetFieldEnumCode("��ͨ��λ");
					continue;
				}
				if (field.GetFieldName().equals("��������") || field.GetFieldName().toUpperCase().equals("TU_RANG_LX")) {
					field.SetFieldEnumCode("��������");
					continue;
				}
				if (field.GetFieldName().equals("��̬��λ") || field.GetFieldName().toUpperCase().equals("SH_TAI_QW")) {
					field.SetFieldEnumCode("��̬��λ");
					continue;
				}
				if (field.GetFieldName().equals("ǰ������Ȩ��") || field.GetFieldName().toUpperCase().equals("Q_LD_QS")) {
					field.SetFieldEnumCode("����Ȩ��");
					continue;
				}
				if (field.GetFieldName().equals("����Ȩ��") || field.GetFieldName().toUpperCase().equals("LD_QS")) {
					field.SetFieldEnumCode("����Ȩ��");
					continue;
				}
				if (field.GetFieldName().equals("����ʹ��Ȩ") || field.GetFieldName().toUpperCase().equals("LD_SY_QS")) {
					field.SetFieldEnumCode("����ʹ��Ȩ");
					continue;
				}
				if (field.GetFieldName().equals("��ľȨ��") || field.GetFieldName().toUpperCase().equals("LM_QS")) {
					field.SetFieldEnumCode("��ľȨ��");
					continue;
				}
				if (field.GetFieldName().equals("��ľʹ��Ȩ") || field.GetFieldName().toUpperCase().equals("LM_SY_QS")) {
					field.SetFieldEnumCode("��ľʹ��Ȩ");
					continue;
				}
				if (field.GetFieldName().equals("ǰ�ڵ���") || field.GetFieldName().toUpperCase().equals("Q_DI_LEI")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("DI_LEI")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("ǰ������") || field.GetFieldName().toUpperCase().equals("Q_L_Z")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("LIN_ZHONG")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("��Դ") || field.GetFieldName().toUpperCase().equals("QI_YUAN")) {
					field.SetFieldEnumCode("��Դ");
					continue;
				}
				if (field.GetFieldName().equals("ǰ��ɭ�����") || field.GetFieldName().toUpperCase().equals("Q_SEN_LB")) {
					field.SetFieldEnumCode("ɭ�֣��ֵأ����");
					continue;
				}
				if (field.GetFieldName().equals("ɭ�����") || field.GetFieldName().toUpperCase().equals("SEN_LIN_LB")) {
					field.SetFieldEnumCode("ɭ�֣��ֵأ����");
					continue;
				}

				if (field.GetFieldName().equals("ǰ����Ȩ�ȼ�") || field.GetFieldName().toUpperCase().equals("Q_SQ_D")) {
					field.SetFieldEnumCode("��Ȩ�ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("��Ȩ�ȼ�") || field.GetFieldName().toUpperCase().equals("SHI_QUAN_D")) {
					field.SetFieldEnumCode("��Ȩ�ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ط������ֱ����ȼ�")
						|| field.GetFieldName().toUpperCase().equals("DFGYL_BHDJ")) {
					field.SetFieldEnumCode("�ط������ֱ����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("���ҹ����ֱ����ȼ�")
						|| field.GetFieldName().toUpperCase().equals("GJGYL_BHDJ")) {
					field.SetFieldEnumCode("���ҹ����ֱ����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("ǰ�ڹ������") || field.GetFieldName().toUpperCase().equals("Q_GC_LB")) {
					field.SetFieldEnumCode("�������");
					continue;
				}

				if (field.GetFieldName().equals("�������") || field.GetFieldName().toUpperCase().equals("G_CHENG_LB")) {
					field.SetFieldEnumCode("�������");
					continue;
				}

				if (field.GetFieldName().equals("��������") || field.GetFieldName().toUpperCase().equals("YOU_SHI_SZ")) {
					field.SetFieldEnumCode("��������");
					continue;
				}

				if (field.GetFieldName().equals("�������") || field.GetFieldName().toUpperCase().equals("SHUZ_ZC")) {
					field.SetFieldEnumCode("����");
					continue;
				}

				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("LING_ZU")) {
					field.SetFieldEnumCode("����");
					continue;
				}

				if (field.GetFieldName().equals("�����ֲ���") || field.GetFieldName().toUpperCase().equals("CH_QI")) {
					field.SetFieldEnumCode("�����ֲ���");
					continue;
				}

				if (field.GetFieldName().equals("�����˻�����") || field.GetFieldName().toUpperCase().equals("TD_TH_LX")) {
					field.SetFieldEnumCode("�����˻�����");
					continue;
				}

				if (field.GetFieldName().equals("ʪ������") || field.GetFieldName().toUpperCase().equals("SHIDI_LX")) {
					field.SetFieldEnumCode("ʪ������");
					continue;
				}

				if (field.GetFieldName().equals("����״��") || field.GetFieldName().toUpperCase().equals("JK_ZHK")) {
					field.SetFieldEnumCode("ɭ�ֽ����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֺ�����") || field.GetFieldName().toUpperCase().equals("DISPE")) {
					field.SetFieldEnumCode("ɭ���ֺ�����");
					continue;
				}

				if (field.GetFieldName().equals("�ֺ��ȼ�") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("ɭ���ֺ��ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֺ��ȼ�") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("ɭ���ֺ��ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֵ������ȼ�") || field.GetFieldName().toUpperCase().equals("ZL_DJ")) {
					field.SetFieldEnumCode("�ֵ������ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�Ƿ�Ϊ�����ֵ�") || field.GetFieldName().toUpperCase().equals("BCLD")) {
					field.SetFieldEnumCode("�Ƿ�Ϊ�����ֵ�");
					continue;
				}

				if (field.GetFieldName().equals("ǰ�ֵر����ȼ�") || field.GetFieldName().toUpperCase().equals("Q_BH_DJ")) {
					field.SetFieldEnumCode("�ֵر����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֵر����ȼ�") || field.GetFieldName().toUpperCase().equals("BH_DJ")) {
					field.SetFieldEnumCode("�ֵر����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("���幦����") || field.GetFieldName().toUpperCase().equals("QYKZ")) {
					field.SetFieldEnumCode("���幦����");
					continue;
				}

				if (field.GetFieldName().equals("�仯ԭ��") || field.GetFieldName().toUpperCase().equals("BHYY")) {
					field.SetFieldEnumCode("�ֵر仯ԭ��");
					continue;
				}

				if (field.GetFieldName().equals("�ֵع�������") || field.GetFieldName().toUpperCase().equals("GLLX")) {
					field.SetFieldEnumCode("�ֵع�������");
					continue;
				}

				if (field.GetFieldName().equals("��Ӫ��ʩ") || field.GetFieldName().toUpperCase().equals("JYCS")) {
					field.SetFieldEnumCode("��Ӫ��ʩ");
					continue;
				}
			}
		}
		if (strProjectType.equals(ForestryLayerType.LindibiangengLayer)) {
			for (v1_LayerField field : m_EditLayer.GetFieldList()) {

				if (field.GetFieldName().equals("��ò") || field.GetFieldName().toUpperCase().equals("DI_MAO")) {
					field.SetFieldEnumCode("��ò");
					continue;
				}
				if (field.GetFieldName().equals("��λ") || field.GetFieldName().toUpperCase().equals("PO_WEI")) {
					field.SetFieldEnumCode("��λ");
					continue;
				}
				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("PO_XIANG")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("�¶�") || field.GetFieldName().toUpperCase().equals("PO_DU")) {
					field.SetFieldEnumCode("�¶�");
					continue;
				}
				if (field.GetFieldName().equals("��ͨ��λ") || field.GetFieldName().toUpperCase().equals("KE_JI_DU")) {
					field.SetFieldEnumCode("��ͨ��λ");
					continue;
				}
				if (field.GetFieldName().equals("��������(����)")
						|| field.GetFieldName().toUpperCase().equals("TU_RANG_LX")) {
					field.SetFieldEnumCode("��������(����)");
					continue;
				}
				if (field.GetFieldName().equals("��̬��λ") || field.GetFieldName().toUpperCase().equals("SH_TAI_QW")) {
					field.SetFieldEnumCode("��̬��λ");
					continue;
				}
				if (field.GetFieldName().equals("ǰ������Ȩ��") || field.GetFieldName().toUpperCase().equals("Q_LD_QS")) {
					field.SetFieldEnumCode("����Ȩ��");
					continue;
				}
				if (field.GetFieldName().equals("����Ȩ��") || field.GetFieldName().toUpperCase().equals("LD_QS")) {
					field.SetFieldEnumCode("����Ȩ��");
					continue;
				}
				if (field.GetFieldName().equals("����ʹ��Ȩ") || field.GetFieldName().toUpperCase().equals("LD_SY_QS")) {
					field.SetFieldEnumCode("����ʹ��Ȩ");
					continue;
				}
				if (field.GetFieldName().equals("��ľȨ��") || field.GetFieldName().toUpperCase().equals("LM_QS")) {
					field.SetFieldEnumCode("��ľȨ��");
					continue;
				}
				if (field.GetFieldName().equals("��ľʹ��Ȩ") || field.GetFieldName().toUpperCase().equals("LM_SY_QS")) {
					field.SetFieldEnumCode("��ľʹ��Ȩ");
					continue;
				}
				if (field.GetFieldName().equals("ǰ�ڵ���") || field.GetFieldName().toUpperCase().equals("Q_DI_LEI")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("DI_LEI")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("ǰ������") || field.GetFieldName().toUpperCase().equals("Q_L_Z")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("LIN_ZHONG")) {
					field.SetFieldEnumCode("����");
					continue;
				}
				if (field.GetFieldName().equals("��Դ") || field.GetFieldName().toUpperCase().equals("QI_YUAN")) {
					field.SetFieldEnumCode("��Դ");
					continue;
				}
				if (field.GetFieldName().equals("ǰ��ɭ�����") || field.GetFieldName().toUpperCase().equals("Q_SEN_LB")) {
					field.SetFieldEnumCode("ɭ�֣��ֵأ����");
					continue;
				}
				if (field.GetFieldName().equals("ɭ�����") || field.GetFieldName().toUpperCase().equals("SEN_LIN_LB")) {
					field.SetFieldEnumCode("ɭ�֣��ֵأ����");
					continue;
				}

				if (field.GetFieldName().equals("ǰ����Ȩ�ȼ�") || field.GetFieldName().toUpperCase().equals("Q_SQ_D")) {
					field.SetFieldEnumCode("��Ȩ�ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("��Ȩ�ȼ�") || field.GetFieldName().toUpperCase().equals("SHI_QUAN_D")) {
					field.SetFieldEnumCode("��Ȩ�ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�����ֱ����ȼ�") || field.GetFieldName().toUpperCase().equals("DFGYL_BHDJ")) {
					field.SetFieldEnumCode("���ҹ����ֱ����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("���ҹ����ֱ����ȼ�")
						|| field.GetFieldName().toUpperCase().equals("GJGYL_BHDJ")) {
					field.SetFieldEnumCode("���ҹ����ֱ����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("ǰ�ڹ������") || field.GetFieldName().toUpperCase().equals("Q_GC_LB")) {
					field.SetFieldEnumCode("�������");
					continue;
				}

				if (field.GetFieldName().equals("�������") || field.GetFieldName().toUpperCase().equals("G_CHENG_LB")) {
					field.SetFieldEnumCode("�������");
					continue;
				}

				if (field.GetFieldName().equals("��������") || field.GetFieldName().toUpperCase().equals("YOU_SHI_SZ")) {
					field.SetFieldEnumCode("����");
					continue;
				}

				if (field.GetFieldName().equals("�������") || field.GetFieldName().toUpperCase().equals("SHUZ_ZC")) {
					field.SetFieldEnumCode("����");
					continue;
				}

				if (field.GetFieldName().equals("����") || field.GetFieldName().toUpperCase().equals("LING_ZU")) {
					field.SetFieldEnumCode("����");
					continue;
				}

				if (field.GetFieldName().equals("�����ֲ���") || field.GetFieldName().toUpperCase().equals("CH_QI")) {
					field.SetFieldEnumCode("�����ֲ���");
					continue;
				}

				if (field.GetFieldName().equals("�����˻�����") || field.GetFieldName().toUpperCase().equals("TD_TH_LX")) {
					field.SetFieldEnumCode("�����˻�����");
					continue;
				}

				if (field.GetFieldName().equals("ʪ������") || field.GetFieldName().toUpperCase().equals("SHIDI_LX")) {
					field.SetFieldEnumCode("ʪ������");
					continue;
				}

				if (field.GetFieldName().equals("����״��") || field.GetFieldName().toUpperCase().equals("JK_ZHK")) {
					field.SetFieldEnumCode("ɭ�ֽ����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֺ�����") || field.GetFieldName().toUpperCase().equals("DISPE")) {
					field.SetFieldEnumCode("ɭ���ֺ�����");
					continue;
				}

				if (field.GetFieldName().equals("�ֺ��ȼ�") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("ɭ���ֺ��ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֺ��ȼ�") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("ɭ���ֺ��ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֵ������ȼ�") || field.GetFieldName().toUpperCase().equals("ZL_DJ")) {
					field.SetFieldEnumCode("�ֵ������ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�Ƿ�Ϊ�����ֵ�") || field.GetFieldName().toUpperCase().equals("BCLD")) {
					field.SetFieldEnumCode("�Ƿ�Ϊ�����ֵ�");
					continue;
				}

				if (field.GetFieldName().equals("ǰ�ֵر����ȼ�") || field.GetFieldName().toUpperCase().equals("Q_BH_DJ")) {
					field.SetFieldEnumCode("�ֵر����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("�ֵر����ȼ�") || field.GetFieldName().toUpperCase().equals("BH_DJ")) {
					field.SetFieldEnumCode("�ֵر����ȼ�");
					continue;
				}

				if (field.GetFieldName().equals("���幦����") || field.GetFieldName().toUpperCase().equals("QYKZ")) {
					field.SetFieldEnumCode("���幦����");
					continue;
				}

				if (field.GetFieldName().equals("�仯ԭ��") || field.GetFieldName().toUpperCase().equals("BHYY")) {
					field.SetFieldEnumCode("�ֵر仯ԭ��");
					continue;
				}

				if (field.GetFieldName().equals("�ֵع�������") || field.GetFieldName().toUpperCase().equals("GLLX")) {
					field.SetFieldEnumCode("�ֵع�������");
					continue;
				}

				if (field.GetFieldName().equals("��Ӫ��ʩ") || field.GetFieldName().toUpperCase().equals("JYCS")) {
					field.SetFieldEnumCode("��Ӫ��ʩ");
					continue;
				}
			}
		}

		if (PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
				.SaveVectorLayerSetting(m_EditLayer)) {
			LoadLayerInfo();
		}
	}

	// ��ǰ�ֶ��б���ѡ�е��ֶ���
	private v1_LayerField m_SelectField = null;

	private v1_Layer m_OldEditLayer = null;
	private v1_Layer m_EditLayer = null;

	/**
	 * ���õ�ǰ���ڱ༭��ͼ��
	 * 
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr) {
		this.m_OldEditLayer = lyr;
		this.m_EditLayer = lyr.Clone();

		Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype);
		Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_projecttype, m_EditLayer.GetLayerProjecType());
		
		initAnalysisLayer();

	}

	List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

	private void LoadLayerInfo() {
		// ��ͼ���б�
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_listview), "ͼ���ֶ��б�", pCallback);

		// �ж��Ƿ���Ĭ��ͼ�㣬����Ϊ�༭״̬
		if (this.m_EditLayer != null) {

			dataList.clear();
			// ͼ����ֶ��б�
			List<v1_LayerField> lyrFieldList = this.m_EditLayer.GetFieldList();
			for (v1_LayerField Field : lyrFieldList) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("D1", Field.getIsSelect());
				hm.put("D2", Field.GetFieldName());
				hm.put("D3", Field.GetFieldTypeName());
				hm.put("D4", Field.GetFieldSize());
				hm.put("D5", Field.GetFieldDecimal());
				hm.put("D6", Field.GetFieldEnumCode());
				dataList.add(hm);
			}
			hvf.BindDataToListView(dataList);

			// ͼ������
			if (Tools.GetTextValueOnID(_Dialog, R.id.et_name).equals(""))
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, this.m_EditLayer.GetLayerAliasName());

			// ͼ������
			Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, this.m_EditLayer.GetLayerTypeName());
			_Dialog.findViewById(R.id.sp_type).setEnabled(false);

			// ͼ������
			if (this.m_EditLayer.GetLayerProjecType() != null && !this.m_EditLayer.GetLayerProjecType().isEmpty()) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_projecttype, this.m_EditLayer.GetLayerProjecType());
				if (this.m_EditLayer.GetLayerProjecType().contains("̼��")) {
					_Dialog.findViewById(R.id.btnExportTanhui).setVisibility(View.VISIBLE);
				}
			}
			// _Dialog.findViewById(R.id.sp_type).setEnabled(true);

			if (m_EditLayer.getYear() != null) {
				YearPicker yearPicker = (YearPicker) _Dialog.findViewById(R.id.yearPicker);
				yearPicker.setYear(m_EditLayer.getYear());
			}
			if (m_EditLayer.getCity() != null && m_EditLayer.getCity().length() > 0) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_tgds, m_EditLayer.getCity());
				DictXZQH xzqh = new DictXZQH();
				List<HashMap<String, Object>> xian = xzqh
						.getXZQH(xzqh.getCodeByName(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds), "��", "61"), "��");

				ArrayList<String> qxNames = new ArrayList<String>();
				for (HashMap<String, Object> hm : xian) {
					qxNames.add(hm.get("D1").toString());
				}

				ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
						android.R.layout.simple_spinner_item, qxNames.toArray());
				nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner tgqx = ((Spinner) _Dialog.findViewById(R.id.sp_tgqx));
				tgqx.setAdapter(nfAdapter);

				if (m_EditLayer.getCounty() != null && m_EditLayer.getCounty().length() > 0) {
					try {
						Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_tgqx, m_EditLayer.getCounty());
					} catch (Exception ex) {

					}

				}

				// tgqx.setEnabled(false);
			}
		}

		Spinner splayertype = (Spinner) _Dialog.findViewById(R.id.sp_projecttype);

	}

	private void SetButtonEnable(boolean enabled) {
		_Dialog.findViewById(R.id.pln_edit).setEnabled(enabled);
		_Dialog.findViewById(R.id.pln_delete).setEnabled(enabled);
		((TextView) _Dialog.findViewById(R.id.tv_edit)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
		((TextView) _Dialog.findViewById(R.id.tv_delete)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
	}

	public void ShowDialog() {
		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerInfo();
			}
		});
		_Dialog.show();

	}

}
