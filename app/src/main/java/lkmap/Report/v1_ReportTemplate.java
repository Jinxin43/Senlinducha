package lkmap.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dingtu.senlinducha.R;

import lkmap.Dataset.DataSource;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import dingtu.ZRoadMap.PubVar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class v1_ReportTemplate 
{
	private ReportHeader m_ReportHeader = null;
	public ReportHeader GetReportHeader(){return m_ReportHeader;}
	
	/**
	 * �����̶���ͷ
	 * @param frozenHeaderView 
	 * @param headerType ��ͷ����
	 */
	public void CreateReportHeaderForFrozen(RelativeLayout frozenHeaderView,String headerType)
	{
		this.CreateReport(frozenHeaderView,1, headerType);
	}
	
	/**
	 * �����ɹ�����ͷ
	 * @param scrollHeaderView
	 * @param headerType ��ͷ����
	 */
	public void CreateReportHeaderForScroll(RelativeLayout scrollHeaderView,String headerType)
	{
		this.CreateReport(scrollHeaderView,2, headerType);
	}
	
	/**
	 * ������ͷ
	 * @param headerView
	 * @param headerType  1-�̶��У�2-������
	 */
	private void CreateReport(RelativeLayout headerView,int headerType,String headerTag)
	{
		this.m_ReportHeader = new ReportHeader();
		if (headerType==1)this.m_ReportHeader.m_IsFrozen=true;
		if (headerType==2)this.m_ReportHeader.m_IsFrozen=false;
		
		//��ǩ�е���ϸ����
		List<ReportHeaderInfo> _ReportStructInfoList = new ArrayList<ReportHeaderInfo>();
		
		//����ÿ�еĿ��
		List<Integer> _ReportHeaderWidthList = null;
		
		switch(headerType)
		{
			case 1:  //�̶���
				
				//·��
				if (headerTag.equals("�ۺϲ�ѯ_·��_�����ȼ�+·������"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("·��\\�ȼ�",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //�п��
				}
				
				if (headerTag.equals("�ۺϲ�ѯ_·��_��̻���"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("��̷���",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80}); //�п��
				}
				
				if (headerTag.equals("�ۺϲ�ѯ_·��_·����ϸ"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("·��ʶ��",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{200}); //�п��
				}
				
				if (headerTag.equals("ͳ��_·��_�������ȼ�_����") || headerTag.equals("ͳ��_·��_��·������_����"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("��������",1,2,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //�п��
				}
				
				
				//����
				if (headerTag.equals("�ۺϲ�ѯ_����_���羶ͳ��"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{40}); //�п��
				}
				
				if (headerTag.equals("�ۺϲ�ѯ_����_��ʹ������ͳ��"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{40}); //�п��
				}
				
				if (headerTag.equals("�ۺϲ�ѯ_����_������ϸ"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("����ʶ��",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{200}); //�п��
				}

				if (headerTag.equals("ͳ��_����_���羶_����"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("��������",1,3,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //�п��
				}
				if (headerTag.equals("ͳ��_����_������_����"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("��������",1,3,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //�п��
				}
				

				break;
				
			case 2:	  //������
				if (headerType==2)
				{
					//·��
					if (headerTag.equals("�ۺϲ�ѯ_·��_�����ȼ�+·������"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("һ��",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ļ�",1,1,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,6,6,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,80,80,80}); //�п��
					}
					
					if (headerTag.equals("�ۺϲ�ѯ_·��_��̻���"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("��̻���",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��̷���",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��̻���",1,1,3,3,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,100,80}); //�п��
					}
					
					if (headerTag.equals("�ۺϲ�ѯ_·��_·����ϸ"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("�����ȼ�",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("·������",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("���",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ظ�·",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��ͷ·",1,1,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ǹ�·",1,1,6,6,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,50,50,50}); //�п��
					}
					
					if (headerTag.equals("ͳ��_·��_�������ȼ�_����"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("��·���",1,2,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ȼ���·",1,1,2,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",2,2,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("һ��",2,2,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",2,2,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",2,2,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ļ�",2,2,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,2,7,7,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,70,70,70,70,70,70}); //�п��
					}
					
					if (headerTag.equals("ͳ��_·��_��·������_����"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("��·���",1,2,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����װ·��",1,1,2,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("ˮ�������",2,2,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("���������",2,2,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("������ʯ",2,2,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�������ʽ",2,2,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("������װ·��",1,1,6,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("ɰʯ",2,2,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��ʯ",2,2,7,7,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("ש��",2,2,8,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��·��",1,2,9,9,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,90,90,90,80,90,70,70,70}); //�п��
					}
					
					//����
					if (headerTag.equals("�ۺϲ�ѯ_����_���羶ͳ��"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("�ϼ�",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ش���",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("С��",1,1,5,5,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,80,80}); //�п��
					}
					
					if (headerTag.equals("�ۺϲ�ѯ_����_��ʹ������ͳ��"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("�ϼ�",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("Σ��",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("������",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��������",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,5,5,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,80,80}); //�п��
					}
					
					if (headerTag.equals("�ۺϲ�ѯ_����_������ϸ"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("�ų�",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("Σ��",1,1,4,4,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,50}); //�п��
					}
					
					if (headerTag.equals("ͳ��_����_���羶_����"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("�ϼ�",1,2,1,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("���羶��",1,1,3,10,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ش���",2,2,3,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",2,2,5,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("����",2,2,7,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,7,7,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,8,8,false));
						
						_ReportStructInfoList.add(new ReportHeaderInfo("С��",2,2,9,10,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,9,9,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,10,10,false));
						
						_ReportStructInfoList.add(new ReportHeaderInfo("�ϼ���Σ��",1,2,11,12,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,11,11,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,12,12,false));
						
						 //�п��
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,70,70,70,70,70,70,70,70,70,70,70});
					}
					
					if (headerTag.equals("ͳ��_����_������_����"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("�ϼ�",1,2,1,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("���������Ϻ�ʹ�����޷�",1,1,3,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("������",2,2,3,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��������",2,2,5,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��ʱ��",2,2,7,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,7,7,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,8,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("�ϼ���Σ��",1,2,9,10,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,9,9,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("��",3,3,10,10,false));
						 //�п��
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,70,70,70,70,70,70,70,70,70});
					}
					
				}
				break;
		}
		

		//�����е��ܿ�ȣ�ֵ����Ӧ��ͼ����ֹ�̶��еĿ�Ȳ���򳬳�
		int ColumnWidth = 0;
		for(int w :_ReportHeaderWidthList)ColumnWidth+=w;
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
		int RowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30, headerView.getResources().getDisplayMetrics());
		
		//��̬���ɱ�ͷ
		for(ReportHeaderInfo header:_ReportStructInfoList)
		{
			int StartColLeftMargin = 0;int EndColLeftMargin = 0;
			for(int i=0;i<header.StartCol-1;i++)StartColLeftMargin+=_ReportHeaderWidthList.get(i);
			for(int i=0;i<header.EndCol;i++)EndColLeftMargin+=_ReportHeaderWidthList.get(i);
			
			TextView tv = new TextView(headerView.getContext());
			tv.setText(header.Text);
			tv.setTextColor(Color.BLACK);
			tv.setBackgroundResource(R.layout.v1_bk_table_header);
			tv.setGravity(Gravity.CENTER);
			
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
			_ReportHeaderWidthList.add((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,w, headerView.getResources().getDisplayMetrics()));
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
		public ReportHeaderInfo(String text,int startRow,int endRow,int startCol,int endCol,boolean frozen)
		{
			this.Text = text;
			this.StartRow = startRow;
			this.EndRow = endRow;
			this.StartCol = startCol;
			this.EndCol = endCol;
			this.Frozen = frozen;
		}
		public boolean Frozen = false;
	}
	
	public class ReportHeader
	{
		public boolean m_IsFrozen = true;
		
		//ÿ�еĿ��ֵ
		public List<Integer> m_HeaderWidthList = null;
		
		//ÿ����ǩ�е���ϸ��Ϣ
		private List<ReportHeaderInfo> m_HeaderInfoList= null;
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
