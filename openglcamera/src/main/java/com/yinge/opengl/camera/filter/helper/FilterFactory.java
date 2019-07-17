package com.yinge.opengl.camera.filter.helper;


import com.yinge.opengl.camera.filter.advanced.MagicAmaroFilter;
import com.yinge.opengl.camera.filter.advanced.MagicAntiqueFilter;
import com.yinge.opengl.camera.filter.advanced.MagicBlackCatFilter;
import com.yinge.opengl.camera.filter.advanced.MagicBrannanFilter;
import com.yinge.opengl.camera.filter.advanced.MagicBrooklynFilter;
import com.yinge.opengl.camera.filter.advanced.MagicCalmFilter;
import com.yinge.opengl.camera.filter.advanced.MagicCoolFilter;
import com.yinge.opengl.camera.filter.advanced.MagicCrayonFilter;
import com.yinge.opengl.camera.filter.advanced.MagicEarlyBirdFilter;
import com.yinge.opengl.camera.filter.advanced.MagicEmeraldFilter;
import com.yinge.opengl.camera.filter.advanced.MagicEvergreenFilter;
import com.yinge.opengl.camera.filter.advanced.MagicFairytaleFilter;
import com.yinge.opengl.camera.filter.advanced.MagicFreudFilter;
import com.yinge.opengl.camera.filter.advanced.MagicHealthyFilter;
import com.yinge.opengl.camera.filter.advanced.MagicHefeFilter;
import com.yinge.opengl.camera.filter.advanced.MagicHudsonFilter;
import com.yinge.opengl.camera.filter.advanced.MagicImageAdjustFilter;
import com.yinge.opengl.camera.filter.advanced.MagicInkwellFilter;
import com.yinge.opengl.camera.filter.advanced.MagicKevinFilter;
import com.yinge.opengl.camera.filter.advanced.MagicLatteFilter;
import com.yinge.opengl.camera.filter.advanced.MagicLomoFilter;
import com.yinge.opengl.camera.filter.advanced.MagicN1977Filter;
import com.yinge.opengl.camera.filter.advanced.MagicNashvilleFilter;
import com.yinge.opengl.camera.filter.advanced.MagicNostalgiaFilter;
import com.yinge.opengl.camera.filter.advanced.MagicPixarFilter;
import com.yinge.opengl.camera.filter.advanced.MagicRiseFilter;
import com.yinge.opengl.camera.filter.advanced.MagicRomanceFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSakuraFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSierraFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSketchFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSkinWhitenFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSunriseFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSunsetFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSutroFilter;
import com.yinge.opengl.camera.filter.advanced.MagicSweetsFilter;
import com.yinge.opengl.camera.filter.advanced.MagicTenderFilter;
import com.yinge.opengl.camera.filter.advanced.MagicToasterFilter;
import com.yinge.opengl.camera.filter.advanced.MagicValenciaFilter;
import com.yinge.opengl.camera.filter.advanced.MagicWaldenFilter;
import com.yinge.opengl.camera.filter.advanced.MagicWarmFilter;
import com.yinge.opengl.camera.filter.advanced.MagicWhiteCatFilter;
import com.yinge.opengl.camera.filter.advanced.MagicXproIIFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageBrightnessFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageContrastFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageExposureFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageHueFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageSaturationFilter;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageSharpenFilter;

/**
 * 着色器 工厂
 */
public class FilterFactory {
	
	private static FilterType filterType = FilterType.NONE;
	
	public static GPUImageFilter initFilters(FilterType type){
		filterType = type;
		switch (type) {
		case WHITECAT:
			return new MagicWhiteCatFilter();
		case BLACKCAT:
			return new MagicBlackCatFilter();
		case SKINWHITEN:
			return new MagicSkinWhitenFilter();
		case ROMANCE:
			return new MagicRomanceFilter();
		case SAKURA:
			return new MagicSakuraFilter();
		case AMARO:
			return new MagicAmaroFilter();
		case WALDEN:
			return new MagicWaldenFilter();
		case ANTIQUE:
			return new MagicAntiqueFilter();
		case CALM:
			return new MagicCalmFilter();
		case BRANNAN:
			return new MagicBrannanFilter();
		case BROOKLYN:
			return new MagicBrooklynFilter();
		case EARLYBIRD:
			return new MagicEarlyBirdFilter();
		case FREUD:
			return new MagicFreudFilter();
		case HEFE:
			return new MagicHefeFilter();
		case HUDSON:
			return new MagicHudsonFilter();
		case INKWELL:
			return new MagicInkwellFilter();
		case KEVIN:
			return new MagicKevinFilter();
		case LOMO:
			return new MagicLomoFilter();
		case N1977:
			return new MagicN1977Filter();
		case NASHVILLE:
			return new MagicNashvilleFilter();
		case PIXAR:
			return new MagicPixarFilter();
		case RISE:
			return new MagicRiseFilter();
		case SIERRA:
			return new MagicSierraFilter();
		case SUTRO:
			return new MagicSutroFilter();
		case TOASTER2:
			return new MagicToasterFilter();
		case VALENCIA:
			return new MagicValenciaFilter();
		case XPROII:
			return new MagicXproIIFilter();
		case EVERGREEN:
			return new MagicEvergreenFilter();
		case HEALTHY:
			return new MagicHealthyFilter();
		case COOL:
			return new MagicCoolFilter();
		case EMERALD:
			return new MagicEmeraldFilter();
		case LATTE:
			return new MagicLatteFilter();
		case WARM:
			return new MagicWarmFilter();
		case TENDER:
			return new MagicTenderFilter();
		case SWEETS:
			return new MagicSweetsFilter();
		case NOSTALGIA:
			return new MagicNostalgiaFilter();
		case FAIRYTALE:
			return new MagicFairytaleFilter();
		case SUNRISE:
			return new MagicSunriseFilter();
		case SUNSET:
			return new MagicSunsetFilter();
		case CRAYON:
			return new MagicCrayonFilter();
		case SKETCH:
			return new MagicSketchFilter();
		//image adjust
		case BRIGHTNESS:
			return new GPUImageBrightnessFilter();
		case CONTRAST:
			return new GPUImageContrastFilter();
		case EXPOSURE:
			return new GPUImageExposureFilter();
		case HUE:
			return new GPUImageHueFilter();
		case SATURATION:
			return new GPUImageSaturationFilter();
		case SHARPEN:
			return new GPUImageSharpenFilter();
		case IMAGE_ADJUST:
			return new MagicImageAdjustFilter();
		default:
			return null;
		}
	}
	
	public FilterType getCurrentFilterType(){
		return filterType;
	}
}
