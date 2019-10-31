package com.ciba.data.gather.manager.oaid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.JLibrary;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;
import com.ciba.callback.IIdentifierListenerImp;
import com.ciba.data.gather.util.ExceptionUtils;
import com.ciba.data.synchronize.util.StateUtil;

/**
 * oaid获取
 *
 * @author parting_soul
 * @date 2019-10-24
 */
public class OAIDDelegate {

    private OnGetIdCallback mListener;
    private Context mContext;

    public OAIDDelegate(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        try {
            JLibrary.InitEntry(context);
        } catch (Exception e) {
            ExceptionUtils.printStackTrace(e);
        }
    }

    public void setOnGetIdCallback(OnGetIdCallback callback) {
        this.mListener = callback;
    }


    /**
     * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
     */
    private int callFromReflect(Context cxt) {
        IIdentifierListenerImp listener = new IIdentifierListenerImp();
        listener.setCallback(new IIdentifierListenerImp.Callback() {
            @Override
            public void OnSupport(boolean b, IdSupplier idSupplier) {
                if (idSupplier == null) {
                    return;
                }
                String oaid = idSupplier.getOAID();
                idSupplier.shutDown();
                if (mListener != null) {
                    mListener.onIdGetSuccess(oaid);
                }
            }
        });
        int result = 0;
        try {
            result = MdidSdkHelper.InitSdk(cxt, true, listener);
        } catch (Exception e) {
            ExceptionUtils.printStackTrace(e);
        }
        return result;
    }


    public void startGetOAID() {
        int result = callFromReflect(mContext);
        String error = "";
        if (result == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {
            error = "不支持的设备";
        } else if (result == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {
            error = "加载配置文件出错";
        } else if (result == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {
            error = "不支持的设备厂商";
        } else if (result == ErrorCode.INIT_ERROR_RESULT_DELAY) {
            error = "获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程";
        } else if (result == ErrorCode.INIT_HELPER_CALL_ERROR) {
            error = "反射调用出错";
        }
        if (!StateUtil.checkFlag() && !TextUtils.isEmpty(error)) {
            Log.e("OAIDDelegate", "startGetOAID: oaid获取失败 error " + error);
        }
    }

    public interface OnGetIdCallback {
        void onIdGetSuccess(@NonNull String ids);
    }

}
