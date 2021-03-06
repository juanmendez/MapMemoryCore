package info.juanmendez.addressmemorycore.dependencies.cloud;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import info.juanmendez.addressmemorycore.vp.vpAuth.AuthView;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by juan on 1/8/18.
 * This class is required to be a singleton.
 * Both the view and presenter rely on it.
 * View requires it to authenticate and retrieve result from onActivityResult
 * Presenter watches over when user has log in|out
 */
public class AuthService {

    public static final int FB_SESSION = 2018;
    public static final String LOGOUT_ACTION = "info.juanmendez.addressmemory.logout";
    private Auth mAuth;
    private BehaviorSubject<Boolean> mLoginObservable;

    public AuthService(Auth auth) {
        mAuth = auth;
        mLoginObservable = BehaviorSubject.create();
    }

    public void login(@NonNull AuthView view){
        view.startActivityForResult( mAuth.getAuthIntent(), FB_SESSION );
    }

    public void logout(@NonNull AuthView view){
        mAuth.logOut(view).subscribe(aBoolean -> {
            mLoginObservable.onNext(false);
        });
    }

    public Observable<Boolean> getObservable() {

        //we want to fire value initially, specially when it is false
        //in this way the observer catches the last value.
        mLoginObservable.onNext( isLoggedIn() );
        return mLoginObservable.distinct();
    }

    public boolean isLoggedIn() {
        return mAuth.isLoggedIn();
    }

    public void onLoginResponse(int requestCode, int resultCode, Intent data) {
        mLoginObservable.onNext( requestCode == FB_SESSION && resultCode == Activity.RESULT_OK );
    }
}