package com.yinge.opengl.camera.filter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yinge.opengl.camera.R;
import com.yinge.opengl.camera.filter.helper.FilterType;
import com.yinge.opengl.camera.filter.helper.FilterTypeHelper;

import java.util.Arrays;
import java.util.List;

/**
 * 相机滤镜 适配器
 */
public class CameraFilterAdapter extends BaseRecycleViewAdapter<FilterType>{
    
    private int mSelected = 0;

    public CameraFilterAdapter(Context context){
        this(context, null);
    }
    public CameraFilterAdapter(Context context, List<FilterType> filters) {
        super(context, filters);
        setDataList(Arrays.asList(mFilterTypes));
    }

    @Override
    public int getConvertViewResId(int itemViewType) {
        return R.layout.filter_item_layout;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(int viewType, View rootView) {
        return new FilterHolder(rootView);
    }

    /**
     * holder
     */
    public class FilterHolder extends BaseViewHolder<FilterType> {
        private View mViewRoot;
        private ImageView mFilterImage;
        private TextView mFilterName;
        private FrameLayout mFilterSelected;
        private View mFilterSelectedBg;

        public FilterHolder(View itemView) {
            super(itemView);
            mViewRoot = itemView;
            mFilterImage = (ImageView) itemView.findViewById(R.id.filter_image);
            mFilterName = (TextView) itemView.findViewById(R.id.filter_name);
            mFilterSelected = (FrameLayout) itemView.findViewById(R.id.filter_selected);
            mFilterSelectedBg = itemView.findViewById(R.id.filter_selected_bg);
        }

        @Override
        public void onRefreshData(final int position, final FilterType data) {

            mFilterImage.setImageResource(FilterTypeHelper.FilterType2Image(data));
            mFilterName.setText(FilterTypeHelper.FilterType2Name(data));
            mFilterName.setBackgroundColor(context.getResources().getColor(FilterTypeHelper.FilterType2Color(data)));
            if(position == mSelected){
                mFilterSelected.setVisibility(View.VISIBLE);
                mFilterSelectedBg.setBackgroundColor(context.getResources().getColor(FilterTypeHelper.FilterType2Color(data)));
                mFilterSelectedBg.setAlpha(0.7f);
            }else {
                mFilterSelected.setVisibility(View.GONE);
            }

            mViewRoot.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(mSelected == position)
                        return;
                    int lastSelected = mSelected;
                    mSelected = position;
                    notifyItemChanged(lastSelected);
                    notifyItemChanged(position);
                    if (onFilterChangeListener != null) {
                        onFilterChangeListener.onFilterChanged(data);
                    }
                }
            });

        }
    }

    /**
     * 滤镜改变
     */
    public interface onFilterChangeListener{
        /**
         * @param filterType 滤镜改变
         */
        void onFilterChanged(FilterType filterType);
    }
    // 监听器
    private onFilterChangeListener onFilterChangeListener;

    /**
     * @param onFilterChangeListener
     */
    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener){
        this.onFilterChangeListener = onFilterChangeListener;
    }

    // 滤镜数组
    private final FilterType[] mFilterTypes = new FilterType[]{
            FilterType.NONE,
            FilterType.FAIRYTALE,
            FilterType.SUNRISE,
            FilterType.SUNSET,
            FilterType.WHITECAT,
            FilterType.BLACKCAT,
            FilterType.SKINWHITEN,
            FilterType.HEALTHY,
            FilterType.SWEETS,
            FilterType.ROMANCE,
            FilterType.SAKURA,
            FilterType.WARM,
            FilterType.ANTIQUE,
            FilterType.NOSTALGIA,
            FilterType.CALM,
            FilterType.LATTE,
            FilterType.TENDER,
            FilterType.COOL,
            FilterType.EMERALD,
            FilterType.EVERGREEN,
            FilterType.CRAYON,
            FilterType.SKETCH,
            FilterType.AMARO,
            FilterType.BRANNAN,
            FilterType.BROOKLYN,
            FilterType.EARLYBIRD,
            FilterType.FREUD,
            FilterType.HEFE,
            FilterType.HUDSON,
            FilterType.INKWELL,
            FilterType.KEVIN,
            FilterType.LOMO,
            FilterType.N1977,
            FilterType.NASHVILLE,
            FilterType.PIXAR,
            FilterType.RISE,
            FilterType.SIERRA,
            FilterType.SUTRO,
            FilterType.TOASTER2,
            FilterType.VALENCIA,
            FilterType.WALDEN,
            FilterType.XPROII
    };
}
