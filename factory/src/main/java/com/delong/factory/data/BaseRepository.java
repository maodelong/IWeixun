package com.delong.factory.data;

import android.support.annotation.NonNull;

import com.delong.factory.data.helper.DBHelper;
import com.delong.factory.model.db.BaseDbModel;
import com.delong.utils.CollectionUtil;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础的数据库仓库
 * 相当于观察者基类
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
@SuppressWarnings({"unchecked", "WeakerAccess", "ConstantConditions"})
public abstract class BaseRepository<Model extends BaseDbModel> implements DbDataSource<Model>, DBHelper.ChangeListener<Model>,
        QueryTransaction.QueryResultListCallback<Model> {
    private DataSource.SuccessCallback callback;
    protected final List<Model> models = new LinkedList<>();
    private Class<Model> modelClass;

    public BaseRepository() {
        Type[] types = Reflector.getActualTypeArguments(BaseRepository.class,this.getClass());
        modelClass = (Class<Model>) types[0];
    }

    @Override
    public void load(SuccessCallback<List<Model>> callback) {
        this.callback = callback;
        registerDbChangeListener();
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Model> tResult) {
        if (tResult==null||tResult.size()==0){
            models.clear();
            notifyChange();
            return;
        }
        Model[] models = CollectionUtil.toArray(tResult,modelClass);
        onDataSave(models);
    }

    @Override
    public void onDataSave(Model... models) {
        boolean isChanged = false;
        for (Model model : models) {
            if (isRequired(model)) {
                insertOrUpdate(model);
                isChanged = true;
            }
        }
        if (isChanged)
            notifyChange();
    }

    @Override
    public void onDataDelete(Model... models) {
        boolean isChanged = false;
        for (Model model : models) {
            if (this.models.remove(model))
                isChanged=true;
        }
        if (isChanged)
            notifyChange();
    }

    @Override
    public void dispose() {
        this.callback = null;
        DBHelper.removeChangeListener(modelClass,this);
    }

    private void notifyChange() {
        if (callback != null)
            callback.onDataLoaded(models);
    }

    public void insertOrUpdate(Model model) {
        int index = indexOf(model);
        if (index >= 0) {
            replace(index, model);
        } else {
            insert(model);
        }
    }

    public void replace(int index, Model model) {
        models.remove(index);
        models.add(model);
    }

    public void insert(Model model) {
        models.add(model);
    }


    private int indexOf(Model newModel) {
        int index = -1;
        for (Model model : models) {
            index++;
            if (model.isSame(newModel)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * 数据过滤，过滤掉你不需要的数据
     * 需要你自己具体实现
     * @param model 具体的数据的
     * @return true表示需要的数据 false不需要
     */
    protected abstract boolean isRequired(Model model);

    protected void registerDbChangeListener(){
        DBHelper.addChangeListener(modelClass,this);
    }
}
