package lkmap.ToolBar;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

public class EditToolbar {
	private View m_EditBarView = null;

	public void SetEditToolbar(View editBarView) {
		this.m_EditBarView = editBarView;
		editBarView.findViewById(R.id.bt_hidetoolsbar).setOnClickListener(new ViewClick());
	}

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			// �༭��������������չ��
			if (Tag.equals("����")) {
				ShowEditToolsBar(false);
				arg0.setTag("չ��");
				// Button bt = (Button)arg0;
				// //bt.setText("չ��");
				// Drawable bmp =
				// PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.arrow_show);
				// bmp.setBounds(0, 0, bmp.getMinimumWidth(),
				// bmp.getMinimumHeight());
				// bt.setCompoundDrawables(null, null, null, bmp); //������ͼ��
			}

			if (Tag.equals("չ��")) {
				ShowEditToolsBar(true);
				arg0.setTag("����");
				// Button bt = (Button)arg0;
				// //bt.setText("����");
				// Drawable bmp =
				// PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.arrow_hidden);
				// bmp.setBounds(0, 0, bmp.getMinimumWidth(),
				// bmp.getMinimumHeight());
				// bt.setCompoundDrawables(null, null, null, bmp); //������ͼ��
			}
		}
	}

	/**
	 * �Ƿ�չʾ�༭������
	 * 
	 * @param ifShow
	 */
	private void ShowEditToolsBar(boolean ifShow) {
		if (this.m_EditBarView == null)
			return;

		int YFrom = 0, YTo = 1;
		int intVisible = View.INVISIBLE;
		if (ifShow) {
			intVisible = View.VISIBLE;
			YFrom = 1;
			YTo = 0;
		}
		View view = this.m_EditBarView.findViewById(R.id.ll_toolsbarbox);
		// view.setVisibility(intVisible);
		TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, YFrom, Animation.RELATIVE_TO_SELF, YTo);
		mHiddenAction.setDuration(300);
		view.startAnimation(mHiddenAction);
		view.setVisibility(intVisible);

	}

	// �����������б�
	private HashMap<String, v1_IToolsBarCommand> m_ToolsBarCommandList = new HashMap<String, v1_IToolsBarCommand>();

	// ��հ�ť��ѡ��״̬
	public void ClearButtonSelect() {
		for (v1_IToolsBarCommand tbc : this.m_ToolsBarCommandList.values())
			tbc.OnDispose();
	}

	public void ShowToolsItem(String ToolsName, boolean isVectorEdit) {
		int[] SubToolsIdList = { R.id.ic_editbar_poly_all, R.id.ic_editbar_line_all, R.id.bt_hidetoolsbar };

		LinearLayout LL = (LinearLayout) m_EditBarView.findViewById(R.id.ll_toolsbox);
		LL.setPadding(0, 0, 0, 0);

		// ���������ӹ�����
		for (int SubToolsId : SubToolsIdList)
			this.m_EditBarView.findViewById(SubToolsId).setVisibility(View.GONE);
		((Button) this.m_EditBarView.findViewById(R.id.bt_hidetoolsbar)).setText(ToolsName);

		// ����¼�
		for (v1_IToolsBarCommand tbc : this.m_ToolsBarCommandList.values())
			tbc.OnDispose();

		// if (ToolsName.equals("����"))
		// {
		// View view = this.m_EditBarView.findViewById(R.id.ic_editbar_measure);
		// view.setVisibility(View.VISIBLE);
		// if (!this.m_ToolsBarCommandList.containsKey(ToolsName))
		// {
		// v1_Agent_Measure agent_Measure = new v1_Agent_Measure();
		// agent_Measure.SetView(view);
		// this.m_ToolsBarCommandList.put(ToolsName, agent_Measure);
		// }
		// this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		// }
		// if (ToolsName.equals("�ƶ�ɾ��"))
		// {
		// View view = this.m_EditBarView.findViewById(R.id.ic_editbar_movedel);
		// view.setVisibility(View.VISIBLE);
		// if (!this.m_ToolsBarCommandList.containsKey(ToolsName))
		// {
		// v1_Agent_MoveDelete agent_MoveDelete = new v1_Agent_MoveDelete();
		// agent_MoveDelete.SetView(view);
		// this.m_ToolsBarCommandList.put(ToolsName, agent_MoveDelete);
		// }
		// this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		// }
		// if (ToolsName.equals("�ڵ�༭"))
		// {
		// View view = this.m_EditBarView.findViewById(R.id.ic_editbar_vertex);
		// view.setVisibility(View.VISIBLE);
		// if (!this.m_ToolsBarCommandList.containsKey(ToolsName))
		// {
		// v1_Agent_Vertex agent_Vertex = new v1_Agent_Vertex();
		// agent_Vertex.SetView(view);
		// this.m_ToolsBarCommandList.put(ToolsName, agent_Vertex);
		// }
		// this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		// }

		if (ToolsName.equals("�湤��")) {
			this.m_EditBarView.findViewById(R.id.bt_hidetoolsbar).setVisibility(View.VISIBLE);
			View view = this.m_EditBarView.findViewById(R.id.ic_editbar_poly_all);
			view.setVisibility(View.VISIBLE);
			// if (!this.m_ToolsBarCommandList.containsKey(ToolsName)) {
			v1_Agent_Poly_All agent_Poly_All = new v1_Agent_Poly_All();
			agent_Poly_All.SetView(view, isVectorEdit);
			this.m_ToolsBarCommandList.put(ToolsName, agent_Poly_All);
			// }
			this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		}
		if (ToolsName.equals("�߹���")) {
			this.m_EditBarView.findViewById(R.id.bt_hidetoolsbar).setVisibility(View.VISIBLE);
			View view = this.m_EditBarView.findViewById(R.id.ic_editbar_line_all);
			view.setVisibility(View.VISIBLE);
			if (!this.m_ToolsBarCommandList.containsKey(ToolsName)) {
				v1_Agent_Line_All agent_Line_All = new v1_Agent_Line_All();
				agent_Line_All.SetView(view);
				this.m_ToolsBarCommandList.put(ToolsName, agent_Line_All);
			}
			this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		}

		// if (ToolsName.equals("��ָ�"))
		// {
		// View view =
		// this.m_EditBarView.findViewById(R.id.ic_editbar_poly_split);
		// view.setVisibility(View.VISIBLE);
		// if (!this.m_ToolsBarCommandList.containsKey(ToolsName))
		// {
		// v1_Agent_Poly_Split agent_Poly_Split = new v1_Agent_Poly_Split();
		// agent_Poly_Split.SetView(view);
		// this.m_ToolsBarCommandList.put(ToolsName, agent_Poly_Split);
		// }
		// this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		// }
		//
		// if (ToolsName.equals("�湫����"))
		// {
		// View view =
		// this.m_EditBarView.findViewById(R.id.ic_editbar_poly_publicborder);
		// view.setVisibility(View.VISIBLE);
		// if (!this.m_ToolsBarCommandList.containsKey(ToolsName))
		// {
		// v1_Agent_Poly_PublicBorder agent_Poly_PublicBorder = new
		// v1_Agent_Poly_PublicBorder();
		// agent_Poly_PublicBorder.SetView(view);
		// this.m_ToolsBarCommandList.put(ToolsName, agent_Poly_PublicBorder);
		// }
		// this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		// }
		// if (ToolsName.equals("��µ�"))
		// {
		// View view =
		// this.m_EditBarView.findViewById(R.id.ic_editbar_poly_hole);
		// view.setVisibility(View.VISIBLE);
		// if (!this.m_ToolsBarCommandList.containsKey(ToolsName))
		// {
		// v1_Agent_Poly_Hole agent_Poly_Hole = new v1_Agent_Poly_Hole();
		// agent_Poly_Hole.SetView(view);
		// this.m_ToolsBarCommandList.put(ToolsName, agent_Poly_Hole);
		// }
		// this.m_ToolsBarCommandList.get(ToolsName).OnPrepare();
		// }
		//
		//
		// if (ToolsName.equals("�ߴ��"))
		// {
		// //this.m_EditBarView.findViewById(R.id.ic_editbar_drawline).setVisibility(View.VISIBLE);
		// }
		// if (ToolsName.equals("������"))
		// {
		// //this.m_EditBarView.findViewById(R.id.ic_editbar_drawline).setVisibility(View.VISIBLE);
		// }
		//
		// if (ToolsName.equals("����Բ��"))
		// {
		// this.m_EditBarView.findViewById(R.id.ic_editbar_linesmoth).setVisibility(View.VISIBLE);
		// }

		new Handler().postDelayed(new Runnable() {
			public void run() {
				// �жϸ߶�
				int ScrollViewHeight = m_EditBarView.findViewById(R.id.sv_toolsbox).getHeight();
				int LLViewHeight = m_EditBarView.findViewById(R.id.ll_toolsbox).getHeight();
				LinearLayout LL = (LinearLayout) m_EditBarView.findViewById(R.id.ll_toolsbox);
				if (LLViewHeight < ScrollViewHeight) {
					LL.setPadding(0, ScrollViewHeight - LLViewHeight, 0, 0);
				}
			}
		}, 100 * 1);
	}
}
