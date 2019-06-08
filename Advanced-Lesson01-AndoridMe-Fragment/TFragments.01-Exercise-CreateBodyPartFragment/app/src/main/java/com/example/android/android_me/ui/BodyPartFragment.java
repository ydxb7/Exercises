package com.example.android.android_me.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;


// TODO (2) Create a new class called BodyPartFragment to display an image of an Android-Me body part
// In this class, you'll need to implement an empty constructor and the onCreateView method

public class BodyPartFragment extends Fragment {

    public BodyPartFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_body_part, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.body_part_image_view);
        // TODO (3) Show the first image in the list of head images
        // Soon, you'll update this image display code to show any image you want
        imageView.setImageResource(AndroidImageAssets.getHeads().get(0));

        return rootView;
    }
}
