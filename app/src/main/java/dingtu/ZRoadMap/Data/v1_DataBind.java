package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.HashMapEx;

public class v1_DataBind {

	public static void SetBindSpinner(Context C, String PromptText, int arrayid, int spinnerid) {
		// 第一个参数，上下文对象，，
		// 第二个参数，引用了strings.xml文件当中定义的string数组
		// 第三参数是用来指定spinner的样式，是一个布局文件ID,
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(C, arrayid,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) ((Activity) C).findViewById(spinnerid);
		spinner.setAdapter(adapter);
		spinner.setPrompt(PromptText);
	}

	public static void SetBindSpinner(Dialog C, String PromptText, int arrayid, int spinnerid) {
		SetBindSpinner(C, PromptText, arrayid, spinnerid, -1);
	}

	public static void SetBindSpinner(Dialog C, String PromptText, int arrayid, int spinnerid, int DefaultIndex) {
		// 第一个参数，上下文对象，，
		// 第二个参数，引用了strings.xml文件当中定义的string数组
		// 第三参数是用来指定spinner的样式，是一个布局文件ID,
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(C.getContext(), arrayid,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		View v = (C).findViewById(spinnerid);
		String VType = v.getClass().getName();
		if (VType.equals("android.widget.Spinner")) {
			Spinner spinner = (Spinner) v;
			spinner.setAdapter(adapter);
			if (DefaultIndex != -1)
				spinner.setSelection(DefaultIndex, true);
			spinner.setPrompt(PromptText);
		}
		if (VType.equals("lkmap.ZRoadMapData.DataCombox")) {
			dingtu.ZRoadMap.Data.DataCombox spinner2 = (dingtu.ZRoadMap.Data.DataCombox) v;
			spinner2.setAdapter(adapter);// if
											// (DefaultIndex!=-1)spinner.setSelection(DefaultIndex,true);
			spinner2.setPrompt(PromptText);
		}
	}

	public static void SetBindListSpinner(Context C, String PromptText, List<String> ItemList, int spinnerid) {
		Spinner spinner = (Spinner) ((Activity) C).findViewById(spinnerid);
		SetBindListSpinner(C, PromptText, ItemList, spinner);
	}

	public static void SetBindListSpinner(Context C, String PromptText, String[] ItemList, int spinnerid) {
		Spinner spinner = (Spinner) ((Activity) C).findViewById(spinnerid);

		List<String> itemListC = new ArrayList<String>();
		for (String Str : ItemList)
			itemListC.add(Str);

		SetBindListSpinner(C, PromptText, itemListC, spinner);
	}

	public static void SetBindListSpinner(Context C, String PromptText, List<String> ItemList, Spinner spinnerView) {
		List<CharSequence> itemListC = new ArrayList<CharSequence>();
		for (String Str : ItemList)
			itemListC.add(Str);

		// 第一个参数，上下文对象，，
		// 第二个参数，引用了strings.xml文件当中定义的string数组
		// 第三参数是用来指定spinner的样式，是一个布局文件ID,
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(C, android.R.layout.simple_spinner_item,
				itemListC);
		// ArrayAdapter<CharSequence> adapter =
		// ArrayAdapter.createFromResource(C, arrayid,
		// android.R.layout.simple_spinner_item);
		// adapter.add("aaa");
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = spinnerView;
		spinner.setAdapter(adapter);
		spinner.setPrompt(PromptText);
	}

	public static void SetBindListSpinner(Dialog C, String PromptText, String[] ItemList, int spinnerid) {
		SetBindListSpinner(C, PromptText, Tools.StrArrayToList(ItemList), spinnerid, null);
	}

	public static void SetBindListSpinner(Dialog C, String PromptText, List<String> ItemList, int spinnerid) {
		SetBindListSpinner(C, PromptText, ItemList, spinnerid, null);
	}

	public static void SetBindListSpinner(Dialog C, String PromptText, List<String> ItemList, int spinnerid,
			final ICallback OnItemSelectCallback) {
		List<CharSequence> itemListC = new ArrayList<CharSequence>();
		for (String Str : ItemList)
			itemListC.add(Str);

		// 第一个参数，上下文对象，，
		// 第二个参数，引用了strings.xml文件当中定义的string数组
		// 第三参数是用来指定spinner的样式，是一个布局文件ID,
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(C.getContext(),
				android.R.layout.simple_spinner_item, itemListC);
		// ArrayAdapter<CharSequence> adapter =
		// ArrayAdapter.createFromResource(C, arrayid,
		// android.R.layout.simple_spinner_item);
		// adapter.add("aaa");
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinner = (Spinner) (C.findViewById(spinnerid));
		spinner.setAdapter(adapter);
		spinner.setPrompt(PromptText);

		if (OnItemSelectCallback != null) {
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					OnItemSelectCallback.OnClick("OnItemSelected", String.valueOf(spinner.getSelectedItem()));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	public static void SetBindListSpinnerByHashMap(Dialog C, String PromptText, List<HashMapEx> dataList, int spinnerid,
			final ICallback OnItemSelectCallback) {
		// 第一个参数，上下文对象，，
		// 第二个参数，引用了strings.xml文件当中定义的string数组
		// 第三参数是用来指定spinner的样式，是一个布局文件ID,
		ArrayAdapter<HashMapEx> adapter = new ArrayAdapter<HashMapEx>(C.getContext(),
				android.R.layout.simple_spinner_item, dataList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinner = (Spinner) (C.findViewById(spinnerid));
		spinner.setAdapter(adapter);
		spinner.setPrompt(PromptText);

		if (OnItemSelectCallback != null) {
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					OnItemSelectCallback.OnClick("OnItemSelected", spinner.getSelectedItem());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

}
