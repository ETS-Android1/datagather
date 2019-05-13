package com.ciba.datagather.manager;

import com.ciba.datagather.util.device.PackageUtil;
import com.ciba.datasynchronize.entity.OperationData;
import com.ciba.datasynchronize.manager.DataCacheManager;
import com.ciba.datasynchronize.manager.LoaderUploaderManager;
import com.ciba.datasynchronize.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ciba
 * @description 描述
 * @date 2019/3/1
 */
public class OpertionDataManager {
    private static OpertionDataManager instance;

    private OpertionDataManager() {
    }

    public static OpertionDataManager getInstance() {
        if (instance == null) {
            synchronized (OpertionDataManager.class) {
                if (instance == null) {
                    instance = new OpertionDataManager();
                }
            }
        }
        return instance;
    }

    public void uploadOpertionData(String operationType, String scheme) {
        CustomOperationData opertionData = createOpertionData(operationType, scheme);
        uploadOpertionData(opertionData);
    }

    public void uploadOpertionData(CustomOperationData customOperationData) {
        if (customOperationData == null) {
            return;
        }
        List<OperationData> operationDataList = new ArrayList<>();
        operationDataList.add(customOperationData);
        uploadOpertionData(operationDataList);
    }

    public void uploadOpertionDatas(List<CustomOperationData> operationDatas) {
        if (operationDatas == null || operationDatas.isEmpty()) {
            return;
        }
        List<OperationData> operationDataList = new ArrayList<>();
        operationDataList.addAll(operationDatas);
        uploadOpertionData(operationDataList);
    }

    private CustomOperationData createOpertionData(String operationType, String scheme) {
        return new CustomOperationData(operationType, scheme);
    }

    private void uploadOpertionData(List<OperationData> operationDataList) {
        if (operationDataList == null || operationDataList.isEmpty()) {
            return;
        }
        LoaderUploaderManager.getInstance().uploadOperationData(operationDataList);
    }

    public static class CustomOperationData extends OperationData {
        public CustomOperationData(String operationType, String scheme) {
            setOperationType(operationType);
            setMachineType(1);
            setScheme(scheme);
            setStartTime(TimeUtil.getCurrentTime());
            setPackageName(PackageUtil.getPackageName());
            setVersionNo(PackageUtil.getVersionName());
            setMachineId(DataCacheManager.getInstance().getMachineId());
        }
    }

}
