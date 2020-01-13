package lkmap.ZRoadMap.MyControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Enum.lkHeaderListViewItemType;
import lkmap.Tools.Tools;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;

public class v1_HeaderListViewTemplate 
{
	private ReportHeader m_ReportHeader = null;
	public ReportHeader GetReportHeader(){return m_ReportHeader;}
	

	/**
	 * 创建列头
	 * @param headerView
	 */
	public void CreateReport(RelativeLayout headerView,String headerTag,List<HashMap<String,Object>> FieldHeaderList)
	{
		this.m_ReportHeader = new ReportHeader();
		
		//标签列的详细定义
		List<ReportHeaderInfo> _ReportStructInfoList = new ArrayList<ReportHeaderInfo>();
		
		//定义每列的宽度
		List<Integer> _ReportHeaderWidthList = null;
		
		if (headerTag.equals("SQL查询条件列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("查询条件"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("关系"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-70,-15}); //列宽度,负值表示比例
		}
		if (headerTag.equals("查询结果列表"))
		{
			int[] headerWidth = new int[FieldHeaderList.size()];
			for(int i=0;i<FieldHeaderList.size();i++)
			{
				HashMap<String,Object> fieldObj = FieldHeaderList.get(i);
				_ReportStructInfoList.add(new ReportHeaderInfo(fieldObj.get("FieldName")+"",1,1,(i+1),(i+1),false,lkHeaderListViewItemType.enText));
				headerWidth[i]=100;
			}
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, headerWidth); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("查询结果列表1"))
		{
			int[] headerWidth = new int[FieldHeaderList.size()+1];
			headerWidth[0] = 50;
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			for(int i=0;i<FieldHeaderList.size();i++)
			{
				HashMap<String,Object> fieldObj = FieldHeaderList.get(i);
				_ReportStructInfoList.add(new ReportHeaderInfo(fieldObj.get("FieldName")+"",1,1,(i+2),(i+2),false,lkHeaderListViewItemType.enText));
				headerWidth[i+1]=100;
			}
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, headerWidth); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("面叠加分析结果"))
		{
			String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("统计项"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("面积\n("+areaUnit+")"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("百分比"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-20,-30,-30,-20}); //列宽度,负值表示比例
		}
		
		//模板列表
		if (headerTag.equals("我的坐标系"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("坐标系统"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("中央经线"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("椭球转换"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-25,-25,-15,-20}); //列宽度,负值表示比例
		}
		if (headerTag.equals("工程列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("工程名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("创建时间"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("详细"),1,1,4,4,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-40,-30,-15}); //列宽度,负值表示比例
		}
		if (headerTag.equals("图层列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("显示"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("类型"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("符号"),1,1,4,4,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-50,-15,-20}); //列宽度,负值表示比例
		}
		if (headerTag.equals("导出图层列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("导出"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("类型"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数量"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-50,-15,-20}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("上传图层列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("导出"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("类型"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数量"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("上传照片"),1,1,5,5,false,lkHeaderListViewItemType.enCheckBox));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-40,-15,-15,-15}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("图层模板列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层名称"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层类型"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数据量"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-25,-35}); //列宽度,负值表示比例
		}
		if (headerTag.equals("加载图层模板列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层名称"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层类型"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("统计_点图层"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层名称"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数据量"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50}); //列宽度,负值表示比例
		}
		if (headerTag.equals("统计_线图层"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层名称"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数据量"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("长度(米)"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-20,-40}); //列宽度,负值表示比例
		}
		if (headerTag.equals("统计_面图层"))
		{
			String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("图层名称"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数据量"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("面积("+areaUnit+")"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-20,-40}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("多值渲染"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("值域"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("符号"),1,1,3,3,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-50,-35}); //列宽度,负值表示比例
		}
		if (headerTag.equals("多值渲染_选择字段"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("字段名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-85}); //列宽度,负值表示比例
		}
		if (headerTag.equals("符号列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("符号"),1,1,3,3,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-40,-45}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("简单点符号列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("符号样式"),1,1,1,1,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("线符号定义列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("符号"),1,1,2,2,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-85}); //列宽度,负值表示比例
		}
		
		
		
		if (headerTag.equals("字段列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("类型"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("大小"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("精度"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数据字典"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-20,-10,-10,-20}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("图层字段列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("显示"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("类型"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("大小"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("精度"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("数据字典"),1,1,6,6,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-10,-30,-20,-10,-10,-20}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("每木检尺表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("编号"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("树种"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("树种代码"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("胸径(cm)"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("蓄积量"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{150,150,150,150,150}); //列宽度,负值表示比例
		}
		if (headerTag.equals("角规测树表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("序号"),1,1,1,1,true,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("树种"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("D"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("H"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("G"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-16,-21,-21,-21,-21}); //列宽度,负值表示比例
		}
		if (headerTag.equals("底图文件列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("坐标系"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-45,-40}); //列宽度,负值表示比例
		}
		if (headerTag.equals("当前图层"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("图层名称",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo("采集数量",1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50}); //列宽度,负值表示比例
		}
		if (headerTag.equals("字典类别列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("字典类别",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //列宽度,负值表示比例
		}
		if (headerTag.equals("条目大类列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("条目大类",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //列宽度,负值表示比例
		}
		if (headerTag.equals("条目细类列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("条目细类",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //列宽度,负值表示比例
		}
		if (headerTag.equals("多实体属性列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("图层",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo("类型",1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo("属性",1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-30,-20,-50}); //列宽度,负值表示比例
		}
		if (headerTag.equals("导航点列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("选择"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("名称"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("X坐标"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("Y坐标"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-10,-30,-30,-30}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("乡镇列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("乡(林场)"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("代码"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50,}); //列宽度,负值表示比例
		}
		
		if (headerTag.equals("村列表"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("村(营林区)"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("代码"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50,}); //列宽度,负值表示比例
		}
		
		//获取列头控件的宽度
		int ActualColumnWidth = 0;  //除比例外的设置列头宽度
		for(int w :_ReportHeaderWidthList){if (w>0)ActualColumnWidth+=w;}

		ViewGroup.LayoutParams lpheader = (ViewGroup.LayoutParams)headerView.getLayoutParams();
		
		//加入智能部分，也就是当列过少时自动适应宽度
		if (ActualColumnWidth<lpheader.width && ActualColumnWidth>0)
		{
			for(int i=0;i<_ReportHeaderWidthList.size();i++)
			{
				int Scale = (int)(((float)_ReportHeaderWidthList.get(i)/ (float)ActualColumnWidth)*100);
				_ReportHeaderWidthList.set(i, -Scale);
			}
			ActualColumnWidth=0;
		}
		
		//改进部分，可以适比例值，注意：-100表示占用全部空余空间,-30表示占用比例
		int spaceWidth = lpheader.width - ActualColumnWidth;
		for(int i=0;i<_ReportHeaderWidthList.size();i++)
		{
			int scaleT = _ReportHeaderWidthList.get(i);
			if (scaleT<0)
			{
				double aw = Math.abs(Double.parseDouble(scaleT+""))/100.0 * Double.parseDouble(spaceWidth+"");
				_ReportHeaderWidthList.set(i, (int)aw);
			}
		}
		
		//计算列的总宽度，值给相应视图，防止固定列的宽度不足或超出
		int ColumnWidth = 0;
		for(int w :_ReportHeaderWidthList)ColumnWidth+=w;
		
		ViewGroup.LayoutParams lpHeaderView = (ViewGroup.LayoutParams)headerView.getLayoutParams();
		lpHeaderView.width = ColumnWidth;
		headerView.setLayoutParams(lpHeaderView);
		
		ViewParent vp = headerView.getParent();
		View view = ((LinearLayout)vp).getChildAt(1);
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)view.getLayoutParams();
		lp.width = ColumnWidth;
		view.setLayoutParams(lp);
		
		//赋每个标签列的详细信息
		this.m_ReportHeader.SetHeaderInfoList(_ReportStructInfoList);
		
		//每列的宽度列表
		this.m_ReportHeader.SetHeaderWidthList(_ReportHeaderWidthList);
		
		//表头行高
		int RowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40, headerView.getResources().getDisplayMetrics());
		
		//动态生成表头
		for(ReportHeaderInfo header:_ReportStructInfoList)
		{
			int StartColLeftMargin = 0;int EndColLeftMargin = 0;
			for(int i=0;i<header.StartCol-1;i++)StartColLeftMargin+=_ReportHeaderWidthList.get(i);
			for(int i=0;i<header.EndCol;i++)EndColLeftMargin+=_ReportHeaderWidthList.get(i);
			
			TextView tv = new TextView(headerView.getContext());
			tv.setText(header.Text);
			tv.setBackgroundResource(R.layout.v1_bk_table_header);
			tv.setGravity(Gravity.CENTER);
			tv.setTextAppearance(headerView.getContext(),android.R.style.TextAppearance_Medium);
			tv.setTextColor(Color.BLACK);
			
			RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(0, 0);
			Params.leftMargin = StartColLeftMargin;
			Params.topMargin = (header.StartRow-1) * RowHeight;
			Params.width = (EndColLeftMargin - StartColLeftMargin);
			Params.height = (header.EndRow - header.StartRow+1)* RowHeight;
			headerView.addView(tv, Params);
		}

	}
	
	/**
	 * 转换dip到pix
	 * @param headerView
	 * @param dipList
	 * @return
	 */
	private List<Integer> ConverDipToPix(View headerView,int[] dipList)
	{
		List<Integer> _ReportHeaderWidthList = new ArrayList<Integer>();
		for(int w :dipList)//将Dip转换为pix
		{
			if (w<0)_ReportHeaderWidthList.add(w);
			else _ReportHeaderWidthList.add((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,w, headerView.getResources().getDisplayMetrics()));
		}
		return _ReportHeaderWidthList;
	}
	
	public class ReportHeaderInfo
	{
		public String Text;
		public int StartRow;
		public int EndRow;
		public int StartCol;
		public int EndCol;
		public ReportHeaderInfo(String text,int startRow,int endRow,int startCol,int endCol,boolean frozen,lkHeaderListViewItemType itemType)
		{
			this.Text = text;
			this.StartRow = startRow;
			this.EndRow = endRow;
			this.StartCol = startCol;
			this.EndCol = endCol;
			this.Frozen = frozen;
			this.ItemType = itemType;
		}
		public boolean Frozen = false;
		public lkHeaderListViewItemType ItemType = lkHeaderListViewItemType.enText;
		
	}
	
	public class ReportHeader
	{
		public boolean m_IsFrozen = true;
		
		//每列的宽度值
		public List<Integer> m_HeaderWidthList = null;
		
		//每个标签列的详细信息
		public List<ReportHeaderInfo> m_HeaderInfoList= null;
		public void SetHeaderInfoList(List<ReportHeaderInfo> _HeaderInfoList)
		{
			this.m_HeaderInfoList = _HeaderInfoList;
		}
		
		public void SetHeaderWidthList(List<Integer> _HeaderWidthList)
		{
			this.m_HeaderWidthList = _HeaderWidthList;
		}
		
		//报表项的高度列表，主要用于有固定列的情况
		public List<Integer> m_ItemsRelativeHeightList = null;
		public void SetItemsRelativeHeightList(List<Integer> _itemsHeightList)
		{
			this.m_ItemsRelativeHeightList = _itemsHeightList;
		}
	}
	

}
