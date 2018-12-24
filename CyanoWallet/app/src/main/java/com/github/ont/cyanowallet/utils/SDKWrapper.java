package com.github.ont.cyanowallet.utils;

import android.content.SharedPreferences;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.ScanGetTransactionReq;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.MnemonicCode;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.Ong;
import com.github.ontio.smartcontract.nativevm.Ont;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SDKWrapper {
    private static final String TAG = "SDKWrapper";
    private static final long GAS_LIMIT = 20000;
    private static final long GAS_PRICE = 500;
    private static OntSdk ontSdk = OntSdk.getInstance();


    public static void initOntSDK(final SDKCallback callback, final String tag, final String restUrl, final SharedPreferences path) {

        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ontSdk.setRestful(restUrl);
                ontSdk.setDefaultConnect(ontSdk.getRestful());
                ontSdk.openWalletFile(path);
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                callback.onSDKSuccess(tag, aBoolean);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public static void createIdentity(final SDKCallback callback, final String tag, final String password, final String address) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
//                Transaction tx = ontSdk.nativevm().ontId().sendRegister(identity,password,)
                ontSdk.getWalletMgr().writeWallet();
                emitter.onNext("");
                emitter.onComplete();
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String res) {
                callback.onSDKSuccess(tag, res);
            }

            @Override
            public void onError(Throwable e) {
//                try {
//                    SharedPreferences sp = OCApplication.getMyApplicationContext().getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
//                    OntSdk.getInstance().openWalletFile(sp);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void importIdentity(final SDKCallback callback, final String tag, final String key, final String password, final String address, final String saltStr) {
        final OntSdk ontSdk = OntSdk.getInstance();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                byte[] salt = Base64.decode(saltStr, Base64.NO_WRAP);
                Identity identity = ontSdk.getWalletMgr().importIdentity(key, password, salt, address);
                emitter.onNext("");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String res) {


                callback.onSDKSuccess(tag, res);
            }

            @Override
            public void onError(Throwable e) {
//                try {
//                    SharedPreferences sp = OCApplication.getMyApplicationContext().getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
//                    OntSdk.getInstance().openWalletFile(sp);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void createWallet(final SDKCallback callback, final String tag, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String mnemonicCodes = MnemonicCode.generateMnemonicCodesStr();
                byte[] prikeyFromMnemonicCodesStr = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(mnemonicCodes);
                String hexString = Helper.toHexString(prikeyFromMnemonicCodesStr);
                Account account = ontSdk.getWalletMgr().createAccountFromPriKey(password, hexString);
                String encryptedMnemonicCodesStr = MnemonicCode.encryptMnemonicCodesStr(mnemonicCodes, password, account.address);
                ontSdk.getWalletMgr().writeWallet();
                emitter.onNext(account.address);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String key) {
                callback.onSDKSuccess(tag, key);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void importWallet(final SDKCallback callback, final String tag, final String key, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            final OntSdk ontSdk = OntSdk.getInstance();

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Account account;
                if (key.length() == 52) {
                    //wif
                    byte[] bytes = com.github.ontio.account.Account.getPrivateKeyFromWIF(key);
                    account = ontSdk.getWalletMgr().createAccountFromPriKey(password, Helper.toHexString(bytes));
                } else {
                    account = ontSdk.getWalletMgr().createAccountFromPriKey(password, key);
                }
                ontSdk.getWalletMgr().writeWallet();
                emitter.onNext(account.address);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String key) {
                callback.onSDKSuccess(tag, key);
            }

            @Override
            public void onError(Throwable e) {

                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public static void getSendAddress(final SDKCallback callback, final String tag, final String data, final String password, final String address) {
        Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                OntSdk ontSdk = OntSdk.getInstance();
                Transaction[] transactions = ontSdk.makeTransactionByJson(data);
                Transaction transaction = transactions[0];
                Account account = ontSdk.getWalletMgr().getWallet().getAccount(address);
                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                ontSdk.signTx(transaction, new com.github.ontio.account.Account[][]{{account1}});
                ontSdk.setRestful("http://139.219.136.147:20334");
                ontSdk.setDefaultConnect(ontSdk.getRestful());
                Object o = ontSdk.getConnect().sendRawTransactionPreExec(transaction.toHexString());
                ArrayList<String> result = new ArrayList<>();
                result.add(JSON.toJSONString(o));
                result.add(transaction.toHexString());
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<String> s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getGameLogin(final SDKCallback callback, final String tag, final String password, final String data) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String address = SPWrapper.getDefaultAddress();
                Account account = OntSdk.getInstance().getWalletMgr().getWallet().getAccount(address);
                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                DataSignature sign1 = new DataSignature(OntSdk.getInstance().defaultSignScheme, account1, data.getBytes());
                String signature = Helper.toHexString(sign1.signature());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", "login");
                jsonObject.put("error", 0);
                jsonObject.put("desc", "SUCCESS");
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("publicKey", Helper.toHexString(account1.serializePublicKey()));
                jsonObject1.put("type", "account");
                jsonObject1.put("address", address);
                jsonObject1.put("message", data);
                jsonObject1.put("signature", signature);
                jsonObject.put("result", jsonObject1);
                emitter.onNext(jsonObject.toString());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void transfer(final SDKCallback callback, final String tag, final String sendAddress, final String receiveAddress, final String password, final long amount, final String type) {
        Observable.create(new ObservableOnSubscribe<String>() {
            final OntSdk ontSdk = OntSdk.getInstance();

            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Transaction tx = null;
                Account sendAccount = ontSdk.getWalletMgr().getWallet().getAccount(sendAddress);
                byte[] salt = sendAccount.getSalt();
                if (type.equalsIgnoreCase(Constant.ONT)) {
                    Ont ontAssetTx = ontSdk.nativevm().ont();
                    Transaction transaction = ontAssetTx.makeTransfer(sendAddress, receiveAddress, amount, sendAddress, GAS_LIMIT, GAS_PRICE);
                    tx = ontSdk.signTx(transaction, sendAddress, password, salt);
                } else if (type.equalsIgnoreCase(Constant.ONG)) {
                    Ong ong = ontSdk.nativevm().ong();
                    Transaction transaction = ong.makeTransfer(sendAddress, receiveAddress, amount, sendAddress, GAS_LIMIT, GAS_PRICE);
                    tx = ontSdk.signTx(transaction, sendAddress, password, salt);
                } else {
                    throw new Exception("not valid asset");
                }
                ontSdk.getConnect().sendRawTransaction(tx);
                emitter.onNext(tx.hash().toHexString());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String key) {
                callback.onSDKSuccess(tag, key);
            }

            @Override
            public void onError(Throwable e) {

                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void sendTransactionHex(final SDKCallback callback, final String tag, final String data) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Transaction transaction = Transaction.deserializeFrom(Helper.hexToBytes(data));
                boolean b = OntSdk.getInstance().getConnect().sendRawTransaction(transaction);
                if (b) {
                    emitter.onNext(transaction.hash().toString());
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable(""));
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void verifyTX(final SDKCallback callback, final String tag, final String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                ScanGetTransactionReq scanGetTransactionReq = new ScanGetTransactionReq(url);
                scanGetTransactionReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        emitter.onNext((String) result.info);
                    }

                    @Override
                    public void onResultFail(Result error) {
                        emitter.onError(new Throwable(""));
                    }
                });
                scanGetTransactionReq.excute();
            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(final String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        final OntSdk ontSdk = OntSdk.getInstance();
                        Transaction[] transactions = ontSdk.makeTransactionByJson(s);
                        Transaction transaction = transactions[0];
//                        Transaction transaction = Transaction.deserializeFrom(Helper.hexToBytes(s));
//                        boolean b = ontSdk.verifyTransaction(transaction);
                        emitter.onNext(transaction.payer.toBase58());
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void scanLoginSign(final SDKCallback callback, final String tag, final String data, final String address, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Account account = OntSdk.getInstance().getWalletMgr().getWallet().getAccount(address);
                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                DataSignature sign1 = new DataSignature(OntSdk.getInstance().defaultSignScheme, account1, data.getBytes());
                byte[] sign = account1.generateSignature(data.getBytes(), SignatureScheme.SHA256WITHECDSA, null);
                String publicKey = Helper.toHexString(account1.serializePublicKey());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "account");
                jsonObject.put("user", address);
                jsonObject.put("message", data);
                jsonObject.put("publickey", publicKey);
                jsonObject.put("signature", Helper.toHexString(sign));
                emitter.onNext(jsonObject.toString());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void scanAddSign(final SDKCallback callback, final String tag, final String qrcodeUrl, final String address, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                ScanGetTransactionReq scanGetTransactionReq = new ScanGetTransactionReq(qrcodeUrl);
                scanGetTransactionReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        emitter.onNext((String) result.info);
                    }

                    @Override
                    public void onResultFail(Result error) {
                        emitter.onError(new Throwable(""));
                    }
                });
                scanGetTransactionReq.excute();

            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(final String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        OntSdk instance = OntSdk.getInstance();
                        Transaction[] transactions = instance.makeTransactionByJson(s);
                        Transaction transaction = transactions[0];
//                Transaction transaction = instance.getConnect().getTransaction(data);
                        Account account = instance.getWalletMgr().getWallet().getAccount(address);
                        com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                        instance.signTx(transaction, new com.github.ontio.account.Account[][]{{account1}});
                        boolean b = instance.getConnect().sendRawTransaction(transaction);
                        if (b) {
                            emitter.onNext("");
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable(""));
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getWalletKey(final SDKCallback callback, final String tag, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String defaultAddress = SPWrapper.getDefaultAddress();
                Account account = OntSdk.getInstance().getWalletMgr().getWallet().getAccount(defaultAddress);
                byte[] salt = account.getSalt();
                SignatureScheme scheme = OntSdk.getInstance().getWalletMgr().getSignatureScheme();
                String gcmDecodedPrivateKey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(account.key, password, defaultAddress, salt, 4096, scheme);
                emitter.onNext(gcmDecodedPrivateKey);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() != null) {
                    callback.onSDKFail(tag, e.getMessage());
                } else {
                    callback.onSDKFail(tag, "");
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
