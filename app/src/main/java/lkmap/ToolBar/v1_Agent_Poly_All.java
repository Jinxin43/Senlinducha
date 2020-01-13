package lkmap.ToolBar;

import com.dingtu.senlinducha.R;

import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import lkmap.Edit.Vertex;
import lkmap.Enum.lkVertexEditType;
import lkmap.MapControl.Tools;

public class v1_Agent_Poly_All implements v1_IToolsBarCommand {

	@Override
	public void OnDispose() {
		// 清空按钮的选择状态
		this.ClearButtonSelect();
	}

	@Override
	public void OnChange() {

	}

	@Override
	public void OnPrepare() {
	}

	private View m_View = null;

	public void SetView(View view, boolean isVectorEdit) {
		this.m_View = view;
		this.m_View.findViewById(R.id.bt_movevertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_addvertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_delvertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_split).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_polysplit).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_publicborder).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_reshape).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_hole).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_merge).setOnClickListener(new ViewClick());
		if (isVectorEdit) {
			this.m_View.findViewById(R.id.bt_addvertex).setVisibility(View.GONE);
			this.m_View.findViewById(R.id.bt_delvertex).setVisibility(View.GONE);
			this.m_View.findViewById(R.id.bt_publicborder).setVisibility(View.GONE);
			this.m_View.findViewById(R.id.bt_polysplit).setVisibility(View.GONE);
		} else {
			this.m_View.findViewById(R.id.bt_addvertex).setVisibility(View.VISIBLE);
			this.m_View.findViewById(R.id.bt_delvertex).setVisibility(View.VISIBLE);
			this.m_View.findViewById(R.id.bt_publicborder).setVisibility(View.VISIBLE);
			this.m_View.findViewById(R.id.bt_polysplit).setVisibility(View.VISIBLE);
		}

	}

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			if (Tag.equals("移动节点")) {
				SetVertexMode(1);
			}
			if (Tag.equals("增加节点")) {
				SetVertexMode(2);
			}
			if (Tag.equals("删除节点")) {
				SetVertexMode(3);
			}
			if (Tag.equals("分割")) {
				v1_Agent_Poly_Split.StartClip();
			}
			if (Tag.equals("面割")) {
				v1_Agent_Poly_Split.StartPolyClip();
			}
			if (Tag.equals("公共边")) {
				v1_Agent_Poly_PublicBorder.StartPublicBorder();
			}
			if (Tag.equals("修边")) {
				// //测试
				// Polygon pPolygon = new Polygon();
				// Part part = new Part();
				// part.getVertexList().add(new Coordinate(29812477.9837
				// ,47822584.8651));
				// part.getVertexList().add(new Coordinate(29710166.4666
				// ,47772804.3378));
				// part.getVertexList().add(new Coordinate(29709292.1917
				// ,47674116.6753));
				// part.getVertexList().add(new Coordinate(29767006.3654
				// ,47618222.7420));
				// part.getVertexList().add(new Coordinate(29892928.1169
				// ,47612109.4486));
				// part.getVertexList().add(new Coordinate(29949767.7150
				// ,47697696.9072));
				// part.getVertexList().add(new Coordinate(29948893.1393
				// ,47798131.2412));
				// part.getVertexList().add(new Coordinate(29812477.9837
				// ,47822584.8651));
				// pPolygon.AddPart(part);
				//
				// List<Coordinate> clipPointList = new ArrayList<Coordinate>();
				// clipPointList.add(new Coordinate(29799470.6257
				// ,47764318.0932));
				// clipPointList.add(new Coordinate(29744363.0363
				// ,47805795.6553));
				// clipPointList.add(new Coordinate(29671684.5363
				// ,47799414.4226));
				// clipPointList.add(new Coordinate(29626959.6757
				// ,47730817.0157));
				// clipPointList.add(new Coordinate(29633348.7696
				// ,47650254.9945));
				// clipPointList.add(new Coordinate(29668489.9893
				// ,47625528.0274));
				// clipPointList.add(new Coordinate(29727590.9128
				// ,47673386.7096));
				// clipPointList.add(new Coordinate(29781900.0158
				// ,47687744.3143));
				// clipPointList.add(new Coordinate(29805859.7196
				// ,47681363.0816));
				// clipPointList.add(new Coordinate(29817839.7219
				// ,47624730.4015));
				// clipPointList.add(new Coordinate(29825027.6030
				// ,47573681.2155));
				// clipPointList.add(new Coordinate(29875343.3719
				// ,47574478.8415));
				// clipPointList.add(new Coordinate(29940034.9030
				// ,47593622.3143));
				// clipPointList.add(new Coordinate(29954410.9658
				// ,47629516.2697));
				// clipPointList.add(new Coordinate(29906491.2575
				// ,47667005.5895));
				// clipPointList.add(new Coordinate(29889719.4348
				// ,47711673.6554));
				// clipPointList.add(new Coordinate(29911283.0780
				// ,47749960.4885));
				// clipPointList.add(new Coordinate(29936041.8696
				// ,47764318.0932));
				// clipPointList.add(new Coordinate(30028686.4386
				// ,47770699.2133));
				//
				//
				// Polygon reShapePolygon =
				// PolyTools.ReshapePolygon(pPolygon,clipPointList);

				v1_Agent_Poly_Reshape.StartReshape();
			}
			if (Tag.equals("孤岛")) {
				v1_Agent_Poly_Hole.StartClipHole();
			}
			if (Tag.equals("合并")) {
				v1_Agent_Poly_Merge.StartMerge();
			}
		}
	}

	private Vertex m_Vertex = null; // 节点编辑工具
	// 设置节点模式

	private void SetVertexMode(int mode) {
		if (this.m_Vertex == null)
			this.m_Vertex = new Vertex(PubVar.m_MapControl);
		PubVar.m_MapControl.setActiveTools(Tools.MoveObject, this.m_Vertex, this.m_Vertex);
		switch (mode) {
		case 1:
			this.ClearButtonSelect();
			lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_movevertex), true);
			this.m_Vertex.SetVertexEditType(lkVertexEditType.enMove);
			break;
		case 2:
			this.ClearButtonSelect();
			lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_addvertex), true);
			this.m_Vertex.SetVertexEditType(lkVertexEditType.enAdd);
			break;
		case 3:
			this.ClearButtonSelect();
			lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_delvertex), true);
			this.m_Vertex.SetVertexEditType(lkVertexEditType.enDelete);
			break;
		}
	}

	public void ClearButtonSelect() {
		lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_movevertex), false);
		lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_addvertex), false);
		lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_delvertex), false);
		PubVar.m_DoEvent.m_MainBottomToolBar.ClearButtonSelect();
	}

}
