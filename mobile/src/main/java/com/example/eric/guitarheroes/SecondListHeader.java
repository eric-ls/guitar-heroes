package com.example.eric.guitarheroes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kevin on 12/5/2015.
 */
public class SecondListHeader extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.second_list_header, parent, false);
        return v;
    }
}
