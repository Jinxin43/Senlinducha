package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.TuiGengDB;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.LinearLayout;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.ForestryLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.FieldMatcher;

public class MatchFields {
	private v1_FormTemplate dialogView = null;
	private String mProjectType;
	private List<String> srcFieldsName = null;
	private List<String> srcFields = null;
	private List<String> srcDataFields = null;
	private List<FieldMatcher> allFieldControls = new ArrayList<FieldMatcher>();
	private String mLayerID;

	public MatchFields(String projectType, String layerID) {
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.matchfields);
		dialogView.ReSetSize(0.66f, 0.86f);

		dialogView.SetCaption(Tools.ToLocale("字段匹配"));
		dialogView.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("确定") + "  ,确定",
				btnCallback);
		mLayerID = layerID;
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_projecttype, "项目类型：" + projectType);
		initFields(projectType, layerID);

	}

	// 顶部按钮事件
	private ICallback btnCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			// TODO Auto-generated method stub

			if (Str.equals("确定")) {
				Tools.OpenDialog("正在保存字段匹配设置...", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						saveFields();
					}
				});
			}
		}
	};

	// 字段操作完成后的回调
	private ICallback mCallback = null;

	public void SetCallback(ICallback cb) {
		this.mCallback = cb;
	}

	private void saveFields() {
		final List<v1_LayerField> allFields = new ArrayList<v1_LayerField>();
		int max = getMaxSrcField();
		int current = max;

		for (FieldMatcher fm : allFieldControls) {
			v1_LayerField f = fm.getSrcField();
			if (fm.getSelectionIndex() > 0) {
				f.SetDataFieldName(srcDataFields.get(fm.getSelectionIndex() - 1));
			} else {
				current = current + 1;
				if (current > 512) {
					Tools.ShowMessageBox("未匹配字段数量超出数据库限制！");
					return;
				} else {
					f.SetDataFieldName("F" + current);
				}
			}

			allFields.add(f);
		}

		int noContains = 0;
		String fields = "";
		for (v1_LayerField srcDF : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayerID)
				.GetFieldList()) {

			boolean isContains = false;
			for (v1_LayerField f : allFields) {
				if (f.GetDataFieldName().equals(srcDF.GetDataFieldName())) {
					isContains = true;
					break;
				}
			}

			if (!isContains) {
				fields += srcDF.GetFieldName() + ",";
				noContains++;
				// srcDF.SetIsSelect(false);
				allFields.add(srcDF);
			}
		}

		if (noContains > 0) {
			Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "有" + noContains + "个源字段未被匹配，是否继续保存匹配结果？ \r\n" + fields,
					new ICallback() {
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayerID)
										.SetFieldList(allFields);
								TuiGengDB db = new TuiGengDB();

								if (db.updateLayerField(mLayerID, PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer()
										.GetLayerByID(mLayerID).GetFieldListJsonStr())) {
									if (mCallback != null) {
										mCallback.OnClick("字段匹配", allFields);
									}
									dialogView.dismiss();
								}
							}
						}
					});
		} else {
			PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayerID).SetFieldList(allFields);
			TuiGengDB db = new TuiGengDB();

			if (db.updateLayerField(mLayerID,
					PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayerID).GetFieldListJsonStr())) {
				if (mCallback != null) {
					mCallback.OnClick("字段匹配", allFields);
				}
				dialogView.dismiss();
			}
		}
	}

	private int getMaxSrcField() {
		int max = 1;
		for (String f : srcDataFields) {
			if (f.startsWith("F")) {
				try {
					int i = Integer.parseInt(f.replaceFirst("F", ""));
					if (max < i) {
						max = i;
					}

				} catch (Exception ex) {

				}

			}
		}

		return max;
	}

	private void getSrcFields(String layerID) {
		List<v1_LayerField> fieldList = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID)
				.GetFieldList();
		srcFieldsName = new ArrayList<String>();
		srcFields = new ArrayList<String>();
		srcDataFields = new ArrayList<String>();
		srcFields.add("");
		for (v1_LayerField f : fieldList) {
			String showField = f.GetFieldShortName() == null ? f.GetFieldShortName() : f.GetFieldName();
			srcFields.add(f.GetFieldName() + "(" + showField + " " + f.GetFieldTypeName() + ")");
			srcFieldsName.add(f.GetFieldName());
			srcDataFields.add(f.GetDataFieldName());
		}
	}

	private void initFields(String projectType, String layerID) {
		getSrcFields(layerID);
		mProjectType = projectType;
		allFieldControls.clear();
		if (projectType.equals("林地变更")) {
			HashMap<String, String> srcField = new HashMap<String, String>();

			srcField.put("SHENG(省,字符串)", "SHENG");
			v1_LayerField lf1 = createLayerField("SHENG", "省", "字符串", "", true, 255, 0);
			FieldMatcher fm = new FieldMatcher(dialogView.getContext());
			fm.setMatcherValue(lf1, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm);
			allFieldControls.add(fm);

			srcField.put("SHI(地市,字符串)", "SHI");
			FieldMatcher fm2 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf2 = createLayerField("SHI", "地市", "字符串", "", true, 255, 0);
			fm2.setMatcherValue(lf2, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm2);
			allFieldControls.add(fm2);

			srcField.put("XIAN(县,字符串)", "XIAN");
			FieldMatcher fm3 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf3 = createLayerField("XIAN", "县", "字符串", "", true, 255, 0);
			fm3.setMatcherValue(lf3, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm3);
			allFieldControls.add(fm3);

			srcField.put("XIANG(乡,字符串)", "XIANG");
			FieldMatcher fm4 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf4 = createLayerField("XIANG", "乡", "字符串", "", true, 255, 0);
			fm4.setMatcherValue(lf4, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm4);
			allFieldControls.add(fm4);

			srcField.put("CUN(村,字符串)", "CUN");
			FieldMatcher fm5 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf5 = createLayerField("CUN", "村", "字符串", "", true, 255, 0);
			fm5.setMatcherValue(lf5, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm5);
			allFieldControls.add(fm5);

			FieldMatcher fm6 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf6 = createLayerField("LIN_YE_JU", "林业局", "字符串", "", true, 255, 0);
			fm6.setMatcherValue(lf6, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm6);
			allFieldControls.add(fm6);

			FieldMatcher fm7 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf7 = createLayerField("LIN_CHANG", "林场", "字符串", "", true, 255, 0);
			fm7.setMatcherValue(lf7, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm7);
			allFieldControls.add(fm7);

			FieldMatcher fm8 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf8 = createLayerField("Q_LIN_BAN", "原林班号", "字符串", "", true, 255, 0);
			fm8.setMatcherValue(lf8, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm8);
			allFieldControls.add(fm8);

			FieldMatcher fm9 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf9 = createLayerField("LIN_BAN", "林班号", "字符串", "", true, 255, 0);
			fm9.setMatcherValue(lf9, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm9);
			allFieldControls.add(fm9);

			FieldMatcher fm10 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf10 = createLayerField("Q_XIAO_BAN", "原小班号", "字符串", "", true, 255, 0);
			fm10.setMatcherValue(lf10, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm10);
			allFieldControls.add(fm10);

			FieldMatcher fm11 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf11 = createLayerField("XIAO_BAN", "小班号", "字符串", "", true, 255, 0);
			fm11.setMatcherValue(lf11, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm11);
			allFieldControls.add(fm11);

			FieldMatcher fm12 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf12 = createLayerField("DI_MAO", "地貌", "字符串", "地貌", true, 255, 0);
			fm12.setMatcherValue(lf12, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm12);
			allFieldControls.add(fm12);

			FieldMatcher fm13 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf13 = createLayerField("PO_WEI", "坡位", "字符串", "坡位", true, 255, 0);
			fm13.setMatcherValue(lf13, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm13);
			allFieldControls.add(fm13);

			FieldMatcher fm14 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf14 = createLayerField("PO_XIANG", "坡向", "字符串", "坡向", true, 255, 0);
			fm14.setMatcherValue(lf14, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm14);
			allFieldControls.add(fm14);

			FieldMatcher fm15 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf15 = createLayerField("PO_DU", "坡度", "字符串", "坡度", true, 255, 0);
			fm15.setMatcherValue(lf15, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm15);
			allFieldControls.add(fm15);

			FieldMatcher fm16 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf16 = createLayerField("KE_JI_DU", "交通区位", "字符串", "交通区位", true, 255, 0);
			fm16.setMatcherValue(lf16, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm16);
			allFieldControls.add(fm16);

			// FieldMatcher fm17 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf2 =
			// createLayerField("SHI","地市","字符串","",true,255,0);
			// fm17.setMatcherValue("KE_JI_DU","交通区位","字符串",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm17);
			// allFieldControls.add(fm17);

			FieldMatcher fm18 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf18 = createLayerField("TU_RANG_LX", "土壤类型(名称)", "字符串", "土壤类型(名称)", true, 255, 0);
			fm18.setMatcherValue(lf18, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm18);
			allFieldControls.add(fm18);

			FieldMatcher fm19 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf19 = createLayerField("TU_CENG_HD", "土层厚度", "整型", "", true, 8, 0);
			fm19.setMatcherValue(lf19, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm19);
			allFieldControls.add(fm19);

			// FieldMatcher fm20 = new FieldMatcher(dialogView.getContext());
			// fm20.setMatcherValue("TU_CENG_HD","土层厚度","字符串",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm20);

			FieldMatcher fm21 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf21 = createLayerField("MIAN_JI", "面积", "浮点型", "", true, 255, 0);
			fm21.setMatcherValue(lf21, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm21);
			allFieldControls.add(fm21);

			FieldMatcher fm22 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf22 = createLayerField("Q_LD_QS", "前期土地权属", "字符串", "土地权属", true, 255, 0);
			fm22.setMatcherValue(lf22, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm22);
			allFieldControls.add(fm22);

			// FieldMatcher fm23 = new FieldMatcher(dialogView.getContext());
			// fm23.setMatcherValue("LD_QS","前期土地权属","浮点型",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm23);

			FieldMatcher fm24 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf24 = createLayerField("LD_QS", "土地权属", "字符串", "土地权属", true, 255, 0);
			fm24.setMatcherValue(lf24, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm24);
			allFieldControls.add(fm24);

			FieldMatcher fm25 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf25 = createLayerField("Q_DI_LEI", "前期地类", "字符串", "地类", true, 255, 0);
			fm25.setMatcherValue(lf25, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm25);
			allFieldControls.add(fm25);

			FieldMatcher fm26 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf26 = createLayerField("DI_LEI", "地类", "字符串", "地类", true, 255, 0);
			fm26.setMatcherValue(lf26, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm26);
			allFieldControls.add(fm26);

			FieldMatcher fm27 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf27 = createLayerField("Q_L_Z", "前期林种", "字符串", "林种", true, 255, 0);
			fm27.setMatcherValue(lf27, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm27);
			allFieldControls.add(fm27);

			// FieldMatcher fm28 = new FieldMatcher(dialogView.getContext());
			// fm28.setMatcherValue("Q_L_Z","前期林种","字符串",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm28);

			FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf29 = createLayerField("LIN_ZHONG", "林种", "字符串", "林种", true, 255, 0);
			fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
			allFieldControls.add(fm29);

			FieldMatcher fm30 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf30 = createLayerField("QI_YUAN", "起源", "字符串", "起源", true, 255, 0);
			fm30.setMatcherValue(lf30, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm30);
			allFieldControls.add(fm30);

			FieldMatcher fm31 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf31 = createLayerField("Q_SEN_LB", "前期森林类别", "字符串", "森林（林地）类别", true, 255, 0);
			fm31.setMatcherValue(lf31, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm31);
			allFieldControls.add(fm31);

			FieldMatcher fm32 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf32 = createLayerField("SEN_LIN_LB", "森林类别", "字符串", "森林（林地）类别", true, 255, 0);
			fm32.setMatcherValue(lf32, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm32);
			allFieldControls.add(fm32);

			FieldMatcher fm33 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf33 = createLayerField("Q_SQ_D", "前期事权等级", "字符串", "事权等级", true, 255, 0);
			fm33.setMatcherValue(lf33, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm33);
			allFieldControls.add(fm33);

			FieldMatcher fm34 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf34 = createLayerField("SHI_QUAN_D", "事权等级", "字符串", "事权等级", true, 255, 0);
			fm34.setMatcherValue(lf34, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm34);
			allFieldControls.add(fm34);

			// FieldMatcher fm35 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf2 =
			// createLayerField("SHI","地市","字符串","",true,255,0);
			// fm35.setMatcherValue("SHI_QUAN_D","事权等级","字符串",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm35);
			// allFieldControls.add(fm35);

			FieldMatcher fm36 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf36 = createLayerField("GYL_BHDJ", "公益林保护等级", "字符串", "国家级公益林保护等级", true, 255, 0);
			fm36.setMatcherValue(lf36, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm36);
			allFieldControls.add(fm36);

			FieldMatcher fm37 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf37 = createLayerField("GJGYL_BHDJ", "国家公益林保护等级", "字符串", "国家级公益林保护等级", true, 255, 0);
			fm37.setMatcherValue(lf37, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm37);
			allFieldControls.add(fm37);

			FieldMatcher fm38 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf38 = createLayerField("Q_GC_LB", "前期工程类别", "字符串", "工程类别", true, 255, 0);
			fm38.setMatcherValue(lf38, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm38);
			allFieldControls.add(fm38);

			FieldMatcher fm39 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf39 = createLayerField("G_CHENG_LB", "工程类别", "字符串", "工程类别", true, 255, 0);
			fm39.setMatcherValue(lf39, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm39);
			allFieldControls.add(fm39);

			FieldMatcher fm40 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf40 = createLayerField("LING_ZU", "龄组", "字符串", "龄组", true, 255, 0);
			fm40.setMatcherValue(lf40, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm40);
			allFieldControls.add(fm40);

			FieldMatcher fm41 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf41 = createLayerField("YU_BI_DU", "郁密度/覆盖度", "浮点型", "", true, 8, 0);
			fm41.setMatcherValue(lf41, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm41);
			allFieldControls.add(fm41);

			FieldMatcher fm42 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf42 = createLayerField("YOU_SHI_SZ", "优势树种", "字符串", "树种", true, 255, 0);
			fm42.setMatcherValue(lf42, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm42);
			allFieldControls.add(fm42);

			FieldMatcher fm43 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf43 = createLayerField("PINGJUN_XJ", "平均胸径", "浮点型", "", true, 8, 0);
			fm43.setMatcherValue(lf43, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm43);
			allFieldControls.add(fm43);

			FieldMatcher fm44 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf44 = createLayerField("HUO_LMGQXJ", "每公顷蓄积(活立木)", "浮点型", "", true, 8, 0);
			fm44.setMatcherValue(lf44, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm44);
			allFieldControls.add(fm44);

			FieldMatcher fm45 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf45 = createLayerField("MEI_GQ_ZS", "每公顷株数", "整型", "", true, 8, 0);
			fm45.setMatcherValue(lf45, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm45);
			allFieldControls.add(fm45);

			FieldMatcher fm46 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf46 = createLayerField("TD_TH_LX", "土地退化类型", "字符串", "土地退化类型", true, 255, 0);
			fm46.setMatcherValue(lf46, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm46);
			allFieldControls.add(fm46);

			FieldMatcher fm47 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf47 = createLayerField("DISPE", "灾害类型", "字符串", "灾害类型", true, 255, 0);
			fm47.setMatcherValue(lf47, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm47);
			allFieldControls.add(fm47);

			FieldMatcher fm48 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf48 = createLayerField("DISASTER_C", "灾害等级", "字符串", "森林灾害等级", true, 255, 0);
			fm48.setMatcherValue(lf48, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm48);
			allFieldControls.add(fm48);

			FieldMatcher fm49 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf49 = createLayerField("ZL_DJ", "林地质量等级", "字符串", "林地质量等级", true, 255, 0);
			fm49.setMatcherValue(lf49, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm49);
			allFieldControls.add(fm49);

			FieldMatcher fm50 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf50 = createLayerField("LD_CD", "林带长度", "浮点型", "", true, 8, 0);
			fm50.setMatcherValue(lf50, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm50);
			allFieldControls.add(fm50);

			FieldMatcher fm51 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf51 = createLayerField("LD_KD", "林带宽度", "浮点型", "", true, 255, 0);
			fm51.setMatcherValue(lf51, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm51);
			allFieldControls.add(fm51);

			FieldMatcher fm52 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf52 = createLayerField("BCLD", "是否为补充林地", "字符串", "是否为补充林地", true, 255, 0);
			fm52.setMatcherValue(lf52, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm52);
			allFieldControls.add(fm52);

			FieldMatcher fm53 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf53 = createLayerField("Q_BH_DJ", "前期林地保护等级", "字符串", "林地保护等级", true, 255, 0);
			fm53.setMatcherValue(lf53, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm53);
			allFieldControls.add(fm53);

			FieldMatcher fm54 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf54 = createLayerField("BH_DJ", "林地保护等级", "字符串", "林地保护等级", true, 255, 0);
			fm54.setMatcherValue(lf54, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm54);
			allFieldControls.add(fm54);

			FieldMatcher fm55 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf55 = createLayerField("LYFQ", "林地功能分区", "字符串", "", true, 255, 0);
			fm55.setMatcherValue(lf55, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm55);
			allFieldControls.add(fm55);

			FieldMatcher fm56 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf56 = createLayerField("QYKZ", "主体功能区", "字符串", "主体功能区", true, 255, 0);
			fm56.setMatcherValue(lf56, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm56);
			allFieldControls.add(fm56);

			FieldMatcher fm57 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf57 = createLayerField("BHYY", "变化原因", "字符串", "林地变化原因", true, 255, 0);
			fm57.setMatcherValue(lf57, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm57);
			allFieldControls.add(fm57);

			FieldMatcher fm58 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf58 = createLayerField("BHND", "变化年度", "字符串", "", true, 255, 0);
			fm58.setMatcherValue(lf58, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm58);
			allFieldControls.add(fm58);

			FieldMatcher fm59 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf59 = createLayerField("GLLX", "林地管理类型", "字符串", "林地管理类型", true, 255, 0);
			fm59.setMatcherValue(lf59, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm59);
			allFieldControls.add(fm59);

			FieldMatcher fm60 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf60 = createLayerField("REMARKS", "说明", "字符串", "", true, 255, 0);
			fm60.setMatcherValue(lf60, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm60);
			allFieldControls.add(fm60);

			// for xinjiang 不需要补充调查内容
			// FieldMatcher fm62 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf62 =
			// createLayerField("XIAOBAN_XJ","小班蓄积量","浮点型","",true,8,0);
			// fm62.setMatcherValue(lf62, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm62);
			// allFieldControls.add(fm62);
			//
			// FieldMatcher fm73 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf73 =
			// createLayerField("SPS_ZS","四旁树株数","整型","",true,255,0);
			// fm73.setMatcherValue(lf73, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm73);
			// allFieldControls.add(fm73);
			//
			// FieldMatcher fm63 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf63 =
			// createLayerField("SPS_XJ","四旁树蓄积量","浮点型","",true,8,0);
			// fm63.setMatcherValue(lf63, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm63);
			// allFieldControls.add(fm63);
			//
			// FieldMatcher fm64 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf64 =
			// createLayerField("SH_TAI_QW","生态区位","字符串","",true,255,0);
			// fm64.setMatcherValue(lf64, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm64);
			// allFieldControls.add(fm64);
			//
			// FieldMatcher fm65 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf65 =
			// createLayerField("LM_SY","林木权属","字符串","林木权属",true,255,0);
			// fm65.setMatcherValue(lf65, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm65);
			// allFieldControls.add(fm65);
			//
			// FieldMatcher fm66 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf66 =
			// createLayerField("SSM_ZS","散生木株数","整型","",true,255,0);
			// fm66.setMatcherValue(lf66, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm66);
			// allFieldControls.add(fm66);
			//
			// FieldMatcher fm67 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf67 =
			// createLayerField("SSM_XJ","散生木蓄积","浮点型","",true,255,0);
			// fm67.setMatcherValue(lf67, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm67);
			// allFieldControls.add(fm67);
			//
			// FieldMatcher fm68 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf68 =
			// createLayerField("SSZ_ZS","散生竹株数","整型","",true,255,0);
			// fm68.setMatcherValue(lf68, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm68);
			// allFieldControls.add(fm68);
			//
			// FieldMatcher fm69 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf69 =
			// createLayerField("DIAOCHA_FS","小班蓄积调查方式","字符串","小班蓄积调查方式",true,255,0);
			// fm69.setMatcherValue(lf69, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm69);
			// allFieldControls.add(fm69);
			//
			// FieldMatcher fm70 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf70 =
			// createLayerField("DIAOCHA_RQ","调查日期","日期型","",true,255,0);
			// fm70.setMatcherValue(lf70, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm70);
			// allFieldControls.add(fm70);
			//
			// FieldMatcher fm71 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf71 =
			// createLayerField("DC_REN","调查人","字符串","",true,255,0);
			// fm71.setMatcherValue(lf71, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm71);
			// allFieldControls.add(fm71);
		}

		if (projectType.contains("退耕还林")) {
			initTuiGengField();
		} else if (projectType.equals(ForestryLayerType.DuChaYanZheng)) {
//			initDuChaYanZhengField();
			initDuCha2019();
		}
	}
	
	private void addLayerFieldToView(String shortName, String fieldName, String fieldType, String enumCode,
			boolean enumEidt, int size, int deciaml) {
		v1_LayerField newFile = new v1_LayerField();
		newFile.SetFieldName(fieldName);
		newFile.SetFieldTypeName(fieldType);
		newFile.SetFieldEnumCode(enumCode);
		newFile.SetFieldEnumEdit(enumEidt);
		newFile.SetFieldSize(size);
		newFile.SetFieldDecimal(deciaml);
		newFile.SetIsSelect(true);
		newFile.SetFieldShortName(shortName);

		FieldMatcher fm = new FieldMatcher(dialogView.getContext());
		fm.setMatcherValue(newFile, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm);
		allFieldControls.add(fm);
	}
	
	private void initDuCha2019() {
		allFieldControls.clear();

		addLayerFieldToView("Id", "顺序号", "整型", "", false, 8, 0);
		addLayerFieldToView("PAN_NO_TB", "判读图斑编号", "整型", "", false, 6, 0);
		addLayerFieldToView("SHENG", "省", "字符串", "", false, 2, 0);
		addLayerFieldToView("XIAN", "县", "字符串", "", false, 40, 0);
		addLayerFieldToView("XIANG", "乡镇", "字符串", "", false, 40, 0);
		addLayerFieldToView("CUN", "村", "字符串", "", false, 40, 0);
		addLayerFieldToView("LIN_YE_JU", "林业局", "字符串", "", false, 40, 0);
		addLayerFieldToView("LIN_CHANG", "林场", "字符串", "", false, 40, 0);
		addLayerFieldToView("LIN_BAN", "林班", "字符串", "", false, 40, 0);
		addLayerFieldToView("GPS_X", "横坐标", "整型", "", true, 8, 0);
		addLayerFieldToView("GPS_Y", "纵坐标", "整型", "", true, 7, 0);
		addLayerFieldToView("MIAN_JI", "判读面积", "浮点型", "", true, 18, 4);
		addLayerFieldToView("PAN_BHYY", "判读变化原因", "整形", "", false, 2, 0);
		addLayerFieldToView("PAN_DILEI", "判读地类", "整形", "", false, 5, 0);
		addLayerFieldToView("HSXBH", "核实细斑号", "字符串", "", false, 12, 0);
		addLayerFieldToView("LDGLDW", "林地管理单位", "字符串", "", false, 40, 0);
		addLayerFieldToView("BHYY", "变化原因", "整形", "", false, 3, 0);
		addLayerFieldToView("BEIZU", "判读备注", "字符串", "", false, 250, 0);

		addLayerFieldToView("SHIFOU_WF", "违法违规", "整形", "", false, 1, 0);
		
		addLayerFieldToView("hs_qdl", "前地类", "字符串", "", false, 4, 0);
		addLayerFieldToView("hs_xzdl", "现状地类", "字符串", "", false, 4, 0);

		addLayerFieldToView("XMMC", "项目名称", "字符串", "", false, 100, 0);
		addLayerFieldToView("SH_WH", "审核文号", "字符串", "", false, 50, 0);
		addLayerFieldToView("SH_ND", "审核年度", "字符串", "", false, 4, 0);
		addLayerFieldToView("SH_MJ", "审核面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("SJ_MJ", "实际改变林地用途面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ", "违规违法改变林地用途面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_ZRBHD_MJ", "违规违法中自然保护地面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_ZRBHD_MC", "自然保护地名称", "字符串", "", false, 60, 0);
		addLayerFieldToView("WF_ZRBHD_JB", "自然保护地级别", "整形", "", false, 2, 0);

		addLayerFieldToView("WF_MJ_QM", "违规违法中乔木林地面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_ZL", "违规违法中竹林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_HSL", "违规违法中红树林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_TM", "违规违法中国家特灌林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_GM", "违规违法中其他灌木林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_QT", "违规违法中其他林地面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_YJGYL", "违规违法中一级国家公益林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_EJGYL", "违规违法中二级国家公益林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_DFGYL", "违规违法中地方公益林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_SPL", "违规违法中商品林面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("SYLDXZ", "使用林地性质", "整形", "", false, 1, 0);
		addLayerFieldToView("CFZH", "林木采伐许可证号", "字符串", "", false, 100, 0);
		addLayerFieldToView("FZMJ", "发证面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("FZXJ", "发证蓄积", "浮点型", "", false, 10, 1);
		addLayerFieldToView("PZMJ", "凭证采伐面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("PZXJ", "凭证采伐蓄积", "浮点型", "", false, 10, 1);
		addLayerFieldToView("CZMJ", "超证采伐面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("CZXJ", "超证采伐蓄积", "浮点型", "", false, 10, 1);
		addLayerFieldToView("WZMJ", "无证采伐面积", "浮点型", "", false, 10, 4);
		addLayerFieldToView("WZXJ", "无证采伐蓄积", "浮点型", "", false, 10, 1);
		addLayerFieldToView("BEIZHU2", "备注", "字符串", "", false, 250, 0);
		
		v1_LayerField lf29 = createLayerField("是否违法违规", "是否违法违规", "整形", "", true, 1, 0);
		FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
		fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
		allFieldControls.add(fm29);
	}

	private void initDuChaYanZhengField() {
		allFieldControls.clear();
		v1_LayerField lf1 = createLayerField("省", "省", "字符串", "", true, 30, 0);
		FieldMatcher fm = new FieldMatcher(dialogView.getContext());
		fm.setMatcherValue(lf1, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm);
		allFieldControls.add(fm);

		v1_LayerField lf2 = createLayerField("县", "县", "字符串", "", true, 30, 0);
		FieldMatcher fm2 = new FieldMatcher(dialogView.getContext());
		fm2.setMatcherValue(lf2, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm2);
		allFieldControls.add(fm2);

		v1_LayerField lf3 = createLayerField("乡镇", "乡镇", "字符串", "", true, 30, 0);
		FieldMatcher fm3 = new FieldMatcher(dialogView.getContext());
		fm3.setMatcherValue(lf3, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm3);
		allFieldControls.add(fm3);

		v1_LayerField lf4 = createLayerField("村", "村", "字符串", "", true, 30, 0);
		FieldMatcher fm4 = new FieldMatcher(dialogView.getContext());
		fm4.setMatcherValue(lf4, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm4);
		allFieldControls.add(fm4);

		v1_LayerField lf26 = createLayerField("图斑号", "图斑号", "整形", "", true, 5, 0);
		FieldMatcher fm26 = new FieldMatcher(dialogView.getContext());
		fm26.setMatcherValue(lf26, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm26);
		allFieldControls.add(fm26);

		v1_LayerField lf27 = createLayerField("横坐标", "横坐标", "浮点型", "", true, 12, 3);
		FieldMatcher fm27 = new FieldMatcher(dialogView.getContext());
		fm27.setMatcherValue(lf27, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm27);
		allFieldControls.add(fm27);

		v1_LayerField lf28 = createLayerField("纵坐标", "纵坐标", "浮点型", "", true, 12, 3);
		FieldMatcher fm28 = new FieldMatcher(dialogView.getContext());
		fm28.setMatcherValue(lf28, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm28);
		allFieldControls.add(fm28);

		v1_LayerField lf5 = createLayerField("判读面积", "判读面积", "浮点型", "", true, 18, 4);
		FieldMatcher fm5 = new FieldMatcher(dialogView.getContext());
		fm5.setMatcherValue(lf5, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm5);
		allFieldControls.add(fm5);

		v1_LayerField lf6 = createLayerField("前期时间", "前期时间", "字符串", "", true, 8, 0);
		FieldMatcher fm6 = new FieldMatcher(dialogView.getContext());
		fm6.setMatcherValue(lf6, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm6);
		allFieldControls.add(fm6);

		v1_LayerField lf7 = createLayerField("后期时间", "后期时间", "字符串", "", true, 8, 0);
		FieldMatcher fm7 = new FieldMatcher(dialogView.getContext());
		fm7.setMatcherValue(lf7, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm7);
		allFieldControls.add(fm7);

		v1_LayerField lf8 = createLayerField("备注", "备注", "字符串", "", true, 255, 0);
		FieldMatcher fm8 = new FieldMatcher(dialogView.getContext());
		fm8.setMatcherValue(lf8, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm8);
		allFieldControls.add(fm8);

		v1_LayerField lf9 = createLayerField("前地类", "前地类", "整形", "", true, 1, 0);
		FieldMatcher fm9 = new FieldMatcher(dialogView.getContext());
		fm9.setMatcherValue(lf9, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm9);
		allFieldControls.add(fm9);

		v1_LayerField lf10 = createLayerField("现地类", "现地类", "整形", "", true, 1, 0);
		FieldMatcher fm10 = new FieldMatcher(dialogView.getContext());
		fm10.setMatcherValue(lf10, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm10);
		allFieldControls.add(fm10);

		v1_LayerField lf11 = createLayerField("重点生态区域名称", "重点生态区域名称", "字符串", "", true, 64, 0);
		FieldMatcher fm11 = new FieldMatcher(dialogView.getContext());
		fm11.setMatcherValue(lf11, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm11);
		allFieldControls.add(fm11);

		v1_LayerField lf12 = createLayerField("改变面积", "改变面积", "浮点型", "", true, 18, 4);
		FieldMatcher fm12 = new FieldMatcher(dialogView.getContext());
		fm12.setMatcherValue(lf12, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm12);
		allFieldControls.add(fm12);

		v1_LayerField lf13 = createLayerField("违规改变面积", "违规改变面积", "浮点型", "", true, 18, 4);
		FieldMatcher fm13 = new FieldMatcher(dialogView.getContext());
		fm13.setMatcherValue(lf13, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm13);
		allFieldControls.add(fm13);

		v1_LayerField lf14 = createLayerField("采伐蓄积", "采伐蓄积", "浮点型", "", true, 12, 4);
		FieldMatcher fm14 = new FieldMatcher(dialogView.getContext());
		fm14.setMatcherValue(lf14, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm14);
		allFieldControls.add(fm14);

		v1_LayerField lf15 = createLayerField("违规采伐蓄积", "违规采伐蓄积", "浮点型", "", true, 12, 4);
		FieldMatcher fm15 = new FieldMatcher(dialogView.getContext());
		fm15.setMatcherValue(lf15, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm15);
		allFieldControls.add(fm15);

		v1_LayerField lf16 = createLayerField("变化原因", "变化原因", "整形", "", true, 2, 0);
		FieldMatcher fm16 = new FieldMatcher(dialogView.getContext());
		fm16.setMatcherValue(lf16, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm16);
		allFieldControls.add(fm16);

		v1_LayerField lf17 = createLayerField("检查级别", "检查级别", "整形", "", true, 1, 0);
		FieldMatcher fm17 = new FieldMatcher(dialogView.getContext());
		fm17.setMatcherValue(lf17, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm17);
		allFieldControls.add(fm17);

		v1_LayerField lf18 = createLayerField("结果是否一致", "结果是否一致", "整形", "", true, 1, 0);
		FieldMatcher fm18 = new FieldMatcher(dialogView.getContext());
		fm18.setMatcherValue(lf18, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm18);
		allFieldControls.add(fm18);

		v1_LayerField lf19 = createLayerField("检查备注", "检查备注", "字符串", "", true, 255, 0);
		FieldMatcher fm19 = new FieldMatcher(dialogView.getContext());
		fm19.setMatcherValue(lf19, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm19);
		allFieldControls.add(fm19);

		v1_LayerField lf20 = createLayerField("检查单位名称", "检查单位名称", "字符串", "", true, 255, 0);
		FieldMatcher fm20 = new FieldMatcher(dialogView.getContext());
		fm20.setMatcherValue(lf20, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm20);
		allFieldControls.add(fm20);

		v1_LayerField lf21 = createLayerField("检查人员", "检查人员", "字符串", "", true, 255, 0);
		FieldMatcher fm21 = new FieldMatcher(dialogView.getContext());
		fm21.setMatcherValue(lf21, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm21);
		allFieldControls.add(fm21);

		v1_LayerField lf22 = createLayerField("检查日期", "检查日期", "字符串", "", true, 8, 0);
		FieldMatcher fm22 = new FieldMatcher(dialogView.getContext());
		fm22.setMatcherValue(lf22, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm22);
		allFieldControls.add(fm22);

		v1_LayerField lf23 = createLayerField("调查年度", "调查年度", "字符串", "", true, 4, 0);
		FieldMatcher fm23 = new FieldMatcher(dialogView.getContext());
		fm23.setMatcherValue(lf23, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm23);
		allFieldControls.add(fm23);

		v1_LayerField lf29 = createLayerField("是否违法违规", "是否违法违规", "整形", "", true, 1, 0);
		FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
		fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
		allFieldControls.add(fm29);

	}

	private void initTuiGengField() {
		allFieldControls.clear();
		HashMap<String, String> srcField = new HashMap<String, String>();

		// srcField.put("地市", "地市");
		v1_LayerField lf1 = createLayerField("地市", "地市", "字符串", "", true, 255, 0);
		FieldMatcher fm = new FieldMatcher(dialogView.getContext());
		fm.setMatcherValue(lf1, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm);
		allFieldControls.add(fm);

		// srcField.put("区县", "区县");
		v1_LayerField lf2 = createLayerField("区县", "区县", "字符串", "", true, 255, 0);
		FieldMatcher fm2 = new FieldMatcher(dialogView.getContext());
		fm2.setMatcherValue(lf2, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm2);
		allFieldControls.add(fm2);

		srcField.put("乡镇", "乡镇");
		v1_LayerField lf3 = createLayerField("乡镇", "乡镇", "字符串", "", true, 255, 0);
		FieldMatcher fm3 = new FieldMatcher(dialogView.getContext());
		fm3.setMatcherValue(lf3, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm3);
		allFieldControls.add(fm3);

		srcField.put("建制村", "建制村");
		v1_LayerField lf4 = createLayerField("建制村", "建制村", "字符串", "", true, 255, 0);
		FieldMatcher fm4 = new FieldMatcher(dialogView.getContext());
		fm4.setMatcherValue(lf4, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm4);
		allFieldControls.add(fm4);

		v1_LayerField lf5 = createLayerField("自然村", "自然村", "字符串", "", true, 255, 0);
		FieldMatcher fm5 = new FieldMatcher(dialogView.getContext());
		fm5.setMatcherValue(lf5, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm5);
		allFieldControls.add(fm5);

		srcField.put("小地名", "小地名");
		v1_LayerField lf6 = createLayerField("小地名", "小地名", "字符串", "", true, 255, 0);
		FieldMatcher fm6 = new FieldMatcher(dialogView.getContext());
		fm6.setMatcherValue(lf6, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm6);
		allFieldControls.add(fm6);

		srcField.put("小班号", "小班号");
		v1_LayerField lf7 = createLayerField("小班号", "小班号", "字符串", "", true, 255, 0);
		FieldMatcher fm7 = new FieldMatcher(dialogView.getContext());
		fm7.setMatcherValue(lf7, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm7);
		allFieldControls.add(fm7);

		v1_LayerField lf8 = createLayerField("地类", "地类", "字符串", "", true, 255, 0);
		FieldMatcher fm8 = new FieldMatcher(dialogView.getContext());
		fm8.setMatcherValue(lf8, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm8);
		allFieldControls.add(fm8);

		v1_LayerField lf9 = createLayerField("", "小班面积", "浮点型", "", true, 16, 2);
		FieldMatcher fm9 = new FieldMatcher(dialogView.getContext());
		fm9.setMatcherValue(lf9, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm9);
		allFieldControls.add(fm9);

		srcField.put("可作业面积", "可作业面积");
		v1_LayerField lf10 = createLayerField("可作业面积", "可作业面积", "浮点型", "", true, 16, 2);
		FieldMatcher fm10 = new FieldMatcher(dialogView.getContext());
		fm10.setMatcherValue(lf10, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm10);
		allFieldControls.add(fm10);

		v1_LayerField lf11 = createLayerField("权属", "权属", "字符串", "", true, 255, 0);
		FieldMatcher fm11 = new FieldMatcher(dialogView.getContext());
		fm11.setMatcherValue(lf11, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm11);
		allFieldControls.add(fm11);

		v1_LayerField lf12 = createLayerField("海拔", "海拔", "浮点型", "", true, 8, 0);
		FieldMatcher fm12 = new FieldMatcher(dialogView.getContext());
		fm12.setMatcherValue(lf12, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm12);
		allFieldControls.add(fm12);

		v1_LayerField lf13 = createLayerField("坡位", "坡位", "字符串", "", true, 255, 0);
		FieldMatcher fm13 = new FieldMatcher(dialogView.getContext());
		fm13.setMatcherValue(lf13, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm13);
		allFieldControls.add(fm13);

		v1_LayerField lf14 = createLayerField("坡向", "坡向", "字符串", "", true, 255, 0);
		FieldMatcher fm14 = new FieldMatcher(dialogView.getContext());
		fm14.setMatcherValue(lf14, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm14);
		allFieldControls.add(fm14);

		v1_LayerField lf15 = createLayerField("", "坡度", "字符串", "", true, 255, 0);
		FieldMatcher fm15 = new FieldMatcher(dialogView.getContext());
		fm15.setMatcherValue(lf15, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm15);
		allFieldControls.add(fm15);

		v1_LayerField lf16 = createLayerField("土壤名称", "土壤名称", "字符串", "", true, 255, 0);
		FieldMatcher fm16 = new FieldMatcher(dialogView.getContext());
		fm16.setMatcherValue(lf16, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm16);
		allFieldControls.add(fm16);

		v1_LayerField lf17 = createLayerField("土层厚度", "土层厚度", "字符串", "", true, 255, 0);
		FieldMatcher fm17 = new FieldMatcher(dialogView.getContext());
		fm17.setMatcherValue(lf17, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm17);
		allFieldControls.add(fm17);

		v1_LayerField lf18 = createLayerField("土壤肥力", "土壤肥力", "字符串", "", true, 255, 0);
		FieldMatcher fm18 = new FieldMatcher(dialogView.getContext());
		fm18.setMatcherValue(lf18, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm18);
		allFieldControls.add(fm18);

		v1_LayerField lf19 = createLayerField("侵蚀程度", "侵蚀程度", "字符串", "", true, 255, 0);
		FieldMatcher fm19 = new FieldMatcher(dialogView.getContext());
		fm19.setMatcherValue(lf19, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm19);
		allFieldControls.add(fm19);

		v1_LayerField lf20 = createLayerField("", "土壤PH值", "浮点型", "", true, 8, 0);
		FieldMatcher fm20 = new FieldMatcher(dialogView.getContext());
		fm20.setMatcherValue(lf20, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm20);
		allFieldControls.add(fm20);

		v1_LayerField lf21 = createLayerField("", "植被类型", "字符串", "", true, 255, 0);
		FieldMatcher fm21 = new FieldMatcher(dialogView.getContext());
		fm21.setMatcherValue(lf21, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm21);
		allFieldControls.add(fm21);

		v1_LayerField lf22 = createLayerField("", "植被盖度", "浮点型", "", true, 8, 0);
		FieldMatcher fm22 = new FieldMatcher(dialogView.getContext());
		fm22.setMatcherValue(lf22, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm22);
		allFieldControls.add(fm22);

		v1_LayerField lf23 = createLayerField("", "植被高度", "浮点型", "", true, 8, 0);
		FieldMatcher fm23 = new FieldMatcher(dialogView.getContext());
		fm23.setMatcherValue(lf23, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm23);
		allFieldControls.add(fm23);

		v1_LayerField lf24 = createLayerField("是否变更", "是否变更", "字符串", "", true, 255, 0);
		FieldMatcher fm24 = new FieldMatcher(dialogView.getContext());
		fm24.setMatcherValue(lf24, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm24);
		allFieldControls.add(fm24);

		v1_LayerField lf25 = createLayerField("变更原因", "变更原因", "字符串", "", true, 255, 0);
		FieldMatcher fm25 = new FieldMatcher(dialogView.getContext());
		fm25.setMatcherValue(lf25, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm25);
		allFieldControls.add(fm25);

		v1_LayerField lf26 = createLayerField("图幅号", "图幅号", "字符串", "", true, 255, 0);
		FieldMatcher fm26 = new FieldMatcher(dialogView.getContext());
		fm26.setMatcherValue(lf26, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm26);
		allFieldControls.add(fm26);

		v1_LayerField lf27 = createLayerField("横坐标", "横坐标", "字符串", "", true, 255, 0);
		FieldMatcher fm27 = new FieldMatcher(dialogView.getContext());
		fm27.setMatcherValue(lf27, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm27);
		allFieldControls.add(fm27);

		v1_LayerField lf28 = createLayerField("纵坐标", "纵坐标", "字符串", "", true, 255, 0);
		FieldMatcher fm28 = new FieldMatcher(dialogView.getContext());
		fm28.setMatcherValue(lf28, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm28);
		allFieldControls.add(fm28);

		v1_LayerField lf29 = createLayerField("设计年度", "设计年度", "字符串", "", true, 255, 0);
		FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
		fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
		allFieldControls.add(fm29);

		v1_LayerField lf30 = createLayerField("模式号", "模式号", "字符串", "", true, 255, 0);
		FieldMatcher fm30 = new FieldMatcher(dialogView.getContext());
		fm30.setMatcherValue(lf30, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm30);
		allFieldControls.add(fm30);

		v1_LayerField lf31 = createLayerField("造林方式", "造林方式", "字符串", "", true, 255, 0);
		FieldMatcher fm31 = new FieldMatcher(dialogView.getContext());
		fm31.setMatcherValue(lf31, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm31);
		allFieldControls.add(fm31);

		v1_LayerField lf32 = createLayerField("造林树种", "造林树种", "字符串", "", true, 255, 0);
		FieldMatcher fm32 = new FieldMatcher(dialogView.getContext());
		fm32.setMatcherValue(lf32, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm32);
		allFieldControls.add(fm32);

		v1_LayerField lf33 = createLayerField("造林面积", "造林面积", "浮点型", "", true, 12, 2);
		FieldMatcher fm33 = new FieldMatcher(dialogView.getContext());
		fm33.setMatcherValue(lf33, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm33);
		allFieldControls.add(fm33);

		v1_LayerField lf34 = createLayerField("立地类型", "立地类型", "字符串", "", true, 255, 0);
		FieldMatcher fm34 = new FieldMatcher(dialogView.getContext());
		fm34.setMatcherValue(lf34, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm34);
		allFieldControls.add(fm34);

		v1_LayerField lf35 = createLayerField("造林林种", "造林林种", "字符串", "", true, 255, 0);
		FieldMatcher fm35 = new FieldMatcher(dialogView.getContext());
		fm35.setMatcherValue(lf35, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm35);
		allFieldControls.add(fm35);

		v1_LayerField lf36 = createLayerField("株行距", "株行距", "字符串", "", true, 255, 0);
		FieldMatcher fm36 = new FieldMatcher(dialogView.getContext());
		fm36.setMatcherValue(lf36, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm36);
		allFieldControls.add(fm36);

		v1_LayerField lf37 = createLayerField("混交比", "混交比", "字符串", "", true, 255, 0);
		FieldMatcher fm37 = new FieldMatcher(dialogView.getContext());
		fm37.setMatcherValue(lf37, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm37);
		allFieldControls.add(fm37);

		v1_LayerField lf38 = createLayerField("抚育年次", "抚育年次", "浮点型", "", true, 8, 0);
		FieldMatcher fm38 = new FieldMatcher(dialogView.getContext());
		fm38.setMatcherValue(lf38, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm38);
		allFieldControls.add(fm38);

		v1_LayerField lf39 = createLayerField("抚育时间", "抚育时间", "字符串", "", true, 255, 0);
		FieldMatcher fm39 = new FieldMatcher(dialogView.getContext());
		fm39.setMatcherValue(lf39, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm39);
		allFieldControls.add(fm39);

		v1_LayerField lf40 = createLayerField("种苗需要量", "种苗需要量", "浮点型", "", true, 8, 0);
		FieldMatcher fm40 = new FieldMatcher(dialogView.getContext());
		fm40.setMatcherValue(lf40, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm40);
		allFieldControls.add(fm40);

		v1_LayerField lf41 = createLayerField("苗木规格", "苗木规格", "字符串", "", true, 255, 0);
		FieldMatcher fm41 = new FieldMatcher(dialogView.getContext());
		fm41.setMatcherValue(lf41, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm41);
		allFieldControls.add(fm41);

		v1_LayerField lf42 = createLayerField("苗木单价", "苗木单价", "浮点型", "", true, 8, 0);
		FieldMatcher fm42 = new FieldMatcher(dialogView.getContext());
		fm42.setMatcherValue(lf42, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm42);
		allFieldControls.add(fm42);

		v1_LayerField lf43 = createLayerField("整地方式", "整地方式", "字符串", "", true, 255, 0);
		FieldMatcher fm43 = new FieldMatcher(dialogView.getContext());
		fm43.setMatcherValue(lf43, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm43);
		allFieldControls.add(fm43);

		v1_LayerField lf44 = createLayerField("整地规格", "整地规格", "字符串", "", true, 255, 0);
		FieldMatcher fm44 = new FieldMatcher(dialogView.getContext());
		fm44.setMatcherValue(lf44, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm44);
		allFieldControls.add(fm44);

		v1_LayerField lf45 = createLayerField("用工量合计", "用工量合计", "字符串", "", true, 255, 0);
		FieldMatcher fm45 = new FieldMatcher(dialogView.getContext());
		fm45.setMatcherValue(lf45, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm45);
		allFieldControls.add(fm45);

		v1_LayerField lf46 = createLayerField("用工量整地", "用工量整地", "字符串", "", true, 255, 0);
		FieldMatcher fm46 = new FieldMatcher(dialogView.getContext());
		fm46.setMatcherValue(lf46, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm46);
		allFieldControls.add(fm46);

		v1_LayerField lf47 = createLayerField("用工量造林", "用工量造林", "字符串", "", true, 255, 0);
		FieldMatcher fm47 = new FieldMatcher(dialogView.getContext());
		fm47.setMatcherValue(lf47, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm47);
		allFieldControls.add(fm47);

		v1_LayerField lf48 = createLayerField("用工量补植", "用工量补植", "字符串", "", true, 255, 0);
		FieldMatcher fm48 = new FieldMatcher(dialogView.getContext());
		fm48.setMatcherValue(lf48, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm48);
		allFieldControls.add(fm48);

		v1_LayerField lf49 = createLayerField("用工量抚育", "用工量抚育", "字符串", "", true, 255, 0);
		FieldMatcher fm49 = new FieldMatcher(dialogView.getContext());
		fm49.setMatcherValue(lf49, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm49);
		allFieldControls.add(fm49);

		v1_LayerField lf50 = createLayerField("工日单价", "工日单价", "字符串", "", true, 255, 0);
		FieldMatcher fm50 = new FieldMatcher(dialogView.getContext());
		fm50.setMatcherValue(lf50, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm50);
		allFieldControls.add(fm50);

		v1_LayerField lf51 = createLayerField("_户名", "_户名", "字符串", "", true, 255, 0);
		FieldMatcher fm51 = new FieldMatcher(dialogView.getContext());
		fm51.setMatcherValue(lf51, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm51);
		allFieldControls.add(fm51);

		v1_LayerField lf52 = createLayerField("_地类", "_地类", "字符串", "", true, 255, 0);
		FieldMatcher fm52 = new FieldMatcher(dialogView.getContext());
		fm52.setMatcherValue(lf52, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm52);
		allFieldControls.add(fm52);

		v1_LayerField lf53 = createLayerField("_面积", "_面积", "字符串", "", true, 255, 0);
		FieldMatcher fm53 = new FieldMatcher(dialogView.getContext());
		fm53.setMatcherValue(lf53, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm53);
		allFieldControls.add(fm53);

		v1_LayerField lf54 = createLayerField("_林种", "_林种", "字符串", "", true, 255, 0);
		FieldMatcher fm54 = new FieldMatcher(dialogView.getContext());
		fm54.setMatcherValue(lf54, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm54);
		allFieldControls.add(fm54);

		v1_LayerField lf55 = createLayerField("_树种", "_树种", "字符串", "", true, 255, 0);
		FieldMatcher fm55 = new FieldMatcher(dialogView.getContext());
		fm55.setMatcherValue(lf55, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm55);
		allFieldControls.add(fm55);

		v1_LayerField lf56 = createLayerField("_种子", "_种子", "字符串", "", true, 255, 0);
		FieldMatcher fm56 = new FieldMatcher(dialogView.getContext());
		fm56.setMatcherValue(lf56, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm56);
		allFieldControls.add(fm56);

		v1_LayerField lf57 = createLayerField("_苗木", "_苗木", "字符串", "", true, 255, 0);
		FieldMatcher fm57 = new FieldMatcher(dialogView.getContext());
		fm57.setMatcherValue(lf57, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm57);
		allFieldControls.add(fm57);

		v1_LayerField lf58 = createLayerField("投资预算", "投资预算", "字符串", "", true, 255, 0);
		FieldMatcher fm58 = new FieldMatcher(dialogView.getContext());
		fm58.setMatcherValue(lf58, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm58);
		allFieldControls.add(fm58);

		v1_LayerField lf59 = createLayerField("备注", "备注", "字符串", "", true, 255, 0);
		FieldMatcher fm59 = new FieldMatcher(dialogView.getContext());
		fm59.setMatcherValue(lf59, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm59);
		allFieldControls.add(fm59);

		v1_LayerField lf60 = createLayerField("地市代码", "地市代码", "字符串", "", true, 255, 0);
		FieldMatcher fm60 = new FieldMatcher(dialogView.getContext());
		fm60.setMatcherValue(lf60, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm60);
		allFieldControls.add(fm60);

		v1_LayerField lf61 = createLayerField("区县代码", "区县代码", "字符串", "", true, 255, 0);
		FieldMatcher fm61 = new FieldMatcher(dialogView.getContext());
		fm61.setMatcherValue(lf61, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm61);
		allFieldControls.add(fm61);

		v1_LayerField lf62 = createLayerField("乡镇代码", "乡镇代码", "字符串", "", true, 255, 0);
		FieldMatcher fm62 = new FieldMatcher(dialogView.getContext());
		fm62.setMatcherValue(lf62, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm62);
		allFieldControls.add(fm62);

		v1_LayerField lf63 = createLayerField("村代码", "村代码", "字符串", "", true, 255, 0);
		FieldMatcher fm63 = new FieldMatcher(dialogView.getContext());
		fm63.setMatcherValue(lf63, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm63);
		allFieldControls.add(fm63);

		v1_LayerField lf64 = createLayerField("D_户名", "D_户名", "字符串", "", true, 255, 0);
		FieldMatcher fm64 = new FieldMatcher(dialogView.getContext());
		fm64.setMatcherValue(lf64, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm64);
		allFieldControls.add(fm64);

		v1_LayerField lf65 = createLayerField("D_兑现年度", "D_兑现年度", "字符串", "", true, 255, 0);
		FieldMatcher fm65 = new FieldMatcher(dialogView.getContext());
		fm65.setMatcherValue(lf65, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm65);
		allFieldControls.add(fm65);

		v1_LayerField lf66 = createLayerField("D_兑现金额", "D_兑现金额", "字符串", "", true, 255, 0);
		FieldMatcher fm66 = new FieldMatcher(dialogView.getContext());
		fm66.setMatcherValue(lf66, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm66);
		allFieldControls.add(fm66);

		v1_LayerField lf67 = createLayerField("D_政策补助", "D_政策补助", "字符串", "", true, 255, 0);
		FieldMatcher fm67 = new FieldMatcher(dialogView.getContext());
		fm67.setMatcherValue(lf67, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm67);
		allFieldControls.add(fm67);

		v1_LayerField lf68 = createLayerField("D_补助标准", "D_补助标准", "字符串", "", true, 255, 0);
		FieldMatcher fm68 = new FieldMatcher(dialogView.getContext());
		fm68.setMatcherValue(lf68, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm68);
		allFieldControls.add(fm68);

		v1_LayerField lf69 = createLayerField("D_种苗费", "D_种苗费", "字符串", "", true, 255, 0);
		FieldMatcher fm69 = new FieldMatcher(dialogView.getContext());
		fm69.setMatcherValue(lf69, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm69);
		allFieldControls.add(fm69);

		v1_LayerField lf70 = createLayerField("D_是否兑现", "D_是否兑现", "字符串", "", true, 255, 0);
		FieldMatcher fm70 = new FieldMatcher(dialogView.getContext());
		fm70.setMatcherValue(lf70, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm70);
		allFieldControls.add(fm70);

		v1_LayerField lf71 = createLayerField("D_备注", "D_备注", "字符串", "", true, 255, 0);
		FieldMatcher fm71 = new FieldMatcher(dialogView.getContext());
		fm71.setMatcherValue(lf71, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm71);
		allFieldControls.add(fm71);

		v1_LayerField lf72 = createLayerField("_是否贫困户", "_是否贫困户", "字符串", "", true, 255, 0);
		FieldMatcher fm72 = new FieldMatcher(dialogView.getContext());
		fm72.setMatcherValue(lf72, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm72);
		allFieldControls.add(fm72);

	}

	private v1_LayerField createLayerField(String shortName, String fieldName, String fieldType, String enumCode,
			boolean enumEidt, int size, int deciaml) {
		v1_LayerField newFile = new v1_LayerField();
		newFile.SetFieldName(fieldName);
		newFile.SetFieldTypeName(fieldType);
		newFile.SetFieldEnumCode(enumCode);
		newFile.SetFieldEnumEdit(enumEidt);
		newFile.SetFieldSize(size);
		newFile.SetFieldDecimal(deciaml);
		newFile.SetIsSelect(true);
		newFile.SetFieldShortName(shortName);
		return newFile;
	}

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {

			}
		});
		dialogView.show();
	}
}
