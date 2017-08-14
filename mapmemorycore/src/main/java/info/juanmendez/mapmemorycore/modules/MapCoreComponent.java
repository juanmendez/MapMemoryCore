package info.juanmendez.mapmemorycore.modules;

import javax.inject.Singleton;

import dagger.Component;
import info.juanmendez.mapmemorycore.vp.vpAddress.AddressPresenter;
import info.juanmendez.mapmemorycore.vp.vpAddresses.AddressesPresenter;
import info.juanmendez.mapmemorycore.vp.vpPhoto.PhotoPresenter;

/**
 * Created by Juan Mendez on 6/24/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@Singleton
@Component(modules={MapCoreModule.class})
public interface MapCoreComponent {
    void inject( AddressesPresenter addressesPresenter );
    void inject( AddressPresenter addressPresenter );
    void inject( PhotoPresenter photoPresenter );
}