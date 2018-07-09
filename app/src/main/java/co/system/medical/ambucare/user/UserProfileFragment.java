package co.system.medical.ambucare.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.system.medical.ambucare.R;

public class UserProfileFragment extends Fragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public UserProfileFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view_user_profile = inflater.inflate(R.layout.fragment_user_profile,container,false);


        return view_user_profile;
    }



}
