package dingtu.ZRoadMap.Data;

import com.dingtu.DTGIS.LDBG.LinDiBianGengData;
import com.dingtu.DTGIS.LDBG.XiaoBanXuji;
import com.dingtu.DTGIS.TuiGeng.TuiGengData;
import com.dingtu.SLDuCha.TuBanYanZheng;

import android.graphics.Canvas;
import android.view.MotionEvent;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.Dataset;
import lkmap.Enum.ForestryLayerType;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.IOnTouchCommand;
import lkmap.Tools.Tools;

public class v1_CGpsPoly implements IOnTouchCommand, IOnPaint {

	/**
	 * 设置相关数据集Dataset
	 * 
	 * @param pDataset
	 */
	public void SetDataset(Dataset pDataset) {
		this.m_CGPSLine.SetDataset(pDataset);
	}

	public Dataset GetDataset() {
		return this.m_CGPSLine.GetDataset();
	}

	private v1_CGpsLine m_CGPSLine = null;

	public v1_CGpsLine getGPSLine() {
		return m_CGPSLine;
	}

	public v1_CGpsPoly() {
		this.m_CGPSLine = new v1_CGpsLine();
		this.m_CGPSLine.SetIfCalArea(true); // 是否计算面积
	}

	/**
	 * 编辑属性
	 * 
	 * @param LayerID
	 *            图层ID
	 * @param SYS_ID
	 *            实体ID
	 */
	public void Edit(final String LayerID, final int SYS_ID) {

		if (!PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID))
			return;

		String projectType = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerID).GetLayerProjecType();
		if (projectType != null && projectType.equals(ForestryLayerType.TuigengLayer)) {
			Tools.OpenDialog(new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					TuiGengData tuiGengData = new TuiGengData(LayerID, SYS_ID, false);

				}
			});

		} else if (projectType != null && projectType.equals(ForestryLayerType.DuChaYanZheng)) {
			TuBanYanZheng tuBanYanZheng = new TuBanYanZheng(LayerID, SYS_ID);
		} else if (projectType != null && projectType.equals(ForestryLayerType.LindibiangengLayer)) {
			Tools.OpenDialog(new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					LinDiBianGengData ldbg = new LinDiBianGengData(LayerID, SYS_ID);
					ldbg.ShowView();

				}
			});

		} else if (projectType != null && projectType.equals(ForestryLayerType.XiaoBanXuji)) {
			XiaoBanXuji wpzf = new XiaoBanXuji(LayerID, SYS_ID);
			wpzf.ShowView();
		} else {
			Tools.OpenDialog(new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					Edit(LayerID, SYS_ID, null);

				}
			});
		}
	}

	public void Edit(String LayerID, int SYS_ID, ICallback cb) {
		// TanhuiDataTemplate _DT = new TanhuiDataTemplate();
		// _DT.SetEditInfo(LayerID, SYS_ID);
		// _DT.SetCallback(cb);
		// _DT.ShowDialog();
		GeneralDateEditor dataEdit = new GeneralDateEditor(LayerID, SYS_ID);
		dataEdit.SetCallback(cb);
	}

	@Override
	public void OnPaint(Canvas canvas) {
		this.m_CGPSLine.OnPaint(canvas);
	}

	@Override
	public void SetOnTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		this.m_CGPSLine.SetOnTouchEvent(e);
	}

}
