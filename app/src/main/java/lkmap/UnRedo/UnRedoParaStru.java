package lkmap.UnRedo;
import java.util.ArrayList;
import java.util.List;
import lkmap.Enum.*;

/*������˵����ݽṹ����������(Command)�����������Ϣ(ParaList����ReUndo��ʶ(Type)
 * �������AllObject,MoveObject,Vertex_Move,Vertex_AddDel,SplitMerge
 * ������Ϣ��AllObject->ͼ�����ƣ�ʵ��ObjectIndex1,ʵ��ObjectIndex2,ʵ��ObjectIndex.....
 * 

 * 
 *           MoveObject->ͼ�����ƣ�ʵ��ObjectIndex,OffsetX,OffsetY
 *           Vertex_Move->ͼ�����ƣ�ʵ��ObjectIndex���ڵ�VertexIndex��ԭʼ����ֵ��X:Y)��������ֵ(X:Y)
 *           Vertex_AddDel->ͼ�����ƣ�ʵ��ObjectIndex���ڵ�VertexIndex,�ڵ�����ֵ
 *           SplitMerge->ͼ�����ƣ�ʵ��ObjectIndex
 * */
public class UnRedoParaStru
{
    public lkReUndoCommand Command;          //��������
    public List<UnRedoDataItem> DataItemList;   //������Ϣ

    public UnRedoParaStru()
    {
        this.DataItemList = new ArrayList<UnRedoDataItem>();
    }

}


