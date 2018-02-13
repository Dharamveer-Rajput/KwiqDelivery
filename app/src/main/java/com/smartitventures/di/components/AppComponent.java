package com.smartitventures.di.components;


import com.smartitventures.BaseActivity;
import com.smartitventures.BaseFragment;
import com.smartitventures.Dialog.EnterOTPDialog;
import com.smartitventures.Dialog.SignatureDialogActivity;
import com.smartitventures.Network.ApiService;
import com.smartitventures.adapters.AdapterDashboardFragment;
import com.smartitventures.di.modules.HttpModule;
import com.smartitventures.di.modules.SharedPrefsHelper;
import com.smartitventures.service.LocationService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {HttpModule.class, SharedPrefsHelper.class})
public interface AppComponent {

    void inject(BaseActivity activity);
    void inject(BaseFragment fragment);
    void inject(EnterOTPDialog dialogMenu);
    void inject(SignatureDialogActivity signatureDialog);
    void inject(AdapterDashboardFragment adapterDashboardFragment);

    void inject(LocationService locationService);


    ApiService api();


}
