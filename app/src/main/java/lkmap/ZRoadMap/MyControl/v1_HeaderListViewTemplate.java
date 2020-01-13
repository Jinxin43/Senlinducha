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
	 * ������ͷ
	 * @param headerView
	 */
	public void CreateReport(RelativeLayout headerView,String headerTag,List<HashMap<String,Object>> FieldHeaderList)
	{
		this.m_ReportHeader = new ReportHeader();
		
		//��ǩ�е���ϸ����
		List<ReportHeaderInfo> _ReportStructInfoList = new ArrayList<ReportHeaderInfo>();
		
		//����ÿ�еĿ��
		List<Integer> _ReportHeaderWidthList = null;
		
		if (headerTag.equals("SQL��ѯ�����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��ѯ����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��ϵ"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-70,-15}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("��ѯ����б�"))
		{
			int[] headerWidth = new int[FieldHeaderList.size()];
			for(int i=0;i<FieldHeaderList.size();i++)
			{
				HashMap<String,Object> fieldObj = FieldHeaderList.get(i);
				_ReportStructInfoList.add(new ReportHeaderInfo(fieldObj.get("FieldName")+"",1,1,(i+1),(i+1),false,lkHeaderListViewItemType.enText));
				headerWidth[i]=100;
			}
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, headerWidth); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("��ѯ����б�1"))
		{
			int[] headerWidth = new int[FieldHeaderList.size()+1];
			headerWidth[0] = 50;
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			for(int i=0;i<FieldHeaderList.size();i++)
			{
				HashMap<String,Object> fieldObj = FieldHeaderList.get(i);
				_ReportStructInfoList.add(new ReportHeaderInfo(fieldObj.get("FieldName")+"",1,1,(i+2),(i+2),false,lkHeaderListViewItemType.enText));
				headerWidth[i+1]=100;
			}
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, headerWidth); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("����ӷ������"))
		{
			String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ��"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͳ����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("���\n("+areaUnit+")"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("�ٷֱ�"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-20,-30,-30,-20}); //�п��,��ֵ��ʾ����
		}
		
		//ģ���б�
		if (headerTag.equals("�ҵ�����ϵ"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����ϵͳ"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("���뾭��"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����ת��"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-25,-25,-15,-20}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("�����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��������"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����ʱ��"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��ϸ"),1,1,4,4,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-40,-30,-15}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("ͼ���б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��ʾ"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,4,4,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-50,-15,-20}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("����ͼ���б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-50,-15,-20}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("�ϴ�ͼ���б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("�ϴ���Ƭ"),1,1,5,5,false,lkHeaderListViewItemType.enCheckBox));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-40,-15,-15,-15}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("ͼ��ģ���б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ������"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ������"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("������"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-25,-35}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("����ͼ��ģ���б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ������"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ������"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("ͳ��_��ͼ��"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ������"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("������"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("ͳ��_��ͼ��"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ������"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("������"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����(��)"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-20,-40}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("ͳ��_��ͼ��"))
		{
			String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ͼ������"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("������"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("���("+areaUnit+")"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-20,-40}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("��ֵ��Ⱦ"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ֵ��"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,3,3,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-50,-35}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("��ֵ��Ⱦ_ѡ���ֶ�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("�ֶ�����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-85}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("�����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,3,3,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-40,-45}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("�򵥵�����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("������ʽ"),1,1,1,1,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("�߷��Ŷ����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enImage));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-85}); //�п��,��ֵ��ʾ����
		}
		
		
		
		if (headerTag.equals("�ֶ��б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��С"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("�����ֵ�"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-40,-20,-10,-10,-20}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("ͼ���ֶ��б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��ʾ"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��С"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("�����ֵ�"),1,1,6,6,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-10,-30,-20,-10,-10,-20}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("ÿľ��߱�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("���"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("���ִ���"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("�ؾ�(cm)"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("�����"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{150,150,150,150,150}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("�ǹ������"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("���"),1,1,1,1,true,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("D"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("H"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("G"),1,1,5,5,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-16,-21,-21,-21,-21}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("��ͼ�ļ��б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����ϵ"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-15,-45,-40}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("��ǰͼ��"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("ͼ������",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo("�ɼ�����",1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("�ֵ�����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("�ֵ����",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("��Ŀ�����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("��Ŀ����",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("��Ŀϸ���б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("��Ŀϸ��",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-100}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("��ʵ�������б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo("ͼ��",1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-30,-20,-50}); //�п��,��ֵ��ʾ����
		}
		if (headerTag.equals("�������б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("ѡ��"),1,1,1,1,false,lkHeaderListViewItemType.enCheckBox));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("X����"),1,1,3,3,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("Y����"),1,1,4,4,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-10,-30,-30,-30}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("�����б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��(�ֳ�)"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50,}); //�п��,��ֵ��ʾ����
		}
		
		if (headerTag.equals("���б�"))
		{
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("��(Ӫ����)"),1,1,1,1,false,lkHeaderListViewItemType.enText));
			_ReportStructInfoList.add(new ReportHeaderInfo(Tools.ToLocale("����"),1,1,2,2,false,lkHeaderListViewItemType.enText));
			_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{-50,-50,}); //�п��,��ֵ��ʾ����
		}
		
		//��ȡ��ͷ�ؼ��Ŀ��
		int ActualColumnWidth = 0;  //���������������ͷ���
		for(int w :_ReportHeaderWidthList){if (w>0)ActualColumnWidth+=w;}

		ViewGroup.LayoutParams lpheader = (ViewGroup.LayoutParams)headerView.getLayoutParams();
		
		//�������ܲ��֣�Ҳ���ǵ��й���ʱ�Զ���Ӧ���
		if (ActualColumnWidth<lpheader.width && ActualColumnWidth>0)
		{
			for(int i=0;i<_ReportHeaderWidthList.size();i++)
			{
				int Scale = (int)(((float)_ReportHeaderWidthList.get(i)/ (float)ActualColumnWidth)*100);
				_ReportHeaderWidthList.set(i, -Scale);
			}
			ActualColumnWidth=0;
		}
		
		//�Ľ����֣������ʱ���ֵ��ע�⣺-100��ʾռ��ȫ������ռ�,-30��ʾռ�ñ���
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
		
		//�����е��ܿ�ȣ�ֵ����Ӧ��ͼ����ֹ�̶��еĿ�Ȳ���򳬳�
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
		
		//��ÿ����ǩ�е���ϸ��Ϣ
		this.m_ReportHeader.SetHeaderInfoList(_ReportStructInfoList);
		
		//ÿ�еĿ���б�
		this.m_ReportHeader.SetHeaderWidthList(_ReportHeaderWidthList);
		
		//��ͷ�и�
		int RowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40, headerView.getResources().getDisplayMetrics());
		
		//��̬���ɱ�ͷ
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
	 * ת��dip��pix
	 * @param headerView
	 * @param dipList
	 * @return
	 */
	private List<Integer> ConverDipToPix(View headerView,int[] dipList)
	{
		List<Integer> _ReportHeaderWidthList = new ArrayList<Integer>();
		for(int w :dipList)//��Dipת��Ϊpix
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
		
		//ÿ�еĿ��ֵ
		public List<Integer> m_HeaderWidthList = null;
		
		//ÿ����ǩ�е���ϸ��Ϣ
		public List<ReportHeaderInfo> m_HeaderInfoList= null;
		public void SetHeaderInfoList(List<ReportHeaderInfo> _HeaderInfoList)
		{
			this.m_HeaderInfoList = _HeaderInfoList;
		}
		
		public void SetHeaderWidthList(List<Integer> _HeaderWidthList)
		{
			this.m_HeaderWidthList = _HeaderWidthList;
		}
		
		//������ĸ߶��б���Ҫ�����й̶��е����
		public List<Integer> m_ItemsRelativeHeightList = null;
		public void SetItemsRelativeHeightList(List<Integer> _itemsHeightList)
		{
			this.m_ItemsRelativeHeightList = _itemsHeightList;
		}
	}
	

}
