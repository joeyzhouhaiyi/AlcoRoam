package com.elec390coen.alcoroam.Controllers;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireBaseAuthHelper {

    private FirebaseAuth mAuth;

    public FireBaseAuthHelper()
    {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth()
    {
        return mAuth;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isUserSignedIn()
    {
        return mAuth.getCurrentUser()!=null;
    }

}

