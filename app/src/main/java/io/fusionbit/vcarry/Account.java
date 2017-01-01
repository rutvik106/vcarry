package io.fusionbit.vcarry;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by rutvik on 12/31/2016 at 11:50 AM.
 */

public class Account extends AbstractAccountAuthenticator
{


    public Account(Context context)
    {
        super(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s)
    {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s1, String[] strings, Bundle bundle) throws NetworkErrorException
    {



        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, android.accounts.Account account, Bundle bundle) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, android.accounts.Account account, String s, Bundle bundle) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s)
    {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, android.accounts.Account account, String s, Bundle bundle) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, android.accounts.Account account, String[] strings) throws NetworkErrorException
    {
        return null;
    }
}
