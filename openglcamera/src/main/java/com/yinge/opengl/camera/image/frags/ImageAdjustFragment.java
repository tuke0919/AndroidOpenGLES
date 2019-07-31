package com.yinge.opengl.camera.image.frags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.yinge.opengl.camera.R;
import com.yinge.opengl.camera.filter.helper.FilterAdjuster;
import com.yinge.opengl.camera.filter.helper.FilterType;
import com.yinge.opengl.camera.image.widget.TwoLineSeekBar;

@SuppressLint("ValidFragment")
public class ImageAdjustFragment extends BaseEditFragment{
    // 进度条
	private TwoLineSeekBar mSeekBar;
	// 对比度
	private float contrast = -50.0f;
	// 曝光
	private float exposure = 0.0f;
	// 饱和度
	private float saturation = 0.0f;
	// 锐化
	private float sharpness = 0.0f;
	// 亮度
	private float brightness = 0.0f;
	// 色调
	private float hue = 0.0f;
	// 按钮
	private RadioGroup mRadioGroup;

	// 过滤器类型
	private FilterType mCurrentFilterType = FilterType.NONE;

	private ImageView mLabel;
	private TextView mVal;
	private LinearLayout mSeekBarLayout;

	// 调整
    public OnAdjustListener mAdjustListener;
    // 滤镜调整器
    public FilterAdjuster mFilterAdjuster;
	
	public ImageAdjustFragment(Context context) {
		super(context);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_image_edit_adjust, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mRadioGroup = (RadioGroup)getView().findViewById(R.id.fragment_adjust_radiogroup);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId != -1)
				mSeekBarLayout.setVisibility(View.VISIBLE);
                if (checkedId == R.id.fragment_radio_contrast) {
                    mCurrentFilterType = FilterType.CONTRAST;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-100, 100, -50, 1);
                    mSeekBar.setValue(contrast);
                    mLabel.setBackgroundResource(R.drawable.selector_image_edit_adjust_contrast);

                } else if (checkedId == R.id.fragment_radio_exposure) {
                    mCurrentFilterType = FilterType.EXPOSURE;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-100, 100, 0, 1);
                    mSeekBar.setValue(exposure);
                    mLabel.setBackgroundResource(R.drawable.selector_image_edit_adjust_exposure);

                } else if (checkedId == R.id.fragment_radio_saturation) {
                    mCurrentFilterType = FilterType.SATURATION;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-100, 100, 0, 1);
                    mSeekBar.setValue(saturation);
                    mLabel.setBackgroundResource(R.drawable.selector_image_edit_adjust_saturation);

                } else if (checkedId == R.id.fragment_radio_sharpness) {
                    mCurrentFilterType = FilterType.SHARPEN;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-100, 100, 0, 1);
                    mSeekBar.setValue(sharpness);
                    mLabel.setBackgroundResource(R.drawable.selector_image_edit_adjust_saturation);

                } else if (checkedId == R.id.fragment_radio_bright) {
                    mCurrentFilterType = FilterType.BRIGHTNESS;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-100, 100, 0, 1);
                    mSeekBar.setValue(brightness);

                } else if (checkedId == R.id.fragment_radio_hue) {
                    mCurrentFilterType = FilterType.HUE;
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(0, 360, 0, 1);
                    mSeekBar.setValue(hue);

                } else {
                }
			}
		});

		mSeekBar = (TwoLineSeekBar)view.findViewById(R.id.item_seek_bar);
		mSeekBar.setOnSeekChangeListener(mOnSeekChangeListener);
		mVal = (TextView)view.findViewById(R.id.item_val);
		mLabel = (ImageView)view.findViewById(R.id.item_label);
		mSeekBarLayout = (LinearLayout)view.findViewById(R.id.seek_bar_item_menu);

		// 设置组合滤镜
        if (mOnFilterChangeListener != null) {
            mOnFilterChangeListener.onSetFilter(FilterType.IMAGE_ADJUST);
        }
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){
			contrast = -50.0f;
			exposure = 0.0f; 
			saturation = 0.0f;
			sharpness = 0.0f;
			brightness = 0.0f;
			hue = 0.0f;
			mRadioGroup.clearCheck();
            if (mOnFilterChangeListener != null) {
                mOnFilterChangeListener.onSetFilter(FilterType.NONE);
            }
			mSeekBarLayout.setVisibility(View.INVISIBLE);
			mCurrentFilterType = FilterType.NONE;
		}else{
            // 设置组合滤镜
            if (mOnFilterChangeListener != null) {
                mOnFilterChangeListener.onSetFilter(FilterType.IMAGE_ADJUST);
            }
		}
	}
	
	protected boolean isChanged(){
		return contrast != -50.0f
                || exposure != 0.0f
                || saturation != 0.0f
				|| sharpness != 0.0f
                || brightness != 0.0f
                || hue != 0.0f;
	}


	private TwoLineSeekBar.OnSeekChangeListener mOnSeekChangeListener = new TwoLineSeekBar.OnSeekChangeListener() {
		
		@Override
		public void onSeekStopped(float value, float step) {

		}
		
		@Override
		public void onSeekChanged(float value, float step) {
			mVal.setText(""+value);
			mLabel.setPressed(value != 0.0f);
			// 回调
			if (mAdjustListener != null) {
                mAdjustListener.onAdjustFilter(convertToProgress(value), mCurrentFilterType);
            }
		}
	};

    /**
     * 转换成进度
     * @param value
     * @return
     */
    private int convertToProgress(float value){
        int i = mRadioGroup.getCheckedRadioButtonId();
        if (i == R.id.fragment_radio_contrast) {
            contrast = value;
            return (int) Math.round((value + 100) / 2);
        } else if (i == R.id.fragment_radio_exposure) {
            exposure = value;
            return (int) Math.round((value + 100) / 2);
        } else if (i == R.id.fragment_radio_saturation) {
            saturation = value;
            return (int) Math.round((value + 100) / 2);
        } else if (i == R.id.fragment_radio_sharpness) {
            sharpness = value;
            return (int) Math.round((value + 100) / 2);
        } else if (i == R.id.fragment_radio_bright) {
            brightness = value;
            return (int) Math.round((value + 100) / 2);
        } else if (i == R.id.fragment_radio_hue) {
            hue = value;
            return (int) Math.round(100 * value / 360.0f);
        } else {
            return 0;
        }
    }

    /**
     * 设置 监听器
     * @param mAdjustListener
     */
    public void setAdjustListener(OnAdjustListener mAdjustListener) {
        this.mAdjustListener = mAdjustListener;
    }

    /**
     * 监听器
     */
	public interface OnAdjustListener{

        /**
         * 调整
         * @param progress          进度
         * @param currentFilterType 当前调整的滤镜类型
         */
	    void onAdjustFilter(int progress, FilterType currentFilterType);
    }

    public FilterAdjuster getFilterAdjuster() {
        return mFilterAdjuster;
    }

    public void setFilterAdjuster(FilterAdjuster mFilterAdjuster) {
        this.mFilterAdjuster = mFilterAdjuster;
    }
}
