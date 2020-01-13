package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.Controls.YearPicker;
import com.dingtu.DTGIS.DataService.DictXZQH;
import com.dingtu.senlinducha.R;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkRenderType;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class LayerManagerCreateDataLayer {
	private v1_FormTemplate _Dialog = null;
	private String msg = "";
	// ��ǰ���� ����
	private String _ProjectName = "";

	public LayerManagerCreateDataLayer() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.d_datalayer_new);
		// _Dialog.ReSetSize(1f, 0.95f);
		_Dialog.ReSetSize(0.6f, 0.9f);
		// ��������
		this._ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;

		// ��ʼ��ʾ����ť
		_Dialog.SetCaption("��" + this._ProjectName + "��" + Tools.ToLocale("ͼ��"));
		_Dialog.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("ȷ��") + "  ,ȷ��", pCallback);

		// �����ֶΣ��ֶ����ԣ�ɾ���ֶΰ�ť
		_Dialog.findViewById(R.id.pln_add).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_edit).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_delete).setOnClickListener(new ViewClick());

		// ������֧��
		int[] viewid = new int[] { R.id.pln_add, R.id.pln_edit, R.id.pln_delete, R.id.tvLocaleText1, R.id.tvLocaleText2,
				R.id.tvLocaleText3, R.id.tvLocaleText4 };
		for (int id : viewid) {
			Tools.ToLocale(_Dialog.findViewById(id));
		}

		try {
			ApplicationInfo appInfo = PubVar.m_DoEvent.m_Context.getPackageManager()
					.getApplicationInfo(PubVar.m_DoEvent.m_Context.getPackageName(), PackageManager.GET_META_DATA);
			msg = appInfo.metaData.getString("version");
		} catch (Exception ex) {

		}

		List<String> listProjecTypes = new ArrayList<String>();

		if (msg.equals("TGHL")) {
			initTuiGengEnum();
			listProjecTypes = Tools.StrArrayToList(new String[] { ForestryLayerType.DefaultLayer,
					ForestryLayerType.TuigengLayer, ForestryLayerType.LindibiangengLayer, ForestryLayerType.LinyeErdiao,
					ForestryLayerType.XiaoBanXuji, ForestryLayerType.TianbaozaolinLayer,
					ForestryLayerType.TianbaoFuYuLayer, ForestryLayerType.TianbaoFengYuLayer,
					ForestryLayerType.TanhuiLayer });

		} else {
			initTuiGengEnum();
			listProjecTypes = Tools.StrArrayToList(new String[] { ForestryLayerType.DefaultLayer,
					ForestryLayerType.DuChaYanZheng, ForestryLayerType.TuigengLayer,
					ForestryLayerType.WeipianJianchaLayer, ForestryLayerType.WeipianShujuLayer,
					ForestryLayerType.LindibiangengLayer, ForestryLayerType.LinyeErdiao, ForestryLayerType.XiaoBanXuji,
					ForestryLayerType.TianbaozaolinLayer, ForestryLayerType.TianbaoFuYuLayer,
					ForestryLayerType.TianbaoFengYuLayer, ForestryLayerType.TanhuiLayer });
		}

		v1_DataBind.SetBindListSpinner(_Dialog, "��������", listProjecTypes, R.id.sp_projecttype);

		// ��ͼ�������б�
		v1_DataBind.SetBindListSpinner(_Dialog, "ͼ������", Tools.StrArrayToList(new String[] { "��", "��", "��" }),
				R.id.sp_type);

		Spinner splayertype = (Spinner) _Dialog.findViewById(R.id.sp_projecttype);
		splayertype.setOnItemSelectedListener(new ProjectTypeOnItemSelectedListener());

		Spinner splayertype2 = (Spinner) _Dialog.findViewById(R.id.sp_tgqx);
		splayertype2.setOnItemSelectedListener(new ProjectTypeOnItemSelectedListener2());

		this.SetButtonEnable(false);
	}

	class ProjectTypeOnItemSelectedListener2 implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
			// Spinner splayertype2 =
			// (Spinner)_Dialog.findViewById(R.id.sp_tgqx);
			//
			// PubVar.xian = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgqx);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	// Ĭ��ͼ������
	private int preSelectedPosition = 0;

	class ProjectTypeOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {

			_Dialog.findViewById(R.id.ll_weipiandataselect).setVisibility(View.GONE);

			preSelectedPosition = position;

			String currentProjectType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype);

			_Dialog.findViewById(R.id.ll_field).setVisibility(View.VISIBLE);
			_Dialog.findViewById(R.id.ll_tuigeng).setVisibility(View.VISIBLE);

			m_EditLayer.GetFieldList().clear();

			if (currentProjectType.equals(ForestryLayerType.DefaultLayer))// �Զ���ͼ��
			{
				addDefaultField();
			}

			if (currentProjectType.equals(ForestryLayerType.DuChaYanZheng))// �Զ���ͼ��
			{
				addDuChaYanZhengFields();
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "��");
			}

			if (currentProjectType.equals(ForestryLayerType.TianbaozaolinLayer)) {
				addTianBaoZaoLinFields();
			}

			if (currentProjectType.equals(ForestryLayerType.TianbaoFengYuLayer)) {
				addTianBaoFengyuFields();
			}

			if (currentProjectType.equals(ForestryLayerType.TianbaoFuYuLayer)) {
				addTianBaoFuYuFields();
			}

			if (currentProjectType.equals(ForestryLayerType.LinyeErdiao)) {
				addErDiaoField();
				initTuiGengEnum();
				_Dialog.findViewById(R.id.ll_tuigeng).setVisibility(View.VISIBLE);
				((TextView) _Dialog.findViewById(R.id.tvLocaleText5)).setText("ͼ����Ϣ");

			}

			if (currentProjectType.equals(ForestryLayerType.TanhuiLayer))// ̼��-ÿľ���
			{
				AddMeiMuJianChiFields();
			}

			if (currentProjectType.equals(ForestryLayerType.XiaoBanXuji)) {
				AddXiaoBanXujiFields();
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "��");
			}

			if (currentProjectType.equals(ForestryLayerType.LindibiangengLayer))// �ֵر��
			{
				addLinDiBianGengField();
			}

			if (currentProjectType.equals(ForestryLayerType.WeipianJianchaLayer)) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "��");
				addZhiFaJianchaField();
				setWeipianxiafaLayer();
				_Dialog.findViewById(R.id.ll_weipiandataselect).setVisibility(View.VISIBLE);
			}

			// ��Ƭִ��-�����·�
			if (currentProjectType.equals(ForestryLayerType.WeipianShujuLayer)) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "��");
				addZhiFaShujuFields();
			}

			if (currentProjectType.equals(ForestryLayerType.TuigengLayer)) {
				addTuiGengField();
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "��");
				initTuiGengEnum();
				_Dialog.findViewById(R.id.ll_tuigeng).setVisibility(View.VISIBLE);

			}
			LoadLayerInfo();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	ArrayList<String> listWPSJXFLayer = new ArrayList<String>();
	ArrayList<String> listWPSJLayerIDs = new ArrayList<String>();

	private void setWeipianxiafaLayer() {
		listWPSJXFLayer.clear();
		listWPSJLayerIDs.clear();

		List<v1_Layer> dataGridList = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList();
		for (v1_Layer layer : dataGridList) {
			if (layer.GetLayerProjecType().equals(ForestryLayerType.WeipianShujuLayer)) {
				listWPSJXFLayer.add(layer.GetLayerAliasName());
				listWPSJLayerIDs.add(layer.GetLayerID());
			}
		}

		ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(_Dialog.getContext(),
				android.R.layout.simple_spinner_item, listWPSJXFLayer.toArray());
		nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spWPXFLayer = ((Spinner) _Dialog.findViewById(R.id.sp_wpsjxf));
		spWPXFLayer.setAdapter(nfAdapter);

	}

	private void addDuChaYanZhengFields() {
//		m_EditLayer.GetFieldList().add(createLayerField("ʡ", "�ַ���", "F2", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F3", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F4", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F5", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("ͼ�ߺ�", "����", "F6", "", false, 5, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("������", "������", "F7", "", false, 12, 3, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("������", "������", "F8", "", false, 12, 3, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�ж����", "������", "F9", "", false, 18, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("ǰ��ʱ��", "�ַ���", "F10", "", false, 8, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("����ʱ��", "�ַ���", "F11", "", false, 8, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("��ע", "�ַ���", "F12", "", false, 255, 0, ""));
//
//		m_EditLayer.GetFieldList().add(createLayerField("ǰ����", "����", "F13", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�ֵ���", "����", "F14", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�ص���̬��������", "�ַ���", "F15", "", false, 64, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�ı����", "������", "F16", "", false, 18, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("Υ��ı����", "������", "F17", "", false, 18, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�ɷ����", "������", "F18", "", false, 12, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("Υ��ɷ����", "������", "F19", "", false, 12, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�仯ԭ��", "����", "F20", "", false, 2, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("��鼶��", "����", "F21", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("����Ƿ�һ��", "����", "F22", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("��鱸ע", "�ַ���", "F23", "", false, 255, 0, ""));
//
//		m_EditLayer.GetFieldList().add(createLayerField("��鵥λ����", "�ַ���", "F24", "", false, 64, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�����Ա", "�ַ���", "F25", "", false, 64, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�������", "�ַ���", "F26", "", false, 8, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�������", "�ַ���", "F27", "", false, 4, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�Υ��Υ��", "����", "F28", "", false, 1, 0, ""));
		
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();
		newFields.add(createLayerField("˳���", "����", "F1", "", false, 8, 0, "Id"));
		newFields.add(createLayerField("�ж�ͼ�߱��", "����", "F2", "", false, 6, 0, "PAN_NO_TB"));
		newFields.add(createLayerField("ʡ", "�ַ���", "F3", "", false, 2, 0, "SHENG"));
		newFields.add(createLayerField("��", "�ַ���", "F4", "", false, 40, 0, "XIAN"));
		newFields.add(createLayerField("����", "�ַ���", "F5", "", false, 40, 0, "XIANG"));
		newFields.add(createLayerField("��", "�ַ���", "F6", "", false, 40, 0, "CUN"));
		newFields.add(createLayerField("��ҵ��", "�ַ���", "F7", "", false, 40, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("�ֳ�", "�ַ���", "F8", "", false, 40, 0, "LIN_CHANG"));
		newFields.add(createLayerField("�ְ�", "�ַ���", "F9", "", false, 40, 0, "LIN_BAN"));
		newFields.add(createLayerField("������", "����", "F10", "", true, 8, 0, "GPS_X"));
		newFields.add(createLayerField("������", "����", "F11", "", true, 7, 0, "GPS_Y"));
		newFields.add(createLayerField("�ж����", "������", "F12", "", true, 18, 4, "MIAN_JI"));
		newFields.add(createLayerField("�ж��仯ԭ��", "����", "F13", "", false, 2, 0, "PAN_BHYY"));
		newFields.add(createLayerField("�ж�����", "����", "F14", "", false, 5, 0, "PAN_DILEI"));
		newFields.add(createLayerField("��ʵϸ�ߺ�", "�ַ���", "F15", "", false, 12, 0, "HSXBH"));
		newFields.add(createLayerField("�ֵع���λ", "�ַ���", "F16", "", false, 40, 0, "LDGLDW"));
		newFields.add(createLayerField("�仯ԭ��", "����", "F17", "", false, 3, 0, "BHYY"));
		newFields.add(createLayerField("�ж���ע", "�ַ���", "F18", "", false, 250, 0, "BEIZHU"));
		newFields.add(createLayerField("Υ��Υ��", "������", "F51", "", false, 1, 0, "SHIFOU_WF"));

		newFields.add(createLayerField("ǰ����", "�ַ���", "F19", "", false, 4, 0, "hs_qdl"));
		newFields.add(createLayerField("��״����", "�ַ���", "F20", "", false, 4, 0, "hs_xzdl"));

		newFields.add(createLayerField("��Ŀ����", "�ַ���", "F21", "", false, 100, 0, "XMMC"));
		newFields.add(createLayerField("����ĺ�", "�ַ���", "F22", "", false, 50, 0, "SH_WH"));
		newFields.add(createLayerField("������", "�ַ���", "F23", "", false, 4, 0, "SH_ND"));
		newFields.add(createLayerField("������", "������", "F24", "", false, 10, 4, "SH_MJ"));
		newFields.add(createLayerField("ʵ�ʸı��ֵ���;���", "������", "F25", "", false, 10, 4, "SJ_MJ"));
		newFields.add(createLayerField("Υ��Υ���ı��ֵ���;���", "������", "F26", "", false, 10, 4, "WF_MJ"));
		newFields.add(createLayerField("Υ��Υ������Ȼ���������", "������", "F27", "", false, 10, 4, "WF_ZRBHD_MJ"));
		newFields.add(createLayerField("��Ȼ����������", "�ַ���", "F28", "", false, 60, 0, "WF_ZRBHD_MC"));
		newFields.add(createLayerField("��Ȼ�����ؼ���", "����", "F29", "", false, 2, 0, "WF_ZRBHD_JB"));

		newFields.add(createLayerField("Υ��Υ������ľ�ֵ����", "������", "F30", "", false, 10, 4, "WF_MJ_QM"));
		newFields.add(createLayerField("Υ��Υ�����������", "������", "F31", "", false, 10, 4, "WF_MJ_ZL"));
		newFields.add(createLayerField("Υ��Υ���к��������", "������", "F32", "", false, 10, 4, "WF_MJ_HSL"));
		newFields.add(createLayerField("Υ��Υ���й����ع������", "������", "F33", "", false, 10, 4, "WF_MJ_TM"));
		newFields.add(createLayerField("Υ��Υ����������ľ�����", "������", "F34", "", false, 10, 4, "WF_MJ_GM"));
		newFields.add(createLayerField("Υ��Υ���������ֵ����", "������", "F35", "", false, 10, 4, "WF_MJ_QT"));
		newFields.add(createLayerField("Υ��Υ����һ�����ҹ��������", "������", "F36", "", false, 10, 4, "WF_MJ_YJGYL"));
		newFields.add(createLayerField("Υ��Υ���ж������ҹ��������", "������", "F37", "", false, 10, 4, "WF_MJ_EJGYL"));
		newFields.add(createLayerField("Υ��Υ���еط����������", "������", "F38", "", false, 10, 4, "WF_MJ_DFGYL"));
		newFields.add(createLayerField("Υ��Υ������Ʒ�����", "������", "F39", "", false, 10, 4, "WF_MJ_SPL"));
		newFields.add(createLayerField("ʹ���ֵ�����", "����", "F40", "", false, 1, 0, "SYLDXZ"));
		newFields.add(createLayerField("��ľ�ɷ����֤��", "�ַ���", "F41", "", false, 100, 0, "CFZH"));
		newFields.add(createLayerField("��֤���", "������", "F42", "", false, 10, 4, "FZMJ"));
		newFields.add(createLayerField("��֤���", "������", "F43", "", false, 10, 1, "FZXJ"));
		newFields.add(createLayerField("ƾ֤�ɷ����", "������", "F44", "", false, 10, 4, "PZMJ"));
		newFields.add(createLayerField("ƾ֤�ɷ����", "������", "F45", "", false, 10, 1, "PZXJ"));
		newFields.add(createLayerField("��֤�ɷ����", "������", "F46", "", false, 10, 4, "CZMJ"));
		newFields.add(createLayerField("��֤�ɷ����", "������", "F47", "", false, 10, 1, "CZXJ"));
		newFields.add(createLayerField("��֤�ɷ����", "������", "F48", "", false, 10, 4, "WZMJ"));
		newFields.add(createLayerField("��֤�ɷ����", "������", "F49", "", false, 10, 1, "WZXJ"));
		newFields.add(createLayerField("��ע", "�ַ���", "F50", "", false, 250, 0, "BEIZHU2"));

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}

	}

	private void addTianBaoFengyuFields() {
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ش���", "�ַ���", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ְ�", "����", "F5", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��", "����", "F6", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����С��", "����", "F7", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��λ��", "�ַ���", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ����", "�ַ���", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С�����(Ķ)", "������", "F10", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С�����(����)", "������", "F11", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ����Ȩ", "�ַ���", "F12", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��Դ", "�ַ���", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ����Ȩ", "�ַ���", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "������", "F17", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�¶�", "�ַ���", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F19", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������Ƶ��", "�ַ���", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ������", "�ַ���", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����״��", "�ַ���", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����״��", "�ַ���", "F23", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F24", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������", "�ַ���", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����̼����", "�ַ���", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����PHֵ", "�ַ���", "F27", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��ľ��������", "�ַ���", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�Ƕ�", "�ַ���", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�߶�", "������", "F30", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�ֲ�", "�ַ���", "F31", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("�ݱ���������", "�ַ���", "F32", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��Ƕ�", "�ַ���", "F33", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��߶�", "������", "F34", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��ֲ�", "�ַ���", "F35", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ϡ����", "�ַ���", "F36", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F37", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ù���", "������", "F38", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ", "�ַ���", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ʲ��Ϸ�", "������", "F40", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С����Ͷ��", "������", "F41", "", false, 15, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F43", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F44", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ч����", "�ַ���", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ч���", "�ַ���", "F46", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Ŀ������", "�ַ���", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F48", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ʵ�Ч", "�ַ���", "F49", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("����", "������", "F50", "", false, 8, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ն�", "������", "F51", "", false, 8, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ���ؾ�", "������", "F52", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ������", "������", "F53", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "����", "F54", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���", "������", "F55", "", false, 12, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�ݱ��Ƕ�", "������", "F56", "", false, 12, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F57", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���������", "����", "F58", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F59", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������", "����", "F60", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ľ����", "����", "F61", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����Ŀ����", "����", "F62", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ɷ�����", "����", "F63", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ɷ�ǿ��", "����", "F64", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�䷥���", "����", "F65", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��֦����", "����", "F66", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ǿ��", "����", "F67", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("����˹���", "����", "F68", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ƹ�����", "����", "F69", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͱ���", "����", "F70", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ʋ�����", "����", "F71", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������Ʒ�", "����", "F72", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������߷�", "����", "F73", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F74", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ӷ�", "����", "F75", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ��", "����", "F76", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������߷�", "����", "F77", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F78", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͨ�ڷ�", "����", "F79", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͨ�ڷ�", "����", "F80", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ���ӷ�", "����", "F81", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ��ľ��", "����", "F82", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ���߷�", "����", "F83", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ�˹���", "����", "F84", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲͨ�ڷ�", "����", "F85", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F86", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ܻ���", "����", "F87", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����", "����", "F88", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ֱ�ӷ�", "����", "F89", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ��", "����", "F90", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ��", "����", "F91", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Ӫ�ֵ�·", "����", "F92", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƽ�֧��", "����", "F93", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���溦����", "����", "F94", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����ɱ�", "����", "F95", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("���ʱ��", "�ַ���", "F96", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����", "�ַ���", "F97", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ݱ�ʶ", "�ַ���", "F98", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Ψһֵ", "�ַ���", "F99", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע1", "�ַ���", "F100", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע2", "�ַ���", "F101", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע3", "�ַ���", "F102", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע4", "�ַ���", "F103", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע5", "�ַ���", "F104", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ������", "�ַ���", "F105", "", false, 4, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ�γ���", "������", "F106", "", false, 15, 4, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ�����", "������", "F107", "", false, 15, 4, ""));
	}

	private void addTianBaoFuYuFields() {
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ش���", "�ַ���", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ְ�", "����", "F5", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��", "����", "F6", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����С��", "����", "F7", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��λ��", "�ַ���", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ����", "�ַ���", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С�����(Ķ)", "������", "F10", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С�����(����)", "������", "F11", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ����Ȩ", "�ַ���", "F12", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��Դ", "�ַ���", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ����Ȩ", "�ַ���", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "������", "F17", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�¶�", "�ַ���", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F19", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������Ƶ��", "�ַ���", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ������", "�ַ���", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����״��", "�ַ���", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����״��", "�ַ���", "F23", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F24", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������", "�ַ���", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����̼����", "�ַ���", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����PHֵ", "�ַ���", "F27", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��ľ��������", "�ַ���", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�Ƕ�", "�ַ���", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�߶�", "������", "F30", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�ֲ�", "�ַ���", "F31", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("�ݱ���������", "�ַ���", "F32", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��Ƕ�", "�ַ���", "F33", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��߶�", "������", "F34", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��ֲ�", "�ַ���", "F35", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ϡ����", "�ַ���", "F36", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F37", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ù���", "������", "F38", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ", "�ַ���", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ʲ��Ϸ�", "������", "F40", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С����Ͷ��", "������", "F41", "", false, 15, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F43", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F44", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ч����", "�ַ���", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ч���", "�ַ���", "F46", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Ŀ������", "�ַ���", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F48", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ʵ�Ч", "�ַ���", "F49", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("����", "������", "F50", "", false, 8, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ն�", "������", "F51", "", false, 8, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ���ؾ�", "������", "F52", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ������", "������", "F53", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "����", "F54", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���", "������", "F55", "", false, 12, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�ݱ��Ƕ�", "������", "F56", "", false, 12, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F57", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���������", "����", "F58", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F59", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������", "����", "F60", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ľ����", "����", "F61", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����Ŀ����", "����", "F62", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ɷ�����", "����", "F63", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ɷ�ǿ��", "����", "F64", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�䷥���", "����", "F65", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��֦����", "����", "F66", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ǿ��", "����", "F67", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("����˹���", "����", "F68", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ƹ�����", "����", "F69", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͱ���", "����", "F70", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ʋ�����", "����", "F71", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������Ʒ�", "����", "F72", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������߷�", "����", "F73", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F74", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ӷ�", "����", "F75", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ��", "����", "F76", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������߷�", "����", "F77", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F78", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͨ�ڷ�", "����", "F79", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͨ�ڷ�", "����", "F80", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ���ӷ�", "����", "F81", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ��ľ��", "����", "F82", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ���߷�", "����", "F83", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ�˹���", "����", "F84", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲͨ�ڷ�", "����", "F85", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F86", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ܻ���", "����", "F87", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����", "����", "F88", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ֱ�ӷ�", "����", "F89", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ��", "����", "F90", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ��", "����", "F91", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Ӫ�ֵ�·", "����", "F92", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƽ�֧��", "����", "F93", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���溦����", "����", "F94", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����ɱ�", "����", "F95", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("���ʱ��", "�ַ���", "F96", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����", "�ַ���", "F97", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ݱ�ʶ", "�ַ���", "F98", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Ψһֵ", "�ַ���", "F99", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע1", "�ַ���", "F100", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע2", "�ַ���", "F101", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע3", "�ַ���", "F102", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע4", "�ַ���", "F103", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע5", "�ַ���", "F104", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ������", "�ַ���", "F105", "", false, 4, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ�γ���", "������", "F106", "", false, 15, 4, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ�����", "������", "F107", "", false, 15, 4, ""));
	}

	private void addTianBaoZaoLinFields() {
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ش���", "�ַ���", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ְ�", "����", "F5", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��", "����", "F6", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����С��", "����", "F7", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��λ��", "�ַ���", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ����", "�ַ���", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С�����(Ķ)", "������", "F10", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С�����(����)", "������", "F11", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ����Ȩ", "�ַ���", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "������", "F14", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�¶�", "�ַ���", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������", "�ַ���", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����̼����", "�ַ���", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����PHֵ", "�ַ���", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ܸǶ�", "�ַ���", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ����", "�ַ���", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ���ն�", "�ַ���", "F23", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ��������", "�ַ���", "F24", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ������������", "�ַ���", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ��������", "�ַ���", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�Ƕ�", "�ַ���", "F27", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ���������", "�ַ���", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��Ƕ�", "�ַ���", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F30", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F31", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F32", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F33", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�콻����", "�ַ���", "F34", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�콻����", "�ַ���", "F35", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�콻��ʽ", "�ַ���", "F36", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���о�", "�ַ���", "F37", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ʱ��", "�ַ���", "F38", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ�ܶ�", "�ַ���", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ʱ��", "�ַ���", "F40", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ط�ʽ", "�ַ���", "F41", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲʱ��", "�ַ���", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ��ʽ", "�ַ���", "F43", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ��ʽ", "�ַ���", "F44", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��������(����ֲ)", "�ַ���", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ(����)�ȼ�", "�ַ���", "F46", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ʱ��", "�ַ���", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����", "�ַ���", "F48", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����˹���", "����", "F49", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ƹ�����", "����", "F50", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͱ���", "����", "F51", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Ʋ�����", "����", "F52", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������Ʒ�", "����", "F53", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������߷�", "����", "F54", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F55", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ӷ�", "����", "F56", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ��", "����", "F57", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������߷�", "����", "F58", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F59", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͨ�ڷ�", "����", "F60", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ͨ�ڷ�", "����", "F61", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ���ӷ�", "����", "F62", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ��ľ��", "����", "F63", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ���߷�", "����", "F64", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲ�˹���", "����", "F65", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ֲͨ�ڷ�", "����", "F66", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����˹���", "����", "F67", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ܻ���", "����", "F68", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����", "����", "F69", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ֱ�ӷ�", "����", "F70", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ��", "����", "F71", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������ʩ��", "����", "F72", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Ӫ�ֵ�·", "����", "F73", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƽ�֧��", "����", "F74", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���溦����", "����", "F75", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����ɱ�", "����", "F76", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ݱ�ʶ", "����", "F77", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Դ", "�ַ���", "F78", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����Ƶ��", "�ַ���", "F79", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����Ƶ��", "�ַ���", "F80", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ������", "����", "F81", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����״��", "����", "F82", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�߶�", "������", "F83", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ľ�ֲ�", "�ַ���", "F84", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��߶�", "������", "F85", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ݱ��ֲ�", "�ַ���", "F86", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ϡ����", "�ַ���", "F87", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F88", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���������", "����", "F89", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "����", "F90", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������", "����", "F91", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ľ����", "����", "F92", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����Ŀ����", "����", "F93", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ɷ�����", "����", "F94", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ɷ�ǿ��", "����", "F95", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�䷥���", "����", "F96", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��֦����", "����", "F97", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ǿ��", "����", "F98", "", false, 8, 0, ""));

	}

	private void addZhiFaShujuFields() {
		m_EditLayer.GetFieldList().add(createLayerField("ʡ", "�ַ���", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F3", "", false, 255, 0, ""));

		// ͼ�ߵǼ�
		m_EditLayer.GetFieldList().add(createLayerField("ͼ�ߺ�", "�ַ���", "F6", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F5", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F7", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ǰ����", "�ַ���", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ֵ���", "�ַ���", "F10", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�仯����", "�ַ���", "F11", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���Գ̶�", "�ַ���", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ж����", "������", "F13", "", false, 15, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���޲ɷ�", "�ַ���", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�����", "�ַ���", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�����", "�ַ���", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ֵ�����", "�ַ���", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע", "�ַ���", "F18", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("�ж���Ա", "�ַ���", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ǽ�����", "�ַ���", "F20", "", false, 255, 0, ""));
	}

	private void addZhiFaJianchaField() {

		m_EditLayer.GetFieldList().add(createLayerField("ʡ", "�ַ���", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F3", "", false, 255, 0, ""));

		// ͼ�ߵǼ�
		m_EditLayer.GetFieldList().add(createLayerField("ͼ�ߺ�", "�ַ���", "F6", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��", "�ַ���", "F5", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F7", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ǰ����", "�ַ���", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ֵ���", "�ַ���", "F10", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�仯����", "�ַ���", "F11", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���Գ̶�", "�ַ���", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ж����", "������", "F13", "", false, 15, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���޲ɷ�", "�ַ���", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�����", "�ַ���", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�����", "�ַ���", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ֵ�����", "�ַ���", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע", "�ַ���", "F18", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("�ж���Ա", "�ַ���", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ǽ�����", "�ַ���", "F20", "", false, 255, 0, ""));

		// //�����֤-�����ж�����
		// m_EditLayer.GetFieldList().add(createLayerField("ͼ���2","�ַ���","F21","",false,255,0,"TuBan"));
		// m_EditLayer.GetFieldList().add(createLayerField("������2","�ַ���","F22","",false,255,0,"MapX"));
		// m_EditLayer.GetFieldList().add(createLayerField("������2","�ַ���","F23","",false,255,0,"MapY"));
		// m_EditLayer.GetFieldList().add(createLayerField("ǰ����2","�ַ���","F24","����",false,255,0,"Q_DI_LEI"));
		// m_EditLayer.GetFieldList().add(createLayerField("�ֵ���2","�ַ���","F25","����",false,255,0,"DI_LEI"));
		// m_EditLayer.GetFieldList().add(createLayerField("�仯����2","�ַ���","F26","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("���Գ̶�2","�ַ���","F27","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("�ж����2","������","F28","",false,10,2,""));
		// m_EditLayer.GetFieldList().add(createLayerField("���޲ɷ�2","�ַ���","F29","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�����2","�ַ���","F30","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�����2","�ַ���","F31","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("�ֵ�����2","�ַ���","F32","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("��ע2","�ַ���","F33","",false,255,0,""));

		// �����֤-�ֵ���֤����
		m_EditLayer.GetFieldList().add(createLayerField("������3", "�ַ���", "F34", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������3", "�ַ���", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ǰ����3", "�ַ���", "F36", "����", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ֵ���3", "�ַ���", "F37", "����", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��;�仯", "�ַ���", "F38", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���޲ɷ�3", "�ַ���", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�Ƿ�����3", "�ַ���", "F40", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��ע3", "�ַ���", "F41", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F43", "", false, 255, 0, ""));

		// �����¼
		m_EditLayer.GetFieldList().add(createLayerField("�����鵥λ", "�ַ���", "F44", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����¼", "�ַ���", "F46", "", false, 512, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��¼����", "�ַ���", "F48", "", false, 255, 0, ""));

		// ������������м�¼
		// �Ƿ�������Ӳ����ܵ���
		m_EditLayer.GetFieldList().add(createLayerField("����������", "�ַ���", "F498", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������", "�ַ���", "F450", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����ʱ��", "�ַ���", "F51", "", false, 255, 0, ""));
	}

	private void AddXiaoBanXujiFields() {
		// m_EditLayer.GetFieldList().add(createLayerField("ʡ","�ַ���","F1","",false,255,0,""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��(��)", "�ַ���", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����(�ֳ�)", "�ַ���", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������(Ӫ����)", "�ַ���", "F5", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�ְ�(����С��)", "�ַ���", "F6", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("С��", "�ַ���", "F7", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ����", "�ַ���", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�¶�", "�ַ���", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��λ", "�ַ���", "F10", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F11", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("�۲���", "�ַ���", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("X����", "�ַ���", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Y����", "�ַ���", "F14", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��Դ", "�ַ���", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������(��)", "�ַ���", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ������", "�ַ���", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ���ؾ�", "�ַ���", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ƽ������", "�ַ���", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("���ն�", "�ַ���", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F23", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("ÿ���������", "�ַ���", "F24", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("�ϼ�", "�ַ���", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����", "�ַ���", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("������Ա", "�ַ���", "F27", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("��������", "�ַ���", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�����Ա", "�ַ���", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("�������", "�ַ���", "F30", "", false, 255, 0, ""));
	}

	private void AddMeiMuJianChiFields() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		v1_LayerField newFile1 = new v1_LayerField();
		newFile1.SetFieldName("���غ�");
		newFile1.SetFieldTypeName("�ַ���");
		newFile1.SetFieldSize(50);
		newFile1.SetFieldDecimal(1);
		newFile1.SetFieldEnumCode("");
		newFile1.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile1);
		newFields.add(newFile1);

		v1_LayerField newFile2 = new v1_LayerField();
		newFile2.SetFieldName("С���");
		newFile2.SetFieldTypeName("�ַ���");
		newFile2.SetFieldSize(50);
		newFile2.SetFieldDecimal(1);
		newFile2.SetFieldEnumCode("");
		newFile2.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile2);
		newFields.add(newFile2);

		v1_LayerField newFile3 = new v1_LayerField();
		newFile3.SetFieldName("��׼�غ�");
		newFile3.SetFieldTypeName("�ַ���");
		newFile3.SetFieldSize(50);
		newFile3.SetFieldDecimal(1);
		newFile3.SetFieldEnumCode("");
		newFile3.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile3);
		newFields.add(newFile3);

		v1_LayerField newFile4 = new v1_LayerField();
		newFile4.SetFieldName("ƽ����");
		newFile4.SetFieldTypeName("������");
		newFile4.SetFieldSize(50);
		newFile4.SetFieldDecimal(2);
		newFile4.SetFieldEnumCode("");
		newFile4.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile4);
		newFields.add(newFile4);

		v1_LayerField newFile5 = new v1_LayerField();
		newFile5.SetFieldName("����");
		newFile5.SetFieldTypeName("����");
		newFile5.SetFieldSize(50);
		newFile5.SetFieldDecimal(0);
		newFile5.SetFieldEnumCode("");
		newFile5.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile5);
		newFields.add(newFile5);

		v1_LayerField newFile6 = new v1_LayerField();
		newFile6.SetFieldName("�������");
		newFile6.SetFieldTypeName("������");
		newFile6.SetFieldSize(50);
		newFile6.SetFieldDecimal(2);
		newFile6.SetFieldEnumCode("");
		newFile6.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile6);
		newFields.add(newFile6);

		v1_LayerField newFile7 = new v1_LayerField();
		newFile7.SetFieldName("С���");
		newFile7.SetFieldTypeName("�ַ���");
		newFile7.SetFieldSize(510);
		newFile7.SetFieldDecimal(0);
		newFile7.SetFieldEnumCode("");
		newFile7.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile7);
		newFields.add(newFile7);

		for (int i = 1; i < 32; i++) {
			v1_LayerField newFile = new v1_LayerField();
			newFile.SetFieldName("ÿľ��߱�" + i);
			newFile.SetFieldTypeName("�ַ���");
			newFile.SetFieldSize(255);
			newFile.SetFieldDecimal(0);
			newFile.SetFieldEnumCode("");
			newFile.SetFieldEnumEdit(true);
			m_EditLayer.GetFieldList().add(newFile);
			newFields.add(newFile);
		}

		// �Զ���ȡ��Ӧ���������ֶ�
		for (v1_LayerField editField : newFields) {
			for (int i = 1; i <= 50; i++) {
				boolean haveField = false;
				for (v1_LayerField LF : m_EditLayer.GetFieldList()) {
					if (("F" + i).equals(LF.GetDataFieldName())) {
						haveField = true;
						break;
					}
				}
				if (!haveField) {
					editField.SetDataFieldName("F" + i);
					break;
				}
			}
		}
	}

	private void addXinJiangLDBG() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		newFields.add(createLayerField("ʡ", "�ַ���", "F1", "", false, 255, 0, "SHENG"));
		// newFields.add(createLayerField("��","�ַ���","F2","",false,255,0,"SHI"));
		newFields.add(createLayerField("��", "�ַ���", "F3", "", false, 255, 0, "XIAN"));
		newFields.add(createLayerField("��", "�ַ���", "F4", "", false, 255, 0, "XIANG"));
		newFields.add(createLayerField("��", "�ַ���", "F5", "", false, 255, 0, "CUN"));
		// ��������

		newFields.add(createLayerField("��ҵ��", "�ַ���", "F6", "", false, 255, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("�ֳ�", "�ַ���", "F7", "", false, 255, 0, "LIN_CHANG"));
		newFields.add(createLayerField("ԭ�ְ��", "�ַ���", "F8", "", false, 255, 0, "Q_LIN_BAN"));
		newFields.add(createLayerField("�ְ��", "�ַ���", "F9", "", false, 255, 0, "LIN_BAN"));
		newFields.add(createLayerField("ԭС���", "�ַ���", "F10", "", false, 255, 0, "Q_XIAO_BAN"));
		newFields.add(createLayerField("С���", "�ַ���", "F11", "", false, 255, 0, "XIAO_BAN"));
		newFields.add(createLayerField("������", "�ַ���", "F12", "", false, 255, 0));
		newFields.add(createLayerField("������", "�ַ���", "F13", "", false, 255, 0));

		newFields.add(createLayerField("��ò", "�ַ���", "F16", "��ò", false, 255, 0, "DI_MAO"));
		newFields.add(createLayerField("��λ", "�ַ���", "F17", "��λ", false, 255, 0, "PO_XIANG"));
		newFields.add(createLayerField("����", "�ַ���", "F18", "����", false, 255, 0, "PO_WEI"));
		newFields.add(createLayerField("�¶�", "�ַ���", "F19", "�¶�", false, 255, 0, "PO_DU"));
		newFields.add(createLayerField("��ͨ��λ", "�ַ���", "F20", "��ͨ��λ", false, 255, 0, "KE_JI_DU"));
		newFields.add(createLayerField("��������(����)", "�ַ���", "F21", "��������(����)", true, 255, 0, "TU_RANG_LX"));
		newFields.add(createLayerField("������", "����", "F22", "", false, 8, 0, "TU_CENG_HD"));
		newFields.add(createLayerField("���", "������", "F23", "", false, 10, 2, "MIAN_JI"));
		newFields.add(createLayerField("ǰ������Ȩ��", "�ַ���", "F30", "����Ȩ��", false, 255, 0, "Q_LD_QS"));
		newFields.add(createLayerField("����Ȩ��", "�ַ���", "F31", "����Ȩ��", false, 255, 0, "LD_QS"));

		// �ֵ�����
		newFields.add(createLayerField("ǰ�ڵ���", "�ַ���", "F24", "����", false, 255, 0, "Q_DI_LEI"));
		newFields.add(createLayerField("����", "�ַ���", "F25", "����", false, 255, 0, "DI_LEI"));
		newFields.add(createLayerField("ǰ������", "�ַ���", "F28", "����", false, 255, 0, "Q_L_Z"));
		newFields.add(createLayerField("����", "�ַ���", "F29", "����", false, 255, 0, "LIN_ZHONG"));
		newFields.add(createLayerField("��Դ", "�ַ���", "F47", "��Դ", false, 255, 0, "QI_YUAN"));
		newFields.add(createLayerField("ǰ��ɭ�����", "�ַ���", "F35", "ɭ�֣��ֵأ����", false, 255, 0, "Q_SEN_LB"));
		newFields.add(createLayerField("ɭ�����", "�ַ���", "F36", "ɭ�֣��ֵأ����", false, 255, 0, "SEN_LIN_LB"));
		newFields.add(createLayerField("ǰ����Ȩ�ȼ�", "�ַ���", "F37", "��Ȩ�ȼ�", false, 255, 0, "Q_SQ_D"));
		newFields.add(createLayerField("��Ȩ�ȼ�", "�ַ���", "F38", "��Ȩ�ȼ�", false, 255, 0, "SHI_QUAN_D"));
		// newFields.add(createLayerField("�����ֱ����ȼ�","�ַ���","F42","���Ҽ������ֱ����ȼ�",false,255,0,"GYL_BHDJ"));
		newFields.add(createLayerField("���ҹ����ֱ����ȼ�", "�ַ���", "F41", "���Ҽ������ֱ����ȼ�", false, 255, 0, "GJGYL_BHDJ"));
		newFields.add(createLayerField("ǰ�ڹ������", "�ַ���", "F33", "�������", false, 255, 0, "Q_GC_LB"));
		newFields.add(createLayerField("�������", "�ַ���", "F34", "�������", false, 255, 0, "G_CHENG_LB"));
		newFields.add(createLayerField("����", "�ַ���", "F48", "����", false, 255, 0, "LING_ZU"));
		newFields.add(createLayerField("���ܶ�/���Ƕ�", "������", "F49", "", false, 6, 2, "YU_BI_DU"));
		newFields.add(createLayerField("��������", "�ַ���", "F59", "����", false, 255, 0, "YOU_SHI_SZ"));
		newFields.add(createLayerField("ƽ���ؾ�", "������", "F60", "", false, 6, 1, "PINGJUN_XJ"));
		newFields.add(createLayerField("ÿ�������(����ľ)", "������", "F54", "", false, 12, 2, "HUO_LMGQXJ"));
		newFields.add(createLayerField("ÿ��������", "����", "F53", "", false, 255, 0, "MEI_GQ_ZS"));

		newFields.add(createLayerField("�����˻�����", "�ַ���", "F64", "�����˻�����", false, 8, 0, "TD_TH_LX"));
		newFields.add(createLayerField("�ֺ�����", "�ַ���", "F65", "�ֺ�����", false, 255, 0, "DISPE"));
		newFields.add(createLayerField("�ֺ��ȼ�", "�ַ���", "F66", "ɭ���ֺ��ȼ�", false, 255, 0, "DISASTER_C"));
		newFields.add(createLayerField("�ֵ������ȼ�", "�ַ���", "F26", "�ֵ������ȼ�", false, 255, 0, "ZL_DJ"));
		newFields.add(createLayerField("�ִ�����", "������", "F14", "", false, 8, 1, "LD_ZD"));
		newFields.add(createLayerField("�ִ����", "������", "F15", "", false, 8, 1, "LD_KD"));

		newFields.add(createLayerField("�Ƿ�Ϊ�����ֵ�", "�ַ���", "F67", "�Ƿ�Ϊ�����ֵ�", false, 8, 0, "BCLD"));
		newFields.add(createLayerField("ǰ���ֵر����ȼ�", "�ַ���", "F39", "�ֵر����ȼ�", false, 255, 0, "Q_BH_DJ"));
		newFields.add(createLayerField("�ֵر����ȼ�", "�ַ���", "F40", "�ֵر����ȼ�", false, 255, 0, "BH_DJ"));
		newFields.add(createLayerField("�ֵع��ܷ���", "�ַ���", "F63", "", false, 8, 0, "LYFQ"));
		newFields.add(createLayerField("���幦����", "�ַ���", "F62", "���幦����", false, 8, 0, "QYKZ"));
		newFields.add(createLayerField("�仯ԭ��", "�ַ���", "F45", "�ֵر仯ԭ��", false, 255, 0, "BHYY"));
		newFields.add(createLayerField("�仯���", "�ַ���", "F46", "", false, 255, 0, "BHND"));
		newFields.add(createLayerField("�ֵع�������", "�ַ���", "F44", "�ֵع�������", false, 255, 0, "GLLX"));
		newFields.add(createLayerField("˵��", "�ַ���", "F70", "", false, 255, 0, "REMARKS"));

		// newFields.add(createLayerField("С�������","������","F50","",false,10,1,"XIAOBAN_XJ"));
		// newFields.add(createLayerField("����������","����","F51","",false,255,0,"SPS_ZS"));
		// newFields.add(createLayerField("�����������","������","F52","",false,10,1,"SPS_XJ"));
		// //��������
		// newFields.add(createLayerField("��̬��λ","�ַ���","F27","",false,255,0,"SH_TAI_QW"));
		// newFields.add(createLayerField("��ľȨ��","�ַ���","F32","��ľȨ��",false,255,0,"LM_SY"));

		// newFields.add(createLayerField("ɢ��ľ����","����","F55","",false,255,0,"SSM_ZS"));
		// newFields.add(createLayerField("ɢ��ľ���","������","F57","",false,15,3,"SSM_XJ"));
		// newFields.add(createLayerField("ɢ��������","����","F56","",false,12,0,"SSZ_ZS"));
		// newFields.add(createLayerField("С��������鷽ʽ","�ַ���","F58","С��������鷽ʽ",false,8,0,"DIAOCHA_FS"));
		// newFields.add(createLayerField("��������","������","F68","",false,8,0,"DIAOCHA_RQ"));
		// newFields.add(createLayerField("������","�ַ���","F69","",false,255,0,"DC_REN"));

		// �����ĸ��ֶ� �������� �ȼ� ���� ��ע

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}
	}

	private void addErDiaoField() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		newFields.add(createLayerField("ʡ", "�ַ���", "F1", "", false, 32, 0, "SHENG"));
		newFields.add(createLayerField("��", "�ַ���", "F2", "", false, 32, 0, "SHI"));
		newFields.add(createLayerField("��", "�ַ���", "F3", "", false, 32, 0, "XIAN"));
		newFields.add(createLayerField("��", "�ַ���", "F4", "", false, 32, 0, "XIANG"));
		newFields.add(createLayerField("��", "�ַ���", "F5", "", false, 32, 0, "CUN"));
		newFields.add(createLayerField("��ҵ��", "�ַ���", "F6", "", false, 32, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("�ֳ�", "�ַ���", "F7", "", false, 32, 0, "LIN_CHANG"));
		newFields.add(createLayerField("ԭ�ְ�", "�ַ���", "F8", "", false, 4, 0, "Q_LIN_BAN"));
		newFields.add(createLayerField("�ְ�", "�ַ���", "F9", "", false, 4, 0, "LIN_BAN"));
		newFields.add(createLayerField("ԭС��", "�ַ���", "F10", "", false, 5, 0, "Q_XIAO_BAN"));
		newFields.add(createLayerField("С��", "�ַ���", "F11", "", false, 5, 0, "XIAO_BAN"));
		newFields.add(createLayerField("������", "������", "F12", "", false, 16, 3));
		newFields.add(createLayerField("������", "������", "F13", "", false, 16, 3));

		newFields.add(createLayerField("����", "����", "F15", "", false, 4, 0, "DI_MAO"));
		newFields.add(createLayerField("��ò", "�ַ���", "F16", "��ò", false, 255, 0, "DI_MAO"));
		newFields.add(createLayerField("��λ", "�ַ���", "F17", "��λ", false, 255, 0, "PO_WEI"));
		newFields.add(createLayerField("����", "�ַ���", "F18", "����", false, 255, 0, "PO_XIANG"));
		newFields.add(createLayerField("�¶�", "����", "F19", "�¶�", false, 255, 0, "PO_DU"));
		newFields.add(createLayerField("��ͨ��λ", "�ַ���", "F20", "��ͨ��λ", false, 6, 0, "KE_JI_DU"));
		newFields.add(createLayerField("��������", "�ַ���", "F21", "��������", true, 20, 0, "TU_RANG_LX"));
		newFields.add(createLayerField("������", "����", "F22", "", false, 3, 0, "TU_CENG_HD"));
		newFields.add(createLayerField("���", "������", "F23", "", false, 18, 2, "MIAN_JI"));
		newFields.add(createLayerField("����", "�ַ���", "F24", "", false, 6, 0, "YOU_SHU"));
		newFields.add(createLayerField("������������", "����", "F25", "", false, 4, 0, "YS_GQ_ZS"));
		newFields.add(createLayerField("���µȼ�", "�ַ���", "F26", "", false, 1, 0, "GX_DJ"));
		newFields.add(createLayerField("��ľ����", "�ַ���", "F27", "", false, 6, 0, "XM_ZL"));
		newFields.add(createLayerField("��ľ�Ƕ�", "����", "F28", "", false, 2, 0, "XM_GD"));
		newFields.add(createLayerField("��������", "�ַ���", "F29", "", false, 6, 0, "LD_LX"));
		newFields.add(createLayerField("�ֲ�", "�ַ���", "F30", "", false, 8, 0, "LIN_CENG"));
		newFields.add(createLayerField("Ⱥ��ṹ", "�ַ���", "F31", "", false, 16, 0, "QL_JG"));
		newFields.add(createLayerField("��Ȼ��", "�ַ���", "F32", "", false, 16, 0, "LIN_CENG"));
		newFields.add(createLayerField("��̬��λ", "�ַ���", "F33", "��̬��λ", false, 3, 0, "SH_TAI_QW"));

		newFields.add(createLayerField("ǰ������Ȩ��", "�ַ���", "F35", "����Ȩ��", false, 2, 0, "Q_LD_QS"));
		newFields.add(createLayerField("����Ȩ��", "�ַ���", "F36", "����Ȩ��", false, 2, 0, "LD_QS"));
		newFields.add(createLayerField("����ʹ��Ȩ", "�ַ���", "F37", "����ʹ��Ȩ", false, 2, 0, "LD_SY_QS"));
		newFields.add(createLayerField("��ľȨ��", "�ַ���", "F38", "��ľȨ��", false, 2, 0, "LM_QS"));
		newFields.add(createLayerField("��ľʹ��Ȩ", "�ַ���", "F39", "��ľʹ��Ȩ", false, 2, 0, "LM_SY_QS"));

		newFields.add(createLayerField("ǰ�ڵ���", "�ַ���", "F40", "����", false, 4, 0, "Q_DI_LEI"));
		newFields.add(createLayerField("����", "�ַ���", "F41", "����", false, 4, 0, "DI_LEI"));
		newFields.add(createLayerField("ǰ������", "�ַ���", "F42", "����", false, 3, 0, "Q_L_Z"));
		newFields.add(createLayerField("����", "�ַ���", "F43", "����", false, 3, 0, "LIN_ZHONG"));
		newFields.add(createLayerField("��Դ", "�ַ���", "F44", "��Դ", false, 2, 0, "QI_YUAN"));
		newFields.add(createLayerField("ǰ��ɭ�����", "�ַ���", "F45", "ɭ�֣��ֵأ����", false, 32, 0, "Q_SEN_LB"));
		newFields.add(createLayerField("ɭ�����", "�ַ���", "F46", "ɭ�֣��ֵأ����", false, 32, 0, "SEN_LIN_LB"));
		newFields.add(createLayerField("ǰ����Ȩ�ȼ�", "�ַ���", "F47", "��Ȩ�ȼ�", false, 32, 0, "Q_SQ_D"));
		newFields.add(createLayerField("��Ȩ�ȼ�", "�ַ���", "F48", "��Ȩ�ȼ�", false, 32, 0, "SHI_QUAN_D"));
		newFields.add(createLayerField("�ط������ֱ����ȼ�", "�ַ���", "F49", "�ط������ֱ����ȼ�", false, 16, 0, "DFGYL_BHDJ"));
		newFields.add(createLayerField("���ҹ����ֱ����ȼ�", "�ַ���", "F50", "���Ҽ������ֱ����ȼ�", false, 16, 0, "GJGYL_BHDJ"));
		newFields.add(createLayerField("ǰ�ڹ������", "�ַ���", "F51", "�������", false, 255, 0, "Q_GC_LB"));
		newFields.add(createLayerField("�������", "�ַ���", "F52", "�������", false, 255, 0, "G_CHENG_LB"));
		newFields.add(createLayerField("��������", "�ַ���", "F53", "��������", false, 255, 0, "YOU_SHI_SZ"));
		newFields.add(createLayerField("�������", "�ַ���", "F54", "����", false, 255, 0, "SHUZ_ZC"));
		newFields.add(createLayerField("ƽ���ؾ�", "������", "F55", "", false, 6, 1, "PINGJUN_XJ"));
		newFields.add(createLayerField("ƽ������", "������", "F56", "", false, 6, 1, "PINGJUN_SG"));

		newFields.add(createLayerField("����", "�ַ���", "F57", "", false, 3, 0, "NIAN_LIN"));
		newFields.add(createLayerField("�伶", "�ַ���", "F58", "", false, 255, 0, "LIN_JI"));
		newFields.add(createLayerField("����", "�ַ���", "F59", "����", false, 255, 0, "LING_ZU"));
		newFields.add(createLayerField("���ܶ�/���Ƕ�", "������", "F60", "", false, 6, 2, "YU_BI_DU"));
		newFields.add(createLayerField("�������(����ľ)", "������", "F61", "", false, 12, 2, "HUO_LMGQXJ"));
		newFields.add(createLayerField("ÿ��������", "����", "F62", "", false, 8, 0, "MEI_GQ_ZS"));
		newFields.add(createLayerField("С�������", "������", "F63", "", false, 10, 1, "XIAOBAN_XJ"));
		newFields.add(createLayerField("����������", "����", "F64", "", false, 255, 0, "SPS_ZS"));
		newFields.add(createLayerField("�����������", "������", "F65", "", false, 10, 1, "SPS_XJ"));
		newFields.add(createLayerField("ɢ��ľ����", "����", "F66", "", false, 8, 0, "SSM_ZS"));
		newFields.add(createLayerField("ɢ��ľ���", "������", "F67", "", false, 10, 1, "SSM_XJ"));
		newFields.add(createLayerField("ɢ��������", "����", "F68", "", false, 8, 0, "SSZ_ZS"));
		newFields.add(createLayerField("����ľ���", "������", "F69", "", false, 10, 1, "KLM_XJ"));
		newFields.add(createLayerField("����ľ���", "������", "F70", "", false, 10, 1, "DLM_XJ"));
		newFields.add(createLayerField("�����ֲ���", "�ַ���", "F71", "�����ֲ���", false, 2, 0, "CH_QI"));
		newFields.add(createLayerField("�����˻�����", "�ַ���", "F72", "�����˻�����", false, 1, 0, "TD_TH_LX"));
		newFields.add(createLayerField("ʪ������", "�ַ���", "F73", "ʪ������", false, 1, 0, "SHIDI_LX"));
		newFields.add(createLayerField("����״��", "�ַ���", "F74", "ɭ�ֽ����ȼ�", false, 8, 0, "JK_ZHK"));
		newFields.add(createLayerField("�ֺ�����", "�ַ���", "F75", "ɭ���ֺ�����", false, 2, 0, "DISPE"));
		newFields.add(createLayerField("�ֺ��ȼ�", "�ַ���", "F76", "ɭ���ֺ��ȼ�", false, 1, 0, "DISASTER_C"));
		newFields.add(createLayerField("�ֵ������ȼ�", "�ַ���", "F77", "�ֵ������ȼ�", false, 1, 0, "ZL_DJ"));
		newFields.add(createLayerField("�ִ�����", "������", "F78", "", false, 8, 1, "LD_ZD"));
		newFields.add(createLayerField("�ִ����", "������", "F79", "", false, 8, 1, "LD_KD"));
		newFields.add(createLayerField("�Ƿ�Ϊ�����ֵ�", "�ַ���", "F80", "�Ƿ�Ϊ�����ֵ�", false, 16, 0, "BCLD"));
		newFields.add(createLayerField("ǰ�ֵر����ȼ�", "�ַ���", "F81", "�ֵر����ȼ�", false, 16, 0, "Q_BH_DJ"));
		newFields.add(createLayerField("�ֵر����ȼ�", "�ַ���", "F82", "�ֵر����ȼ�", false, 16, 0, "BH_DJ"));
		newFields.add(createLayerField("�ֵع��ܷ���", "�ַ���", "F83", "", false, 50, 0, "LYFQ"));
		newFields.add(createLayerField("���幦����", "�ַ���", "F84", "���幦����", false, 1, 0, "QYKZ"));
		newFields.add(createLayerField("�仯ԭ��", "�ַ���", "F85", "�ֵر仯ԭ��", false, 2, 0, "BHYY"));
		newFields.add(createLayerField("�仯���", "�ַ���", "F86", "", false, 4, 0, "BHND"));
		newFields.add(createLayerField("�ֵع�������", "�ַ���", "F87", "�ֵع�������", false, 32, 0, "GLLX"));

		newFields.add(createLayerField("��Ӫ��ʩ", "�ַ���", "F88", "��Ӫ��ʩ", false, 32, 0, "JYCS"));
		newFields.add(createLayerField("�ɻ���", "������", "F89", "", false, 10, 1, "CH_HL"));
		newFields.add(createLayerField("��������", "������", "F90", "", false, 8, 0, "DIAOCHA_RQ"));
		newFields.add(createLayerField("������", "�ַ���", "F91", "", false, 255, 0, "DC_REN"));
		newFields.add(createLayerField("˵��", "�ַ���", "F92", "", false, 255, 0, "REMARKS"));

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}
	}

	private void addLinDiBianGengField() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		newFields.add(createLayerField("ʡ", "�ַ���", "F1", "", false, 255, 0, "SHENG"));
		newFields.add(createLayerField("��", "�ַ���", "F2", "", false, 255, 0, "SHI"));
		newFields.add(createLayerField("��", "�ַ���", "F3", "", false, 255, 0, "XIAN"));
		newFields.add(createLayerField("��", "�ַ���", "F4", "", false, 255, 0, "XIANG"));
		newFields.add(createLayerField("��", "�ַ���", "F5", "", false, 255, 0, "CUN"));
		// ��������

		newFields.add(createLayerField("��ҵ��", "�ַ���", "F6", "", false, 255, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("�ֳ�", "�ַ���", "F7", "", false, 255, 0, "LIN_CHANG"));
		newFields.add(createLayerField("ԭ�ְ��", "�ַ���", "F8", "", false, 255, 0, "Q_LIN_BAN"));
		newFields.add(createLayerField("�ְ��", "�ַ���", "F9", "", false, 255, 0, "LIN_BAN"));
		newFields.add(createLayerField("ԭС���", "�ַ���", "F10", "", false, 255, 0, "Q_XIAO_BAN"));
		newFields.add(createLayerField("С���", "�ַ���", "F11", "", false, 255, 0, "XIAO_BAN"));
		newFields.add(createLayerField("������", "�ַ���", "F12", "", false, 255, 0));
		newFields.add(createLayerField("������", "�ַ���", "F13", "", false, 255, 0));

		newFields.add(createLayerField("��ò", "�ַ���", "F14", "��ò", false, 255, 0, "DI_MAO"));
		newFields.add(createLayerField("��λ", "�ַ���", "F15", "��λ", false, 255, 0, "PO_XIANG"));
		newFields.add(createLayerField("����", "�ַ���", "F16", "����", false, 255, 0, "PO_WEI"));
		newFields.add(createLayerField("�¶�", "�ַ���", "F17", "�¶�", false, 255, 0, "PO_DU"));
		newFields.add(createLayerField("��ͨ��λ", "�ַ���", "F18", "��ͨ��λ", false, 255, 0, "KE_JI_DU"));
		newFields.add(createLayerField("��������(����)", "�ַ���", "F19", "��������(����)", true, 255, 0, "TU_RANG_LX"));
		newFields.add(createLayerField("������", "����", "F20", "", false, 8, 0, "TU_CENG_HD"));
		newFields.add(createLayerField("���", "������", "F21", "", false, 18, 2, "MIAN_JI"));
		newFields.add(createLayerField("ǰ������Ȩ��", "�ַ���", "F22", "����Ȩ��", false, 255, 0, "Q_LD_QS"));
		newFields.add(createLayerField("����Ȩ��", "�ַ���", "F23", "����Ȩ��", false, 255, 0, "LD_QS"));

		// �ֵ�����
		newFields.add(createLayerField("ǰ�ڵ���", "�ַ���", "F24", "����", false, 255, 0, "Q_DI_LEI"));
		newFields.add(createLayerField("����", "�ַ���", "F25", "����", false, 255, 0, "DI_LEI"));
		newFields.add(createLayerField("ǰ������", "�ַ���", "F26", "����", false, 255, 0, "Q_L_Z"));
		newFields.add(createLayerField("����", "�ַ���", "F27", "����", false, 255, 0, "LIN_ZHONG"));
		newFields.add(createLayerField("��Դ", "�ַ���", "F28", "��Դ", false, 255, 0, "QI_YUAN"));
		newFields.add(createLayerField("ǰ��ɭ�����", "�ַ���", "F29", "ɭ�֣��ֵأ����", false, 255, 0, "Q_SEN_LB"));
		newFields.add(createLayerField("ɭ�����", "�ַ���", "F30", "ɭ�֣��ֵأ����", false, 255, 0, "SEN_LIN_LB"));
		newFields.add(createLayerField("ǰ����Ȩ�ȼ�", "�ַ���", "F31", "��Ȩ�ȼ�", false, 255, 0, "Q_SQ_D"));
		newFields.add(createLayerField("��Ȩ�ȼ�", "�ַ���", "F32", "��Ȩ�ȼ�", false, 255, 0, "SHI_QUAN_D"));
		newFields.add(createLayerField("�����ֱ����ȼ�", "�ַ���", "F33", "���Ҽ������ֱ����ȼ�", false, 255, 0, "GYL_BHDJ"));
		newFields.add(createLayerField("���ҹ����ֱ����ȼ�", "�ַ���", "F34", "���Ҽ������ֱ����ȼ�", false, 255, 0, "GJGYL_BHDJ"));
		newFields.add(createLayerField("ǰ�ڹ������", "�ַ���", "F35", "�������", false, 255, 0, "Q_GC_LB"));
		newFields.add(createLayerField("�������", "�ַ���", "F36", "�������", false, 255, 0, "G_CHENG_LB"));
		newFields.add(createLayerField("����", "�ַ���", "F37", "����", false, 255, 0, "LING_ZU"));
		newFields.add(createLayerField("���ܶ�/���Ƕ�", "������", "F38", "", false, 6, 2, "YU_BI_DU"));
		newFields.add(createLayerField("��������", "�ַ���", "F49", "����", false, 255, 0, "YOU_SHI_SZ"));
		newFields.add(createLayerField("ƽ���ؾ�", "������", "F50", "", false, 6, 1, "PINGJUN_XJ"));
		newFields.add(createLayerField("ÿ�������(����ľ)", "������", "F51", "", false, 12, 2, "HUO_LMGQXJ"));
		newFields.add(createLayerField("ÿ��������", "����", "F52", "", false, 4, 0, "MEI_GQ_ZS"));

		newFields.add(createLayerField("�����˻�����", "�ַ���", "F53", "�����˻�����", false, 8, 0, "TD_TH_LX"));
		newFields.add(createLayerField("�ֺ�����", "�ַ���", "F54", "�ֺ�����", false, 255, 0, "DISPE"));
		newFields.add(createLayerField("�ֺ��ȼ�", "�ַ���", "F55", "ɭ���ֺ��ȼ�", false, 255, 0, "DISASTER_C"));
		newFields.add(createLayerField("�ֵ������ȼ�", "�ַ���", "F56", "�ֵ������ȼ�", false, 255, 0, "ZL_DJ"));
		newFields.add(createLayerField("�ִ�����", "������", "F57", "", false, 8, 1, "LD_ZD"));
		newFields.add(createLayerField("�ִ����", "������", "F58", "", false, 8, 1, "LD_KD"));

		newFields.add(createLayerField("�Ƿ�Ϊ�����ֵ�", "�ַ���", "F59", "�Ƿ�Ϊ�����ֵ�", false, 8, 0, "BCLD"));
		newFields.add(createLayerField("ǰ���ֵر����ȼ�", "�ַ���", "F60", "�ֵر����ȼ�", false, 255, 0, "Q_BH_DJ"));
		newFields.add(createLayerField("�ֵر����ȼ�", "�ַ���", "F61", "�ֵر����ȼ�", false, 255, 0, "BH_DJ"));
		newFields.add(createLayerField("�ֵع��ܷ���", "�ַ���", "F62", "", false, 8, 0, "LYFQ"));
		newFields.add(createLayerField("���幦����", "�ַ���", "F63", "���幦����", false, 8, 0, "QYKZ"));
		newFields.add(createLayerField("�仯ԭ��", "�ַ���", "F64", "�ֵر仯ԭ��", false, 255, 0, "BHYY"));
		newFields.add(createLayerField("�仯���", "�ַ���", "F65", "", false, 255, 0, "BHND"));
		newFields.add(createLayerField("�ֵع�������", "�ַ���", "F66", "�ֵع�������", false, 255, 0, "GLLX"));
		newFields.add(createLayerField("˵��", "�ַ���", "F67", "", false, 255, 0, "REMARKS"));

		newFields.add(createLayerField("�����ֲ���", "�ַ���", "F79", "�����ֲ���", false, 2, 0, "CH_QI"));

		// for xinjiang ����Ҫ�����������
		newFields.add(createLayerField("С�������", "������", "F68", "", false, 10, 1, "XIAOBAN_XJ"));
		newFields.add(createLayerField("����������", "����", "F69", "", false, 255, 0, "SPS_ZS"));
		newFields.add(createLayerField("�����������", "������", "F70", "", false, 10, 1, "SPS_XJ"));
		// ��������
		newFields.add(createLayerField("��̬��λ", "�ַ���", "F71", "", false, 255, 0, "SH_TAI_QW"));
		newFields.add(createLayerField("��ľȨ��", "�ַ���", "F72", "��ľȨ��", false, 255, 0, "LM_SY"));
		//
		//
		newFields.add(createLayerField("ɢ��ľ����", "����", "F73", "", false, 255, 0, "SSM_ZS"));
		newFields.add(createLayerField("ɢ��ľ���", "������", "F74", "", false, 15, 3, "SSM_XJ"));
		newFields.add(createLayerField("ɢ��������", "����", "F75", "", false, 12, 0, "SSZ_ZS"));
		newFields.add(createLayerField("С��������鷽ʽ", "�ַ���", "F76", "С��������鷽ʽ", false, 8, 0, "DIAOCHA_FS"));
		newFields.add(createLayerField("��������", "������", "F77", "", false, 8, 0, "DIAOCHA_RQ"));
		newFields.add(createLayerField("������", "�ַ���", "F78", "", false, 255, 0, "DC_REN"));

		// �����ĸ��ֶ� �������� �ȼ� ���� ��ע

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}
	}

	private void initTuiGengEnum() {
		DictXZQH xzqh = new DictXZQH();
		city = xzqh.getXZQH("61", "��");
		ArrayList<String> cityNames = new ArrayList<String>();
		for (HashMap<String, Object> hm : city) {
			cityNames.add(hm.get("D1").toString());
		}

		ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, cityNames.toArray());
		nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		Spinner tgds = ((Spinner) _Dialog.findViewById(R.id.sp_tgds));
		tgds.setAdapter(nfAdapter);
		tgds.setOnItemSelectedListener(new tgdsOnItemSelectedListener());

	}

	List<HashMap<String, Object>> city = new ArrayList<HashMap<String, Object>>();

	class tgdsOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
			DictXZQH xzqh = new DictXZQH();
			List<HashMap<String, Object>> xian = xzqh
					.getXZQH(xzqh.getCodeByName(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds), "��", "61"), "��");

			ArrayList<String> cityNames = new ArrayList<String>();
			for (HashMap<String, Object> hm : xian) {
				cityNames.add(hm.get("D1").toString());
			}

			ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
					android.R.layout.simple_spinner_item, cityNames.toArray());
			nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
			Spinner tgds = ((Spinner) _Dialog.findViewById(R.id.sp_tgqx));
			tgds.setAdapter(nfAdapter);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	private void addDefaultField() {
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F1", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F2", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F3", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("���ƴ�", "�ַ���", "F4", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("��Ȼ��", "�ַ���", "F5", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("С����", "�ַ���", "F6", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("С���", "�ַ���", "F7", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("ͼ����", "�ַ���", "F8", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F9", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("С�����", "������", "F10", "", false, 15, 2));
		m_EditLayer.GetFieldList().add(createLayerField("����ҵ���", "������", "F11", "", false, 15, 2));

		m_EditLayer.GetFieldList().add(createLayerField("Ȩ��", "�ַ���", "F12", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("����", "������", "F13", "", false, 8, 2));
		m_EditLayer.GetFieldList().add(createLayerField("��λ", "�ַ���", "F14", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F15", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("�¶�", "�ַ���", "F16", "", false, 64, 0));

	}

	private void addTuiGengField() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		v1_LayerField newFile1 = new v1_LayerField();

		newFields.add(createLayerField("����", "�ַ���", "F1", "", false, 255, 0));
		newFields.add(createLayerField("����", "�ַ���", "F2", "", false, 255, 0));
		newFields.add(createLayerField("����", "�ַ���", "F3", "", false, 255, 0));
		newFields.add(createLayerField("���ƴ�", "�ַ���", "F4", "", false, 255, 0));
		newFields.add(createLayerField("��Ȼ��", "�ַ���", "F5", "", false, 255, 0));
		newFields.add(createLayerField("С����", "�ַ���", "F6", "", false, 255, 0));
		newFields.add(createLayerField("С���", "�ַ���", "F7", "", false, 255, 0));
		newFields.add(createLayerField("����", "�ַ���", "F8", "", false, 255, 0));
		newFields.add(createLayerField("С�����", "������", "F9", "", false, 15, 2));

		newFields.add(createLayerField("����ҵ���", "������", "F10", "", false, 15, 2));

		newFields.add(createLayerField("Ȩ��", "�ַ���", "F11", "", false, 255, 0));
		newFields.add(createLayerField("����", "������", "F12", "", false, 12, 2));

		newFields.add(createLayerField("��λ", "�ַ���", "F13", "", false, 255, 0));
		newFields.add(createLayerField("����", "�ַ���", "F14", "", false, 255, 0));
		newFields.add(createLayerField("�¶�", "�ַ���", "F15", "", false, 255, 0));
		newFields.add(createLayerField("��������", "�ַ���", "F16", "", false, 255, 0));
		newFields.add(createLayerField("������", "�ַ���", "F17", "", false, 255, 0));
		newFields.add(createLayerField("��������", "�ַ���", "F18", "", false, 255, 0));
		// ƥ����ܲ�ȫ
		newFields.add(createLayerField("��ʴ�̶�", "�ַ���", "F19", "", false, 255, 0));

		newFields.add(createLayerField("����PHֵ", "������", "F20", "", false, 8, 2));
		newFields.add(createLayerField("ֲ������", "�ַ���", "F21", "", false, 255, 0));
		newFields.add(createLayerField("ֲ���Ƕ�", "������", "F22", "", false, 12, 2));
		newFields.add(createLayerField("ֲ���߶�", "������", "F23", "", false, 12, 2));

		newFields.add(createLayerField("�Ƿ���", "�ַ���", "F24", "", false, 255, 0));
		newFields.add(createLayerField("���ԭ��", "�ַ���", "F25", "", false, 255, 0));
		newFields.add(createLayerField("������", "�ַ���", "F26", "", false, 255, 0));
		newFields.add(createLayerField("������", "�ַ���", "F27", "", false, 255, 0));
		newFields.add(createLayerField("������", "�ַ���", "F28", "", false, 255, 0));
		newFields.add(createLayerField("ͼ����", "�ַ���", "F29", "", false, 255, 0));

		newFields.add(createLayerField("���ַ�ʽ", "�ַ���", "F31", "", false, 255, 0));
		newFields.add(createLayerField("��������", "�ַ���", "F32", "", false, 255, 0));

		newFields.add(createLayerField("�������", "������", "F33", "", false, 12, 2));
		newFields.add(createLayerField("��������", "�ַ���", "F34", "", false, 255, 0));
		newFields.add(createLayerField("��������", "�ַ���", "F35", "", false, 255, 0));
		newFields.add(createLayerField("���о�", "�ַ���", "F36", "", false, 255, 0));
		newFields.add(createLayerField("�콻��", "�ַ���", "F37", "", false, 255, 0));
		newFields.add(createLayerField("�������", "����", "F38", "", false, 8, 0));
		newFields.add(createLayerField("����ʱ��", "�ַ���", "F39", "", false, 255, 0));
		newFields.add(createLayerField("������Ҫ��", "������", "F40", "", false, 12, 2));

		newFields.add(createLayerField("���ط�ʽ", "�ַ���", "F41", "", false, 255, 0));
		newFields.add(createLayerField("���ع��", "�ַ���", "F42", "", false, 255, 0));
		newFields.add(createLayerField("�ù����ϼ�", "������", "F43", "", false, 12, 2));
		newFields.add(createLayerField("�ù�������", "������", "F44", "", false, 12, 2));
		newFields.add(createLayerField("�ù�������", "������", "F45", "", false, 12, 2));
		newFields.add(createLayerField("�ù�����ֲ", "������", "F46", "", false, 12, 2));
		newFields.add(createLayerField("�ù�������", "������", "F47", "", false, 12, 2));

		newFields.add(createLayerField("ģʽ��", "�ַ���", "F50", "", false, 255, 0));
		newFields.add(createLayerField("Ͷ��Ԥ��", "�ַ���", "F51", "", false, 255, 0));
		newFields.add(createLayerField("��ע", "�ַ���", "F52", "", false, 255, 0));
		newFields.add(createLayerField("��ľ���", "�ַ���", "F53", "", false, 255, 0));
		newFields.add(createLayerField("��ľ����", "������", "F54", "", false, 8, 2));
		newFields.add(createLayerField("���д���", "�ַ���", "F55", "", false, 255, 0));
		newFields.add(createLayerField("���ش���", "�ַ���", "F56", "", false, 255, 0));
		newFields.add(createLayerField("�������", "�ַ���", "F57", "", false, 255, 0));
		newFields.add(createLayerField("�����", "�ַ���", "F58", "", false, 255, 0));
		newFields.add(createLayerField("���յ���", "������", "F60", "", false, 12, 2));

		newFields.add(createLayerField("_����", "�ַ���", "F61", "", false, 255, 0));
		newFields.add(createLayerField("_����", "�ַ���", "F62", "", false, 255, 0));
		newFields.add(createLayerField("_���", "�ַ���", "F63", "", false, 255, 0));
		newFields.add(createLayerField("_����", "�ַ���", "F64", "", false, 255, 0));
		newFields.add(createLayerField("_����", "�ַ���", "F65", "", false, 255, 0));
		newFields.add(createLayerField("_��ľ", "�ַ���", "F66", "", false, 255, 0));
		newFields.add(createLayerField("_����", "�ַ���", "F67", "", false, 255, 0));

		newFields.add(createLayerField("D_����", "�ַ���", "F68", "", false, 255, 0));
		newFields.add(createLayerField("D_�������", "�ַ���", "F69", "", false, 255, 0));
		newFields.add(createLayerField("D_���ֽ��", "�ַ���", "F70", "", false, 255, 0));
		newFields.add(createLayerField("D_���߲���", "�ַ���", "F71", "", false, 255, 0));
		newFields.add(createLayerField("D_������׼", "�ַ���", "F72", "", false, 255, 0));
		newFields.add(createLayerField("D_�����", "�ַ���", "F73", "", false, 255, 0));
		newFields.add(createLayerField("D_�Ƿ����", "�ַ���", "F74", "", false, 255, 0));
		newFields.add(createLayerField("D_��ע", "�ַ���", "F75", "", false, 255, 0));

		newFields.add(createLayerField("_�Ƿ�ƶ����", "�ַ���", "F77", "", false, 255, 0));

		// newFields.add(createLayerField("J_�������","�ַ���","F2","",false,255,0));
		// newFields.add(createLayerField("J_��ҵ���","�ַ���","F2","",false,255,0));
		// newFields.add(createLayerField("J_�˸�����","�ַ���","F2","",false,255,0));
		// newFields.add(createLayerField("J_�ֵ�Ȩ��","�ַ���","F2","",false,255,0));
		// newFields.add(createLayerField("J_��ľȨ��","�ַ���","F2","",false,255,0));
		// newFields.add(createLayerField("J_����","�ַ���","F2","",false,255,0));
		// newFields.add(createLayerField("J_����","�ַ���","F2","",false,255,0));
		// newFields.add(createLayerField("J_ֲ������","�ַ���","F2","",false,255,0));

		// newFields.add(createLayerField("�����","�ַ���","F30","",false,255,0));

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}
	}

	private v1_LayerField createLayerField(String fieldName, String fieldType, String dataFileName, String enumCode,
			boolean enumEidt, int size, int deciaml) {

		return createLayerField(fieldName, fieldType, dataFileName, enumCode, enumEidt, size, deciaml, "");
	}

	private v1_LayerField createLayerField(String fieldName, String fieldType, String dataFileName, String enumCode,
			boolean enumEidt, int size, int deciaml, String shortName) {
		v1_LayerField newFile = new v1_LayerField();
		newFile.SetFieldName(fieldName);
		newFile.SetFieldTypeName(fieldType);
		newFile.SetDataFieldName(dataFileName);
		newFile.SetFieldEnumCode(enumCode);
		newFile.SetFieldEnumEdit(enumEidt);
		newFile.SetFieldSize(size);
		newFile.SetFieldDecimal(deciaml);
		newFile.SetIsSelect(true);
		newFile.SetFieldShortName(shortName);
		return newFile;
	}

	// ���ð�ť��״̬
	private void SetButtonEnable(boolean enabled) {
		_Dialog.findViewById(R.id.pln_edit).setEnabled(enabled);
		_Dialog.findViewById(R.id.pln_delete).setEnabled(enabled);
		((TextView) _Dialog.findViewById(R.id.tv_edit)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
		((TextView) _Dialog.findViewById(R.id.tv_delete)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			DoCommand(Tag);
		}
	}

	// ��ť�¼�
	private void DoCommand(String StrCommand) {
		this.m_EditLayer.SetLayerProjectType(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype));

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

		}
	};

	// ����ͼ���Ļص�
	private ICallback m_Callback = null;

	/**
	 * ����ͼ���Ļص�
	 * 
	 * @param cb
	 */
	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	// ���е�ͼ���б�
	private List<v1_Layer> m_HaveLayerList = null;

	public void SetHaveLayerList(List<v1_Layer> haveLayerList) {
		this.m_HaveLayerList = haveLayerList;
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
	}

	/**
	 * ����ͼ����Ϣ
	 * 
	 * @return
	 */
	private boolean SaveLayerInfo() {
		// ��ȡͼ����Ϣ
		String LayerName = Tools.GetTextValueOnID(_Dialog, R.id.et_name); // ͼ������
		String LayerType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_type); // ͼ������
		String layerProjectType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype);// ��ҵ��������

		// ��֤ͼ����Ϣ
		String ErrorInfo = "";
		if (LayerName.equals(""))
			ErrorInfo += "��ͼ�����ơ�������Ϊ��ֵ��\r\n";
		for (v1_Layer lyr : this.m_HaveLayerList) {
			if (lyr.GetLayerAliasName().equals(LayerName) && (!lyr.GetLayerID().equals(this.m_EditLayer.GetLayerID())))
				ErrorInfo += "��ͼ�����ơ��������ظ���\r\n";
		}
		if (this.m_EditLayer.GetFieldList().size() == 0) {
			ErrorInfo += "���ֶΡ�����������Ϊ0����\r\n";
		} else {
			try {
				int i = 0;
				for (v1_LayerField field : this.m_EditLayer.GetFieldList()) {
					field.SetIsSelect(Boolean.parseBoolean(dataList.get(i).get("D1") + ""));
				}
			} catch (Exception ex) {
				Tools.ShowMessageBox("�����ֶ��Ƿ���ʾ����" + ex.getMessage());
			}

		}

		if (!ErrorInfo.equals("")) {
			Tools.ShowMessageBox(_Dialog.getContext(), ErrorInfo);
			return false;
		}

		// ����
		this.m_EditLayer.SetLayerAliasName(LayerName);
		this.m_EditLayer.SetLayerTypeName(LayerType);
		this.m_EditLayer.SetLayerProjectType(layerProjectType);
		if ((this.m_EditLayer.GetEditMode() == lkEditMode.enNew)) {
			// ����ͼ����������Ĭ�Ϸ���
			if (this.m_EditLayer.GetLayerType() == lkGeoLayerType.enPoint) {
				PointSymbol PS = new PointSymbol();
				this.m_EditLayer.SetSimpleSymbol(PS.ToBase64());
			}
			if (this.m_EditLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
				LineSymbol LS = new LineSymbol();
				this.m_EditLayer.SetSimpleSymbol(LS.ToBase64());
			}
			if (this.m_EditLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
				PolySymbol PS = new PolySymbol();
				this.m_EditLayer.SetSimpleSymbol(PS.ToBase64());
				this.m_EditLayer.SetTransparent(125);
			}

			this.m_EditLayer.SetRenderType(lkRenderType.enSimple);

			m_EditLayer.setCity(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds));
			m_EditLayer.setCounty(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgqx));
			YearPicker datepicker = (YearPicker) _Dialog.findViewById(R.id.yearPicker);
			m_EditLayer.setYear(datepicker.getYear());

			// if(this.m_EditLayer.GetLayerProjecType().contains("�˸�����"))
			// {
			// m_EditLayer.setCity(Tools.GetSpinnerValueOnID(_Dialog,
			// R.id.sp_tgds));
			// m_EditLayer.setCounty(Tools.GetSpinnerValueOnID(_Dialog,
			// R.id.sp_tgqx));
			// YearPicker datepicker =
			// (YearPicker)_Dialog.findViewById(R.id.yearPicker);
			// m_EditLayer.setYear(datepicker.getYear());
			// }
			// else
			// {
			// if(this.m_EditLayer.GetLayerProjecType().equals(ForestryLayerType.LinyeErdiao))
			// {
			// m_EditLayer.setCity(Tools.GetSpinnerValueOnID(_Dialog,
			// R.id.sp_tgds));
			// m_EditLayer.setCounty(Tools.GetSpinnerValueOnID(_Dialog,
			// R.id.sp_tgqx));
			// YearPicker datepicker =
			// (YearPicker)_Dialog.findViewById(R.id.yearPicker);
			// m_EditLayer.setYear(datepicker.getYear());
			// }
			// }

			if (this.m_EditLayer.GetLayerProjecType().equals(ForestryLayerType.WeipianJianchaLayer)) {
				if (listWPSJLayerIDs.size() > 0) {
					Spinner spWPXF = (Spinner) _Dialog.findViewById(R.id.sp_wpsjxf);
					m_EditLayer.setWeipianDataLayer(listWPSJLayerIDs.get(spWPXF.getSelectedItemPosition()));
				}
			}

		} else {
			this.m_EditLayer.SetEditMode(lkEditMode.enEdit);
		}

		return true;
	}

	/**
	 * ����ͼ�������Ϣ
	 */
	List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

	@SuppressLint("NewApi")
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
			// _Dialog.findViewById(R.id.sp_type).setEnabled(false);
			// _Dialog.findViewById(R.id.sp_projecttype).setEnabled(false);
			// ͼ������
			if (!this.m_EditLayer.GetLayerProjecType().isEmpty()) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_projecttype, this.m_EditLayer.GetLayerProjecType());
				// if(this.m_EditLayer.GetLayerProjecType().contains("̼��"))
				// {
				// _Dialog.findViewById(R.id.btnExportTanhui).setVisibility(View.VISIBLE);
				// }
			}

		} else {
			// ����ͼ��
			this.m_EditLayer = new v1_Layer();
			this.m_EditLayer.SetEditMode(lkEditMode.enNew);
		}

		this.SetButtonEnable(false);

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
