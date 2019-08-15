package com.delong.factory.model.db;

import com.delong.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public abstract class BaseDbModel<Model> extends BaseModel implements DiffUiDataCallback.UiDataDiffer<Model> {
}
