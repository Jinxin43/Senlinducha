package lkmap.ZRoadMap.MyControl;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Tools.Tools;

public class v1_EditSpinnerDialog extends Spinner {
	private EditText m_Text = null;
	private Context m_Context = null;
	private v1_EditSpinnerDialogAdpter m_Adpter = null;

	public v1_EditSpinnerDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.Inti(context, false);
	}

	public v1_EditSpinnerDialog(Context context) {
		super(context);
		this.Inti(context, false);
	}

	public v1_EditSpinnerDialog(Context context, int mode) {
		super(context, mode);
		this.Inti(context, false);
	}

	public v1_EditSpinnerDialog(Context context, int mode, boolean cutValue) {
		super(context, mode);
		this.Inti(context, cutValue);
	}

	public v1_EditSpinnerDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.Inti(context, false);
	}

	private void Inti(Context context, boolean cutValue) {
		this.m_Adpter = new v1_EditSpinnerDialogAdpter(context);
		this.m_Adpter.SetDataList(Tools.StrArrayToList(new String[] { "" }));
		this.setAdapter(this.m_Adpter);

		this.m_Context = context;
		if (cutValue) {
			this.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String value = parent.getItemAtPosition(position).toString();
					if (value.contains("(")) {
						value = (String) value.subSequence(0, value.indexOf("("));
					}
					setText(value);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

	@Override
	public boolean performClick() {
		if (this.m_ItemList.size() > 0) {
			this.ShowOptionMessageBox();
		} else {
			if (this.m_Callback != null)
				this.m_Callback.OnClick("SpinnerCallback", null);
		}
		return false;
	}

	// 回调函数
	private ICallback m_Callback = null;

	/**
	 * 设置回调函数
	 * 
	 * @param cb
	 */
	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	// 选择项列表
	private List<String> m_ItemList = new ArrayList<String>();

	/**
	 * 设置选择项列表
	 * 
	 * @param itemList
	 */
	public void SetSelectItemList(List<String> itemList) {
		this.m_ItemList = itemList;
	}

	/**
	 * 设置文本
	 */
	public void setText(String v) {
		this.m_Adpter.SetDataList(Tools.StrArrayToList(new String[] { v }));
	}

	/**
	 * 获取文本
	 * 
	 * @return
	 */
	public String getText() {
		return this.m_Adpter.GetEditTextView().getText().toString();
	}

	/**
	 * 得到文本框控件
	 * 
	 * @return
	 */
	public EditText getEditTextView() {
		return this.m_Adpter.GetEditTextView();
	}
	// private String _Title = "选项";
	// public void setPrompt(String prt)
	// {
	// _Title = prt;
	// }
	//
	// private boolean _MultiSelect = false;
	// public void SetMultiSelectMode(boolean _Mode)
	// {
	// _MultiSelect = _Mode;
	// }
	//
	// //设置数据输入模式
	// public void SetNumberDecimalMode()
	// {
	// et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
	// et.setSingleLine();
	// }

	// 显示选项对话框
	private boolean[] _SelectItemBoolean = null;

	private void ShowOptionMessageBox() {
		// 创建AlertDialog
		AlertDialog.Builder menuDialog = new AlertDialog.Builder(this.m_Context);
		menuDialog.setTitle("选择项");
		menuDialog.setSingleChoiceItems(lkmap.Tools.Tools.StrListToArray(this.m_ItemList), -1,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String[] datalist = new String[] { m_ItemList.get(arg1).toString() };
						m_Adpter.SetDataList(Tools.StrArrayToList(datalist));
						setAdapter(m_Adpter);
						arg0.dismiss();
						if (m_Callback != null)
							m_Callback.OnClick("SpinnerSelectCallback", m_ItemList.get(arg1).toString());
					}
				});

		menuDialog.show();
	}

	private void SetEditTextFocus() {
		// tv.setFocusable(true);tv.setSelected(true);tv.setSelection(tv.getText().length());
	}
}
