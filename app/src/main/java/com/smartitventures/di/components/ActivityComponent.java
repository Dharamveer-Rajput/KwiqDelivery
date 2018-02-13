package com.smartitventures.di.components;



import com.smartitventures.di.modules.ActivityModule;
import com.smartitventures.di.scopes.PerActivity;

import dagger.Component;


@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {


}
