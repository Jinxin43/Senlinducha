package lkmap.UnRedo;
import java.util.ArrayList;
import java.util.List;
import lkmap.Enum.*;

/*定义回退的数据结构：操作命令(Command)，分类操作信息(ParaList），ReUndo标识(Type)
 * 操作命令：AllObject,MoveObject,Vertex_Move,Vertex_AddDel,SplitMerge
 * 操作信息：AllObject->图层名称，实体ObjectIndex1,实体ObjectIndex2,实体ObjectIndex.....
 * 

 * 
 *           MoveObject->图层名称，实体ObjectIndex,OffsetX,OffsetY
 *           Vertex_Move->图层名称，实体ObjectIndex，节点VertexIndex，原始坐标值（X:Y)，新坐标值(X:Y)
 *           Vertex_AddDel->图层名称，实体ObjectIndex，节点VertexIndex,节点坐标值
 *           SplitMerge->图层名称，实体ObjectIndex
 * */
public class UnRedoParaStru
{
    public lkReUndoCommand Command;          //操作命令
    public List<UnRedoDataItem> DataItemList;   //操作信息

    public UnRedoParaStru()
    {
        this.DataItemList = new ArrayList<UnRedoDataItem>();
    }

}


