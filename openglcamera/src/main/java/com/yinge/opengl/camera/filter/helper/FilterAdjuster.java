package com.yinge.opengl.camera.filter.helper;


import android.util.Log;

import com.yinge.opengl.camera.filter.advanced.MagicImageAdjustFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageBrightnessFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageContrastFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageExposureFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageHueFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageSaturationFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageSharpenFilter;

import static com.yinge.opengl.camera.filter.helper.FilterType.*;
import static com.yinge.opengl.camera.filter.helper.FilterType.BRIGHTNESS;
import static com.yinge.opengl.camera.filter.helper.FilterType.CONTRAST;
import static com.yinge.opengl.camera.filter.helper.FilterType.EXPOSURE;
import static com.yinge.opengl.camera.filter.helper.FilterType.SATURATION;
import static com.yinge.opengl.camera.filter.helper.FilterType.SHARPEN;

/**
 * 
 * idea from  jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.FilterAdjuster
 */
public class FilterAdjuster {
    // 调整器接口
	private final Adjuster<? extends GPUImageFilter> adjuster;

    public FilterAdjuster(final GPUImageFilter filter) {
        if (filter instanceof GPUImageSharpenFilter) {
            // 锐化调整器
            adjuster = new SharpnessAdjuster().filter(filter);
        } else if (filter instanceof GPUImageContrastFilter) {
            // 饱和度调整器
            adjuster = new ContrastAdjuster().filter(filter);
        } else if (filter instanceof GPUImageHueFilter) {
            // 色调调整器
            adjuster = new HueAdjuster().filter(filter);
        } else if (filter instanceof GPUImageSaturationFilter) {
            // 对比度调整器
            adjuster = new SaturationAdjuster().filter(filter);
        } else if (filter instanceof GPUImageExposureFilter) {
            // 曝光调整器
            adjuster = new ExposureAdjuster().filter(filter);
        } else if (filter instanceof GPUImageBrightnessFilter) {
            // 亮度调整器
            adjuster = new BrightnessAdjuster().filter(filter);
        } else if (filter instanceof MagicImageAdjustFilter) {
            // 组合滤镜 调整器
            adjuster = new ImageAdjustAdjuster().filter(filter);
        } else {
            adjuster = null;
        }
    }

    public boolean canAdjust() {
        return adjuster != null;
    }
    
    public void adjust(final int percentage) {
        if (adjuster != null) {
            adjuster.adjust(percentage);
        }
    }
    
    public void adjust(final int percentage,final FilterType type) {
        if (adjuster != null) {
            adjuster.adjust(percentage, type);
        }
    }

    /**
     * 调整器接口
     * @param <T>
     */
    private abstract class Adjuster<T extends GPUImageFilter> {
        private T filter;
        
        @SuppressWarnings("unchecked")
        public Adjuster<T> filter(final GPUImageFilter filter) {
            this.filter = (T) filter;
            return this;
        }

		public T getFilter() {
            return filter;
        }

        /**
         * @param percentage
         */
        public abstract void adjust(int percentage);

        /**
         * @param percentage
         * @param type
         */
        public void adjust(int percentage, FilterType type) {
        	adjust(percentage);
		}
        
        protected float range(final int percentage, final float start, final float end) {
            return (end - start) * percentage / 100.0f + start;
        }

        protected int range(final int percentage, final int start, final int end) {
            return (end - start) * percentage / 100 + start;
        }
    }

    private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
    	
        @Override
        public void adjust(final int percentage) {
            getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
        }
    }

    private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
    	
        @Override
        public void adjust(final int percentage) {
        	getFilter().setHue(range(percentage, 0.0f, 360.0f));
        }
    }

    private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
       	
        @Override
        public void adjust(final int percentage) {
            getFilter().setContrast(range(percentage, 0.0f, 4.0f));
        }
    }

    private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
    	    	
        @Override
        public void adjust(final int percentage) {
            getFilter().setBrightness(range(percentage, -0.5f, 0.5f));
        }
    }
    
    private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
    	
        @Override
        public void adjust(final int percentage) {
            getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
        }
    }
    
    private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setExposure(range(percentage, -2.0f, 2.0f));
        }
    }  

    // 组合滤镜 调整器
    private class ImageAdjustAdjuster extends Adjuster<MagicImageAdjustFilter> {

        @Override
        public void adjust(final int percentage) {
            
        }
        
        public void adjust(final int percentage, final FilterType type) {
            Log.e("editor", "filterType = " + type.name() + " percentage = " + percentage);
            switch (type) {
			case CONTRAST:
				getFilter().setContrast(range(percentage, 0.0f, 4.0f));
				break;
			case SHARPEN:
				getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
				break;
			case SATURATION:
				getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
				break;
			case EXPOSURE:
				getFilter().setExposure(range(percentage, -2.0f, 2.0f));
				break;
			case BRIGHTNESS:
				getFilter().setBrightness(range(percentage, -0.5f, 0.5f));
				break;
			case HUE:
				getFilter().setHue(range(percentage, 0.0f, 360.0f));
				break;
			default:
				break;
			}
        }
    }  
}
