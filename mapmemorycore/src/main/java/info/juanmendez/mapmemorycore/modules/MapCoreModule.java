package info.juanmendez.mapmemorycore.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.juanmendez.mapmemorycore.CoreApp;
import info.juanmendez.mapmemorycore.dependencies.db.AddressProvider;
import info.juanmendez.mapmemorycore.dependencies.autocomplete.AutocompleteService;

/**
 * Created by Juan Mendez on 6/24/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

@Module
public class MapCoreModule {

    private static MapCoreComponent component;
    private CoreApp app;

    public static void setApp(CoreApp app ){
        component = DaggerMapCoreComponent.builder().mapCoreModule(new MapCoreModule(app)).build();
    }

    public MapCoreModule(CoreApp app) {
        this.app = app;
    }

    @Singleton
    @Provides
    public AddressProvider getAddressProvider(){
        return app.getAddressProvider();
    }

    @Provides
    public AutocompleteService autocompleteService(){
        return  app.getAutocomplete();
    }

    @Singleton
    @Provides
    public Application getApplication(){
        return app.getApplication();
    }

    public static MapCoreComponent getComponent(){
        return component;
    }
}
