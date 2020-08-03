/**
 * @author wysaid
 * @mail admin@wysaid.org
 *
*/

package com.yinge.opengl.camera.filter.base;

import android.content.Context;
import android.opengl.GLES20;

import com.yinge.opengl.camera.R;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageFilter;
import com.yinge.opengl.camera.filter.helper.GPUPower;
import com.yinge.opengl.camera.util.OpenGlUtils;


/**
 * 双边滤波器
 */
public class MagicBilateralFilter extends GPUImageFilter {
	
	private float mDistanceNormalizationFactor = 4.0f;
	private int mDisFactorLocation;
	private int mSingleStepOffsetLocation;
	
	public MagicBilateralFilter(Context context) {
		super(NO_FILTER_VERTEX_SHADER,
                GPUPower.mGPUPower == 1 ?
					OpenGlUtils.readShaderFromRawResource(R.raw.bilateralfilter):
					OpenGlUtils.readShaderFromRawResource(R.raw.bilateralfilter_low));
	}
	
	@Override
	protected void onInit() {
		super.onInit();
		mDisFactorLocation = GLES20.glGetUniformLocation(getProgram(), "distanceNormalizationFactor");
		mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
	}
	
	@Override
	protected void onInitialized() {
		super.onInitialized();
		setDistanceNormalizationFactor(mDistanceNormalizationFactor);
	}
	
	public void setDistanceNormalizationFactor(final float newValue) {
		mDistanceNormalizationFactor = newValue;
		setFloat(mDisFactorLocation, newValue);
	}
	
	private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] {2.0f / w, 2.0f / h});
	}
	
	@Override
    public void onInputSizeChanged(final int width, final int height) { // onDisplaySizeChanged?
        super.onInputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
