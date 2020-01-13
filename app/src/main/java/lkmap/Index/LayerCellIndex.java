package lkmap.Index;

import java.util.ArrayList;
import java.util.List;

import lkmap.Map.StaticObject;

/* ͼ�㼶��������������Map��ȫ��������һ�£�Ҳ���ǻ���Cells*Cells����
 * ��ÿ�������м��¼�ű���ʵ�������ֵ�������ѯ�����
 */
public class LayerCellIndex 
{
    //ͼ�㼶��������������:CellIndex[0]=List<ObjectIndex>������:CellIndex[����������]=List<ʵ�����б��е�������>
    private List<ArrayList<Integer>> _CellIndex = null;              
    public List<ArrayList<Integer>> getCellIndex()
    {
        return _CellIndex;
    }
    public void setCellIndex(List<ArrayList<Integer>> value)
    {
    	_CellIndex=value;
    }


	public LayerCellIndex()
    {
        //��ʼ��ͼ��ĸ�������
        int Cells = 32;
        _CellIndex = new ArrayList<ArrayList<Integer>>(Cells*Cells);
        for (int i = 0; i < Cells * Cells; i++)
        {
        	_CellIndex.add(new ArrayList<Integer>());
        }
    }

    //����ָ�����������е�ʵ������ֵ
    public void SetIndex(List<Integer> IndexList, int ObjectIndex)
    {
        for (int IL : IndexList)
        {
        	_CellIndex.get(IL).add(ObjectIndex);
        }
    }

    //��ͼ�㼶��������ɾ��ָ��ʵ�������
    public void RemoveIndex(int ObjectIndex)
    {
        for (List<Integer> IdxList : this._CellIndex)
        {
            int idx = IdxList.indexOf(ObjectIndex);
            if (idx >= 0) 
            { 
            	IdxList.remove(idx); 
            }
        }
    }

    //����ͼ�㼶��ָ����ʵ�������ֵ
    public void UpdateIndex(List<Integer> IndexList, int ObjectIndex)
    {
        this.RemoveIndex(ObjectIndex);
        this.SetIndex(IndexList, ObjectIndex);
    }

    //�������ͼ�㼶����
    public void ClearAllIndex()
    {
        for (List<Integer> pIndex : this._CellIndex)
        {
            pIndex.clear();
        }
    }

    //��ǰ�ӿ���������������
    private List<Integer> _CurrentCellIndex = new ArrayList<Integer>();
    public List<Integer> getCurrentCellIndex()
    {
        return _CurrentCellIndex;
    }
    public void setCurrentCellIndex(List<Integer> value)
    {
    	_CurrentCellIndex = value;
    }
}
