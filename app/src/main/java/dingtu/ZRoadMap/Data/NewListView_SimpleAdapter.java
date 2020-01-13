package dingtu.ZRoadMap.Data;

import java.util.List;
import java.util.Map;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

public class NewListView_SimpleAdapter extends SimpleAdapter 
{
	private LayoutInflater mInflater;
	public NewListView mListView;
	
	int Count = 0;
	public NewListView_SimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		Count = data.size();
		mInflater = (LayoutInflater) (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.newlistview_item, null);
		}
		//校正（处理同时上下和左右滚动出现错位情况）
		View child = ((ViewGroup) convertView).getChildAt(1);
		int head = mListView.getHeadScrollX();
		if (child.getScrollX() != head) {
			child.scrollTo(mListView.getHeadScrollX(), 0);
		}
		return convertView;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Count;
	}
}
