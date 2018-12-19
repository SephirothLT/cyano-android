/*
 * *************************************************************************************
 *   Copyright © 2014-2018 Ontology Foundation Ltd.
 *   All rights reserved.
 *
 *   This software is supplied only under the terms of a license agreement,
 *   nondisclosure agreement or other written agreement with Ontology Foundation Ltd.
 *   Use, redistribution or other disclosure of any parts of this
 *   software is prohibited except in accordance with the terms of such written
 *   agreement with Ontology Foundation Ltd. This software is confidential
 *   and proprietary information of Ontology Foundation Ltd.
 *
 * *************************************************************************************
 */

package com.github.ont.cyanowallet.request;

import com.github.ont.cyanowallet.network.net.BaseTask;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.network.volley.Request;
import com.github.ont.cyanowallet.network.volley.VolleyError;
import com.github.ont.cyanowallet.utils.Constant;

import org.json.JSONObject;

/**
 * Created by shenyin.sy on 2018/3/24.
 */

public class BalanceReq extends BaseTask {

    private String address;

    public BalanceReq(String address) {
        this.address = address;
    }

    @Override
    public Result onSuccess(JSONObject jsonObject) {
        Result result = new Result(false);
        if (null == jsonObject) {
            return result;
        }
        int error = jsonObject.optInt("Error");
        if (error == 0) {
            result.isSuccess = true;
//            {"ong":"9980000000","ont":"7"}
//            JSONArray array = jsonObject.optJSONArray("Result");
//            JSONObject jsonResult = new JSONObject();
//            for (int i = 0; i < array.length(); i++) {
//                try {
//                    JSONObject jsonBean = array.getJSONObject(i);
//                    if (TextUtils.equals(jsonBean.getString("AssetName"), "ont")) {
//                        jsonResult.put(jsonBean.getString("AssetName"), jsonBean.getString("Balance"));
//                    } else if (TextUtils.equals(jsonBean.getString("AssetName"), "ong")) {
//                        String balance = jsonBean.getString("Balance");
//                        jsonResult.put(jsonBean.getString("AssetName"), CommonUtil.formatDoubleOngToString(balance));
//                    } else if (TextUtils.equals(jsonBean.getString("AssetName"), "totalpumpkin")) {
//                        String balance = jsonBean.getString("Balance");
//                        jsonResult.put(jsonBean.getString("AssetName"), jsonBean.getString("Balance"));
//                    } else {
//                        jsonResult.put(jsonBean.getString("AssetName"), jsonBean.getString("Balance"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
            result.info = jsonObject.toString();
        } else {
            result.isSuccess = false;
        }
        return result;
    }

    @Override
    public Result onFail(VolleyError error) {
        return new Result(false);
    }

    @Override
    public String getUrl() {
        return Constant.EXPLORER_SERVER + getPath() + address;
    }

    @Override
    public String getPath() {
        return Constant.GET_BALANCE;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public JSONObject getBody() {
        return null;
    }
}
