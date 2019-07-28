package com.yinge.opengl.camera.filter.advanced;

import com.yinge.opengl.camera.filter.base.MagicBaseGroupFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageBrightnessFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageContrastFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageExposureFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageHueFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageSaturationFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageSharpenFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 对比度，曝光度，饱和度，锐化，亮度，色调 组合滤镜
 */
public class MagicImageAdjustFilter extends MagicBaseGroupFilter{
	
	public MagicImageAdjustFilter() {
		super(initFilters());
	}
	
	private static List<GPUImageFilter> initFilters(){
		List<GPUImageFilter> filters = new ArrayList<GPUImageFilter>();
		filters.add(new GPUImageContrastFilter());
		filters.add(new GPUImageBrightnessFilter());
		filters.add(new GPUImageExposureFilter());
		filters.add(new GPUImageHueFilter());
		filters.add(new GPUImageSaturationFilter());
		filters.add(new GPUImageSharpenFilter());
		return filters;		
	}
	
	public void setSharpness(final float range){
		((GPUImageSharpenFilter) filters.get(5)).setSharpness(range);
	}
	
	public void setHue(final float range){
		((GPUImageHueFilter) filters.get(3)).setHue(range);
	}
	
	public void setBrightness(final float range){
		((GPUImageBrightnessFilter) filters.get(1)).setBrightness(range);
	}
	
	public void setContrast(final float range){
		((GPUImageContrastFilter) filters.get(0)).setContrast(range);
	}
	
	public void setSaturation(final float range){
		((GPUImageSaturationFilter) filters.get(4)).setSaturation(range);
	}
	
	public void setExposure(final float range){
		((GPUImageExposureFilter) filters.get(2)).setExposure(range);
	}
}
