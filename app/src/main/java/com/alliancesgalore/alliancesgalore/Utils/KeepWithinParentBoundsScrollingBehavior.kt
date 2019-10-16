package com.alliancesgalore.alliancesgalore.Utils
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * Custom extension of AppBarLayout.ScrollingViewBehavior to deal with ViewPager height
 * in a bunch with Collapsing Toolbar Layout. Works dynamically when AppBar Layout height is changing.
 */
class KeepWithinParentBoundsScrollingBehavior : AppBarLayout.ScrollingViewBehavior {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (dependency !is AppBarLayout) {
            return super.onDependentViewChanged(parent, child, dependency)
        }

        val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = parent.height - dependency.bottom
        child.layoutParams = layoutParams
        return super.onDependentViewChanged(parent, child, dependency)
    }
}