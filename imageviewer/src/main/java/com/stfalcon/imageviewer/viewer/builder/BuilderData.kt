/*
 * Copyright 2018 stfalcon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stfalcon.imageviewer.viewer.builder

import android.graphics.Color
import android.view.View
import com.stfalcon.imageviewer.common.pager.RecyclingPagerAdapter
import com.stfalcon.imageviewer.listeners.OnDismissListener
import com.stfalcon.imageviewer.loader.GetViewType
import com.stfalcon.imageviewer.listeners.OnImageChangeListener
import com.stfalcon.imageviewer.listeners.OnStateListener
import com.stfalcon.imageviewer.loader.OnCreateView
import com.stfalcon.imageviewer.loader.ImageLoader

internal class BuilderData<T>(
    val images: List<T>,
    val imageLoader: ImageLoader<T>,
    val getViewType: GetViewType,
    val createItemView: OnCreateView
) {
    var backgroundColor = Color.BLACK
    var startPosition: Int = 0
    var imageChangeListener: OnImageChangeListener? = null
    var onDismissListener: OnDismissListener? = null
    var onStateListener: OnStateListener? = null
    var overlayView: View? = null
    var overlayViewSwitchAnimationEnable: Boolean = true
    var imageMarginPixels: Int = 0
    var shouldStatusBarHide = false
    var isZoomingAllowed = true
    var isSwipeToDismissAllowed = true
    var transitionView: View? = null
    var useDialogStyle = false
    var statusBarTransparent = false
}