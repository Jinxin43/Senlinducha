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

		dialogView.SetCaption(Tools.ToLocale("�ֶ�ƥ��"));
		dialogView.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("ȷ��") + "  ,ȷ��",
				btnCallback);
		mLayerID = layerID;
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_projecttype, "��Ŀ���ͣ�" + projectType);
		initFields(projectType, layerID);

	}

	// ������ť�¼�
	private ICallback btnCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			// TODO Auto-generated method stub

			if (Str.equals("ȷ��")) {
				Tools.OpenDialog("���ڱ����ֶ�ƥ������...", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						saveFields();
					}
				});
			}
		}
	};

	// �ֶβ�����ɺ�Ļص�
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
					Tools.ShowMessageBox("δƥ���ֶ������������ݿ����ƣ�");
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
			Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "��" + noContains + "��Դ�ֶ�δ��ƥ�䣬�Ƿ��������ƥ������ \r\n" + fields,
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
										mCallback.OnClick("�ֶ�ƥ��", allFields);
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
					mCallback.OnClick("�ֶ�ƥ��", allFields);
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
		if (projectType.equals("�ֵر��")) {
			HashMap<String, String> srcField = new HashMap<String, String>();

			srcField.put("SHENG(ʡ,�ַ���)", "SHENG");
			v1_LayerField lf1 = createLayerField("SHENG", "ʡ", "�ַ���", "", true, 255, 0);
			FieldMatcher fm = new FieldMatcher(dialogView.getContext());
			fm.setMatcherValue(lf1, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm);
			allFieldControls.add(fm);

			srcField.put("SHI(����,�ַ���)", "SHI");
			FieldMatcher fm2 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf2 = createLayerField("SHI", "����", "�ַ���", "", true, 255, 0);
			fm2.setMatcherValue(lf2, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm2);
			allFieldControls.add(fm2);

			srcField.put("XIAN(��,�ַ���)", "XIAN");
			FieldMatcher fm3 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf3 = createLayerField("XIAN", "��", "�ַ���", "", true, 255, 0);
			fm3.setMatcherValue(lf3, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm3);
			allFieldControls.add(fm3);

			srcField.put("XIANG(��,�ַ���)", "XIANG");
			FieldMatcher fm4 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf4 = createLayerField("XIANG", "��", "�ַ���", "", true, 255, 0);
			fm4.setMatcherValue(lf4, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm4);
			allFieldControls.add(fm4);

			srcField.put("CUN(��,�ַ���)", "CUN");
			FieldMatcher fm5 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf5 = createLayerField("CUN", "��", "�ַ���", "", true, 255, 0);
			fm5.setMatcherValue(lf5, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm5);
			allFieldControls.add(fm5);

			FieldMatcher fm6 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf6 = createLayerField("LIN_YE_JU", "��ҵ��", "�ַ���", "", true, 255, 0);
			fm6.setMatcherValue(lf6, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm6);
			allFieldControls.add(fm6);

			FieldMatcher fm7 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf7 = createLayerField("LIN_CHANG", "�ֳ�", "�ַ���", "", true, 255, 0);
			fm7.setMatcherValue(lf7, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm7);
			allFieldControls.add(fm7);

			FieldMatcher fm8 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf8 = createLayerField("Q_LIN_BAN", "ԭ�ְ��", "�ַ���", "", true, 255, 0);
			fm8.setMatcherValue(lf8, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm8);
			allFieldControls.add(fm8);

			FieldMatcher fm9 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf9 = createLayerField("LIN_BAN", "�ְ��", "�ַ���", "", true, 255, 0);
			fm9.setMatcherValue(lf9, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm9);
			allFieldControls.add(fm9);

			FieldMatcher fm10 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf10 = createLayerField("Q_XIAO_BAN", "ԭС���", "�ַ���", "", true, 255, 0);
			fm10.setMatcherValue(lf10, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm10);
			allFieldControls.add(fm10);

			FieldMatcher fm11 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf11 = createLayerField("XIAO_BAN", "С���", "�ַ���", "", true, 255, 0);
			fm11.setMatcherValue(lf11, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm11);
			allFieldControls.add(fm11);

			FieldMatcher fm12 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf12 = createLayerField("DI_MAO", "��ò", "�ַ���", "��ò", true, 255, 0);
			fm12.setMatcherValue(lf12, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm12);
			allFieldControls.add(fm12);

			FieldMatcher fm13 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf13 = createLayerField("PO_WEI", "��λ", "�ַ���", "��λ", true, 255, 0);
			fm13.setMatcherValue(lf13, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm13);
			allFieldControls.add(fm13);

			FieldMatcher fm14 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf14 = createLayerField("PO_XIANG", "����", "�ַ���", "����", true, 255, 0);
			fm14.setMatcherValue(lf14, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm14);
			allFieldControls.add(fm14);

			FieldMatcher fm15 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf15 = createLayerField("PO_DU", "�¶�", "�ַ���", "�¶�", true, 255, 0);
			fm15.setMatcherValue(lf15, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm15);
			allFieldControls.add(fm15);

			FieldMatcher fm16 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf16 = createLayerField("KE_JI_DU", "��ͨ��λ", "�ַ���", "��ͨ��λ", true, 255, 0);
			fm16.setMatcherValue(lf16, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm16);
			allFieldControls.add(fm16);

			// FieldMatcher fm17 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf2 =
			// createLayerField("SHI","����","�ַ���","",true,255,0);
			// fm17.setMatcherValue("KE_JI_DU","��ͨ��λ","�ַ���",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm17);
			// allFieldControls.add(fm17);

			FieldMatcher fm18 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf18 = createLayerField("TU_RANG_LX", "��������(����)", "�ַ���", "��������(����)", true, 255, 0);
			fm18.setMatcherValue(lf18, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm18);
			allFieldControls.add(fm18);

			FieldMatcher fm19 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf19 = createLayerField("TU_CENG_HD", "������", "����", "", true, 8, 0);
			fm19.setMatcherValue(lf19, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm19);
			allFieldControls.add(fm19);

			// FieldMatcher fm20 = new FieldMatcher(dialogView.getContext());
			// fm20.setMatcherValue("TU_CENG_HD","������","�ַ���",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm20);

			FieldMatcher fm21 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf21 = createLayerField("MIAN_JI", "���", "������", "", true, 255, 0);
			fm21.setMatcherValue(lf21, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm21);
			allFieldControls.add(fm21);

			FieldMatcher fm22 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf22 = createLayerField("Q_LD_QS", "ǰ������Ȩ��", "�ַ���", "����Ȩ��", true, 255, 0);
			fm22.setMatcherValue(lf22, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm22);
			allFieldControls.add(fm22);

			// FieldMatcher fm23 = new FieldMatcher(dialogView.getContext());
			// fm23.setMatcherValue("LD_QS","ǰ������Ȩ��","������",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm23);

			FieldMatcher fm24 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf24 = createLayerField("LD_QS", "����Ȩ��", "�ַ���", "����Ȩ��", true, 255, 0);
			fm24.setMatcherValue(lf24, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm24);
			allFieldControls.add(fm24);

			FieldMatcher fm25 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf25 = createLayerField("Q_DI_LEI", "ǰ�ڵ���", "�ַ���", "����", true, 255, 0);
			fm25.setMatcherValue(lf25, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm25);
			allFieldControls.add(fm25);

			FieldMatcher fm26 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf26 = createLayerField("DI_LEI", "����", "�ַ���", "����", true, 255, 0);
			fm26.setMatcherValue(lf26, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm26);
			allFieldControls.add(fm26);

			FieldMatcher fm27 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf27 = createLayerField("Q_L_Z", "ǰ������", "�ַ���", "����", true, 255, 0);
			fm27.setMatcherValue(lf27, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm27);
			allFieldControls.add(fm27);

			// FieldMatcher fm28 = new FieldMatcher(dialogView.getContext());
			// fm28.setMatcherValue("Q_L_Z","ǰ������","�ַ���",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm28);

			FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf29 = createLayerField("LIN_ZHONG", "����", "�ַ���", "����", true, 255, 0);
			fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
			allFieldControls.add(fm29);

			FieldMatcher fm30 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf30 = createLayerField("QI_YUAN", "��Դ", "�ַ���", "��Դ", true, 255, 0);
			fm30.setMatcherValue(lf30, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm30);
			allFieldControls.add(fm30);

			FieldMatcher fm31 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf31 = createLayerField("Q_SEN_LB", "ǰ��ɭ�����", "�ַ���", "ɭ�֣��ֵأ����", true, 255, 0);
			fm31.setMatcherValue(lf31, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm31);
			allFieldControls.add(fm31);

			FieldMatcher fm32 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf32 = createLayerField("SEN_LIN_LB", "ɭ�����", "�ַ���", "ɭ�֣��ֵأ����", true, 255, 0);
			fm32.setMatcherValue(lf32, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm32);
			allFieldControls.add(fm32);

			FieldMatcher fm33 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf33 = createLayerField("Q_SQ_D", "ǰ����Ȩ�ȼ�", "�ַ���", "��Ȩ�ȼ�", true, 255, 0);
			fm33.setMatcherValue(lf33, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm33);
			allFieldControls.add(fm33);

			FieldMatcher fm34 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf34 = createLayerField("SHI_QUAN_D", "��Ȩ�ȼ�", "�ַ���", "��Ȩ�ȼ�", true, 255, 0);
			fm34.setMatcherValue(lf34, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm34);
			allFieldControls.add(fm34);

			// FieldMatcher fm35 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf2 =
			// createLayerField("SHI","����","�ַ���","",true,255,0);
			// fm35.setMatcherValue("SHI_QUAN_D","��Ȩ�ȼ�","�ַ���",
			// srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm35);
			// allFieldControls.add(fm35);

			FieldMatcher fm36 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf36 = createLayerField("GYL_BHDJ", "�����ֱ����ȼ�", "�ַ���", "���Ҽ������ֱ����ȼ�", true, 255, 0);
			fm36.setMatcherValue(lf36, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm36);
			allFieldControls.add(fm36);

			FieldMatcher fm37 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf37 = createLayerField("GJGYL_BHDJ", "���ҹ����ֱ����ȼ�", "�ַ���", "���Ҽ������ֱ����ȼ�", true, 255, 0);
			fm37.setMatcherValue(lf37, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm37);
			allFieldControls.add(fm37);

			FieldMatcher fm38 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf38 = createLayerField("Q_GC_LB", "ǰ�ڹ������", "�ַ���", "�������", true, 255, 0);
			fm38.setMatcherValue(lf38, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm38);
			allFieldControls.add(fm38);

			FieldMatcher fm39 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf39 = createLayerField("G_CHENG_LB", "�������", "�ַ���", "�������", true, 255, 0);
			fm39.setMatcherValue(lf39, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm39);
			allFieldControls.add(fm39);

			FieldMatcher fm40 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf40 = createLayerField("LING_ZU", "����", "�ַ���", "����", true, 255, 0);
			fm40.setMatcherValue(lf40, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm40);
			allFieldControls.add(fm40);

			FieldMatcher fm41 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf41 = createLayerField("YU_BI_DU", "���ܶ�/���Ƕ�", "������", "", true, 8, 0);
			fm41.setMatcherValue(lf41, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm41);
			allFieldControls.add(fm41);

			FieldMatcher fm42 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf42 = createLayerField("YOU_SHI_SZ", "��������", "�ַ���", "����", true, 255, 0);
			fm42.setMatcherValue(lf42, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm42);
			allFieldControls.add(fm42);

			FieldMatcher fm43 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf43 = createLayerField("PINGJUN_XJ", "ƽ���ؾ�", "������", "", true, 8, 0);
			fm43.setMatcherValue(lf43, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm43);
			allFieldControls.add(fm43);

			FieldMatcher fm44 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf44 = createLayerField("HUO_LMGQXJ", "ÿ�������(����ľ)", "������", "", true, 8, 0);
			fm44.setMatcherValue(lf44, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm44);
			allFieldControls.add(fm44);

			FieldMatcher fm45 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf45 = createLayerField("MEI_GQ_ZS", "ÿ��������", "����", "", true, 8, 0);
			fm45.setMatcherValue(lf45, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm45);
			allFieldControls.add(fm45);

			FieldMatcher fm46 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf46 = createLayerField("TD_TH_LX", "�����˻�����", "�ַ���", "�����˻�����", true, 255, 0);
			fm46.setMatcherValue(lf46, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm46);
			allFieldControls.add(fm46);

			FieldMatcher fm47 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf47 = createLayerField("DISPE", "�ֺ�����", "�ַ���", "�ֺ�����", true, 255, 0);
			fm47.setMatcherValue(lf47, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm47);
			allFieldControls.add(fm47);

			FieldMatcher fm48 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf48 = createLayerField("DISASTER_C", "�ֺ��ȼ�", "�ַ���", "ɭ���ֺ��ȼ�", true, 255, 0);
			fm48.setMatcherValue(lf48, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm48);
			allFieldControls.add(fm48);

			FieldMatcher fm49 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf49 = createLayerField("ZL_DJ", "�ֵ������ȼ�", "�ַ���", "�ֵ������ȼ�", true, 255, 0);
			fm49.setMatcherValue(lf49, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm49);
			allFieldControls.add(fm49);

			FieldMatcher fm50 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf50 = createLayerField("LD_CD", "�ִ�����", "������", "", true, 8, 0);
			fm50.setMatcherValue(lf50, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm50);
			allFieldControls.add(fm50);

			FieldMatcher fm51 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf51 = createLayerField("LD_KD", "�ִ����", "������", "", true, 255, 0);
			fm51.setMatcherValue(lf51, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm51);
			allFieldControls.add(fm51);

			FieldMatcher fm52 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf52 = createLayerField("BCLD", "�Ƿ�Ϊ�����ֵ�", "�ַ���", "�Ƿ�Ϊ�����ֵ�", true, 255, 0);
			fm52.setMatcherValue(lf52, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm52);
			allFieldControls.add(fm52);

			FieldMatcher fm53 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf53 = createLayerField("Q_BH_DJ", "ǰ���ֵر����ȼ�", "�ַ���", "�ֵر����ȼ�", true, 255, 0);
			fm53.setMatcherValue(lf53, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm53);
			allFieldControls.add(fm53);

			FieldMatcher fm54 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf54 = createLayerField("BH_DJ", "�ֵر����ȼ�", "�ַ���", "�ֵر����ȼ�", true, 255, 0);
			fm54.setMatcherValue(lf54, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm54);
			allFieldControls.add(fm54);

			FieldMatcher fm55 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf55 = createLayerField("LYFQ", "�ֵع��ܷ���", "�ַ���", "", true, 255, 0);
			fm55.setMatcherValue(lf55, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm55);
			allFieldControls.add(fm55);

			FieldMatcher fm56 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf56 = createLayerField("QYKZ", "���幦����", "�ַ���", "���幦����", true, 255, 0);
			fm56.setMatcherValue(lf56, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm56);
			allFieldControls.add(fm56);

			FieldMatcher fm57 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf57 = createLayerField("BHYY", "�仯ԭ��", "�ַ���", "�ֵر仯ԭ��", true, 255, 0);
			fm57.setMatcherValue(lf57, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm57);
			allFieldControls.add(fm57);

			FieldMatcher fm58 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf58 = createLayerField("BHND", "�仯���", "�ַ���", "", true, 255, 0);
			fm58.setMatcherValue(lf58, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm58);
			allFieldControls.add(fm58);

			FieldMatcher fm59 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf59 = createLayerField("GLLX", "�ֵع�������", "�ַ���", "�ֵع�������", true, 255, 0);
			fm59.setMatcherValue(lf59, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm59);
			allFieldControls.add(fm59);

			FieldMatcher fm60 = new FieldMatcher(dialogView.getContext());
			v1_LayerField lf60 = createLayerField("REMARKS", "˵��", "�ַ���", "", true, 255, 0);
			fm60.setMatcherValue(lf60, srcFieldsName, srcFields);
			((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm60);
			allFieldControls.add(fm60);

			// for xinjiang ����Ҫ�����������
			// FieldMatcher fm62 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf62 =
			// createLayerField("XIAOBAN_XJ","С�������","������","",true,8,0);
			// fm62.setMatcherValue(lf62, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm62);
			// allFieldControls.add(fm62);
			//
			// FieldMatcher fm73 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf73 =
			// createLayerField("SPS_ZS","����������","����","",true,255,0);
			// fm73.setMatcherValue(lf73, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm73);
			// allFieldControls.add(fm73);
			//
			// FieldMatcher fm63 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf63 =
			// createLayerField("SPS_XJ","�����������","������","",true,8,0);
			// fm63.setMatcherValue(lf63, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm63);
			// allFieldControls.add(fm63);
			//
			// FieldMatcher fm64 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf64 =
			// createLayerField("SH_TAI_QW","��̬��λ","�ַ���","",true,255,0);
			// fm64.setMatcherValue(lf64, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm64);
			// allFieldControls.add(fm64);
			//
			// FieldMatcher fm65 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf65 =
			// createLayerField("LM_SY","��ľȨ��","�ַ���","��ľȨ��",true,255,0);
			// fm65.setMatcherValue(lf65, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm65);
			// allFieldControls.add(fm65);
			//
			// FieldMatcher fm66 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf66 =
			// createLayerField("SSM_ZS","ɢ��ľ����","����","",true,255,0);
			// fm66.setMatcherValue(lf66, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm66);
			// allFieldControls.add(fm66);
			//
			// FieldMatcher fm67 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf67 =
			// createLayerField("SSM_XJ","ɢ��ľ���","������","",true,255,0);
			// fm67.setMatcherValue(lf67, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm67);
			// allFieldControls.add(fm67);
			//
			// FieldMatcher fm68 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf68 =
			// createLayerField("SSZ_ZS","ɢ��������","����","",true,255,0);
			// fm68.setMatcherValue(lf68, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm68);
			// allFieldControls.add(fm68);
			//
			// FieldMatcher fm69 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf69 =
			// createLayerField("DIAOCHA_FS","С��������鷽ʽ","�ַ���","С��������鷽ʽ",true,255,0);
			// fm69.setMatcherValue(lf69, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm69);
			// allFieldControls.add(fm69);
			//
			// FieldMatcher fm70 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf70 =
			// createLayerField("DIAOCHA_RQ","��������","������","",true,255,0);
			// fm70.setMatcherValue(lf70, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm70);
			// allFieldControls.add(fm70);
			//
			// FieldMatcher fm71 = new FieldMatcher(dialogView.getContext());
			// v1_LayerField lf71 =
			// createLayerField("DC_REN","������","�ַ���","",true,255,0);
			// fm71.setMatcherValue(lf71, srcFieldsName,srcFields);
			// ((LinearLayout)dialogView.findViewById(R.id.llFieldsList)).addView(fm71);
			// allFieldControls.add(fm71);
		}

		if (projectType.contains("�˸�����")) {
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

		addLayerFieldToView("Id", "˳���", "����", "", false, 8, 0);
		addLayerFieldToView("PAN_NO_TB", "�ж�ͼ�߱��", "����", "", false, 6, 0);
		addLayerFieldToView("SHENG", "ʡ", "�ַ���", "", false, 2, 0);
		addLayerFieldToView("XIAN", "��", "�ַ���", "", false, 40, 0);
		addLayerFieldToView("XIANG", "����", "�ַ���", "", false, 40, 0);
		addLayerFieldToView("CUN", "��", "�ַ���", "", false, 40, 0);
		addLayerFieldToView("LIN_YE_JU", "��ҵ��", "�ַ���", "", false, 40, 0);
		addLayerFieldToView("LIN_CHANG", "�ֳ�", "�ַ���", "", false, 40, 0);
		addLayerFieldToView("LIN_BAN", "�ְ�", "�ַ���", "", false, 40, 0);
		addLayerFieldToView("GPS_X", "������", "����", "", true, 8, 0);
		addLayerFieldToView("GPS_Y", "������", "����", "", true, 7, 0);
		addLayerFieldToView("MIAN_JI", "�ж����", "������", "", true, 18, 4);
		addLayerFieldToView("PAN_BHYY", "�ж��仯ԭ��", "����", "", false, 2, 0);
		addLayerFieldToView("PAN_DILEI", "�ж�����", "����", "", false, 5, 0);
		addLayerFieldToView("HSXBH", "��ʵϸ�ߺ�", "�ַ���", "", false, 12, 0);
		addLayerFieldToView("LDGLDW", "�ֵع���λ", "�ַ���", "", false, 40, 0);
		addLayerFieldToView("BHYY", "�仯ԭ��", "����", "", false, 3, 0);
		addLayerFieldToView("BEIZU", "�ж���ע", "�ַ���", "", false, 250, 0);

		addLayerFieldToView("SHIFOU_WF", "Υ��Υ��", "����", "", false, 1, 0);
		
		addLayerFieldToView("hs_qdl", "ǰ����", "�ַ���", "", false, 4, 0);
		addLayerFieldToView("hs_xzdl", "��״����", "�ַ���", "", false, 4, 0);

		addLayerFieldToView("XMMC", "��Ŀ����", "�ַ���", "", false, 100, 0);
		addLayerFieldToView("SH_WH", "����ĺ�", "�ַ���", "", false, 50, 0);
		addLayerFieldToView("SH_ND", "������", "�ַ���", "", false, 4, 0);
		addLayerFieldToView("SH_MJ", "������", "������", "", false, 10, 4);
		addLayerFieldToView("SJ_MJ", "ʵ�ʸı��ֵ���;���", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ", "Υ��Υ���ı��ֵ���;���", "������", "", false, 10, 4);
		addLayerFieldToView("WF_ZRBHD_MJ", "Υ��Υ������Ȼ���������", "������", "", false, 10, 4);
		addLayerFieldToView("WF_ZRBHD_MC", "��Ȼ����������", "�ַ���", "", false, 60, 0);
		addLayerFieldToView("WF_ZRBHD_JB", "��Ȼ�����ؼ���", "����", "", false, 2, 0);

		addLayerFieldToView("WF_MJ_QM", "Υ��Υ������ľ�ֵ����", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_ZL", "Υ��Υ�����������", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_HSL", "Υ��Υ���к��������", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_TM", "Υ��Υ���й����ع������", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_GM", "Υ��Υ����������ľ�����", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_QT", "Υ��Υ���������ֵ����", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_YJGYL", "Υ��Υ����һ�����ҹ��������", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_EJGYL", "Υ��Υ���ж������ҹ��������", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_DFGYL", "Υ��Υ���еط����������", "������", "", false, 10, 4);
		addLayerFieldToView("WF_MJ_SPL", "Υ��Υ������Ʒ�����", "������", "", false, 10, 4);
		addLayerFieldToView("SYLDXZ", "ʹ���ֵ�����", "����", "", false, 1, 0);
		addLayerFieldToView("CFZH", "��ľ�ɷ����֤��", "�ַ���", "", false, 100, 0);
		addLayerFieldToView("FZMJ", "��֤���", "������", "", false, 10, 4);
		addLayerFieldToView("FZXJ", "��֤���", "������", "", false, 10, 1);
		addLayerFieldToView("PZMJ", "ƾ֤�ɷ����", "������", "", false, 10, 4);
		addLayerFieldToView("PZXJ", "ƾ֤�ɷ����", "������", "", false, 10, 1);
		addLayerFieldToView("CZMJ", "��֤�ɷ����", "������", "", false, 10, 4);
		addLayerFieldToView("CZXJ", "��֤�ɷ����", "������", "", false, 10, 1);
		addLayerFieldToView("WZMJ", "��֤�ɷ����", "������", "", false, 10, 4);
		addLayerFieldToView("WZXJ", "��֤�ɷ����", "������", "", false, 10, 1);
		addLayerFieldToView("BEIZHU2", "��ע", "�ַ���", "", false, 250, 0);
		
		v1_LayerField lf29 = createLayerField("�Ƿ�Υ��Υ��", "�Ƿ�Υ��Υ��", "����", "", true, 1, 0);
		FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
		fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
		allFieldControls.add(fm29);
	}

	private void initDuChaYanZhengField() {
		allFieldControls.clear();
		v1_LayerField lf1 = createLayerField("ʡ", "ʡ", "�ַ���", "", true, 30, 0);
		FieldMatcher fm = new FieldMatcher(dialogView.getContext());
		fm.setMatcherValue(lf1, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm);
		allFieldControls.add(fm);

		v1_LayerField lf2 = createLayerField("��", "��", "�ַ���", "", true, 30, 0);
		FieldMatcher fm2 = new FieldMatcher(dialogView.getContext());
		fm2.setMatcherValue(lf2, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm2);
		allFieldControls.add(fm2);

		v1_LayerField lf3 = createLayerField("����", "����", "�ַ���", "", true, 30, 0);
		FieldMatcher fm3 = new FieldMatcher(dialogView.getContext());
		fm3.setMatcherValue(lf3, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm3);
		allFieldControls.add(fm3);

		v1_LayerField lf4 = createLayerField("��", "��", "�ַ���", "", true, 30, 0);
		FieldMatcher fm4 = new FieldMatcher(dialogView.getContext());
		fm4.setMatcherValue(lf4, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm4);
		allFieldControls.add(fm4);

		v1_LayerField lf26 = createLayerField("ͼ�ߺ�", "ͼ�ߺ�", "����", "", true, 5, 0);
		FieldMatcher fm26 = new FieldMatcher(dialogView.getContext());
		fm26.setMatcherValue(lf26, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm26);
		allFieldControls.add(fm26);

		v1_LayerField lf27 = createLayerField("������", "������", "������", "", true, 12, 3);
		FieldMatcher fm27 = new FieldMatcher(dialogView.getContext());
		fm27.setMatcherValue(lf27, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm27);
		allFieldControls.add(fm27);

		v1_LayerField lf28 = createLayerField("������", "������", "������", "", true, 12, 3);
		FieldMatcher fm28 = new FieldMatcher(dialogView.getContext());
		fm28.setMatcherValue(lf28, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm28);
		allFieldControls.add(fm28);

		v1_LayerField lf5 = createLayerField("�ж����", "�ж����", "������", "", true, 18, 4);
		FieldMatcher fm5 = new FieldMatcher(dialogView.getContext());
		fm5.setMatcherValue(lf5, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm5);
		allFieldControls.add(fm5);

		v1_LayerField lf6 = createLayerField("ǰ��ʱ��", "ǰ��ʱ��", "�ַ���", "", true, 8, 0);
		FieldMatcher fm6 = new FieldMatcher(dialogView.getContext());
		fm6.setMatcherValue(lf6, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm6);
		allFieldControls.add(fm6);

		v1_LayerField lf7 = createLayerField("����ʱ��", "����ʱ��", "�ַ���", "", true, 8, 0);
		FieldMatcher fm7 = new FieldMatcher(dialogView.getContext());
		fm7.setMatcherValue(lf7, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm7);
		allFieldControls.add(fm7);

		v1_LayerField lf8 = createLayerField("��ע", "��ע", "�ַ���", "", true, 255, 0);
		FieldMatcher fm8 = new FieldMatcher(dialogView.getContext());
		fm8.setMatcherValue(lf8, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm8);
		allFieldControls.add(fm8);

		v1_LayerField lf9 = createLayerField("ǰ����", "ǰ����", "����", "", true, 1, 0);
		FieldMatcher fm9 = new FieldMatcher(dialogView.getContext());
		fm9.setMatcherValue(lf9, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm9);
		allFieldControls.add(fm9);

		v1_LayerField lf10 = createLayerField("�ֵ���", "�ֵ���", "����", "", true, 1, 0);
		FieldMatcher fm10 = new FieldMatcher(dialogView.getContext());
		fm10.setMatcherValue(lf10, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm10);
		allFieldControls.add(fm10);

		v1_LayerField lf11 = createLayerField("�ص���̬��������", "�ص���̬��������", "�ַ���", "", true, 64, 0);
		FieldMatcher fm11 = new FieldMatcher(dialogView.getContext());
		fm11.setMatcherValue(lf11, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm11);
		allFieldControls.add(fm11);

		v1_LayerField lf12 = createLayerField("�ı����", "�ı����", "������", "", true, 18, 4);
		FieldMatcher fm12 = new FieldMatcher(dialogView.getContext());
		fm12.setMatcherValue(lf12, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm12);
		allFieldControls.add(fm12);

		v1_LayerField lf13 = createLayerField("Υ��ı����", "Υ��ı����", "������", "", true, 18, 4);
		FieldMatcher fm13 = new FieldMatcher(dialogView.getContext());
		fm13.setMatcherValue(lf13, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm13);
		allFieldControls.add(fm13);

		v1_LayerField lf14 = createLayerField("�ɷ����", "�ɷ����", "������", "", true, 12, 4);
		FieldMatcher fm14 = new FieldMatcher(dialogView.getContext());
		fm14.setMatcherValue(lf14, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm14);
		allFieldControls.add(fm14);

		v1_LayerField lf15 = createLayerField("Υ��ɷ����", "Υ��ɷ����", "������", "", true, 12, 4);
		FieldMatcher fm15 = new FieldMatcher(dialogView.getContext());
		fm15.setMatcherValue(lf15, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm15);
		allFieldControls.add(fm15);

		v1_LayerField lf16 = createLayerField("�仯ԭ��", "�仯ԭ��", "����", "", true, 2, 0);
		FieldMatcher fm16 = new FieldMatcher(dialogView.getContext());
		fm16.setMatcherValue(lf16, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm16);
		allFieldControls.add(fm16);

		v1_LayerField lf17 = createLayerField("��鼶��", "��鼶��", "����", "", true, 1, 0);
		FieldMatcher fm17 = new FieldMatcher(dialogView.getContext());
		fm17.setMatcherValue(lf17, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm17);
		allFieldControls.add(fm17);

		v1_LayerField lf18 = createLayerField("����Ƿ�һ��", "����Ƿ�һ��", "����", "", true, 1, 0);
		FieldMatcher fm18 = new FieldMatcher(dialogView.getContext());
		fm18.setMatcherValue(lf18, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm18);
		allFieldControls.add(fm18);

		v1_LayerField lf19 = createLayerField("��鱸ע", "��鱸ע", "�ַ���", "", true, 255, 0);
		FieldMatcher fm19 = new FieldMatcher(dialogView.getContext());
		fm19.setMatcherValue(lf19, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm19);
		allFieldControls.add(fm19);

		v1_LayerField lf20 = createLayerField("��鵥λ����", "��鵥λ����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm20 = new FieldMatcher(dialogView.getContext());
		fm20.setMatcherValue(lf20, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm20);
		allFieldControls.add(fm20);

		v1_LayerField lf21 = createLayerField("�����Ա", "�����Ա", "�ַ���", "", true, 255, 0);
		FieldMatcher fm21 = new FieldMatcher(dialogView.getContext());
		fm21.setMatcherValue(lf21, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm21);
		allFieldControls.add(fm21);

		v1_LayerField lf22 = createLayerField("�������", "�������", "�ַ���", "", true, 8, 0);
		FieldMatcher fm22 = new FieldMatcher(dialogView.getContext());
		fm22.setMatcherValue(lf22, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm22);
		allFieldControls.add(fm22);

		v1_LayerField lf23 = createLayerField("�������", "�������", "�ַ���", "", true, 4, 0);
		FieldMatcher fm23 = new FieldMatcher(dialogView.getContext());
		fm23.setMatcherValue(lf23, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm23);
		allFieldControls.add(fm23);

		v1_LayerField lf29 = createLayerField("�Ƿ�Υ��Υ��", "�Ƿ�Υ��Υ��", "����", "", true, 1, 0);
		FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
		fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
		allFieldControls.add(fm29);

	}

	private void initTuiGengField() {
		allFieldControls.clear();
		HashMap<String, String> srcField = new HashMap<String, String>();

		// srcField.put("����", "����");
		v1_LayerField lf1 = createLayerField("����", "����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm = new FieldMatcher(dialogView.getContext());
		fm.setMatcherValue(lf1, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm);
		allFieldControls.add(fm);

		// srcField.put("����", "����");
		v1_LayerField lf2 = createLayerField("����", "����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm2 = new FieldMatcher(dialogView.getContext());
		fm2.setMatcherValue(lf2, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm2);
		allFieldControls.add(fm2);

		srcField.put("����", "����");
		v1_LayerField lf3 = createLayerField("����", "����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm3 = new FieldMatcher(dialogView.getContext());
		fm3.setMatcherValue(lf3, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm3);
		allFieldControls.add(fm3);

		srcField.put("���ƴ�", "���ƴ�");
		v1_LayerField lf4 = createLayerField("���ƴ�", "���ƴ�", "�ַ���", "", true, 255, 0);
		FieldMatcher fm4 = new FieldMatcher(dialogView.getContext());
		fm4.setMatcherValue(lf4, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm4);
		allFieldControls.add(fm4);

		v1_LayerField lf5 = createLayerField("��Ȼ��", "��Ȼ��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm5 = new FieldMatcher(dialogView.getContext());
		fm5.setMatcherValue(lf5, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm5);
		allFieldControls.add(fm5);

		srcField.put("С����", "С����");
		v1_LayerField lf6 = createLayerField("С����", "С����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm6 = new FieldMatcher(dialogView.getContext());
		fm6.setMatcherValue(lf6, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm6);
		allFieldControls.add(fm6);

		srcField.put("С���", "С���");
		v1_LayerField lf7 = createLayerField("С���", "С���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm7 = new FieldMatcher(dialogView.getContext());
		fm7.setMatcherValue(lf7, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm7);
		allFieldControls.add(fm7);

		v1_LayerField lf8 = createLayerField("����", "����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm8 = new FieldMatcher(dialogView.getContext());
		fm8.setMatcherValue(lf8, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm8);
		allFieldControls.add(fm8);

		v1_LayerField lf9 = createLayerField("", "С�����", "������", "", true, 16, 2);
		FieldMatcher fm9 = new FieldMatcher(dialogView.getContext());
		fm9.setMatcherValue(lf9, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm9);
		allFieldControls.add(fm9);

		srcField.put("����ҵ���", "����ҵ���");
		v1_LayerField lf10 = createLayerField("����ҵ���", "����ҵ���", "������", "", true, 16, 2);
		FieldMatcher fm10 = new FieldMatcher(dialogView.getContext());
		fm10.setMatcherValue(lf10, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm10);
		allFieldControls.add(fm10);

		v1_LayerField lf11 = createLayerField("Ȩ��", "Ȩ��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm11 = new FieldMatcher(dialogView.getContext());
		fm11.setMatcherValue(lf11, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm11);
		allFieldControls.add(fm11);

		v1_LayerField lf12 = createLayerField("����", "����", "������", "", true, 8, 0);
		FieldMatcher fm12 = new FieldMatcher(dialogView.getContext());
		fm12.setMatcherValue(lf12, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm12);
		allFieldControls.add(fm12);

		v1_LayerField lf13 = createLayerField("��λ", "��λ", "�ַ���", "", true, 255, 0);
		FieldMatcher fm13 = new FieldMatcher(dialogView.getContext());
		fm13.setMatcherValue(lf13, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm13);
		allFieldControls.add(fm13);

		v1_LayerField lf14 = createLayerField("����", "����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm14 = new FieldMatcher(dialogView.getContext());
		fm14.setMatcherValue(lf14, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm14);
		allFieldControls.add(fm14);

		v1_LayerField lf15 = createLayerField("", "�¶�", "�ַ���", "", true, 255, 0);
		FieldMatcher fm15 = new FieldMatcher(dialogView.getContext());
		fm15.setMatcherValue(lf15, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm15);
		allFieldControls.add(fm15);

		v1_LayerField lf16 = createLayerField("��������", "��������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm16 = new FieldMatcher(dialogView.getContext());
		fm16.setMatcherValue(lf16, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm16);
		allFieldControls.add(fm16);

		v1_LayerField lf17 = createLayerField("������", "������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm17 = new FieldMatcher(dialogView.getContext());
		fm17.setMatcherValue(lf17, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm17);
		allFieldControls.add(fm17);

		v1_LayerField lf18 = createLayerField("��������", "��������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm18 = new FieldMatcher(dialogView.getContext());
		fm18.setMatcherValue(lf18, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm18);
		allFieldControls.add(fm18);

		v1_LayerField lf19 = createLayerField("��ʴ�̶�", "��ʴ�̶�", "�ַ���", "", true, 255, 0);
		FieldMatcher fm19 = new FieldMatcher(dialogView.getContext());
		fm19.setMatcherValue(lf19, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm19);
		allFieldControls.add(fm19);

		v1_LayerField lf20 = createLayerField("", "����PHֵ", "������", "", true, 8, 0);
		FieldMatcher fm20 = new FieldMatcher(dialogView.getContext());
		fm20.setMatcherValue(lf20, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm20);
		allFieldControls.add(fm20);

		v1_LayerField lf21 = createLayerField("", "ֲ������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm21 = new FieldMatcher(dialogView.getContext());
		fm21.setMatcherValue(lf21, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm21);
		allFieldControls.add(fm21);

		v1_LayerField lf22 = createLayerField("", "ֲ���Ƕ�", "������", "", true, 8, 0);
		FieldMatcher fm22 = new FieldMatcher(dialogView.getContext());
		fm22.setMatcherValue(lf22, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm22);
		allFieldControls.add(fm22);

		v1_LayerField lf23 = createLayerField("", "ֲ���߶�", "������", "", true, 8, 0);
		FieldMatcher fm23 = new FieldMatcher(dialogView.getContext());
		fm23.setMatcherValue(lf23, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm23);
		allFieldControls.add(fm23);

		v1_LayerField lf24 = createLayerField("�Ƿ���", "�Ƿ���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm24 = new FieldMatcher(dialogView.getContext());
		fm24.setMatcherValue(lf24, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm24);
		allFieldControls.add(fm24);

		v1_LayerField lf25 = createLayerField("���ԭ��", "���ԭ��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm25 = new FieldMatcher(dialogView.getContext());
		fm25.setMatcherValue(lf25, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm25);
		allFieldControls.add(fm25);

		v1_LayerField lf26 = createLayerField("ͼ����", "ͼ����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm26 = new FieldMatcher(dialogView.getContext());
		fm26.setMatcherValue(lf26, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm26);
		allFieldControls.add(fm26);

		v1_LayerField lf27 = createLayerField("������", "������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm27 = new FieldMatcher(dialogView.getContext());
		fm27.setMatcherValue(lf27, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm27);
		allFieldControls.add(fm27);

		v1_LayerField lf28 = createLayerField("������", "������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm28 = new FieldMatcher(dialogView.getContext());
		fm28.setMatcherValue(lf28, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm28);
		allFieldControls.add(fm28);

		v1_LayerField lf29 = createLayerField("������", "������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm29 = new FieldMatcher(dialogView.getContext());
		fm29.setMatcherValue(lf29, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm29);
		allFieldControls.add(fm29);

		v1_LayerField lf30 = createLayerField("ģʽ��", "ģʽ��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm30 = new FieldMatcher(dialogView.getContext());
		fm30.setMatcherValue(lf30, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm30);
		allFieldControls.add(fm30);

		v1_LayerField lf31 = createLayerField("���ַ�ʽ", "���ַ�ʽ", "�ַ���", "", true, 255, 0);
		FieldMatcher fm31 = new FieldMatcher(dialogView.getContext());
		fm31.setMatcherValue(lf31, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm31);
		allFieldControls.add(fm31);

		v1_LayerField lf32 = createLayerField("��������", "��������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm32 = new FieldMatcher(dialogView.getContext());
		fm32.setMatcherValue(lf32, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm32);
		allFieldControls.add(fm32);

		v1_LayerField lf33 = createLayerField("�������", "�������", "������", "", true, 12, 2);
		FieldMatcher fm33 = new FieldMatcher(dialogView.getContext());
		fm33.setMatcherValue(lf33, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm33);
		allFieldControls.add(fm33);

		v1_LayerField lf34 = createLayerField("��������", "��������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm34 = new FieldMatcher(dialogView.getContext());
		fm34.setMatcherValue(lf34, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm34);
		allFieldControls.add(fm34);

		v1_LayerField lf35 = createLayerField("��������", "��������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm35 = new FieldMatcher(dialogView.getContext());
		fm35.setMatcherValue(lf35, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm35);
		allFieldControls.add(fm35);

		v1_LayerField lf36 = createLayerField("���о�", "���о�", "�ַ���", "", true, 255, 0);
		FieldMatcher fm36 = new FieldMatcher(dialogView.getContext());
		fm36.setMatcherValue(lf36, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm36);
		allFieldControls.add(fm36);

		v1_LayerField lf37 = createLayerField("�콻��", "�콻��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm37 = new FieldMatcher(dialogView.getContext());
		fm37.setMatcherValue(lf37, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm37);
		allFieldControls.add(fm37);

		v1_LayerField lf38 = createLayerField("�������", "�������", "������", "", true, 8, 0);
		FieldMatcher fm38 = new FieldMatcher(dialogView.getContext());
		fm38.setMatcherValue(lf38, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm38);
		allFieldControls.add(fm38);

		v1_LayerField lf39 = createLayerField("����ʱ��", "����ʱ��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm39 = new FieldMatcher(dialogView.getContext());
		fm39.setMatcherValue(lf39, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm39);
		allFieldControls.add(fm39);

		v1_LayerField lf40 = createLayerField("������Ҫ��", "������Ҫ��", "������", "", true, 8, 0);
		FieldMatcher fm40 = new FieldMatcher(dialogView.getContext());
		fm40.setMatcherValue(lf40, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm40);
		allFieldControls.add(fm40);

		v1_LayerField lf41 = createLayerField("��ľ���", "��ľ���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm41 = new FieldMatcher(dialogView.getContext());
		fm41.setMatcherValue(lf41, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm41);
		allFieldControls.add(fm41);

		v1_LayerField lf42 = createLayerField("��ľ����", "��ľ����", "������", "", true, 8, 0);
		FieldMatcher fm42 = new FieldMatcher(dialogView.getContext());
		fm42.setMatcherValue(lf42, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm42);
		allFieldControls.add(fm42);

		v1_LayerField lf43 = createLayerField("���ط�ʽ", "���ط�ʽ", "�ַ���", "", true, 255, 0);
		FieldMatcher fm43 = new FieldMatcher(dialogView.getContext());
		fm43.setMatcherValue(lf43, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm43);
		allFieldControls.add(fm43);

		v1_LayerField lf44 = createLayerField("���ع��", "���ع��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm44 = new FieldMatcher(dialogView.getContext());
		fm44.setMatcherValue(lf44, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm44);
		allFieldControls.add(fm44);

		v1_LayerField lf45 = createLayerField("�ù����ϼ�", "�ù����ϼ�", "�ַ���", "", true, 255, 0);
		FieldMatcher fm45 = new FieldMatcher(dialogView.getContext());
		fm45.setMatcherValue(lf45, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm45);
		allFieldControls.add(fm45);

		v1_LayerField lf46 = createLayerField("�ù�������", "�ù�������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm46 = new FieldMatcher(dialogView.getContext());
		fm46.setMatcherValue(lf46, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm46);
		allFieldControls.add(fm46);

		v1_LayerField lf47 = createLayerField("�ù�������", "�ù�������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm47 = new FieldMatcher(dialogView.getContext());
		fm47.setMatcherValue(lf47, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm47);
		allFieldControls.add(fm47);

		v1_LayerField lf48 = createLayerField("�ù�����ֲ", "�ù�����ֲ", "�ַ���", "", true, 255, 0);
		FieldMatcher fm48 = new FieldMatcher(dialogView.getContext());
		fm48.setMatcherValue(lf48, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm48);
		allFieldControls.add(fm48);

		v1_LayerField lf49 = createLayerField("�ù�������", "�ù�������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm49 = new FieldMatcher(dialogView.getContext());
		fm49.setMatcherValue(lf49, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm49);
		allFieldControls.add(fm49);

		v1_LayerField lf50 = createLayerField("���յ���", "���յ���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm50 = new FieldMatcher(dialogView.getContext());
		fm50.setMatcherValue(lf50, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm50);
		allFieldControls.add(fm50);

		v1_LayerField lf51 = createLayerField("_����", "_����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm51 = new FieldMatcher(dialogView.getContext());
		fm51.setMatcherValue(lf51, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm51);
		allFieldControls.add(fm51);

		v1_LayerField lf52 = createLayerField("_����", "_����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm52 = new FieldMatcher(dialogView.getContext());
		fm52.setMatcherValue(lf52, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm52);
		allFieldControls.add(fm52);

		v1_LayerField lf53 = createLayerField("_���", "_���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm53 = new FieldMatcher(dialogView.getContext());
		fm53.setMatcherValue(lf53, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm53);
		allFieldControls.add(fm53);

		v1_LayerField lf54 = createLayerField("_����", "_����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm54 = new FieldMatcher(dialogView.getContext());
		fm54.setMatcherValue(lf54, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm54);
		allFieldControls.add(fm54);

		v1_LayerField lf55 = createLayerField("_����", "_����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm55 = new FieldMatcher(dialogView.getContext());
		fm55.setMatcherValue(lf55, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm55);
		allFieldControls.add(fm55);

		v1_LayerField lf56 = createLayerField("_����", "_����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm56 = new FieldMatcher(dialogView.getContext());
		fm56.setMatcherValue(lf56, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm56);
		allFieldControls.add(fm56);

		v1_LayerField lf57 = createLayerField("_��ľ", "_��ľ", "�ַ���", "", true, 255, 0);
		FieldMatcher fm57 = new FieldMatcher(dialogView.getContext());
		fm57.setMatcherValue(lf57, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm57);
		allFieldControls.add(fm57);

		v1_LayerField lf58 = createLayerField("Ͷ��Ԥ��", "Ͷ��Ԥ��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm58 = new FieldMatcher(dialogView.getContext());
		fm58.setMatcherValue(lf58, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm58);
		allFieldControls.add(fm58);

		v1_LayerField lf59 = createLayerField("��ע", "��ע", "�ַ���", "", true, 255, 0);
		FieldMatcher fm59 = new FieldMatcher(dialogView.getContext());
		fm59.setMatcherValue(lf59, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm59);
		allFieldControls.add(fm59);

		v1_LayerField lf60 = createLayerField("���д���", "���д���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm60 = new FieldMatcher(dialogView.getContext());
		fm60.setMatcherValue(lf60, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm60);
		allFieldControls.add(fm60);

		v1_LayerField lf61 = createLayerField("���ش���", "���ش���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm61 = new FieldMatcher(dialogView.getContext());
		fm61.setMatcherValue(lf61, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm61);
		allFieldControls.add(fm61);

		v1_LayerField lf62 = createLayerField("�������", "�������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm62 = new FieldMatcher(dialogView.getContext());
		fm62.setMatcherValue(lf62, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm62);
		allFieldControls.add(fm62);

		v1_LayerField lf63 = createLayerField("�����", "�����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm63 = new FieldMatcher(dialogView.getContext());
		fm63.setMatcherValue(lf63, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm63);
		allFieldControls.add(fm63);

		v1_LayerField lf64 = createLayerField("D_����", "D_����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm64 = new FieldMatcher(dialogView.getContext());
		fm64.setMatcherValue(lf64, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm64);
		allFieldControls.add(fm64);

		v1_LayerField lf65 = createLayerField("D_�������", "D_�������", "�ַ���", "", true, 255, 0);
		FieldMatcher fm65 = new FieldMatcher(dialogView.getContext());
		fm65.setMatcherValue(lf65, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm65);
		allFieldControls.add(fm65);

		v1_LayerField lf66 = createLayerField("D_���ֽ��", "D_���ֽ��", "�ַ���", "", true, 255, 0);
		FieldMatcher fm66 = new FieldMatcher(dialogView.getContext());
		fm66.setMatcherValue(lf66, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm66);
		allFieldControls.add(fm66);

		v1_LayerField lf67 = createLayerField("D_���߲���", "D_���߲���", "�ַ���", "", true, 255, 0);
		FieldMatcher fm67 = new FieldMatcher(dialogView.getContext());
		fm67.setMatcherValue(lf67, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm67);
		allFieldControls.add(fm67);

		v1_LayerField lf68 = createLayerField("D_������׼", "D_������׼", "�ַ���", "", true, 255, 0);
		FieldMatcher fm68 = new FieldMatcher(dialogView.getContext());
		fm68.setMatcherValue(lf68, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm68);
		allFieldControls.add(fm68);

		v1_LayerField lf69 = createLayerField("D_�����", "D_�����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm69 = new FieldMatcher(dialogView.getContext());
		fm69.setMatcherValue(lf69, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm69);
		allFieldControls.add(fm69);

		v1_LayerField lf70 = createLayerField("D_�Ƿ����", "D_�Ƿ����", "�ַ���", "", true, 255, 0);
		FieldMatcher fm70 = new FieldMatcher(dialogView.getContext());
		fm70.setMatcherValue(lf70, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm70);
		allFieldControls.add(fm70);

		v1_LayerField lf71 = createLayerField("D_��ע", "D_��ע", "�ַ���", "", true, 255, 0);
		FieldMatcher fm71 = new FieldMatcher(dialogView.getContext());
		fm71.setMatcherValue(lf71, srcFieldsName, srcFields);
		((LinearLayout) dialogView.findViewById(R.id.llFieldsList)).addView(fm71);
		allFieldControls.add(fm71);

		v1_LayerField lf72 = createLayerField("_�Ƿ�ƶ����", "_�Ƿ�ƶ����", "�ַ���", "", true, 255, 0);
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
		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {

			}
		});
		dialogView.show();
	}
}
