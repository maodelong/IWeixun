package com.delong.factory.data;

import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public interface DbDataSource<Data> extends DataSource{
    void load(SuccessCallback<List<Data>> callback);
}
