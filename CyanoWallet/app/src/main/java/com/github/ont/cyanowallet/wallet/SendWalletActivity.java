package com.github.ont.cyanowallet.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.BalanceReq;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ont.cyanowallet.view.PasswordDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class SendWalletActivity extends BaseActivity implements View.OnClickListener, SDKCallback {
    private static final String TAG = "SendWalletActivity";

    private EditText etAddress;
    private EditText etAmount;
    private Button btnType;
    private PasswordDialog passwordDialog;
    private BalanceReq balanceReq;
    private long ont;
    private long ong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_wallet);
        initView();
    }

    private void initView() {
        Button btnConfirm = (Button) findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnType = (Button) findViewById(R.id.btn_type);
        etAddress = (EditText) findViewById(R.id.et_address);
        etAmount = (EditText) findViewById(R.id.et_amount);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnType.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                String address = etAddress.getText().toString();
                String amount = etAmount.getText().toString();
                String type = btnType.getText().toString();
                showDialog(address, amount, type);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_type:
                if (TextUtils.equals(btnType.getText(), Constant.ONT)) {
                    btnType.setText(Constant.ONG);
                    etAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    setPoint(etAmount);
                } else {
                    btnType.setText(Constant.ONT);
                    etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                break;
            default:
        }
    }

    //小数的位数
    private static final int DECIMAL_DIGITS = 9;

    public static void setPoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + DECIMAL_DIGITS + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //显示付款
    private void showDialog(final String address, final String amount, final String type) {
        if (passwordDialog != null && passwordDialog.isShowing()) {
            passwordDialog.dismiss();
        }
        passwordDialog = new PasswordDialog(this);
        passwordDialog.setConfirmListener(new PasswordDialog.ConfirmListener() {
            @Override
            public void passwordConfirm(String password) {
                passwordDialog.dismiss();
                showLoading();
                sendTransaction(address, amount, type, password);
            }
        });
        passwordDialog.show();
    }

    private void sendTransaction(final String address, final String amount, final String type, final String password) {
        if (balanceReq != null) {
            balanceReq.cancel();
        }
        balanceReq = new BalanceReq(SPWrapper.getDefaultAddress());
        balanceReq.setRequestTag(TAG);
        balanceReq.setOnResultListener(new BaseRequest.ResultListener() {
            @Override
            public void onResult(Result result) {
                if (result.isSuccess) {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject((String) result.info);
                    JSONArray array = jsonObject.getJSONArray("Result");
                    com.alibaba.fastjson.JSONObject bean;
                    for (int i = 0; i < array.size(); i++) {
                        bean = array.getJSONObject(i);
                        switch (bean.getString("AssetName")) {
                            case Constant.ONG:
                                ong = CommonUtil.formatDoubleOngToLong(bean.getString("Balance"));
                                break;
                            case Constant.ONT:
                                ont = Long.parseLong(bean.getString("Balance"));
                                break;
                            default:
                        }
                    }
                    switch (type) {
                        case Constant.ONG:
                            long sendData = CommonUtil.formatDoubleOngToLong(amount);
                            if ((sendData + 10000000) > ong) {
                                dismissLoading();
                                ToastUtil.showToast(baseActivity, "insufficient ONG balance");
                                return;
                            } else {
                                SDKWrapper.transfer(SendWalletActivity.this, TAG, SPWrapper.getDefaultAddress(), etAddress.getText().toString(), password, sendData, Constant.ONG);
                            }
                            break;
                        case Constant.ONT:
                            if (Long.parseLong(amount) > ont) {
                                dismissLoading();
                                ToastUtil.showToast(baseActivity, "insufficient ONT balance");
                                return;
                            } else if (10000000 > ong) {
                                dismissLoading();
                                ToastUtil.showToast(baseActivity, "insufficient ONG balance,each will cost 0.01 ONG");
                                return;
                            } else {
                                SDKWrapper.transfer(SendWalletActivity.this, TAG, SPWrapper.getDefaultAddress(), address, password, Long.parseLong(amount), Constant.ONT);
                            }
                            break;
                        default:
                    }
                } else {
                    dismissLoading();
                    ToastUtil.showToast(baseActivity, "Net Error");
                }
            }

            @Override
            public void onResultFail(Result error) {
                dismissLoading();
                ToastUtil.showToast(baseActivity, "Net Error");
            }
        });
        balanceReq.excute();
    }

    @Override
    protected void onDestroy() {
        if (balanceReq != null) {
            balanceReq.cancel();
        }
        if (passwordDialog != null) {
            passwordDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onSDKSuccess(String tag, Object message) {
        ToastUtil.showToast(baseActivity, "Success");
        finish();
    }

    @Override
    public void onSDKFail(String tag, String message) {
        dismissLoading();
        showAttention(message);
    }
}
