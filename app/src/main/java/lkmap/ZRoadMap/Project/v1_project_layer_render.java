package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkRenderType;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_ColorPicker_RGB;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_ImageSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class v1_project_layer_render {
	private v1_FormTemplate _Dialog = null;

	public v1_project_layer_render() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_project_layer_render);
		// _Dialog.ReSetSize(1f, -1f);
		_Dialog.SetCaption(Tools.ToLocale("ͼ����Ⱦ"));

		_Dialog.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("ȷ��") + "  ,ȷ��", pCallback);

		// //������֧��
		// int[] viewID = new
		// int[]{R.id.tvLoaleText1,R.id.tvLoaleText2,R.id.tvLoaleText3,R.id.tvLoaleText4,
		// R.id.tvLoaleText5,R.id.tvLoaleText6,R.id.tvLoaleText7,R.id.tvLoaleText8,
		// R.id.tvLoaleText9,R.id.et_label};
		// for(int vid:viewID)
		// {
		// Tools.ToLocale(_Dialog.findViewById(vid));
		// }
	}

	private ICallback m_Callback = null;

	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	// �ϲ���ť�¼�
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("ȷ��")) {

				// �Ƿ�ѡ���˱�ע�ֶ�
				boolean bIfLabel = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_iflabel).equals("��") ? true : false;
				if (bIfLabel) {
					if (Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_labelfield).trim().equals("")) {
						Tools.ShowMessageBox(_Dialog.getContext(), "��ѡ���ע�ֶΣ�");
						return;
					}
				}

				// ͼ������
				String RenderType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_rendertype);
				if (RenderType.equals("��ֵ����")) {
					// ͼ�����
					v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog) _Dialog.findViewById(R.id.isd_symbol);
					m_EditLayer.SetSimpleSymbol(ISD.GetSelectSymbolObject().SymbolBase64Str);
					m_EditLayer.SetRenderType(lkRenderType.enSimple);
				}
				if (RenderType.equals("��ֵ����")) {
					m_EditLayer.SetRenderType(lkRenderType.enUniqueValue);
				}

				// ͼ��͸����
				SeekBar sb = (SeekBar) _Dialog.findViewById(R.id.sb_transparent);
				m_EditLayer.SetTransparent(sb.getProgress());

				// �Ƿ��ע
				m_EditLayer.SetIfLabel(bIfLabel);

				// ��ע�ֶ�
				String[] labelFieldList = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_labelfield).trim().split(",");
				if (labelFieldList.length > 0) {
					List<String> labelDataFieldList = new ArrayList<String>();
					for (String labelField : labelFieldList) {
						v1_LayerField dataFiled = m_EditLayer.GetDataFieldByFieldName(labelField);
						if (dataFiled != null) {
							labelDataFieldList.add(dataFiled.GetDataFieldName());
						}
						// for(v1_LayerField LF:m_EditLayer.GetFieldList())
						// {
						// if
						// (LF.GetFieldName().equals(labelField))labelDataFieldList.add(LF.GetDataFieldName());
						// }
					}
					if (labelDataFieldList.size() > 0) {
						if (labelDataFieldList.size() > 15) {
							m_EditLayer.SetLabelDataField(labelDataFieldList.get(0));
						} else {
							m_EditLayer.SetLabelDataField(Tools.JoinT(",", labelDataFieldList));
						}

					}

					else
						m_EditLayer.SetIfLabel(false);
				} else {
					m_EditLayer.SetIfLabel(false);
				}

				// ��עˮӡ
				String[] waterMarkFieldList = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_watermarkfield).trim()
						.split(",");
				if (waterMarkFieldList.length > 0) {
					List<String> waterDataFieldListDataFieldList = new ArrayList<String>();
					for (String labelField : waterMarkFieldList) {
						v1_LayerField dataFiled = m_EditLayer.GetDataFieldByFieldName(labelField);
						if (dataFiled != null) {
							waterDataFieldListDataFieldList.add(dataFiled.GetDataFieldName());
						}
					}

					if (waterDataFieldListDataFieldList.size() > 0) {
						m_EditLayer.setWaterMarkDataFields(Tools.JoinT(",", waterDataFieldListDataFieldList));
					}

				}

				// ��ע��ʽ
				TextView TVT = (TextView) _Dialog.findViewById(R.id.et_label);
				int LabelColor = TVT.getTextColors().getDefaultColor();

				String LabelSize = Tools.GetTextValueOnID(_Dialog, R.id.sp_labelsize1);
				v1_ColorPicker_RGB CP = new v1_ColorPicker_RGB();
				CP.R = Color.red(LabelColor);
				CP.G = Color.green(LabelColor);
				CP.B = Color.blue(LabelColor);
				m_EditLayer.SetLabelFont("#" + CP.ToHex() + "," + LabelSize);

				// ��С�����ɼ�����
				v1_EditSpinnerDialog esd_min = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.esp_minscale);
				if (Tools.IsInteger(esd_min.getText()))
					m_EditLayer.SetVisibleScaleMin(Integer.parseInt(esd_min.getText()));

				v1_EditSpinnerDialog esd_max = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.esp_maxscale);
				if (Tools.IsInteger(esd_max.getText()))
					m_EditLayer.SetVisibleScaleMax(Integer.parseInt(esd_max.getText()));

				if (m_Callback != null)
					m_Callback.OnClick("ͼ����Ⱦ", null);
				_Dialog.dismiss();
			}

			// �򿪷���ѡȡ�Ի��򣬶��ڵ�ֵ�������ֵ���Ų�ͬ����
			if (Str.equals("ImageSpinnerCallback")) {
				String RenderType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_rendertype);
				if (RenderType.equals("��ֵ����")) {
					v1_project_layer_render_symbolexplorer plrs = new v1_project_layer_render_symbolexplorer();
					plrs.SetGeoLayerType(m_EditLayer.GetLayerType());
					plrs.SetCallback(pCallback);
					v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog) _Dialog.findViewById(R.id.isd_symbol);
					if (ISD.GetSelectSymbolObject() != null) {
						plrs.SetDefaultSymbolObject(ISD.GetSelectSymbolObject());
					}
					plrs.ShowDialog();
				}
				if (RenderType.equals("��ֵ����")) {
					v1_project_layer_render_uniquevalue plru = new v1_project_layer_render_uniquevalue();
					plru.SetEditLayer(m_EditLayer);
					plru.ShowDialog();
				}
			}

			// �ӷ��ſ���ѡ��һ���ź�Ļص�
			if (Str.equals("���ſ�")) {
				v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog) _Dialog.findViewById(R.id.isd_symbol);
				List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
				SOList.add((v1_SymbolObject) ExtraStr);
				ISD.SetImageItemList(SOList);
			}
		}
	};

	private v1_Layer m_EditLayer = null;

	/**
	 * ���õ�ǰ���ڱ༭��ͼ��
	 * 
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr) {
		this.m_EditLayer = lyr;
		this._Dialog.SetCaption("��" + this.m_EditLayer.GetLayerAliasName() + "��ͼ����Ⱦ");
	}

	/**
	 * ����ͼ����Ϣ
	 */
	private void LoadLayerRenderInfo() {
		if (this.m_EditLayer == null)
			return;

		// ��������
		v1_DataBind.SetBindListSpinner(_Dialog, "��������", Tools.StrArrayToList(new String[] { "��ֵ����", "��ֵ����" }),
				R.id.sp_rendertype, new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("OnItemSelected")) {
							// ͼ�����
							v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog) _Dialog.findViewById(R.id.isd_symbol);
							int W = 64;
							if (m_EditLayer.GetLayerType() == lkGeoLayerType.enPolyline)
								W = 100;
							v1_SymbolObject SO = null;
							if (ExtraStr.toString().equals("��ֵ����"))
								SO = PubVar.m_DoEvent.m_ConfigDB.GetSymbolExplorer()
										.GetSymbolObject(m_EditLayer.GetSimpleSymbol(), m_EditLayer.GetLayerType(), W);
							if (ExtraStr.toString().equals("��ֵ����")) {
								// ����������ʽ����ʾΪ��ֵ����
								SO = UniqueValueRender.CreateMSymbolObject(200, 30);
							}
							List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
							SOList.add(SO);
							ISD.SetImageItemList(SOList);
							ISD.SetCallback(pCallback);
						}
					}
				});
		String RenderTypeStr = "";
		if (this.m_EditLayer.GetRenderType() == lkRenderType.enSimple)
			RenderTypeStr = "��ֵ����";
		if (this.m_EditLayer.GetRenderType() == lkRenderType.enUniqueValue)
			RenderTypeStr = "��ֵ����";
		Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_rendertype, RenderTypeStr);

		// ͼ��͸����
		if (this.m_EditLayer.GetLayerType() == lkGeoLayerType.enPolygon)
			_Dialog.findViewById(R.id.ll_transparent).setVisibility(View.VISIBLE);
		SeekBar sb = (SeekBar) _Dialog.findViewById(R.id.sb_transparent);
		sb.setProgress(this.m_EditLayer.GetTransparet());

		// �Ƿ��ע
		v1_DataBind.SetBindListSpinner(_Dialog, "�Ƿ��ע", Tools.StrArrayToList(new String[] { "��", "��" }),
				R.id.sp_iflabel, new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("OnItemSelected")) {
							if (ExtraStr.toString().equals("��"))
								SetLabelInfoEnable(false);
							else
								SetLabelInfoEnable(true);
						}
					}
				});
		Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_iflabel, this.m_EditLayer.GetIfLabel() == true ? "��" : "��");
		this.SetLabelInfoEnable(this.m_EditLayer.GetIfLabel());

		// ��ע�ֶ�
		v1_SpinnerDialog vsd = (v1_SpinnerDialog) _Dialog.findViewById(R.id.sp_labelfield);
		v1_DataBind.SetBindListSpinner(_Dialog, "��ע�ֶ�", new String[] { this.m_EditLayer.GetLabelFieldStr() },
				R.id.sp_labelfield);
		vsd.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_project_layer_render_uniquevalue_selectfield pnl = new v1_project_layer_render_uniquevalue_selectfield();
				pnl.SetEditLayer(m_EditLayer);
				pnl.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						List<v1_LayerField> LFList = (List<v1_LayerField>) ExtraStr;
						List<String> FieldNameList = new ArrayList<String>();
						for (v1_LayerField LF : LFList) {
							FieldNameList.add(LF.GetFieldName());
						}
						v1_DataBind.SetBindListSpinner(_Dialog, "", new String[] { Tools.JoinT(",", FieldNameList) },
								R.id.sp_labelfield);
					}
				});
				pnl.ShowDialog();
			}
		});

		// ��ע��ɫ
		v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.sp_labelcolor);
		ESD.getEditTextView().setEnabled(false);
		String labelColor = this.m_EditLayer.GetLabelFont().split(",")[0]; // ������ʽ
		ESD.getEditTextView().setBackgroundColor(Color.parseColor(labelColor));
		TextView TV = (TextView) _Dialog.findViewById(R.id.et_label);
		TV.setTextColor(Color.parseColor(labelColor));
		ESD.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_project_layer_render_colorpicker plrc = new v1_project_layer_render_colorpicker();
				plrc.SetICallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_EditSpinnerDialog ESDColor = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.sp_labelcolor);
						ESDColor.getEditTextView().setBackgroundColor(Color.parseColor(ExtraStr.toString()));
						TextView TVT = (TextView) _Dialog.findViewById(R.id.et_label);
						TVT.setTextColor(Color.parseColor(ExtraStr.toString()));
					}
				});
				plrc.ShowDialog();

			}
		});

		// ��ע��С
		final EditText ESD_Size = (EditText) _Dialog.findViewById(R.id.sp_labelsize1);
		ESD_Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		String labelSize = this.m_EditLayer.GetLabelFont().split(",")[1]; // ������ʽ
		ESD_Size.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				float s = (float) PubVar.m_DisplayMetrics.densityDpi / 96f;
				TextView TV = (TextView) _Dialog.findViewById(R.id.et_label);
				if (Tools.IsFloat(arg0.toString()))
					TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, Float.parseFloat(arg0.toString()) * s);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
		});
		ESD_Size.setText(labelSize);

		// ��С�����ɼ�����
		v1_EditSpinnerDialog esd_min = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.esp_minscale);
		esd_min.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
		esd_min.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_EditSpinnerDialog esd2 = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.esp_minscale);
				if (ExtraStr.equals("��С�ɼ�����"))
					esd2.setText("0");
				if (ExtraStr.equals("���ɼ�����"))
					esd2.setText(Integer.MAX_VALUE + "");
				if (ExtraStr.equals("��ǰ����")) {
					DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
					double D = dm.densityDpi * PubVar.m_Map.getViewConvert().getZoomScale();
					D = D / 0.0254;
					esd2.setText(Tools.ConvertToDigi(D + "", 0).replace(".", ""));
					// esd2.setText((int)PubVar.m_Map.getViewConvert().getZoom()+"");
				}
			}
		});
		esd_min.getEditTextView().setEnabled(true);
		esd_min.SetSelectItemList(Tools.StrArrayToList(new String[] { "��С�ɼ�����", "���ɼ�����", "��ǰ����" }));
		esd_min.setText(this.m_EditLayer.GetVisibleScaleMin() + "");

		v1_EditSpinnerDialog esd_max = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.esp_maxscale);
		esd_max.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
		esd_max.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_EditSpinnerDialog esd2 = (v1_EditSpinnerDialog) _Dialog.findViewById(R.id.esp_maxscale);
				if (ExtraStr.equals("��С�ɼ�����"))
					esd2.setText("0");
				if (ExtraStr.equals("���ɼ�����"))
					esd2.setText(Integer.MAX_VALUE + "");
				if (ExtraStr.equals("��ǰ����")) {
					// esd2.setText((int)PubVar.m_Map.getViewConvert().getZoom()+"");
					DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
					double D = dm.densityDpi * PubVar.m_Map.getViewConvert().getZoomScale();
					D = D / 0.0254;
					esd2.setText(Tools.ConvertToDigi(D + "", 0).replace(".", ""));
				}
			}
		});
		esd_max.getEditTextView().setEnabled(true);
		esd_max.SetSelectItemList(Tools.StrArrayToList(new String[] { "��С�ɼ�����", "���ɼ�����", "��ǰ����" }));
		esd_max.setText(this.m_EditLayer.GetVisibleScaleMax() + "");

		// �Ƿ��ע
		v1_DataBind.SetBindListSpinner(_Dialog, "�Ƿ����ˮӡ", Tools.StrArrayToList(new String[] { "��", "��" }),
				R.id.sp_ifWaterMark, new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("OnItemSelected")) {
							if (ExtraStr.toString().equals("��")) {
								SetWaterMarkLable(false);

							} else
								SetWaterMarkLable(true);
						}
					}
				});
		Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_ifWaterMark,
				this.m_EditLayer.GetShowWaterMark() == true ? "��" : "��");
		this.SetWaterMarkLable(this.m_EditLayer.GetShowWaterMark());

		v1_SpinnerDialog waterMarkFile = (v1_SpinnerDialog) _Dialog.findViewById(R.id.sp_watermarkfield);
		v1_DataBind.SetBindListSpinner(_Dialog, "ˮӡ�ֶ�", new String[] { this.m_EditLayer.GetWaterMarkFieldStr() },
				R.id.sp_watermarkfield);
		waterMarkFile.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_project_layer_render_uniquevalue_selectfield pnl = new v1_project_layer_render_uniquevalue_selectfield();
				pnl.SetEditLayer(m_EditLayer);
				pnl.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						List<v1_LayerField> LFList = (List<v1_LayerField>) ExtraStr;
						List<String> FieldNameList = new ArrayList<String>();
						for (v1_LayerField LF : LFList) {
							FieldNameList.add(LF.GetFieldName());
						}
						v1_DataBind.SetBindListSpinner(_Dialog, "", new String[] { Tools.JoinT(",", FieldNameList) },
								R.id.sp_watermarkfield);
					}
				});
				pnl.ShowDialog();
			}
		});

	}

	public void ShowDialog() {
		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerRenderInfo();
			}
		});
		_Dialog.show();
	}

	// �������Ƿ��עΪfalseʱ���������ò�����
	private void SetLabelInfoEnable(boolean enabled) {
		_Dialog.findViewById(R.id.sp_labelfield).setEnabled(enabled);
		_Dialog.findViewById(R.id.sp_labelcolor).setEnabled(enabled);
		_Dialog.findViewById(R.id.sp_labelsize1).setEnabled(enabled);
	}

	// �������Ƿ��עΪfalseʱ���������ò�����
	private void SetWaterMarkLable(boolean enabled) {
		this.m_EditLayer.SetShowWaterMark(enabled);
		_Dialog.findViewById(R.id.sp_watermarkfield).setEnabled(enabled);
	}
}
