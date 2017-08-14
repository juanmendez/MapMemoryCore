package info.juanmendez.mapmemorycore.vp.vpAddress;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import info.juanmendez.mapmemorycore.dependencies.NavigationService;
import info.juanmendez.mapmemorycore.dependencies.Response;
import info.juanmendez.mapmemorycore.dependencies.AddressService;
import info.juanmendez.mapmemorycore.dependencies.AddressProvider;
import info.juanmendez.mapmemorycore.dependencies.NetworkService;
import info.juanmendez.mapmemorycore.dependencies.PhotoService;
import info.juanmendez.mapmemorycore.models.ShortAddress;
import info.juanmendez.mapmemorycore.models.MapMemoryException;
import info.juanmendez.mapmemorycore.models.SubmitError;
import info.juanmendez.mapmemorycore.modules.MapCoreModule;
import info.juanmendez.mapmemorycore.vp.ViewPresenter;
import rx.Subscription;

/**
 * Created by Juan Mendez on 6/26/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
public class AddressPresenter implements ViewPresenter<AddressPresenter,AddressFragment>{

    @Inject
    AddressProvider addressProvider;

    @Inject
    AddressService addressService;

    @Inject
    NetworkService networkService;

    @Inject
    PhotoService photoService;

    @Inject
    NavigationService navigationService;

    AddressFragment view;
    File photoSelected;

    public static final String ADDRESS_VIEW_TAG = "viewAddressTag";
    public static final String ADDDRESS_EDIT_TAG = "editAddressTag";
    private Subscription fileSubscription;

    @Override
    public AddressPresenter register(AddressFragment view) {
        this.view = view;
        MapCoreModule.getComponent().inject(this);
        return this;
    }

    @Override
    public void active( String action ) {

        networkService.reset();
        networkService.connect(new Response<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                view.onNetworkStatus( result );
            }

            @Override
            public void onError(Exception exception) {

            }
        });

        addressService.onStart( view.getActivity() );
        refreshView();
    }

    @Override
    public void inactive() {
        networkService.disconnect();
        addressService.onStop();
    }
    private void refreshView() {
        if( photoSelected != null )
            view.onPhotoSelected( photoSelected );

        checkCanUpdate();
        checkCanDelete();
    }

    private void checkCanUpdate(){
        ShortAddress addressSelected = addressProvider.getSelectedAddress();
        if( addressSelected != null ) {
            view.canSubmit( addressProvider.validate(addressSelected).isEmpty() );
        }
    }

    private void checkCanDelete(){
        ShortAddress addressSelected = addressProvider.getSelectedAddress();
        if( addressSelected != null ) {
            view.canDelete( SubmitError.initialized(addressSelected.getAddressId()) );
        }
    }

    public void setAddressEdited(ShortAddress addressEdited) {
        addressProvider.selectAddress(addressEdited);

        if( photoSelected != null && !photoSelected.getAbsolutePath().isEmpty() ){
            addressEdited.setPhotoLocation( photoSelected.getAbsolutePath() );
        }
    }

    public void submitAddress(Response<ShortAddress> response) {

        ShortAddress addressEdited = addressProvider.getSelectedAddress();
        List<SubmitError> errors = addressProvider.validate( addressProvider.getSelectedAddress() );

        if( errors.isEmpty() ){
            if( photoSelected != null && !photoSelected.getAbsolutePath().isEmpty() ){
                addressEdited.setPhotoLocation( photoSelected.getAbsolutePath() );
            }

            addressProvider.updateAddressAsync(addressEdited, new Response<ShortAddress>() {
                @Override
                public void onResult(ShortAddress result) {
                    addressProvider.selectAddress( result );
                    response.onResult( result );
                    checkCanDelete();
                    checkCanUpdate();
                }

                @Override
                public void onError(Exception exception) {
                    response.onError( exception );
                }
            });

        }else{
            response.onError( MapMemoryException.build("On Submit there are errors").setErrors( errors ) );
        }
    }

    public void deleteAddress( Response<Boolean> response ){

        long addressId = addressProvider.getSelectedAddress().getAddressId();
        addressProvider.deleteAddressAsync( addressId, response );
    }

    /**
     * View is requesting to pull address based on geolocation
     * this is done in an asynchronous way
     */
    public void requestAddressByGeolocation(){
        if( networkService.isConnected() ){
            addressService.geolocateAddress(new Response<ShortAddress>() {
                @Override
                public void onResult(ShortAddress result) {
                    addressProvider.selectAddress( result );
                    view.onAddressResult( result );
                    checkCanUpdate();
                }

                @Override
                public void onError(Exception exception) {
                    view.onAddressError(exception);
                }
            });
        }else{
            view.onAddressError( new MapMemoryException("networkService has no connection"));
        }
    }

    /**
     * View is requesting addresses by query which is replied asynchronously
     */
    public void requestAddressSuggestions( String query ){
        if( networkService.isConnected() && !query.isEmpty() ){
            addressService.suggestAddress(query, new Response<List<ShortAddress>>() {
                @Override
                public void onResult(List<ShortAddress> results ) {
                    view.onAddressesSuggested( results );
                }

                @Override
                public void onError(Exception exception) {
                    view.onAddressError( exception );
                }
            });
        }else{
            if( !networkService.isConnected() )
                view.onAddressError( new MapMemoryException("networkService has no connection"));
            else if( query.isEmpty() )
                view.onAddressError( new MapMemoryException("query is empty"));
        }
    }

    //view requests to pick photo from public gallery
    public void requestPickPhoto(){
        fileSubscription = photoService.pickPhoto(view.getActivity()).subscribe(file -> {
            photoSelected = file;
            view.onPhotoSelected( file );
        }, throwable -> {

        });
    }

    //view requests to take a photo
    public void requestTakePhoto(){
        fileSubscription = photoService.takePhoto(view.getActivity()).subscribe(file -> {
            photoSelected = file;
            view.onPhotoSelected( file );
        }, throwable -> {

        });
    }

    public void submitName( String name ){
        ShortAddress address = addressProvider.getSelectedAddress();
        address.setName( name );
        view.canSubmit( addressProvider.validate( address ).isEmpty() );
    }

    public void submitAddress(String addressLine1, String addressLine2 ){
        ShortAddress address = addressProvider.getSelectedAddress();

        address.setAddress1( addressLine1 );
        address.setAddress2( addressLine2 );
        view.canSubmit( addressProvider.validate( address ).isEmpty() );
    }
}