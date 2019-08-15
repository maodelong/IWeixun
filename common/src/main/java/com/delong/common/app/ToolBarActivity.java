package com.delong.common.app;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import com.delong.common.R;

public abstract class ToolBarActivity extends Activity {
    protected Toolbar mToolbar;

    @Override
    protected void initWidget() {
        super.initWidget();
        iniToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void iniToolbar(Toolbar toolbar){
        mToolbar = toolbar;
        if (toolbar!=null){
            setSupportActionBar(toolbar);
        }
        initTitleNeedBack();
    }

    protected void initTitleNeedBack(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
