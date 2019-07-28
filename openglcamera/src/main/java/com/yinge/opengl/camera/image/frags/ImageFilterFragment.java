package com.yinge.opengl.camera.image.frags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yinge.opengl.camera.R;
import com.yinge.opengl.camera.filter.adapter.CameraFilterAdapter;
import com.yinge.opengl.camera.filter.helper.FilterType;

import java.util.Arrays;


@SuppressLint("ValidFragment")
public class ImageFilterFragment extends BaseEditFragment implements View.OnClickListener {

    // 滤镜列表
    private LinearLayout mCameraFilterLayout;
    private RecyclerView mFilterRecyclerView;
    private ImageView mCloseFilter;

    // 适配器
    private CameraFilterAdapter mAdapter;

	public ImageFilterFragment(Context context) {
		super(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_image_edit_filter, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews(view);
	}

	public void initViews(View view) {
        mCameraFilterLayout = (LinearLayout)view.findViewById(R.id.camera_filter_layout);
        mFilterRecyclerView = (RecyclerView) view.findViewById(R.id.camera_filter_recyclerview);
        mCloseFilter = view.findViewById(R.id.btn_camera_close_filter);

        mCloseFilter.setOnClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterRecyclerView.setLayoutManager(manager);

        // 设置适配器
        mAdapter = new CameraFilterAdapter(getContext());
        mFilterRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(new CameraFilterAdapter.onFilterChangeListener() {
            @Override
            public void onFilterChanged(FilterType filterType) {
                // 设置滤镜
                if (mOnFilterChangeListener != null) {
                    mOnFilterChangeListener.onSetFilter(filterType);
                }
            }
        });
    }

	@Override
	public void onHiddenChanged(boolean hidden) {
		if(!hidden){

        }
//			mFilterLayoutUtils.init(getView());
	}

	@Override
	protected boolean isChanged() {
//		return mFilterLayoutUtils.getFilterType() != MagicFilterType.NONE;
        return false;
	}

    @Override
    public void onClick(View v) {

    }
}
