package com.yinge.opengl.camera.image.frags;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.yinge.opengl.camera.filter.helper.FilterType;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/28
 * @email tuke@corp.xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class BaseEditFragment extends Fragment {

    protected Context mContext;
    protected onHideListener mOnHideListener;
    protected onFilterChangeListener mOnFilterChangeListener;

    public BaseEditFragment(Context mContext) {
        this.mContext = mContext;
    }

    public void onHide(){
        if(isChanged()){
            // 是否保存修改
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示").setMessage("是否应用修改？").setNegativeButton("是", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDialogButtonClick(dialog);
//                    mMagicDisplay.commit();
                }
            }).setPositiveButton("否", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDialogButtonClick(dialog);
//                    mMagicDisplay.restore();
                }
            }).create().show();
        }else{
            // 回调隐藏方法
            if (mOnHideListener != null) {
                mOnHideListener.onHide();
            }
        }
    }

    /**
     * @return 是否改变了
     */
    protected abstract boolean isChanged();


    /**
     * @param dialog
     */
    protected void onDialogButtonClick(DialogInterface dialog){
        if(mOnHideListener != null) {
            mOnHideListener.onHide();
        }
        dialog.dismiss();
    }

    /**
     * 设置 隐藏监听器
     * @param l
     */
    public void setOnHideListener(onHideListener l){
        this.mOnHideListener = l;
    }

    public interface onHideListener{
        void onHide();
    }

    /**
     * @param mOnFilterChangeListener
     */
    public void setOnFilterChangeListener(onFilterChangeListener mOnFilterChangeListener) {
        this.mOnFilterChangeListener = mOnFilterChangeListener;
    }

    /**
     * 设置滤镜
     */
    public interface onFilterChangeListener{
        /**
         * 设置
         * @param filterType
         */
        void onSetFilter(FilterType filterType);
    }

}
