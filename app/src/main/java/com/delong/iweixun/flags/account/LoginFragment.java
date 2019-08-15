package com.delong.iweixun.flags.account;

import android.content.Context;
import android.widget.EditText;
import com.delong.common.app.PresenterFragment;
import com.delong.factory.presenter.account.LoginContract;
import com.delong.factory.presenter.account.LoginPresenter;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.MainActivity;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginFragment extends PresenterFragment<LoginContract.Presenter> implements LoginContract.View {
    private AccountTrigger mAccountTrigger;

    @BindView(R.id.et_number)
    EditText et_number;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.loading)
    Loading loading;


    public LoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void loginSuccess() {
        MainActivity.show(Objects.requireNonNull(getContext()));
        Objects.requireNonNull(getActivity()).finish();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tex_go_register)
    void onShowLoginClick() {
        mAccountTrigger.triggerView();
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = et_number.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        mPresenter.login(phone, password);
    }


    @Override
    public void showLoading() {
        super.showLoading();
        loading.start();
        btn_submit.setEnabled(false);
        et_number.setEnabled(false);
        et_password.setEnabled(false);
    }

    @Override
    public void showError(int strId) {
        super.showError(strId);
        loading.stop();
        btn_submit.setEnabled(true);
        et_number.setEnabled(true);
        et_password.setEnabled(true);
    }

}
