package io.duniter.app.widget;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import io.duniter.app.R;
import io.duniter.app.model.Entity.Currency;
import io.duniter.app.model.Entity.Wallet;
import io.duniter.app.model.Entity.services.WalletService;
import io.duniter.app.model.services.SqlService;
import io.duniter.app.task.GenerateKeysTask;
import io.duniter.app.technical.crypto.Base58;
import io.duniter.app.technical.crypto.KeyPair;

/**
 * Created by naivalf27 on 25/02/16.
 */
public class RecordingView {

    private EditText mUid;
    private EditText mSalt;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private SelectorCurrencyView selectorCurrencyView;

    private Context mContext;

    private Action action;

    public RecordingView(Context mContext, EditText mUid, EditText mSalt, EditText mPassword, EditText mConfirmPassword, SelectorCurrencyView selectorCurrencyView, Action action) {
        this.mUid = mUid;
        this.mSalt = mSalt;
        this.mPassword = mPassword;
        this.mConfirmPassword = mConfirmPassword;
        this.selectorCurrencyView = selectorCurrencyView;
        this.mContext = mContext;
        this.action = action;
    }

    public boolean checkField(){
        //validate alias
        if (mUid.getText().toString().isEmpty()) {
            mUid.setError(mContext.getString(R.string.salt_cannot_be_empty));
            return false;
        }

        //validate salt
        if (mSalt.getText().toString().trim().isEmpty()) {
            mSalt.setError(mContext.getString(R.string.salt_cannot_be_empty));
            return false;
        }

        //validate password
        if (mPassword.getText().toString().trim().isEmpty()) {
            mPassword.setError(mContext.getString(R.string.password_cannot_be_empty));
            return false;
        }

        //validate confirm password
        if (mConfirmPassword.getText().toString().isEmpty()) {
            mConfirmPassword.setError(mContext.getString(R.string.confirm_password_cannot_be_empty));
            return false;
        }

        if(!mPassword.getText().toString().trim().equals(mConfirmPassword.getText().toString().trim())){
            mConfirmPassword.setError(mContext.getString(R.string.passwords_dont_match));
            return false;
        }

        return selectorCurrencyView.checkField();
    }

    public void validateWallet(final Currency currency, final FragmentManager manager) {
        GenerateKeysTask task = new GenerateKeysTask(new GenerateKeysTask.OnTaskFinishedListener() {
            @Override
            public void onTaskFinished(KeyPair keyPair) {
                Wallet wallet = new Wallet();
                wallet.setCurrency(currency);
                wallet.setSalt(mSalt.getText().toString());
                wallet.setAlias(mUid.getText().toString());
                wallet.setPublicKey(Base58.encode(keyPair.getPubKey()));
                wallet.setPrivateKey(Base58.encode(keyPair.getSecKey()));
                wallet.setHaveIdentity(false);
                wallet.setId(SqlService.getWalletSql(mContext).insert(wallet));

//                WalletService.updateWallet(mContext,wallet,true,null);

                action.onFinish();
            }
        });

        Bundle args = new Bundle();
        args.putString("salt", mSalt.getText().toString());
        args.putString("password", mPassword.getText().toString());
        task.execute(args);
    }

    public interface Action{
        void onFinish();
    }

}