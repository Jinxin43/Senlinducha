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
	// 当前工程 名称
	private String _ProjectName = "";

	public LayerManagerCreateDataLayer() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.d_datalayer_new);
		// _Dialog.ReSetSize(1f, 0.95f);
		_Dialog.ReSetSize(0.6f, 0.9f);
		// 工程名称
		this._ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;

		// 初始显示及按钮
		_Dialog.SetCaption("【" + this._ProjectName + "】" + Tools.ToLocale("图层"));
		_Dialog.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("确定") + "  ,确定", pCallback);

		// 新增字段，字段属性，删除字段按钮
		_Dialog.findViewById(R.id.pln_add).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_edit).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_delete).setOnClickListener(new ViewClick());

		// 多语言支持
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

		v1_DataBind.SetBindListSpinner(_Dialog, "工程类型", listProjecTypes, R.id.sp_projecttype);

		// 绑定图层类型列表
		v1_DataBind.SetBindListSpinner(_Dialog, "图层类型", Tools.StrArrayToList(new String[] { "面", "线", "点" }),
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

	// 默认图层类型
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

			if (currentProjectType.equals(ForestryLayerType.DefaultLayer))// 自定义图层
			{
				addDefaultField();
			}

			if (currentProjectType.equals(ForestryLayerType.DuChaYanZheng))// 自定义图层
			{
				addDuChaYanZhengFields();
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "面");
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
				((TextView) _Dialog.findViewById(R.id.tvLocaleText5)).setText("图层信息");

			}

			if (currentProjectType.equals(ForestryLayerType.TanhuiLayer))// 碳汇-每木检尺
			{
				AddMeiMuJianChiFields();
			}

			if (currentProjectType.equals(ForestryLayerType.XiaoBanXuji)) {
				AddXiaoBanXujiFields();
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "点");
			}

			if (currentProjectType.equals(ForestryLayerType.LindibiangengLayer))// 林地变更
			{
				addLinDiBianGengField();
			}

			if (currentProjectType.equals(ForestryLayerType.WeipianJianchaLayer)) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "面");
				addZhiFaJianchaField();
				setWeipianxiafaLayer();
				_Dialog.findViewById(R.id.ll_weipiandataselect).setVisibility(View.VISIBLE);
			}

			// 卫片执法-数据下发
			if (currentProjectType.equals(ForestryLayerType.WeipianShujuLayer)) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "面");
				addZhiFaShujuFields();
			}

			if (currentProjectType.equals(ForestryLayerType.TuigengLayer)) {
				addTuiGengField();
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, "面");
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
//		m_EditLayer.GetFieldList().add(createLayerField("省", "字符串", "F2", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("县", "字符串", "F3", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("乡镇", "字符串", "F4", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("村", "字符串", "F5", "", false, 30, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("图斑号", "整形", "F6", "", false, 5, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("横坐标", "浮点型", "F7", "", false, 12, 3, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("纵坐标", "浮点型", "F8", "", false, 12, 3, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("判读面积", "浮点型", "F9", "", false, 18, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("前期时间", "字符串", "F10", "", false, 8, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("后期时间", "字符串", "F11", "", false, 8, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("备注", "字符串", "F12", "", false, 255, 0, ""));
//
//		m_EditLayer.GetFieldList().add(createLayerField("前地类", "整形", "F13", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("现地类", "整形", "F14", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("重点生态区域名称", "字符串", "F15", "", false, 64, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("改变面积", "浮点型", "F16", "", false, 18, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("违规改变面积", "浮点型", "F17", "", false, 18, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("采伐蓄积", "浮点型", "F18", "", false, 12, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("违规采伐蓄积", "浮点型", "F19", "", false, 12, 4, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("变化原因", "整形", "F20", "", false, 2, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("检查级别", "整形", "F21", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("结果是否一致", "整形", "F22", "", false, 1, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("检查备注", "字符串", "F23", "", false, 255, 0, ""));
//
//		m_EditLayer.GetFieldList().add(createLayerField("检查单位名称", "字符串", "F24", "", false, 64, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("检查人员", "字符串", "F25", "", false, 64, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("检查日期", "字符串", "F26", "", false, 8, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("调查年度", "字符串", "F27", "", false, 4, 0, ""));
//		m_EditLayer.GetFieldList().add(createLayerField("是否违法违规", "整形", "F28", "", false, 1, 0, ""));
		
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();
		newFields.add(createLayerField("顺序号", "整型", "F1", "", false, 8, 0, "Id"));
		newFields.add(createLayerField("判读图斑编号", "整型", "F2", "", false, 6, 0, "PAN_NO_TB"));
		newFields.add(createLayerField("省", "字符串", "F3", "", false, 2, 0, "SHENG"));
		newFields.add(createLayerField("县", "字符串", "F4", "", false, 40, 0, "XIAN"));
		newFields.add(createLayerField("乡镇", "字符串", "F5", "", false, 40, 0, "XIANG"));
		newFields.add(createLayerField("村", "字符串", "F6", "", false, 40, 0, "CUN"));
		newFields.add(createLayerField("林业局", "字符串", "F7", "", false, 40, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("林场", "字符串", "F8", "", false, 40, 0, "LIN_CHANG"));
		newFields.add(createLayerField("林班", "字符串", "F9", "", false, 40, 0, "LIN_BAN"));
		newFields.add(createLayerField("横坐标", "整型", "F10", "", true, 8, 0, "GPS_X"));
		newFields.add(createLayerField("纵坐标", "整型", "F11", "", true, 7, 0, "GPS_Y"));
		newFields.add(createLayerField("判读面积", "浮点型", "F12", "", true, 18, 4, "MIAN_JI"));
		newFields.add(createLayerField("判读变化原因", "整形", "F13", "", false, 2, 0, "PAN_BHYY"));
		newFields.add(createLayerField("判读地类", "整形", "F14", "", false, 5, 0, "PAN_DILEI"));
		newFields.add(createLayerField("核实细斑号", "字符串", "F15", "", false, 12, 0, "HSXBH"));
		newFields.add(createLayerField("林地管理单位", "字符串", "F16", "", false, 40, 0, "LDGLDW"));
		newFields.add(createLayerField("变化原因", "整形", "F17", "", false, 3, 0, "BHYY"));
		newFields.add(createLayerField("判读备注", "字符串", "F18", "", false, 250, 0, "BEIZHU"));
		newFields.add(createLayerField("违法违规", "布尔型", "F51", "", false, 1, 0, "SHIFOU_WF"));

		newFields.add(createLayerField("前地类", "字符串", "F19", "", false, 4, 0, "hs_qdl"));
		newFields.add(createLayerField("现状地类", "字符串", "F20", "", false, 4, 0, "hs_xzdl"));

		newFields.add(createLayerField("项目名称", "字符串", "F21", "", false, 100, 0, "XMMC"));
		newFields.add(createLayerField("审核文号", "字符串", "F22", "", false, 50, 0, "SH_WH"));
		newFields.add(createLayerField("审核年度", "字符串", "F23", "", false, 4, 0, "SH_ND"));
		newFields.add(createLayerField("审核面积", "浮点型", "F24", "", false, 10, 4, "SH_MJ"));
		newFields.add(createLayerField("实际改变林地用途面积", "浮点型", "F25", "", false, 10, 4, "SJ_MJ"));
		newFields.add(createLayerField("违规违法改变林地用途面积", "浮点型", "F26", "", false, 10, 4, "WF_MJ"));
		newFields.add(createLayerField("违规违法中自然保护地面积", "浮点型", "F27", "", false, 10, 4, "WF_ZRBHD_MJ"));
		newFields.add(createLayerField("自然保护地名称", "字符串", "F28", "", false, 60, 0, "WF_ZRBHD_MC"));
		newFields.add(createLayerField("自然保护地级别", "整形", "F29", "", false, 2, 0, "WF_ZRBHD_JB"));

		newFields.add(createLayerField("违规违法中乔木林地面积", "浮点型", "F30", "", false, 10, 4, "WF_MJ_QM"));
		newFields.add(createLayerField("违规违法中竹林面积", "浮点型", "F31", "", false, 10, 4, "WF_MJ_ZL"));
		newFields.add(createLayerField("违规违法中红树林面积", "浮点型", "F32", "", false, 10, 4, "WF_MJ_HSL"));
		newFields.add(createLayerField("违规违法中国家特灌林面积", "浮点型", "F33", "", false, 10, 4, "WF_MJ_TM"));
		newFields.add(createLayerField("违规违法中其他灌木林面积", "浮点型", "F34", "", false, 10, 4, "WF_MJ_GM"));
		newFields.add(createLayerField("违规违法中其他林地面积", "浮点型", "F35", "", false, 10, 4, "WF_MJ_QT"));
		newFields.add(createLayerField("违规违法中一级国家公益林面积", "浮点型", "F36", "", false, 10, 4, "WF_MJ_YJGYL"));
		newFields.add(createLayerField("违规违法中二级国家公益林面积", "浮点型", "F37", "", false, 10, 4, "WF_MJ_EJGYL"));
		newFields.add(createLayerField("违规违法中地方公益林面积", "浮点型", "F38", "", false, 10, 4, "WF_MJ_DFGYL"));
		newFields.add(createLayerField("违规违法中商品林面积", "浮点型", "F39", "", false, 10, 4, "WF_MJ_SPL"));
		newFields.add(createLayerField("使用林地性质", "整形", "F40", "", false, 1, 0, "SYLDXZ"));
		newFields.add(createLayerField("林木采伐许可证号", "字符串", "F41", "", false, 100, 0, "CFZH"));
		newFields.add(createLayerField("发证面积", "浮点型", "F42", "", false, 10, 4, "FZMJ"));
		newFields.add(createLayerField("发证蓄积", "浮点型", "F43", "", false, 10, 1, "FZXJ"));
		newFields.add(createLayerField("凭证采伐面积", "浮点型", "F44", "", false, 10, 4, "PZMJ"));
		newFields.add(createLayerField("凭证采伐蓄积", "浮点型", "F45", "", false, 10, 1, "PZXJ"));
		newFields.add(createLayerField("超证采伐面积", "浮点型", "F46", "", false, 10, 4, "CZMJ"));
		newFields.add(createLayerField("超证采伐蓄积", "浮点型", "F47", "", false, 10, 1, "CZXJ"));
		newFields.add(createLayerField("无证采伐面积", "浮点型", "F48", "", false, 10, 4, "WZMJ"));
		newFields.add(createLayerField("无证采伐蓄积", "浮点型", "F49", "", false, 10, 1, "WZXJ"));
		newFields.add(createLayerField("备注", "字符串", "F50", "", false, 250, 0, "BEIZHU2"));

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}

	}

	private void addTianBaoFengyuFields() {
		m_EditLayer.GetFieldList().add(createLayerField("县名称", "字符串", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("县代码", "字符串", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乡", "字符串", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("村", "字符串", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林班", "整形", "F5", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班", "整形", "F6", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("复合小班", "整形", "F7", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班位置", "字符串", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图符号", "字符串", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班面积(亩)", "浮点型", "F10", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班面积(公顷)", "浮点型", "F11", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林木所有权", "字符串", "F12", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("起源", "字符串", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林木所有权", "字符串", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林种", "字符串", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("地类", "字符串", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("海拔", "浮点型", "F17", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡度", "字符串", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡向", "字符串", "F19", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("幼树更新频度", "字符串", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均年龄", "字符串", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("生长状况", "字符串", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("生长状况", "字符串", "F23", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("土壤类型", "字符串", "F24", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤厚度", "字符串", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤碳酸盐", "字符串", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤PH值", "字符串", "F27", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("灌木优势种类", "字符串", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木盖度", "字符串", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木高度", "浮点型", "F30", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木分布", "字符串", "F31", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("草本优势种类", "字符串", "F32", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本盖度", "字符串", "F33", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本高度", "浮点型", "F34", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本分布", "字符串", "F35", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("珍稀物种", "字符串", "F36", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("抚育次数", "整形", "F37", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("总用工量", "浮点型", "F38", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("抚育措施", "字符串", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("物质材料费", "浮点型", "F40", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班总投资", "浮点型", "F41", "", false, 15, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("封育类型", "字符串", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("培育树种", "字符串", "F43", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("封育年限", "整形", "F44", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("有效封育", "字符串", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("有效面积", "字符串", "F46", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("目的树种", "字符串", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("封育年限", "整形", "F48", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("低质低效", "字符串", "F49", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("林龄", "浮点型", "F50", "", false, 8, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("郁闭度", "浮点型", "F51", "", false, 8, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均胸径", "浮点型", "F52", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均树高", "浮点型", "F53", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("株数", "整形", "F54", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("蓄积", "浮点型", "F55", "", false, 12, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木草本盖度", "浮点型", "F56", "", false, 12, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("分蘖丛数", "整形", "F57", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("分蘖割除数", "整形", "F58", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("萌蘖丛数", "整形", "F59", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("萌蘖间隔", "整形", "F60", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("枯死木株数", "整形", "F61", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("保留目的树", "整形", "F62", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("采伐株数", "整形", "F63", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("采伐强度", "整形", "F64", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("间伐蓄积", "整形", "F65", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("修枝株数", "整形", "F66", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("割灌除草强度", "整形", "F67", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("设计人工费", "整形", "F68", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计福利费", "整形", "F69", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计劳保费", "整形", "F70", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计补助费", "整形", "F71", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他设计费", "整形", "F72", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地器具费", "整形", "F73", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地人工费", "整形", "F74", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("种子费", "整形", "F75", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("苗木费", "整形", "F76", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林器具费", "整形", "F77", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林人工费", "整形", "F78", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林通勤费", "整形", "F79", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林通勤费", "整形", "F80", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植种子费", "整形", "F81", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植苗木费", "整形", "F82", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植器具费", "整形", "F83", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植人工费", "整形", "F84", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植通勤费", "整形", "F85", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("抚育人工费", "整形", "F86", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("管护费", "整形", "F87", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("管理费", "整形", "F88", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他直接费", "整形", "F89", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("苗圃设施费", "整形", "F90", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("防火设施费", "整形", "F91", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("营林道路", "整形", "F92", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("科技支撑", "整形", "F93", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("病虫害防治", "整形", "F94", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他成本", "整形", "F95", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("设计时间", "字符串", "F96", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计人", "字符串", "F97", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("数据标识", "字符串", "F98", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("唯一值", "字符串", "F99", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注1", "字符串", "F100", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注2", "字符串", "F101", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注3", "字符串", "F102", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注4", "字符串", "F103", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注5", "字符串", "F104", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图层类型", "字符串", "F105", "", false, 4, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图形长度", "浮点型", "F106", "", false, 15, 4, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图形面积", "浮点型", "F107", "", false, 15, 4, ""));
	}

	private void addTianBaoFuYuFields() {
		m_EditLayer.GetFieldList().add(createLayerField("县名称", "字符串", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("县代码", "字符串", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乡", "字符串", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("村", "字符串", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林班", "整形", "F5", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班", "整形", "F6", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("复合小班", "整形", "F7", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班位置", "字符串", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图符号", "字符串", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班面积(亩)", "浮点型", "F10", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班面积(公顷)", "浮点型", "F11", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林木所有权", "字符串", "F12", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("起源", "字符串", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林木所有权", "字符串", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林种", "字符串", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("地类", "字符串", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("海拔", "浮点型", "F17", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡度", "字符串", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡向", "字符串", "F19", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("幼树更新频度", "字符串", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均年龄", "字符串", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("生长状况", "字符串", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("生长状况", "字符串", "F23", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("土壤类型", "字符串", "F24", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤厚度", "字符串", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤碳酸盐", "字符串", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤PH值", "字符串", "F27", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("灌木优势种类", "字符串", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木盖度", "字符串", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木高度", "浮点型", "F30", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木分布", "字符串", "F31", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("草本优势种类", "字符串", "F32", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本盖度", "字符串", "F33", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本高度", "浮点型", "F34", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本分布", "字符串", "F35", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("珍稀物种", "字符串", "F36", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("抚育次数", "整形", "F37", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("总用工量", "浮点型", "F38", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("抚育措施", "字符串", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("物质材料费", "浮点型", "F40", "", false, 15, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班总投资", "浮点型", "F41", "", false, 15, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("封育类型", "字符串", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("培育树种", "字符串", "F43", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("封育年限", "整形", "F44", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("有效封育", "字符串", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("有效面积", "字符串", "F46", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("目的树种", "字符串", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("封育年限", "整形", "F48", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("低质低效", "字符串", "F49", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("林龄", "浮点型", "F50", "", false, 8, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("郁闭度", "浮点型", "F51", "", false, 8, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均胸径", "浮点型", "F52", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均树高", "浮点型", "F53", "", false, 6, 1, ""));
		m_EditLayer.GetFieldList().add(createLayerField("株数", "整形", "F54", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("蓄积", "浮点型", "F55", "", false, 12, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木草本盖度", "浮点型", "F56", "", false, 12, 3, ""));

		m_EditLayer.GetFieldList().add(createLayerField("分蘖丛数", "整形", "F57", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("分蘖割除数", "整形", "F58", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("萌蘖丛数", "整形", "F59", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("萌蘖间隔", "整形", "F60", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("枯死木株数", "整形", "F61", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("保留目的树", "整形", "F62", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("采伐株数", "整形", "F63", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("采伐强度", "整形", "F64", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("间伐蓄积", "整形", "F65", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("修枝株数", "整形", "F66", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("割灌除草强度", "整形", "F67", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("设计人工费", "整形", "F68", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计福利费", "整形", "F69", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计劳保费", "整形", "F70", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计补助费", "整形", "F71", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他设计费", "整形", "F72", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地器具费", "整形", "F73", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地人工费", "整形", "F74", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("种子费", "整形", "F75", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("苗木费", "整形", "F76", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林器具费", "整形", "F77", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林人工费", "整形", "F78", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林通勤费", "整形", "F79", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林通勤费", "整形", "F80", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植种子费", "整形", "F81", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植苗木费", "整形", "F82", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植器具费", "整形", "F83", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植人工费", "整形", "F84", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植通勤费", "整形", "F85", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("抚育人工费", "整形", "F86", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("管护费", "整形", "F87", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("管理费", "整形", "F88", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他直接费", "整形", "F89", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("苗圃设施费", "整形", "F90", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("防火设施费", "整形", "F91", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("营林道路", "整形", "F92", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("科技支撑", "整形", "F93", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("病虫害防治", "整形", "F94", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他成本", "整形", "F95", "", false, 8, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("设计时间", "字符串", "F96", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计人", "字符串", "F97", "", false, 28, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("数据标识", "字符串", "F98", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("唯一值", "字符串", "F99", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注1", "字符串", "F100", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注2", "字符串", "F101", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注3", "字符串", "F102", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注4", "字符串", "F103", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注5", "字符串", "F104", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图层类型", "字符串", "F105", "", false, 4, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图形长度", "浮点型", "F106", "", false, 15, 4, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图形面积", "浮点型", "F107", "", false, 15, 4, ""));
	}

	private void addTianBaoZaoLinFields() {
		m_EditLayer.GetFieldList().add(createLayerField("县名称", "字符串", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("县代码", "字符串", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乡", "字符串", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("村", "字符串", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林班", "整形", "F5", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班", "整形", "F6", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("复合小班", "整形", "F7", "", false, 12, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班位置", "字符串", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图符号", "字符串", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班面积(亩)", "浮点型", "F10", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班面积(公顷)", "浮点型", "F11", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林木所有权", "字符串", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("地类", "字符串", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("海拔", "浮点型", "F14", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡度", "字符串", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡向", "字符串", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤名称", "字符串", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤厚度", "字符串", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤碳酸盐", "字符串", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("土壤PH值", "字符串", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("总盖度", "字符串", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乔木树种", "字符串", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乔木郁闭度", "字符串", "F23", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乔木公顷株数", "字符串", "F24", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乔木公顷幼苗株数", "字符串", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木优势种类", "字符串", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木盖度", "字符串", "F27", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本优势种类", "字符串", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本盖度", "字符串", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("立地类型", "字符串", "F30", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林类型", "字符串", "F31", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林种", "字符串", "F32", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("主造树种", "字符串", "F33", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("混交树种", "字符串", "F34", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("混交比例", "字符串", "F35", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("混交方式", "字符串", "F36", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("株行距", "字符串", "F37", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林时间", "字符串", "F38", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("初植密度", "字符串", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地时间", "字符串", "F40", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地方式", "字符串", "F41", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("栽植时间", "字符串", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("栽植方式", "字符串", "F43", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("栽植方式", "字符串", "F44", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班用苗量(含补植)", "字符串", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("苗木(种子)等级", "字符串", "F46", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计时间", "字符串", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计人", "字符串", "F48", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计人工费", "整形", "F49", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计福利费", "整形", "F50", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计劳保费", "整形", "F51", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("设计补助费", "整形", "F52", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他设计费", "整形", "F53", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地器具费", "整形", "F54", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("整地人工费", "整形", "F55", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("种子费", "整形", "F56", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("苗木费", "整形", "F57", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林器具费", "整形", "F58", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林人工费", "整形", "F59", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林通勤费", "整形", "F60", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("造林通勤费", "整形", "F61", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植种子费", "整形", "F62", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植苗木费", "整形", "F63", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植器具费", "整形", "F64", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植人工费", "整形", "F65", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("补植通勤费", "整形", "F66", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("抚育人工费", "整形", "F67", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("管护费", "整形", "F68", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("管理费", "整形", "F69", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他直接费", "整形", "F70", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("苗圃设施费", "整形", "F71", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("防火设施费", "整形", "F72", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("营林道路", "整形", "F73", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("科技支撑", "整形", "F74", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("病虫害防治", "整形", "F75", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他成本", "整形", "F76", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("数据标识", "整形", "F77", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("起源", "字符串", "F78", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("更新频度", "字符串", "F79", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("更新频度", "字符串", "F80", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均年龄", "整形", "F81", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("生长状况", "整形", "F82", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木高度", "浮点型", "F83", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("灌木分布", "字符串", "F84", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本高度", "浮点型", "F85", "", false, 12, 3, ""));
		m_EditLayer.GetFieldList().add(createLayerField("草本分布", "字符串", "F86", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("珍稀物种", "字符串", "F87", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("分蘖丛数", "整形", "F88", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("分蘖割除数", "整形", "F89", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("萌蘖丛数", "整形", "F90", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("萌蘖间隔", "整形", "F91", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("枯死木株数", "整形", "F92", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("保留目的树", "整形", "F93", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("采伐株数", "整形", "F94", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("采伐强度", "整形", "F95", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("间伐蓄积", "整形", "F96", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("修枝株数", "整形", "F97", "", false, 8, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("割灌强度", "整形", "F98", "", false, 8, 0, ""));

	}

	private void addZhiFaShujuFields() {
		m_EditLayer.GetFieldList().add(createLayerField("省", "字符串", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("地市", "字符串", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("县", "字符串", "F3", "", false, 255, 0, ""));

		// 图斑登记
		m_EditLayer.GetFieldList().add(createLayerField("图斑号", "字符串", "F6", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乡镇", "字符串", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("村", "字符串", "F5", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("横坐标", "字符串", "F7", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("纵坐标", "字符串", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("前地类", "字符串", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("现地类", "字符串", "F10", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("变化类型", "字符串", "F11", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("明显程度", "字符串", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("判读面积", "浮点型", "F13", "", false, 15, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("有无采伐", "字符串", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("是否审批", "字符串", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("是否掌握", "字符串", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林地依据", "字符串", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注", "字符串", "F18", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("判读人员", "字符串", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("登记日期", "字符串", "F20", "", false, 255, 0, ""));
	}

	private void addZhiFaJianchaField() {

		m_EditLayer.GetFieldList().add(createLayerField("省", "字符串", "F1", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("地市", "字符串", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("县", "字符串", "F3", "", false, 255, 0, ""));

		// 图斑登记
		m_EditLayer.GetFieldList().add(createLayerField("图斑号", "字符串", "F6", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乡镇", "字符串", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("村", "字符串", "F5", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("横坐标", "字符串", "F7", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("纵坐标", "字符串", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("前地类", "字符串", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("现地类", "字符串", "F10", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("变化类型", "字符串", "F11", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("明显程度", "字符串", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("判读面积", "浮点型", "F13", "", false, 15, 2, ""));
		m_EditLayer.GetFieldList().add(createLayerField("有无采伐", "字符串", "F14", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("是否审批", "字符串", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("是否掌握", "字符串", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林地依据", "字符串", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注", "字符串", "F18", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("判读人员", "字符串", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("登记日期", "字符串", "F20", "", false, 255, 0, ""));

		// //抽查验证-室内判读因子
		// m_EditLayer.GetFieldList().add(createLayerField("图班号2","字符串","F21","",false,255,0,"TuBan"));
		// m_EditLayer.GetFieldList().add(createLayerField("横坐标2","字符串","F22","",false,255,0,"MapX"));
		// m_EditLayer.GetFieldList().add(createLayerField("纵坐标2","字符串","F23","",false,255,0,"MapY"));
		// m_EditLayer.GetFieldList().add(createLayerField("前地类2","字符串","F24","地类",false,255,0,"Q_DI_LEI"));
		// m_EditLayer.GetFieldList().add(createLayerField("现地类2","字符串","F25","地类",false,255,0,"DI_LEI"));
		// m_EditLayer.GetFieldList().add(createLayerField("变化类型2","字符串","F26","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("明显程度2","字符串","F27","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("判读面积2","浮点型","F28","",false,10,2,""));
		// m_EditLayer.GetFieldList().add(createLayerField("有无采伐2","字符串","F29","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("是否审批2","字符串","F30","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("是否掌握2","字符串","F31","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("林地依据2","字符串","F32","",false,255,0,""));
		// m_EditLayer.GetFieldList().add(createLayerField("备注2","字符串","F33","",false,255,0,""));

		// 抽查验证-现地验证因子
		m_EditLayer.GetFieldList().add(createLayerField("横坐标3", "字符串", "F34", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("纵坐标3", "字符串", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("前地类3", "字符串", "F36", "地类", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("现地类3", "字符串", "F37", "地类", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("用途变化", "字符串", "F38", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("有无采伐3", "字符串", "F39", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("是否掌握3", "字符串", "F40", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("备注3", "字符串", "F41", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("调查者", "字符串", "F42", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("调查日期", "字符串", "F43", "", false, 255, 0, ""));

		// 调查记录
		m_EditLayer.GetFieldList().add(createLayerField("被调查单位", "字符串", "F44", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("被调查人", "字符串", "F45", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("调查记录", "字符串", "F46", "", false, 512, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("调查人", "字符串", "F47", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("记录日期", "字符串", "F48", "", false, 255, 0, ""));

		// 面积测量表另行记录
		// 是否可以增加测量总点数
		m_EditLayer.GetFieldList().add(createLayerField("其他测量点", "字符串", "F498", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("测量人", "字符串", "F450", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("测量时间", "字符串", "F51", "", false, 255, 0, ""));
	}

	private void AddXiaoBanXujiFields() {
		// m_EditLayer.GetFieldList().add(createLayerField("省","字符串","F1","",false,255,0,""));
		m_EditLayer.GetFieldList().add(createLayerField("地市", "字符串", "F2", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("县(局)", "字符串", "F3", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("乡镇(林场)", "字符串", "F4", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("行政村(营林区)", "字符串", "F5", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("林班(村民小组)", "字符串", "F6", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("小班", "字符串", "F7", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("图符号", "字符串", "F8", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡度", "字符串", "F9", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡位", "字符串", "F10", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("坡向", "字符串", "F11", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("观测点号", "字符串", "F12", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("X坐标", "字符串", "F13", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("Y坐标", "字符串", "F14", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("地类", "字符串", "F15", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("起源", "字符串", "F16", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("优势树种(组)", "字符串", "F17", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均年龄", "字符串", "F18", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("龄组", "字符串", "F19", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均胸径", "字符串", "F20", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("平均树高", "字符串", "F21", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("郁闭度", "字符串", "F22", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("其他记载", "字符串", "F23", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("每公顷蓄积量", "字符串", "F24", "", false, 255, 0, ""));

		m_EditLayer.GetFieldList().add(createLayerField("合计", "字符串", "F25", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("蓄积量", "字符串", "F26", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("调查人员", "字符串", "F27", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("调查日期", "字符串", "F28", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("检查人员", "字符串", "F29", "", false, 255, 0, ""));
		m_EditLayer.GetFieldList().add(createLayerField("检查日期", "字符串", "F30", "", false, 255, 0, ""));
	}

	private void AddMeiMuJianChiFields() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		v1_LayerField newFile1 = new v1_LayerField();
		newFile1.SetFieldName("样地号");
		newFile1.SetFieldTypeName("字符串");
		newFile1.SetFieldSize(50);
		newFile1.SetFieldDecimal(1);
		newFile1.SetFieldEnumCode("");
		newFile1.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile1);
		newFields.add(newFile1);

		v1_LayerField newFile2 = new v1_LayerField();
		newFile2.SetFieldName("小班号");
		newFile2.SetFieldTypeName("字符串");
		newFile2.SetFieldSize(50);
		newFile2.SetFieldDecimal(1);
		newFile2.SetFieldEnumCode("");
		newFile2.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile2);
		newFields.add(newFile2);

		v1_LayerField newFile3 = new v1_LayerField();
		newFile3.SetFieldName("标准地号");
		newFile3.SetFieldTypeName("字符串");
		newFile3.SetFieldSize(50);
		newFile3.SetFieldDecimal(1);
		newFile3.SetFieldEnumCode("");
		newFile3.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile3);
		newFields.add(newFile3);

		v1_LayerField newFile4 = new v1_LayerField();
		newFile4.SetFieldName("平均高");
		newFile4.SetFieldTypeName("浮点型");
		newFile4.SetFieldSize(50);
		newFile4.SetFieldDecimal(2);
		newFile4.SetFieldEnumCode("");
		newFile4.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile4);
		newFields.add(newFile4);

		v1_LayerField newFile5 = new v1_LayerField();
		newFile5.SetFieldName("株数");
		newFile5.SetFieldTypeName("整形");
		newFile5.SetFieldSize(50);
		newFile5.SetFieldDecimal(0);
		newFile5.SetFieldEnumCode("");
		newFile5.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile5);
		newFields.add(newFile5);

		v1_LayerField newFile6 = new v1_LayerField();
		newFile6.SetFieldName("样地蓄积");
		newFile6.SetFieldTypeName("浮点型");
		newFile6.SetFieldSize(50);
		newFile6.SetFieldDecimal(2);
		newFile6.SetFieldEnumCode("");
		newFile6.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile6);
		newFields.add(newFile6);

		v1_LayerField newFile7 = new v1_LayerField();
		newFile7.SetFieldName("小班层");
		newFile7.SetFieldTypeName("字符串");
		newFile7.SetFieldSize(510);
		newFile7.SetFieldDecimal(0);
		newFile7.SetFieldEnumCode("");
		newFile7.SetFieldEnumEdit(true);
		m_EditLayer.GetFieldList().add(newFile7);
		newFields.add(newFile7);

		for (int i = 1; i < 32; i++) {
			v1_LayerField newFile = new v1_LayerField();
			newFile.SetFieldName("每木检尺表" + i);
			newFile.SetFieldTypeName("字符串");
			newFile.SetFieldSize(255);
			newFile.SetFieldDecimal(0);
			newFile.SetFieldEnumCode("");
			newFile.SetFieldEnumEdit(true);
			m_EditLayer.GetFieldList().add(newFile);
			newFields.add(newFile);
		}

		// 自动获取对应空闲数据字段
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

		newFields.add(createLayerField("省", "字符串", "F1", "", false, 255, 0, "SHENG"));
		// newFields.add(createLayerField("市","字符串","F2","",false,255,0,"SHI"));
		newFields.add(createLayerField("县", "字符串", "F3", "", false, 255, 0, "XIAN"));
		newFields.add(createLayerField("乡", "字符串", "F4", "", false, 255, 0, "XIANG"));
		newFields.add(createLayerField("村", "字符串", "F5", "", false, 255, 0, "CUN"));
		// 基础因子

		newFields.add(createLayerField("林业局", "字符串", "F6", "", false, 255, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("林场", "字符串", "F7", "", false, 255, 0, "LIN_CHANG"));
		newFields.add(createLayerField("原林班号", "字符串", "F8", "", false, 255, 0, "Q_LIN_BAN"));
		newFields.add(createLayerField("林班号", "字符串", "F9", "", false, 255, 0, "LIN_BAN"));
		newFields.add(createLayerField("原小班号", "字符串", "F10", "", false, 255, 0, "Q_XIAO_BAN"));
		newFields.add(createLayerField("小班号", "字符串", "F11", "", false, 255, 0, "XIAO_BAN"));
		newFields.add(createLayerField("横坐标", "字符串", "F12", "", false, 255, 0));
		newFields.add(createLayerField("纵坐标", "字符串", "F13", "", false, 255, 0));

		newFields.add(createLayerField("地貌", "字符串", "F16", "地貌", false, 255, 0, "DI_MAO"));
		newFields.add(createLayerField("坡位", "字符串", "F17", "坡位", false, 255, 0, "PO_XIANG"));
		newFields.add(createLayerField("坡向", "字符串", "F18", "坡向", false, 255, 0, "PO_WEI"));
		newFields.add(createLayerField("坡度", "字符串", "F19", "坡度", false, 255, 0, "PO_DU"));
		newFields.add(createLayerField("交通区位", "字符串", "F20", "交通区位", false, 255, 0, "KE_JI_DU"));
		newFields.add(createLayerField("土壤类型(名称)", "字符串", "F21", "土壤类型(名称)", true, 255, 0, "TU_RANG_LX"));
		newFields.add(createLayerField("土层厚度", "整型", "F22", "", false, 8, 0, "TU_CENG_HD"));
		newFields.add(createLayerField("面积", "浮点型", "F23", "", false, 10, 2, "MIAN_JI"));
		newFields.add(createLayerField("前期土地权属", "字符串", "F30", "土地权属", false, 255, 0, "Q_LD_QS"));
		newFields.add(createLayerField("土地权属", "字符串", "F31", "土地权属", false, 255, 0, "LD_QS"));

		// 林地因子
		newFields.add(createLayerField("前期地类", "字符串", "F24", "地类", false, 255, 0, "Q_DI_LEI"));
		newFields.add(createLayerField("地类", "字符串", "F25", "地类", false, 255, 0, "DI_LEI"));
		newFields.add(createLayerField("前期林种", "字符串", "F28", "林种", false, 255, 0, "Q_L_Z"));
		newFields.add(createLayerField("林种", "字符串", "F29", "林种", false, 255, 0, "LIN_ZHONG"));
		newFields.add(createLayerField("起源", "字符串", "F47", "起源", false, 255, 0, "QI_YUAN"));
		newFields.add(createLayerField("前期森林类别", "字符串", "F35", "森林（林地）类别", false, 255, 0, "Q_SEN_LB"));
		newFields.add(createLayerField("森林类别", "字符串", "F36", "森林（林地）类别", false, 255, 0, "SEN_LIN_LB"));
		newFields.add(createLayerField("前期事权等级", "字符串", "F37", "事权等级", false, 255, 0, "Q_SQ_D"));
		newFields.add(createLayerField("事权等级", "字符串", "F38", "事权等级", false, 255, 0, "SHI_QUAN_D"));
		// newFields.add(createLayerField("公益林保护等级","字符串","F42","国家级公益林保护等级",false,255,0,"GYL_BHDJ"));
		newFields.add(createLayerField("国家公益林保护等级", "字符串", "F41", "国家级公益林保护等级", false, 255, 0, "GJGYL_BHDJ"));
		newFields.add(createLayerField("前期工程类别", "字符串", "F33", "工程类别", false, 255, 0, "Q_GC_LB"));
		newFields.add(createLayerField("工程类别", "字符串", "F34", "工程类别", false, 255, 0, "G_CHENG_LB"));
		newFields.add(createLayerField("龄组", "字符串", "F48", "龄组", false, 255, 0, "LING_ZU"));
		newFields.add(createLayerField("郁密度/覆盖度", "浮点型", "F49", "", false, 6, 2, "YU_BI_DU"));
		newFields.add(createLayerField("优势树种", "字符串", "F59", "树种", false, 255, 0, "YOU_SHI_SZ"));
		newFields.add(createLayerField("平均胸径", "浮点型", "F60", "", false, 6, 1, "PINGJUN_XJ"));
		newFields.add(createLayerField("每公顷蓄积(活立木)", "浮点型", "F54", "", false, 12, 2, "HUO_LMGQXJ"));
		newFields.add(createLayerField("每公顷株数", "整型", "F53", "", false, 255, 0, "MEI_GQ_ZS"));

		newFields.add(createLayerField("土地退化类型", "字符串", "F64", "土地退化类型", false, 8, 0, "TD_TH_LX"));
		newFields.add(createLayerField("灾害类型", "字符串", "F65", "灾害类型", false, 255, 0, "DISPE"));
		newFields.add(createLayerField("灾害等级", "字符串", "F66", "森林灾害等级", false, 255, 0, "DISASTER_C"));
		newFields.add(createLayerField("林地质量等级", "字符串", "F26", "林地质量等级", false, 255, 0, "ZL_DJ"));
		newFields.add(createLayerField("林带长度", "浮点型", "F14", "", false, 8, 1, "LD_ZD"));
		newFields.add(createLayerField("林带宽度", "浮点型", "F15", "", false, 8, 1, "LD_KD"));

		newFields.add(createLayerField("是否为补充林地", "字符串", "F67", "是否为补充林地", false, 8, 0, "BCLD"));
		newFields.add(createLayerField("前期林地保护等级", "字符串", "F39", "林地保护等级", false, 255, 0, "Q_BH_DJ"));
		newFields.add(createLayerField("林地保护等级", "字符串", "F40", "林地保护等级", false, 255, 0, "BH_DJ"));
		newFields.add(createLayerField("林地功能分区", "字符串", "F63", "", false, 8, 0, "LYFQ"));
		newFields.add(createLayerField("主体功能区", "字符串", "F62", "主体功能区", false, 8, 0, "QYKZ"));
		newFields.add(createLayerField("变化原因", "字符串", "F45", "林地变化原因", false, 255, 0, "BHYY"));
		newFields.add(createLayerField("变化年度", "字符串", "F46", "", false, 255, 0, "BHND"));
		newFields.add(createLayerField("林地管理类型", "字符串", "F44", "林地管理类型", false, 255, 0, "GLLX"));
		newFields.add(createLayerField("说明", "字符串", "F70", "", false, 255, 0, "REMARKS"));

		// newFields.add(createLayerField("小班蓄积量","浮点型","F50","",false,10,1,"XIAOBAN_XJ"));
		// newFields.add(createLayerField("四旁树株数","整型","F51","",false,255,0,"SPS_ZS"));
		// newFields.add(createLayerField("四旁树蓄积量","浮点型","F52","",false,10,1,"SPS_XJ"));
		// //管理因子
		// newFields.add(createLayerField("生态区位","字符串","F27","",false,255,0,"SH_TAI_QW"));
		// newFields.add(createLayerField("林木权属","字符串","F32","林木权属",false,255,0,"LM_SY"));

		// newFields.add(createLayerField("散生木株数","整型","F55","",false,255,0,"SSM_ZS"));
		// newFields.add(createLayerField("散生木蓄积","浮点型","F57","",false,15,3,"SSM_XJ"));
		// newFields.add(createLayerField("散生竹株数","整型","F56","",false,12,0,"SSZ_ZS"));
		// newFields.add(createLayerField("小班蓄积调查方式","字符串","F58","小班蓄积调查方式",false,8,0,"DIAOCHA_FS"));
		// newFields.add(createLayerField("调查日期","日期型","F68","",false,8,0,"DIAOCHA_RQ"));
		// newFields.add(createLayerField("调查人","字符串","F69","",false,255,0,"DC_REN"));

		// 新增四个字段 数据类型 等级 名称 备注

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}
	}

	private void addErDiaoField() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		newFields.add(createLayerField("省", "字符串", "F1", "", false, 32, 0, "SHENG"));
		newFields.add(createLayerField("市", "字符串", "F2", "", false, 32, 0, "SHI"));
		newFields.add(createLayerField("县", "字符串", "F3", "", false, 32, 0, "XIAN"));
		newFields.add(createLayerField("乡", "字符串", "F4", "", false, 32, 0, "XIANG"));
		newFields.add(createLayerField("村", "字符串", "F5", "", false, 32, 0, "CUN"));
		newFields.add(createLayerField("林业局", "字符串", "F6", "", false, 32, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("林场", "字符串", "F7", "", false, 32, 0, "LIN_CHANG"));
		newFields.add(createLayerField("原林班", "字符串", "F8", "", false, 4, 0, "Q_LIN_BAN"));
		newFields.add(createLayerField("林班", "字符串", "F9", "", false, 4, 0, "LIN_BAN"));
		newFields.add(createLayerField("原小班", "字符串", "F10", "", false, 5, 0, "Q_XIAO_BAN"));
		newFields.add(createLayerField("小班", "字符串", "F11", "", false, 5, 0, "XIAO_BAN"));
		newFields.add(createLayerField("横坐标", "浮点型", "F12", "", false, 16, 3));
		newFields.add(createLayerField("纵坐标", "浮点型", "F13", "", false, 16, 3));

		newFields.add(createLayerField("海拔", "整形", "F15", "", false, 4, 0, "DI_MAO"));
		newFields.add(createLayerField("地貌", "字符串", "F16", "地貌", false, 255, 0, "DI_MAO"));
		newFields.add(createLayerField("坡位", "字符串", "F17", "坡位", false, 255, 0, "PO_WEI"));
		newFields.add(createLayerField("坡向", "字符串", "F18", "坡向", false, 255, 0, "PO_XIANG"));
		newFields.add(createLayerField("坡度", "整形", "F19", "坡度", false, 255, 0, "PO_DU"));
		newFields.add(createLayerField("交通区位", "字符串", "F20", "交通区位", false, 6, 0, "KE_JI_DU"));
		newFields.add(createLayerField("土壤类型", "字符串", "F21", "土壤类型", true, 20, 0, "TU_RANG_LX"));
		newFields.add(createLayerField("土层厚度", "整型", "F22", "", false, 3, 0, "TU_CENG_HD"));
		newFields.add(createLayerField("面积", "浮点型", "F23", "", false, 18, 2, "MIAN_JI"));
		newFields.add(createLayerField("幼树", "字符串", "F24", "", false, 6, 0, "YOU_SHU"));
		newFields.add(createLayerField("幼树公顷株数", "整形", "F25", "", false, 4, 0, "YS_GQ_ZS"));
		newFields.add(createLayerField("更新等级", "字符串", "F26", "", false, 1, 0, "GX_DJ"));
		newFields.add(createLayerField("下木种类", "字符串", "F27", "", false, 6, 0, "XM_ZL"));
		newFields.add(createLayerField("下木盖度", "整形", "F28", "", false, 2, 0, "XM_GD"));
		newFields.add(createLayerField("立地类型", "字符串", "F29", "", false, 6, 0, "LD_LX"));
		newFields.add(createLayerField("林层", "字符串", "F30", "", false, 8, 0, "LIN_CENG"));
		newFields.add(createLayerField("群落结构", "字符串", "F31", "", false, 16, 0, "QL_JG"));
		newFields.add(createLayerField("自然度", "字符串", "F32", "", false, 16, 0, "LIN_CENG"));
		newFields.add(createLayerField("生态区位", "字符串", "F33", "生态区位", false, 3, 0, "SH_TAI_QW"));

		newFields.add(createLayerField("前期土地权属", "字符串", "F35", "土地权属", false, 2, 0, "Q_LD_QS"));
		newFields.add(createLayerField("土地权属", "字符串", "F36", "土地权属", false, 2, 0, "LD_QS"));
		newFields.add(createLayerField("土地使用权", "字符串", "F37", "土地使用权", false, 2, 0, "LD_SY_QS"));
		newFields.add(createLayerField("林木权属", "字符串", "F38", "林木权属", false, 2, 0, "LM_QS"));
		newFields.add(createLayerField("林木使用权", "字符串", "F39", "林木使用权", false, 2, 0, "LM_SY_QS"));

		newFields.add(createLayerField("前期地类", "字符串", "F40", "地类", false, 4, 0, "Q_DI_LEI"));
		newFields.add(createLayerField("地类", "字符串", "F41", "地类", false, 4, 0, "DI_LEI"));
		newFields.add(createLayerField("前期林种", "字符串", "F42", "林种", false, 3, 0, "Q_L_Z"));
		newFields.add(createLayerField("林种", "字符串", "F43", "林种", false, 3, 0, "LIN_ZHONG"));
		newFields.add(createLayerField("起源", "字符串", "F44", "起源", false, 2, 0, "QI_YUAN"));
		newFields.add(createLayerField("前期森林类别", "字符串", "F45", "森林（林地）类别", false, 32, 0, "Q_SEN_LB"));
		newFields.add(createLayerField("森林类别", "字符串", "F46", "森林（林地）类别", false, 32, 0, "SEN_LIN_LB"));
		newFields.add(createLayerField("前期事权等级", "字符串", "F47", "事权等级", false, 32, 0, "Q_SQ_D"));
		newFields.add(createLayerField("事权等级", "字符串", "F48", "事权等级", false, 32, 0, "SHI_QUAN_D"));
		newFields.add(createLayerField("地方公益林保护等级", "字符串", "F49", "地方公益林保护等级", false, 16, 0, "DFGYL_BHDJ"));
		newFields.add(createLayerField("国家公益林保护等级", "字符串", "F50", "国家级公益林保护等级", false, 16, 0, "GJGYL_BHDJ"));
		newFields.add(createLayerField("前期工程类别", "字符串", "F51", "工程类别", false, 255, 0, "Q_GC_LB"));
		newFields.add(createLayerField("工程类别", "字符串", "F52", "工程类别", false, 255, 0, "G_CHENG_LB"));
		newFields.add(createLayerField("优势树种", "字符串", "F53", "优势树种", false, 255, 0, "YOU_SHI_SZ"));
		newFields.add(createLayerField("树种组成", "字符串", "F54", "树种", false, 255, 0, "SHUZ_ZC"));
		newFields.add(createLayerField("平均胸径", "浮点型", "F55", "", false, 6, 1, "PINGJUN_XJ"));
		newFields.add(createLayerField("平均树高", "浮点型", "F56", "", false, 6, 1, "PINGJUN_SG"));

		newFields.add(createLayerField("年龄", "字符串", "F57", "", false, 3, 0, "NIAN_LIN"));
		newFields.add(createLayerField("龄级", "字符串", "F58", "", false, 255, 0, "LIN_JI"));
		newFields.add(createLayerField("龄组", "字符串", "F59", "龄组", false, 255, 0, "LING_ZU"));
		newFields.add(createLayerField("郁密度/覆盖度", "浮点型", "F60", "", false, 6, 2, "YU_BI_DU"));
		newFields.add(createLayerField("公顷蓄积(活立木)", "浮点型", "F61", "", false, 12, 2, "HUO_LMGQXJ"));
		newFields.add(createLayerField("每公顷株数", "整型", "F62", "", false, 8, 0, "MEI_GQ_ZS"));
		newFields.add(createLayerField("小班蓄积量", "浮点型", "F63", "", false, 10, 1, "XIAOBAN_XJ"));
		newFields.add(createLayerField("四旁树株数", "整型", "F64", "", false, 255, 0, "SPS_ZS"));
		newFields.add(createLayerField("四旁树蓄积量", "浮点型", "F65", "", false, 10, 1, "SPS_XJ"));
		newFields.add(createLayerField("散生木株数", "整型", "F66", "", false, 8, 0, "SSM_ZS"));
		newFields.add(createLayerField("散生木蓄积", "浮点型", "F67", "", false, 10, 1, "SSM_XJ"));
		newFields.add(createLayerField("散生竹株数", "整型", "F68", "", false, 8, 0, "SSZ_ZS"));
		newFields.add(createLayerField("枯立木蓄积", "浮点型", "F69", "", false, 10, 1, "KLM_XJ"));
		newFields.add(createLayerField("倒立木蓄积", "浮点型", "F70", "", false, 10, 1, "DLM_XJ"));
		newFields.add(createLayerField("经济林产期", "字符串", "F71", "经济林产期", false, 2, 0, "CH_QI"));
		newFields.add(createLayerField("土地退化类型", "字符串", "F72", "土地退化类型", false, 1, 0, "TD_TH_LX"));
		newFields.add(createLayerField("湿地类型", "字符串", "F73", "湿地类型", false, 1, 0, "SHIDI_LX"));
		newFields.add(createLayerField("健康状况", "字符串", "F74", "森林健康等级", false, 8, 0, "JK_ZHK"));
		newFields.add(createLayerField("灾害类型", "字符串", "F75", "森林灾害类型", false, 2, 0, "DISPE"));
		newFields.add(createLayerField("灾害等级", "字符串", "F76", "森林灾害等级", false, 1, 0, "DISASTER_C"));
		newFields.add(createLayerField("林地质量等级", "字符串", "F77", "林地质量等级", false, 1, 0, "ZL_DJ"));
		newFields.add(createLayerField("林带长度", "浮点型", "F78", "", false, 8, 1, "LD_ZD"));
		newFields.add(createLayerField("林带宽度", "浮点型", "F79", "", false, 8, 1, "LD_KD"));
		newFields.add(createLayerField("是否为补充林地", "字符串", "F80", "是否为补充林地", false, 16, 0, "BCLD"));
		newFields.add(createLayerField("前林地保护等级", "字符串", "F81", "林地保护等级", false, 16, 0, "Q_BH_DJ"));
		newFields.add(createLayerField("林地保护等级", "字符串", "F82", "林地保护等级", false, 16, 0, "BH_DJ"));
		newFields.add(createLayerField("林地功能分区", "字符串", "F83", "", false, 50, 0, "LYFQ"));
		newFields.add(createLayerField("主体功能区", "字符串", "F84", "主体功能区", false, 1, 0, "QYKZ"));
		newFields.add(createLayerField("变化原因", "字符串", "F85", "林地变化原因", false, 2, 0, "BHYY"));
		newFields.add(createLayerField("变化年度", "字符串", "F86", "", false, 4, 0, "BHND"));
		newFields.add(createLayerField("林地管理类型", "字符串", "F87", "林地管理类型", false, 32, 0, "GLLX"));

		newFields.add(createLayerField("经营措施", "字符串", "F88", "经营措施", false, 32, 0, "JYCS"));
		newFields.add(createLayerField("成活率", "浮点型", "F89", "", false, 10, 1, "CH_HL"));
		newFields.add(createLayerField("调查日期", "日期型", "F90", "", false, 8, 0, "DIAOCHA_RQ"));
		newFields.add(createLayerField("调查人", "字符串", "F91", "", false, 255, 0, "DC_REN"));
		newFields.add(createLayerField("说明", "字符串", "F92", "", false, 255, 0, "REMARKS"));

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}
	}

	private void addLinDiBianGengField() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		newFields.add(createLayerField("省", "字符串", "F1", "", false, 255, 0, "SHENG"));
		newFields.add(createLayerField("市", "字符串", "F2", "", false, 255, 0, "SHI"));
		newFields.add(createLayerField("县", "字符串", "F3", "", false, 255, 0, "XIAN"));
		newFields.add(createLayerField("乡", "字符串", "F4", "", false, 255, 0, "XIANG"));
		newFields.add(createLayerField("村", "字符串", "F5", "", false, 255, 0, "CUN"));
		// 基础因子

		newFields.add(createLayerField("林业局", "字符串", "F6", "", false, 255, 0, "LIN_YE_JU"));
		newFields.add(createLayerField("林场", "字符串", "F7", "", false, 255, 0, "LIN_CHANG"));
		newFields.add(createLayerField("原林班号", "字符串", "F8", "", false, 255, 0, "Q_LIN_BAN"));
		newFields.add(createLayerField("林班号", "字符串", "F9", "", false, 255, 0, "LIN_BAN"));
		newFields.add(createLayerField("原小班号", "字符串", "F10", "", false, 255, 0, "Q_XIAO_BAN"));
		newFields.add(createLayerField("小班号", "字符串", "F11", "", false, 255, 0, "XIAO_BAN"));
		newFields.add(createLayerField("横坐标", "字符串", "F12", "", false, 255, 0));
		newFields.add(createLayerField("纵坐标", "字符串", "F13", "", false, 255, 0));

		newFields.add(createLayerField("地貌", "字符串", "F14", "地貌", false, 255, 0, "DI_MAO"));
		newFields.add(createLayerField("坡位", "字符串", "F15", "坡位", false, 255, 0, "PO_XIANG"));
		newFields.add(createLayerField("坡向", "字符串", "F16", "坡向", false, 255, 0, "PO_WEI"));
		newFields.add(createLayerField("坡度", "字符串", "F17", "坡度", false, 255, 0, "PO_DU"));
		newFields.add(createLayerField("交通区位", "字符串", "F18", "交通区位", false, 255, 0, "KE_JI_DU"));
		newFields.add(createLayerField("土壤类型(名称)", "字符串", "F19", "土壤类型(名称)", true, 255, 0, "TU_RANG_LX"));
		newFields.add(createLayerField("土层厚度", "整型", "F20", "", false, 8, 0, "TU_CENG_HD"));
		newFields.add(createLayerField("面积", "浮点型", "F21", "", false, 18, 2, "MIAN_JI"));
		newFields.add(createLayerField("前期土地权属", "字符串", "F22", "土地权属", false, 255, 0, "Q_LD_QS"));
		newFields.add(createLayerField("土地权属", "字符串", "F23", "土地权属", false, 255, 0, "LD_QS"));

		// 林地因子
		newFields.add(createLayerField("前期地类", "字符串", "F24", "地类", false, 255, 0, "Q_DI_LEI"));
		newFields.add(createLayerField("地类", "字符串", "F25", "地类", false, 255, 0, "DI_LEI"));
		newFields.add(createLayerField("前期林种", "字符串", "F26", "林种", false, 255, 0, "Q_L_Z"));
		newFields.add(createLayerField("林种", "字符串", "F27", "林种", false, 255, 0, "LIN_ZHONG"));
		newFields.add(createLayerField("起源", "字符串", "F28", "起源", false, 255, 0, "QI_YUAN"));
		newFields.add(createLayerField("前期森林类别", "字符串", "F29", "森林（林地）类别", false, 255, 0, "Q_SEN_LB"));
		newFields.add(createLayerField("森林类别", "字符串", "F30", "森林（林地）类别", false, 255, 0, "SEN_LIN_LB"));
		newFields.add(createLayerField("前期事权等级", "字符串", "F31", "事权等级", false, 255, 0, "Q_SQ_D"));
		newFields.add(createLayerField("事权等级", "字符串", "F32", "事权等级", false, 255, 0, "SHI_QUAN_D"));
		newFields.add(createLayerField("公益林保护等级", "字符串", "F33", "国家级公益林保护等级", false, 255, 0, "GYL_BHDJ"));
		newFields.add(createLayerField("国家公益林保护等级", "字符串", "F34", "国家级公益林保护等级", false, 255, 0, "GJGYL_BHDJ"));
		newFields.add(createLayerField("前期工程类别", "字符串", "F35", "工程类别", false, 255, 0, "Q_GC_LB"));
		newFields.add(createLayerField("工程类别", "字符串", "F36", "工程类别", false, 255, 0, "G_CHENG_LB"));
		newFields.add(createLayerField("龄组", "字符串", "F37", "龄组", false, 255, 0, "LING_ZU"));
		newFields.add(createLayerField("郁密度/覆盖度", "浮点型", "F38", "", false, 6, 2, "YU_BI_DU"));
		newFields.add(createLayerField("优势树种", "字符串", "F49", "树种", false, 255, 0, "YOU_SHI_SZ"));
		newFields.add(createLayerField("平均胸径", "浮点型", "F50", "", false, 6, 1, "PINGJUN_XJ"));
		newFields.add(createLayerField("每公顷蓄积(活立木)", "浮点型", "F51", "", false, 12, 2, "HUO_LMGQXJ"));
		newFields.add(createLayerField("每公顷株数", "整型", "F52", "", false, 4, 0, "MEI_GQ_ZS"));

		newFields.add(createLayerField("土地退化类型", "字符串", "F53", "土地退化类型", false, 8, 0, "TD_TH_LX"));
		newFields.add(createLayerField("灾害类型", "字符串", "F54", "灾害类型", false, 255, 0, "DISPE"));
		newFields.add(createLayerField("灾害等级", "字符串", "F55", "森林灾害等级", false, 255, 0, "DISASTER_C"));
		newFields.add(createLayerField("林地质量等级", "字符串", "F56", "林地质量等级", false, 255, 0, "ZL_DJ"));
		newFields.add(createLayerField("林带长度", "浮点型", "F57", "", false, 8, 1, "LD_ZD"));
		newFields.add(createLayerField("林带宽度", "浮点型", "F58", "", false, 8, 1, "LD_KD"));

		newFields.add(createLayerField("是否为补充林地", "字符串", "F59", "是否为补充林地", false, 8, 0, "BCLD"));
		newFields.add(createLayerField("前期林地保护等级", "字符串", "F60", "林地保护等级", false, 255, 0, "Q_BH_DJ"));
		newFields.add(createLayerField("林地保护等级", "字符串", "F61", "林地保护等级", false, 255, 0, "BH_DJ"));
		newFields.add(createLayerField("林地功能分区", "字符串", "F62", "", false, 8, 0, "LYFQ"));
		newFields.add(createLayerField("主体功能区", "字符串", "F63", "主体功能区", false, 8, 0, "QYKZ"));
		newFields.add(createLayerField("变化原因", "字符串", "F64", "林地变化原因", false, 255, 0, "BHYY"));
		newFields.add(createLayerField("变化年度", "字符串", "F65", "", false, 255, 0, "BHND"));
		newFields.add(createLayerField("林地管理类型", "字符串", "F66", "林地管理类型", false, 255, 0, "GLLX"));
		newFields.add(createLayerField("说明", "字符串", "F67", "", false, 255, 0, "REMARKS"));

		newFields.add(createLayerField("经济林产期", "字符串", "F79", "经济林产期", false, 2, 0, "CH_QI"));

		// for xinjiang 不需要补充调查内容
		newFields.add(createLayerField("小班蓄积量", "浮点型", "F68", "", false, 10, 1, "XIAOBAN_XJ"));
		newFields.add(createLayerField("四旁树株数", "整型", "F69", "", false, 255, 0, "SPS_ZS"));
		newFields.add(createLayerField("四旁树蓄积量", "浮点型", "F70", "", false, 10, 1, "SPS_XJ"));
		// 管理因子
		newFields.add(createLayerField("生态区位", "字符串", "F71", "", false, 255, 0, "SH_TAI_QW"));
		newFields.add(createLayerField("林木权属", "字符串", "F72", "林木权属", false, 255, 0, "LM_SY"));
		//
		//
		newFields.add(createLayerField("散生木株数", "整型", "F73", "", false, 255, 0, "SSM_ZS"));
		newFields.add(createLayerField("散生木蓄积", "浮点型", "F74", "", false, 15, 3, "SSM_XJ"));
		newFields.add(createLayerField("散生竹株数", "整型", "F75", "", false, 12, 0, "SSZ_ZS"));
		newFields.add(createLayerField("小班蓄积调查方式", "字符串", "F76", "小班蓄积调查方式", false, 8, 0, "DIAOCHA_FS"));
		newFields.add(createLayerField("调查日期", "日期型", "F77", "", false, 8, 0, "DIAOCHA_RQ"));
		newFields.add(createLayerField("调查人", "字符串", "F78", "", false, 255, 0, "DC_REN"));

		// 新增四个字段 数据类型 等级 名称 备注

		for (v1_LayerField editField : newFields) {
			m_EditLayer.GetFieldList().add(editField);
		}
	}

	private void initTuiGengEnum() {
		DictXZQH xzqh = new DictXZQH();
		city = xzqh.getXZQH("61", "市");
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
					.getXZQH(xzqh.getCodeByName(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds), "市", "61"), "县");

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
		m_EditLayer.GetFieldList().add(createLayerField("地市", "字符串", "F1", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("区县", "字符串", "F2", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("乡镇", "字符串", "F3", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("建制村", "字符串", "F4", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("自然村", "字符串", "F5", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("小地名", "字符串", "F6", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("小班号", "字符串", "F7", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("图幅号", "字符串", "F8", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("地类", "字符串", "F9", "", false, 64, 0));

		m_EditLayer.GetFieldList().add(createLayerField("小班面积", "浮点型", "F10", "", false, 15, 2));
		m_EditLayer.GetFieldList().add(createLayerField("可作业面积", "浮点型", "F11", "", false, 15, 2));

		m_EditLayer.GetFieldList().add(createLayerField("权属", "字符串", "F12", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("海拔", "浮点型", "F13", "", false, 8, 2));
		m_EditLayer.GetFieldList().add(createLayerField("坡位", "字符串", "F14", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("坡向", "字符串", "F15", "", false, 64, 0));
		m_EditLayer.GetFieldList().add(createLayerField("坡度", "字符串", "F16", "", false, 64, 0));

	}

	private void addTuiGengField() {
		ArrayList<v1_LayerField> newFields = new ArrayList<v1_LayerField>();

		v1_LayerField newFile1 = new v1_LayerField();

		newFields.add(createLayerField("地市", "字符串", "F1", "", false, 255, 0));
		newFields.add(createLayerField("区县", "字符串", "F2", "", false, 255, 0));
		newFields.add(createLayerField("乡镇", "字符串", "F3", "", false, 255, 0));
		newFields.add(createLayerField("建制村", "字符串", "F4", "", false, 255, 0));
		newFields.add(createLayerField("自然村", "字符串", "F5", "", false, 255, 0));
		newFields.add(createLayerField("小地名", "字符串", "F6", "", false, 255, 0));
		newFields.add(createLayerField("小班号", "字符串", "F7", "", false, 255, 0));
		newFields.add(createLayerField("地类", "字符串", "F8", "", false, 255, 0));
		newFields.add(createLayerField("小班面积", "浮点型", "F9", "", false, 15, 2));

		newFields.add(createLayerField("可作业面积", "浮点型", "F10", "", false, 15, 2));

		newFields.add(createLayerField("权属", "字符串", "F11", "", false, 255, 0));
		newFields.add(createLayerField("海拔", "浮点型", "F12", "", false, 12, 2));

		newFields.add(createLayerField("坡位", "字符串", "F13", "", false, 255, 0));
		newFields.add(createLayerField("坡向", "字符串", "F14", "", false, 255, 0));
		newFields.add(createLayerField("坡度", "字符串", "F15", "", false, 255, 0));
		newFields.add(createLayerField("土壤名称", "字符串", "F16", "", false, 255, 0));
		newFields.add(createLayerField("土层厚度", "字符串", "F17", "", false, 255, 0));
		newFields.add(createLayerField("土壤肥力", "字符串", "F18", "", false, 255, 0));
		// 匹配可能不全
		newFields.add(createLayerField("侵蚀程度", "字符串", "F19", "", false, 255, 0));

		newFields.add(createLayerField("土壤PH值", "浮点型", "F20", "", false, 8, 2));
		newFields.add(createLayerField("植被类型", "字符串", "F21", "", false, 255, 0));
		newFields.add(createLayerField("植被盖度", "浮点型", "F22", "", false, 12, 2));
		newFields.add(createLayerField("植被高度", "浮点型", "F23", "", false, 12, 2));

		newFields.add(createLayerField("是否变更", "字符串", "F24", "", false, 255, 0));
		newFields.add(createLayerField("变更原因", "字符串", "F25", "", false, 255, 0));
		newFields.add(createLayerField("横坐标", "字符串", "F26", "", false, 255, 0));
		newFields.add(createLayerField("纵坐标", "字符串", "F27", "", false, 255, 0));
		newFields.add(createLayerField("设计年度", "字符串", "F28", "", false, 255, 0));
		newFields.add(createLayerField("图幅号", "字符串", "F29", "", false, 255, 0));

		newFields.add(createLayerField("造林方式", "字符串", "F31", "", false, 255, 0));
		newFields.add(createLayerField("造林树种", "字符串", "F32", "", false, 255, 0));

		newFields.add(createLayerField("造林面积", "浮点型", "F33", "", false, 12, 2));
		newFields.add(createLayerField("立地类型", "字符串", "F34", "", false, 255, 0));
		newFields.add(createLayerField("造林林种", "字符串", "F35", "", false, 255, 0));
		newFields.add(createLayerField("株行距", "字符串", "F36", "", false, 255, 0));
		newFields.add(createLayerField("混交比", "字符串", "F37", "", false, 255, 0));
		newFields.add(createLayerField("抚育年次", "整型", "F38", "", false, 8, 0));
		newFields.add(createLayerField("抚育时间", "字符串", "F39", "", false, 255, 0));
		newFields.add(createLayerField("种苗需要量", "浮点型", "F40", "", false, 12, 2));

		newFields.add(createLayerField("整地方式", "字符串", "F41", "", false, 255, 0));
		newFields.add(createLayerField("整地规格", "字符串", "F42", "", false, 255, 0));
		newFields.add(createLayerField("用工量合计", "浮点型", "F43", "", false, 12, 2));
		newFields.add(createLayerField("用工量整地", "浮点型", "F44", "", false, 12, 2));
		newFields.add(createLayerField("用工量造林", "浮点型", "F45", "", false, 12, 2));
		newFields.add(createLayerField("用工量补植", "浮点型", "F46", "", false, 12, 2));
		newFields.add(createLayerField("用工量抚育", "浮点型", "F47", "", false, 12, 2));

		newFields.add(createLayerField("模式号", "字符串", "F50", "", false, 255, 0));
		newFields.add(createLayerField("投资预算", "字符串", "F51", "", false, 255, 0));
		newFields.add(createLayerField("备注", "字符串", "F52", "", false, 255, 0));
		newFields.add(createLayerField("苗木规格", "字符串", "F53", "", false, 255, 0));
		newFields.add(createLayerField("苗木单价", "浮点型", "F54", "", false, 8, 2));
		newFields.add(createLayerField("地市代码", "字符串", "F55", "", false, 255, 0));
		newFields.add(createLayerField("区县代码", "字符串", "F56", "", false, 255, 0));
		newFields.add(createLayerField("乡镇代码", "字符串", "F57", "", false, 255, 0));
		newFields.add(createLayerField("村代码", "字符串", "F58", "", false, 255, 0));
		newFields.add(createLayerField("工日单价", "浮点型", "F60", "", false, 12, 2));

		newFields.add(createLayerField("_户名", "字符串", "F61", "", false, 255, 0));
		newFields.add(createLayerField("_地类", "字符串", "F62", "", false, 255, 0));
		newFields.add(createLayerField("_面积", "字符串", "F63", "", false, 255, 0));
		newFields.add(createLayerField("_林种", "字符串", "F64", "", false, 255, 0));
		newFields.add(createLayerField("_树种", "字符串", "F65", "", false, 255, 0));
		newFields.add(createLayerField("_苗木", "字符串", "F66", "", false, 255, 0));
		newFields.add(createLayerField("_种子", "字符串", "F67", "", false, 255, 0));

		newFields.add(createLayerField("D_户名", "字符串", "F68", "", false, 255, 0));
		newFields.add(createLayerField("D_兑现年度", "字符串", "F69", "", false, 255, 0));
		newFields.add(createLayerField("D_兑现金额", "字符串", "F70", "", false, 255, 0));
		newFields.add(createLayerField("D_政策补助", "字符串", "F71", "", false, 255, 0));
		newFields.add(createLayerField("D_补助标准", "字符串", "F72", "", false, 255, 0));
		newFields.add(createLayerField("D_种苗费", "字符串", "F73", "", false, 255, 0));
		newFields.add(createLayerField("D_是否兑现", "字符串", "F74", "", false, 255, 0));
		newFields.add(createLayerField("D_备注", "字符串", "F75", "", false, 255, 0));

		newFields.add(createLayerField("_是否贫困户", "字符串", "F77", "", false, 255, 0));

		// newFields.add(createLayerField("J_地区类别","字符串","F2","",false,255,0));
		// newFields.add(createLayerField("J_作业年度","字符串","F2","",false,255,0));
		// newFields.add(createLayerField("J_退耕地类","字符串","F2","",false,255,0));
		// newFields.add(createLayerField("J_林地权属","字符串","F2","",false,255,0));
		// newFields.add(createLayerField("J_林木权属","字符串","F2","",false,255,0));
		// newFields.add(createLayerField("J_林种","字符串","F2","",false,255,0));
		// newFields.add(createLayerField("J_树种","字符串","F2","",false,255,0));
		// newFields.add(createLayerField("J_植被类型","字符串","F2","",false,255,0));

		// newFields.add(createLayerField("村代码","字符串","F30","",false,255,0));

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

	// 设置按钮的状态
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

	// 按钮事件
	private void DoCommand(String StrCommand) {
		this.m_EditLayer.SetLayerProjectType(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype));

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

		}
	};

	// 新增图层后的回调
	private ICallback m_Callback = null;

	/**
	 * 新增图层后的回调
	 * 
	 * @param cb
	 */
	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	// 已有的图层列表
	private List<v1_Layer> m_HaveLayerList = null;

	public void SetHaveLayerList(List<v1_Layer> haveLayerList) {
		this.m_HaveLayerList = haveLayerList;
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
	}

	/**
	 * 保存图层信息
	 * 
	 * @return
	 */
	private boolean SaveLayerInfo() {
		// 获取图层信息
		String LayerName = Tools.GetTextValueOnID(_Dialog, R.id.et_name); // 图层名称
		String LayerType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_type); // 图层类型
		String layerProjectType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype);// 林业工程类型

		// 验证图层信息
		String ErrorInfo = "";
		if (LayerName.equals(""))
			ErrorInfo += "【图层名称】不允许为空值！\r\n";
		for (v1_Layer lyr : this.m_HaveLayerList) {
			if (lyr.GetLayerAliasName().equals(LayerName) && (!lyr.GetLayerID().equals(this.m_EditLayer.GetLayerID())))
				ErrorInfo += "【图层名称】不允许重复！\r\n";
		}
		if (this.m_EditLayer.GetFieldList().size() == 0) {
			ErrorInfo += "【字段】数量不允许为0个！\r\n";
		} else {
			try {
				int i = 0;
				for (v1_LayerField field : this.m_EditLayer.GetFieldList()) {
					field.SetIsSelect(Boolean.parseBoolean(dataList.get(i).get("D1") + ""));
				}
			} catch (Exception ex) {
				Tools.ShowMessageBox("保存字段是否显示出错：" + ex.getMessage());
			}

		}

		if (!ErrorInfo.equals("")) {
			Tools.ShowMessageBox(_Dialog.getContext(), ErrorInfo);
			return false;
		}

		// 保存
		this.m_EditLayer.SetLayerAliasName(LayerName);
		this.m_EditLayer.SetLayerTypeName(LayerType);
		this.m_EditLayer.SetLayerProjectType(layerProjectType);
		if ((this.m_EditLayer.GetEditMode() == lkEditMode.enNew)) {
			// 根据图层类型设置默认符号
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

			// if(this.m_EditLayer.GetLayerProjecType().contains("退耕还林"))
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
	 * 加载图层相关信息
	 */
	List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

	@SuppressLint("NewApi")
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
			// _Dialog.findViewById(R.id.sp_type).setEnabled(false);
			// _Dialog.findViewById(R.id.sp_projecttype).setEnabled(false);
			// 图层类型
			if (!this.m_EditLayer.GetLayerProjecType().isEmpty()) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_projecttype, this.m_EditLayer.GetLayerProjecType());
				// if(this.m_EditLayer.GetLayerProjecType().contains("碳汇"))
				// {
				// _Dialog.findViewById(R.id.btnExportTanhui).setVisibility(View.VISIBLE);
				// }
			}

		} else {
			// 新增图层
			this.m_EditLayer = new v1_Layer();
			this.m_EditLayer.SetEditMode(lkEditMode.enNew);
		}

		this.SetButtonEnable(false);

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
