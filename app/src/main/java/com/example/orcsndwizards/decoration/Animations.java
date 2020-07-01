package com.example.orcsndwizards.decoration;
import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.example.orcsndwizards.R;


public class Animations {
    private Activity activity;

    public Animations(Activity activity) {
        this.activity = activity;
    }

    public Animation getFadeInAnim(){
        return AnimationUtils.loadAnimation(activity, R.anim.fadein);
    }
    public Animation getFadeOutAnim(){
        return AnimationUtils.loadAnimation(activity, R.anim.fadeout);
    }
}
