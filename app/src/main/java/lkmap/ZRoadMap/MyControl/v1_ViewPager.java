package lkmap.ZRoadMap.MyControl;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import dingtu.ZRoadMap.Data.ICallback;

public class v1_ViewPager 
{
	private ViewPager m_ViewPager = null;
	public v1_ViewPager(ViewPager viewPager)
	{
		this.m_ViewPager = viewPager;
	}
	
	/**
	 * …Ë÷√“≥¡–±Ì
	 * @param viewList
	 */
	public void SetListView(List<View> viewList)
	{
    	this.m_ViewPager.setAdapter(new MyPagerAdapter(viewList));
    	this.m_ViewPager.setCurrentItem(0);
    	this.m_ViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	private View m_LocatorView = null;
	public void SetLocatorView(View view)
	{
		this.m_LocatorView = view;
	}
	
	private ICallback m_OnPageSelected = null;
	public void SetICallback(ICallback callback)
	{
		this.m_OnPageSelected = callback;
	}
	
	
    /**
     * “≥ø®«–ªªº‡Ã˝
*/
    public class MyOnPageChangeListener implements OnPageChangeListener 
    {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) 
		{
			if (m_LocatorView!=null)
			{
				int PageCount = m_ViewPager.getAdapter().getCount();
				int scrollLen = m_ViewPager.getWidth() / PageCount;
				int StartPos = 0;
				if (m_LocatorView.getTag()!=null)StartPos = Integer.parseInt(m_LocatorView.getTag()+"");
				m_LocatorView.setTag(scrollLen*arg0);
				Animation anim = new TranslateAnimation(StartPos,scrollLen*arg0, 0, 0);
				anim.setFillAfter(true);
				anim.setDuration(300);
				m_LocatorView.setAnimation(anim);
			}
			if (m_OnPageSelected!=null)m_OnPageSelected.OnClick("",arg0);
		}
    }
    
    /**
     * ViewPager  ≈‰∆˜
*/
    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
}
