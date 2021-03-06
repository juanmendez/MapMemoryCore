package info.juanmendez.addressmemorycore.vp.vpPhoto;

import info.juanmendez.addressmemorycore.dependencies.AddressProvider;
import info.juanmendez.addressmemorycore.dependencies.NavigationService;
import info.juanmendez.addressmemorycore.dependencies.PhotoService;
import info.juanmendez.addressmemorycore.dependencies.Response;
import info.juanmendez.addressmemorycore.dependencies.WidgetService;
import info.juanmendez.addressmemorycore.models.AddressException;
import info.juanmendez.addressmemorycore.models.ShortAddress;
import info.juanmendez.addressmemorycore.modules.AddressCoreModule;
import info.juanmendez.addressmemorycore.utils.AddressUtils;
import info.juanmendez.addressmemorycore.utils.ValueUtils;
import info.juanmendez.addressmemorycore.vp.Presenter;

/**
 * Created by Juan Mendez on 8/14/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
public class PhotoPresenter implements Presenter<PhotoViewModel,PhotoView> {

    private PhotoService mPhotoService;
    private AddressProvider mAddressProvider;
    private NavigationService mNavigationService;
    private WidgetService mWidgetService;

    private PhotoView mView;
    private PhotoViewModel mViewModel;
    private boolean mLastRotated;

    public PhotoPresenter(AddressCoreModule module) {
        mPhotoService = module.getPhotoService();
        mAddressProvider = module.getAddressProvider();
        mNavigationService = module.getNavigationService();
        mWidgetService = module.getWidgetService();
    }

    @Override
    public PhotoViewModel getViewModel(PhotoView photoView) {
        mView = photoView;
        return mViewModel = new PhotoViewModel();
    }

    //mView requests to pick photo from public gallery
    public void requestPickPhoto(){
        mPhotoService.pickPhoto(mView.getActivity()).subscribe(photoLocation -> {
            if(!ValueUtils.emptyOrNull(photoLocation)){
                mViewModel.setPhoto( photoLocation );
                mViewModel.isModified.set(true);
            }
        }, throwable -> {
            mViewModel.setPhotoException(new AddressException(throwable.getMessage()));
        });
    }

    //mView requests to take a photo
    public void requestTakePhoto(){
         mPhotoService.takePhoto(mView.getActivity())
                 .subscribe( s -> {

                     if(!ValueUtils.emptyOrNull(s)){
                         mViewModel.setPhoto( s );
                         mViewModel.isModified.set(true);
                     }
                 },
                     throwable -> {
                     mViewModel.setPhotoException(new AddressException(throwable.getMessage()));
                 });
    }

    public void confirmPhoto(){
        if( !mViewModel.getPhoto().isEmpty() ){

            ShortAddress address = mViewModel.getAddress();
            long id = address.getAddressId();
            address.setPhotoLocation(mViewModel.getPhoto());

            if( id > 0 ){

                //update photoLocation only
                ShortAddress realmAddress = AddressUtils.cloneAddress(mAddressProvider.getAddress(id ));
                realmAddress.setPhotoLocation(mViewModel.getPhoto());

                mAddressProvider.updateAddressAsync(realmAddress, new Response<ShortAddress>() {
                    @Override
                    public void onError(Exception exception) {
                    }

                    @Override
                    public void onResult(ShortAddress addressResult) {
                        mWidgetService.updateList();
                        mNavigationService.goBack();
                    }
                });
            }else{
                mNavigationService.goBack();
            }
        }else{
            mViewModel.getAddress().setPhotoLocation("");
            mNavigationService.goBack();
        }
    }

    public void cancelPhoto(){
        mViewModel.setPhoto("");
        mNavigationService.goBack();
    }

    public void deletePhoto(){

        ShortAddress selectedAddress = mViewModel.getAddress();
        mPhotoService.deletePhoto( selectedAddress.getPhotoLocation() );

        if( selectedAddress.getAddressId() > 0 ){

            selectedAddress.setPhotoLocation("");

            mAddressProvider.updateAddressAsync(selectedAddress, new Response<ShortAddress>() {

                @Override
                public void onError(Exception exception) {
                }

                @Override
                public void onResult(ShortAddress result) {
                    mAddressProvider.selectAddress( result );
                    mViewModel.setAddress(result);
                    mViewModel.clearPhoto();
                    mNavigationService.goBack();
                }
            });
        }else{
            mViewModel.clearPhoto();
            mNavigationService.goBack();
        }
    }

    @Override
    public void active(String params ) {
        if( !mLastRotated){
            mViewModel.setAddress( mAddressProvider.getSelectedAddress() );
        }
    }

    @Override
    public void inactive(Boolean rotated) {
        mLastRotated = rotated;
    }
}