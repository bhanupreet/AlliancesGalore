package com.alliancesgalore.alliancesgalore;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mount on 12/8/14.
 */
public class ShadowTransition extends Transition {

    private static final String PROPERTY_TRANSLATION_Z = "shadow:translationZ";
    private static final String[] PROPERTIES = { PROPERTY_TRANSLATION_Z };

    public ShadowTransition() {
    }

    public ShadowTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        return PROPERTIES;
    }

    private void captureValues(TransitionValues transitionValues) {
        float z = transitionValues.view.getTranslationZ();
        transitionValues.values.put(PROPERTY_TRANSLATION_Z, z);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null ||
                !startValues.values.containsKey(PROPERTY_TRANSLATION_Z) ||
                !endValues.values.containsKey(PROPERTY_TRANSLATION_Z)) {
            return null;
        }

        final float startZ = (Float) startValues.values.get(PROPERTY_TRANSLATION_Z);
        final float endZ = (Float) endValues.values.get(PROPERTY_TRANSLATION_Z);

        final View view = endValues.view;
        view.setTranslationZ(startZ);
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, startZ, endZ);
    }
}
