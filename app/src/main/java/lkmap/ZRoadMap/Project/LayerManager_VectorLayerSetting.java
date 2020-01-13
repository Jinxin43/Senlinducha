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

		// 初始显示及按钮
		_Dialog.SetCaption("【" + this._ProjectName + "】" + Tools.ToLocale("图层"));
		_Dialog.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("确定") + "  ,确定", pCallback);

		// 绑定图层类型列表
		v1_DataBind.SetBindListSpinner(_Dialog, "图层类型", Tools.StrArrayToList(new String[] { "面", "线", "点" }),
				R.id.sp_type);
		_Dialog.findViewById(R.id.ll_pipei).setVisibility(View.INVISIBLE);

		List<String> listProjecTypes = new ArrayList<String>();
		listProjecTypes = Tools.StrArrayToList(new String[] { "", "林地变更", "森林资源二类调查" });
		v1_DataBind.SetBindListSpinner(_Dialog, "工程类型", listProjecTypes, R.id.sp_projecttype);
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

		// 2、提取需要分析的面层
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
					Tools.ShowYesNoMessage(_Dialog.getContext(), "确定要取消森林督查现状计算图层？", new ICallback() {

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
			Tools.ShowMessageBox("此图层缺少必要字段：" + calcFieldCopy + "，所以不能作为森林督查现状计算图层！");
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
			Tools.ShowMessageBox("已经将此图层设置为森林督查调查卡片计算基准图层！");
		}

	}

	// 按钮事件
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("确定")) {

				if (SaveLayerInfo()) {
					m_OldEditLayer = m_EditLayer;

					if (m_Callback != null)
						m_Callback.OnClick("图层", m_OldEditLayer);
					_Dialog.dismiss();
				}
			}

			if (Str.equals("字段")) // 表示字段编辑完成后的回调
			{
				LoadLayerInfo(); // 重新刷新图层列表
			}

			// 选中字段列表后的回调
			if (Str.equals("列表选项")) {
				HashMap<String, Object> obj = (HashMap<String, Object>) ExtraStr;
				String FieldName = obj.get("D2").toString();
				List<v1_LayerField> lyrFieldList = m_EditLayer.GetFieldList();
				for (v1_LayerField Field : lyrFieldList) {
					if (Field.GetFieldName().equals(FieldName))
						m_SelectField = Field;
				}
				SetButtonEnable(true);
			}

			if (Str.equals("字段匹配")) {
				if (m_EditLayer != null) {
					m_EditLayer.SetFieldList((List<v1_LayerField>) ExtraStr);
					LoadLayerInfo();
				}
			}

		}
	};

	private boolean SaveLayerInfo() {
		// 获取图层信息
		String LayerName = Tools.GetTextValueOnID(_Dialog, R.id.et_name); // 图层名称
		String LayerType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_type); // 图层类型

		// 验证图层信息
		String ErrorInfo = "";
		if (LayerName.equals(""))
			ErrorInfo += "【图层名称】不允许为空值！\r\n";
		// for (v1_Layer lyr : this.m_HaveLayerList) {
		// if (lyr.GetLayerAliasName().equals(LayerName) &&
		// (!lyr.GetLayerID().equals(this.m_EditLayer.GetLayerID())))
		// ErrorInfo += "【图层名称】不允许重复！\r\n";
		// }
		if (this.m_EditLayer.GetFieldList().size() == 0) {
			ErrorInfo += "【字段】数量不允许为0个！\r\n";
		} else {
			try {
				int i = 0;
				for (v1_LayerField field : this.m_EditLayer.GetFieldList()) {
					field.SetIsSelect(Boolean.parseBoolean(dataList.get(i).get("D1") + ""));
					i++;
				}
			} catch (Exception ex) {
				Tools.ShowMessageBox("保存字段是否显示出错：" + ex.getMessage());
			}
		}

		if (!ErrorInfo.equals("")) {
			Tools.ShowMessageBox(_Dialog.getContext(), ErrorInfo);
			return false;
		}

		// if(this.m_EditLayer.GetLayerProjecType().contains("退耕还林"))
		// {
		m_EditLayer.setCity(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds));
		m_EditLayer.setCounty(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgqx));
		// DatePicker datepicker =
		// (DatePicker)_Dialog.findViewById(R.id.dp_StartDate);
		YearPicker year = (YearPicker) _Dialog.findViewById(R.id.yearPicker);
		m_EditLayer.setYear(year.getYear());
		// }

		// 保存
		this.m_EditLayer.SetLayerAliasName(LayerName);
		this.m_EditLayer.SetLayerTypeName(LayerType);

		return true;
	}

	// 新增图层后的回调
	private ICallback m_Callback = null;

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			DoCommand(Tag);
		}
	}

	private void DoCommand(String StrCommand) {
		if (StrCommand.equals("增加字段")) {
			v1_project_layer_field plf = new v1_project_layer_field();
			plf.SetEditLayer(this.m_EditLayer);
			plf.SetCallback(pCallback); // 字段操作完成后的回调 ，回调标志：字段
			plf.ShowDialog();
		}
		if (StrCommand.equals("字段属性")) {
			v1_project_layer_field plf = new v1_project_layer_field();
			plf.SetEditLayer(this.m_EditLayer);
			plf.SetEditField(this.m_SelectField);
			plf.SetCallback(pCallback); // 字段操作完成后的回调 ，回调标志：字段
			plf.ShowDialog();
		}
		if (StrCommand.equals("删除字段")) {

			if (this.m_SelectField == null) {
				Tools.ShowToast(_Dialog.getContext(), "请先选中要删除的字段！");
				return;
			}

			Tools.ShowYesNoMessage(_Dialog.getContext(),
					Tools.ToLocale("是否删除字段") + "【" + this.m_SelectField.GetFieldName() + "】？", new ICallback() {

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
		if (StrCommand.equals("匹配字典")) {
			autoMatchDict();
		}

	}

	private void autoMatchDict() {
		String strProjectType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype);
		if (!(strProjectType.equals("森林资源二类调查") || strProjectType.equals("林地变更"))) {
			Tools.ShowMessageBox("暂时无法为当前的工程类型匹配数据字典！");
			return;
		}

		if (strProjectType.equals("森林资源二类调查")) {
			for (v1_LayerField field : m_EditLayer.GetFieldList()) {

				if (field.GetFieldName().equals("地貌") || field.GetFieldName().toUpperCase().equals("DI_MAO")) {
					field.SetFieldEnumCode("地貌");
					continue;
				}
				if (field.GetFieldName().equals("坡位") || field.GetFieldName().toUpperCase().equals("PO_WEI")) {
					field.SetFieldEnumCode("坡位");
					continue;
				}
				if (field.GetFieldName().equals("坡向") || field.GetFieldName().toUpperCase().equals("PO_XIANG")) {
					field.SetFieldEnumCode("坡向");
					continue;
				}
				if (field.GetFieldName().equals("坡度") || field.GetFieldName().toUpperCase().equals("PO_DU")) {
					field.SetFieldEnumCode("坡度");
					continue;
				}
				if (field.GetFieldName().equals("交通区位") || field.GetFieldName().toUpperCase().equals("KE_JI_DU")) {
					field.SetFieldEnumCode("交通区位");
					continue;
				}
				if (field.GetFieldName().equals("土壤类型") || field.GetFieldName().toUpperCase().equals("TU_RANG_LX")) {
					field.SetFieldEnumCode("土壤类型");
					continue;
				}
				if (field.GetFieldName().equals("生态区位") || field.GetFieldName().toUpperCase().equals("SH_TAI_QW")) {
					field.SetFieldEnumCode("生态区位");
					continue;
				}
				if (field.GetFieldName().equals("前期土地权属") || field.GetFieldName().toUpperCase().equals("Q_LD_QS")) {
					field.SetFieldEnumCode("土地权属");
					continue;
				}
				if (field.GetFieldName().equals("土地权属") || field.GetFieldName().toUpperCase().equals("LD_QS")) {
					field.SetFieldEnumCode("土地权属");
					continue;
				}
				if (field.GetFieldName().equals("土地使用权") || field.GetFieldName().toUpperCase().equals("LD_SY_QS")) {
					field.SetFieldEnumCode("土地使用权");
					continue;
				}
				if (field.GetFieldName().equals("林木权属") || field.GetFieldName().toUpperCase().equals("LM_QS")) {
					field.SetFieldEnumCode("林木权属");
					continue;
				}
				if (field.GetFieldName().equals("林木使用权") || field.GetFieldName().toUpperCase().equals("LM_SY_QS")) {
					field.SetFieldEnumCode("林木使用权");
					continue;
				}
				if (field.GetFieldName().equals("前期地类") || field.GetFieldName().toUpperCase().equals("Q_DI_LEI")) {
					field.SetFieldEnumCode("地类");
					continue;
				}
				if (field.GetFieldName().equals("地类") || field.GetFieldName().toUpperCase().equals("DI_LEI")) {
					field.SetFieldEnumCode("地类");
					continue;
				}
				if (field.GetFieldName().equals("前期林种") || field.GetFieldName().toUpperCase().equals("Q_L_Z")) {
					field.SetFieldEnumCode("林种");
					continue;
				}
				if (field.GetFieldName().equals("林种") || field.GetFieldName().toUpperCase().equals("LIN_ZHONG")) {
					field.SetFieldEnumCode("林种");
					continue;
				}
				if (field.GetFieldName().equals("起源") || field.GetFieldName().toUpperCase().equals("QI_YUAN")) {
					field.SetFieldEnumCode("起源");
					continue;
				}
				if (field.GetFieldName().equals("前期森林类别") || field.GetFieldName().toUpperCase().equals("Q_SEN_LB")) {
					field.SetFieldEnumCode("森林（林地）类别");
					continue;
				}
				if (field.GetFieldName().equals("森林类别") || field.GetFieldName().toUpperCase().equals("SEN_LIN_LB")) {
					field.SetFieldEnumCode("森林（林地）类别");
					continue;
				}

				if (field.GetFieldName().equals("前期事权等级") || field.GetFieldName().toUpperCase().equals("Q_SQ_D")) {
					field.SetFieldEnumCode("事权等级");
					continue;
				}

				if (field.GetFieldName().equals("事权等级") || field.GetFieldName().toUpperCase().equals("SHI_QUAN_D")) {
					field.SetFieldEnumCode("事权等级");
					continue;
				}

				if (field.GetFieldName().equals("地方公益林保护等级")
						|| field.GetFieldName().toUpperCase().equals("DFGYL_BHDJ")) {
					field.SetFieldEnumCode("地方公益林保护等级");
					continue;
				}

				if (field.GetFieldName().equals("国家公益林保护等级")
						|| field.GetFieldName().toUpperCase().equals("GJGYL_BHDJ")) {
					field.SetFieldEnumCode("国家公益林保护等级");
					continue;
				}

				if (field.GetFieldName().equals("前期工程类别") || field.GetFieldName().toUpperCase().equals("Q_GC_LB")) {
					field.SetFieldEnumCode("工程类别");
					continue;
				}

				if (field.GetFieldName().equals("工程类别") || field.GetFieldName().toUpperCase().equals("G_CHENG_LB")) {
					field.SetFieldEnumCode("工程类别");
					continue;
				}

				if (field.GetFieldName().equals("优势树种") || field.GetFieldName().toUpperCase().equals("YOU_SHI_SZ")) {
					field.SetFieldEnumCode("优势树种");
					continue;
				}

				if (field.GetFieldName().equals("树种组成") || field.GetFieldName().toUpperCase().equals("SHUZ_ZC")) {
					field.SetFieldEnumCode("树种");
					continue;
				}

				if (field.GetFieldName().equals("龄组") || field.GetFieldName().toUpperCase().equals("LING_ZU")) {
					field.SetFieldEnumCode("龄组");
					continue;
				}

				if (field.GetFieldName().equals("经济林产期") || field.GetFieldName().toUpperCase().equals("CH_QI")) {
					field.SetFieldEnumCode("经济林产期");
					continue;
				}

				if (field.GetFieldName().equals("土地退化类型") || field.GetFieldName().toUpperCase().equals("TD_TH_LX")) {
					field.SetFieldEnumCode("土地退化类型");
					continue;
				}

				if (field.GetFieldName().equals("湿地类型") || field.GetFieldName().toUpperCase().equals("SHIDI_LX")) {
					field.SetFieldEnumCode("湿地类型");
					continue;
				}

				if (field.GetFieldName().equals("健康状况") || field.GetFieldName().toUpperCase().equals("JK_ZHK")) {
					field.SetFieldEnumCode("森林健康等级");
					continue;
				}

				if (field.GetFieldName().equals("灾害类型") || field.GetFieldName().toUpperCase().equals("DISPE")) {
					field.SetFieldEnumCode("森林灾害类型");
					continue;
				}

				if (field.GetFieldName().equals("灾害等级") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("森林灾害等级");
					continue;
				}

				if (field.GetFieldName().equals("灾害等级") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("森林灾害等级");
					continue;
				}

				if (field.GetFieldName().equals("林地质量等级") || field.GetFieldName().toUpperCase().equals("ZL_DJ")) {
					field.SetFieldEnumCode("林地质量等级");
					continue;
				}

				if (field.GetFieldName().equals("是否为补充林地") || field.GetFieldName().toUpperCase().equals("BCLD")) {
					field.SetFieldEnumCode("是否为补充林地");
					continue;
				}

				if (field.GetFieldName().equals("前林地保护等级") || field.GetFieldName().toUpperCase().equals("Q_BH_DJ")) {
					field.SetFieldEnumCode("林地保护等级");
					continue;
				}

				if (field.GetFieldName().equals("林地保护等级") || field.GetFieldName().toUpperCase().equals("BH_DJ")) {
					field.SetFieldEnumCode("林地保护等级");
					continue;
				}

				if (field.GetFieldName().equals("主体功能区") || field.GetFieldName().toUpperCase().equals("QYKZ")) {
					field.SetFieldEnumCode("主体功能区");
					continue;
				}

				if (field.GetFieldName().equals("变化原因") || field.GetFieldName().toUpperCase().equals("BHYY")) {
					field.SetFieldEnumCode("林地变化原因");
					continue;
				}

				if (field.GetFieldName().equals("林地管理类型") || field.GetFieldName().toUpperCase().equals("GLLX")) {
					field.SetFieldEnumCode("林地管理类型");
					continue;
				}

				if (field.GetFieldName().equals("经营措施") || field.GetFieldName().toUpperCase().equals("JYCS")) {
					field.SetFieldEnumCode("经营措施");
					continue;
				}
			}
		}
		if (strProjectType.equals(ForestryLayerType.LindibiangengLayer)) {
			for (v1_LayerField field : m_EditLayer.GetFieldList()) {

				if (field.GetFieldName().equals("地貌") || field.GetFieldName().toUpperCase().equals("DI_MAO")) {
					field.SetFieldEnumCode("地貌");
					continue;
				}
				if (field.GetFieldName().equals("坡位") || field.GetFieldName().toUpperCase().equals("PO_WEI")) {
					field.SetFieldEnumCode("坡位");
					continue;
				}
				if (field.GetFieldName().equals("坡向") || field.GetFieldName().toUpperCase().equals("PO_XIANG")) {
					field.SetFieldEnumCode("坡向");
					continue;
				}
				if (field.GetFieldName().equals("坡度") || field.GetFieldName().toUpperCase().equals("PO_DU")) {
					field.SetFieldEnumCode("坡度");
					continue;
				}
				if (field.GetFieldName().equals("交通区位") || field.GetFieldName().toUpperCase().equals("KE_JI_DU")) {
					field.SetFieldEnumCode("交通区位");
					continue;
				}
				if (field.GetFieldName().equals("土壤类型(名称)")
						|| field.GetFieldName().toUpperCase().equals("TU_RANG_LX")) {
					field.SetFieldEnumCode("土壤类型(名称)");
					continue;
				}
				if (field.GetFieldName().equals("生态区位") || field.GetFieldName().toUpperCase().equals("SH_TAI_QW")) {
					field.SetFieldEnumCode("生态区位");
					continue;
				}
				if (field.GetFieldName().equals("前期土地权属") || field.GetFieldName().toUpperCase().equals("Q_LD_QS")) {
					field.SetFieldEnumCode("土地权属");
					continue;
				}
				if (field.GetFieldName().equals("土地权属") || field.GetFieldName().toUpperCase().equals("LD_QS")) {
					field.SetFieldEnumCode("土地权属");
					continue;
				}
				if (field.GetFieldName().equals("土地使用权") || field.GetFieldName().toUpperCase().equals("LD_SY_QS")) {
					field.SetFieldEnumCode("土地使用权");
					continue;
				}
				if (field.GetFieldName().equals("林木权属") || field.GetFieldName().toUpperCase().equals("LM_QS")) {
					field.SetFieldEnumCode("林木权属");
					continue;
				}
				if (field.GetFieldName().equals("林木使用权") || field.GetFieldName().toUpperCase().equals("LM_SY_QS")) {
					field.SetFieldEnumCode("林木使用权");
					continue;
				}
				if (field.GetFieldName().equals("前期地类") || field.GetFieldName().toUpperCase().equals("Q_DI_LEI")) {
					field.SetFieldEnumCode("地类");
					continue;
				}
				if (field.GetFieldName().equals("地类") || field.GetFieldName().toUpperCase().equals("DI_LEI")) {
					field.SetFieldEnumCode("地类");
					continue;
				}
				if (field.GetFieldName().equals("前期林种") || field.GetFieldName().toUpperCase().equals("Q_L_Z")) {
					field.SetFieldEnumCode("林种");
					continue;
				}
				if (field.GetFieldName().equals("林种") || field.GetFieldName().toUpperCase().equals("LIN_ZHONG")) {
					field.SetFieldEnumCode("林种");
					continue;
				}
				if (field.GetFieldName().equals("起源") || field.GetFieldName().toUpperCase().equals("QI_YUAN")) {
					field.SetFieldEnumCode("起源");
					continue;
				}
				if (field.GetFieldName().equals("前期森林类别") || field.GetFieldName().toUpperCase().equals("Q_SEN_LB")) {
					field.SetFieldEnumCode("森林（林地）类别");
					continue;
				}
				if (field.GetFieldName().equals("森林类别") || field.GetFieldName().toUpperCase().equals("SEN_LIN_LB")) {
					field.SetFieldEnumCode("森林（林地）类别");
					continue;
				}

				if (field.GetFieldName().equals("前期事权等级") || field.GetFieldName().toUpperCase().equals("Q_SQ_D")) {
					field.SetFieldEnumCode("事权等级");
					continue;
				}

				if (field.GetFieldName().equals("事权等级") || field.GetFieldName().toUpperCase().equals("SHI_QUAN_D")) {
					field.SetFieldEnumCode("事权等级");
					continue;
				}

				if (field.GetFieldName().equals("公益林保护等级") || field.GetFieldName().toUpperCase().equals("DFGYL_BHDJ")) {
					field.SetFieldEnumCode("国家公益林保护等级");
					continue;
				}

				if (field.GetFieldName().equals("国家公益林保护等级")
						|| field.GetFieldName().toUpperCase().equals("GJGYL_BHDJ")) {
					field.SetFieldEnumCode("国家公益林保护等级");
					continue;
				}

				if (field.GetFieldName().equals("前期工程类别") || field.GetFieldName().toUpperCase().equals("Q_GC_LB")) {
					field.SetFieldEnumCode("工程类别");
					continue;
				}

				if (field.GetFieldName().equals("工程类别") || field.GetFieldName().toUpperCase().equals("G_CHENG_LB")) {
					field.SetFieldEnumCode("工程类别");
					continue;
				}

				if (field.GetFieldName().equals("优势树种") || field.GetFieldName().toUpperCase().equals("YOU_SHI_SZ")) {
					field.SetFieldEnumCode("树种");
					continue;
				}

				if (field.GetFieldName().equals("树种组成") || field.GetFieldName().toUpperCase().equals("SHUZ_ZC")) {
					field.SetFieldEnumCode("树种");
					continue;
				}

				if (field.GetFieldName().equals("龄组") || field.GetFieldName().toUpperCase().equals("LING_ZU")) {
					field.SetFieldEnumCode("龄组");
					continue;
				}

				if (field.GetFieldName().equals("经济林产期") || field.GetFieldName().toUpperCase().equals("CH_QI")) {
					field.SetFieldEnumCode("经济林产期");
					continue;
				}

				if (field.GetFieldName().equals("土地退化类型") || field.GetFieldName().toUpperCase().equals("TD_TH_LX")) {
					field.SetFieldEnumCode("土地退化类型");
					continue;
				}

				if (field.GetFieldName().equals("湿地类型") || field.GetFieldName().toUpperCase().equals("SHIDI_LX")) {
					field.SetFieldEnumCode("湿地类型");
					continue;
				}

				if (field.GetFieldName().equals("健康状况") || field.GetFieldName().toUpperCase().equals("JK_ZHK")) {
					field.SetFieldEnumCode("森林健康等级");
					continue;
				}

				if (field.GetFieldName().equals("灾害类型") || field.GetFieldName().toUpperCase().equals("DISPE")) {
					field.SetFieldEnumCode("森林灾害类型");
					continue;
				}

				if (field.GetFieldName().equals("灾害等级") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("森林灾害等级");
					continue;
				}

				if (field.GetFieldName().equals("灾害等级") || field.GetFieldName().toUpperCase().equals("DISASTER_C")) {
					field.SetFieldEnumCode("森林灾害等级");
					continue;
				}

				if (field.GetFieldName().equals("林地质量等级") || field.GetFieldName().toUpperCase().equals("ZL_DJ")) {
					field.SetFieldEnumCode("林地质量等级");
					continue;
				}

				if (field.GetFieldName().equals("是否为补充林地") || field.GetFieldName().toUpperCase().equals("BCLD")) {
					field.SetFieldEnumCode("是否为补充林地");
					continue;
				}

				if (field.GetFieldName().equals("前林地保护等级") || field.GetFieldName().toUpperCase().equals("Q_BH_DJ")) {
					field.SetFieldEnumCode("林地保护等级");
					continue;
				}

				if (field.GetFieldName().equals("林地保护等级") || field.GetFieldName().toUpperCase().equals("BH_DJ")) {
					field.SetFieldEnumCode("林地保护等级");
					continue;
				}

				if (field.GetFieldName().equals("主体功能区") || field.GetFieldName().toUpperCase().equals("QYKZ")) {
					field.SetFieldEnumCode("主体功能区");
					continue;
				}

				if (field.GetFieldName().equals("变化原因") || field.GetFieldName().toUpperCase().equals("BHYY")) {
					field.SetFieldEnumCode("林地变化原因");
					continue;
				}

				if (field.GetFieldName().equals("林地管理类型") || field.GetFieldName().toUpperCase().equals("GLLX")) {
					field.SetFieldEnumCode("林地管理类型");
					continue;
				}

				if (field.GetFieldName().equals("经营措施") || field.GetFieldName().toUpperCase().equals("JYCS")) {
					field.SetFieldEnumCode("经营措施");
					continue;
				}
			}
		}

		if (PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
				.SaveVectorLayerSetting(m_EditLayer)) {
			LoadLayerInfo();
		}
	}

	// 当前字段列表中选中的字段项
	private v1_LayerField m_SelectField = null;

	private v1_Layer m_OldEditLayer = null;
	private v1_Layer m_EditLayer = null;

	/**
	 * 设置当前正在编辑的图层
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
		// 绑定图层列表
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_listview), "图层字段列表", pCallback);

		// 判读是否有默认图层，有则为编辑状态
		if (this.m_EditLayer != null) {

			dataList.clear();
			// 图层的字段列表
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

			// 图层名称
			if (Tools.GetTextValueOnID(_Dialog, R.id.et_name).equals(""))
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, this.m_EditLayer.GetLayerAliasName());

			// 图层类型
			Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, this.m_EditLayer.GetLayerTypeName());
			_Dialog.findViewById(R.id.sp_type).setEnabled(false);

			// 图层类型
			if (this.m_EditLayer.GetLayerProjecType() != null && !this.m_EditLayer.GetLayerProjecType().isEmpty()) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_projecttype, this.m_EditLayer.GetLayerProjecType());
				if (this.m_EditLayer.GetLayerProjecType().contains("碳汇")) {
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
						.getXZQH(xzqh.getCodeByName(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds), "市", "61"), "县");

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
