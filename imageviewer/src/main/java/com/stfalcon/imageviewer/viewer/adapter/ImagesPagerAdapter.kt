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

package com.stfalcon.imageviewer.viewer.adapter

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.chrisbanes.photoview.PhotoView
import com.stfalcon.imageviewer.common.pager.RecyclingPagerAdapter
import com.stfalcon.imageviewer.loader.BindItemView
import com.stfalcon.imageviewer.loader.CreateItemView
import com.stfalcon.imageviewer.loader.GetViewType
import com.stfalcon.imageviewer.loader.ImageLoader

class ImagesPagerAdapter<T>(
    private val context: Context,
    _images: List<T>,
    private val imageLoader: ImageLoader<T>,
    private val isZoomingAllowed: Boolean,
    private var getViewType: GetViewType,
    private var createItemView: CreateItemView,
    private var bindItemView: BindItemView<T>
) : RecyclingPagerAdapter<ImagesPagerAdapter<T>.ViewHolder>() {

    private var images = _images
    private val holders = mutableListOf<ViewHolder>()
    var itemView: View = View(context)

    companion object {
        const val IMAGE_POSITION_DEFAULT = 0
        const val IMAGE_POSITION_TOP = 1
        const val IMAGE_POSITION_BOTTOM = 2
    }

    fun isScaled(position: Int): Boolean =
        holders.firstOrNull { it.position == position }?.isScaled ?: false

    fun isTopOrBottom(position: Int): Int =
        holders.firstOrNull { it.position == position }?.topOrBottom ?: IMAGE_POSITION_DEFAULT

    fun isInitState(position: Int) =
        holders.firstOrNull { it.position == position }?.isInitState ?: true

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        itemView = container
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, position: Int): ViewHolder {
        val itemView = createItemView.createItemView(context, viewType, isZoomingAllowed)
        return ViewHolder(itemView, viewType).also { holders.add(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = images.size

    override fun getViewType(position: Int) = getViewType.getItemViewType(position)

    internal fun updateImages(images: List<T>) {
        this.images = images
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, private var viewType: Int) :
        RecyclingPagerAdapter.ViewHolder(itemView) {

        var isScaled: Boolean = false
        var isInitState: Boolean = true  //初次加载的状态
        var topOrBottom: Int = IMAGE_POSITION_DEFAULT

        fun bind(position: Int) {
            this.position = position
            bindItemView.bindItemView(itemView, viewType, position, imageLoader)

            when (viewType) {
                VIEW_TYPE_IMAGE -> {
                    val photoView: PhotoView = itemView as PhotoView
                    photoView.setOnScaleChangeListener { _, _, _ ->
                        isScaled = photoView.scale >= 1.1f
                    }
                }

                VIEW_TYPE_SUBSAMPLING_IMAGE -> {
                    val subsamplingScaleImageView: SubsamplingScaleImageView =
                        itemView as SubsamplingScaleImageView
                    isScaled = true
                    isInitState = true
                    subsamplingScaleImageView.setOnStateChangedListener(object :
                        SubsamplingScaleImageView.OnStateChangedListener {
                        override fun onScaleChanged(newScale: Float, origin: Int) {

                        }

                        override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                            val resourceWidth = subsamplingScaleImageView.sWidth   //源文件宽
                            val resourceHeight = subsamplingScaleImageView.sHeight   //源文件高
                            val rect = Rect()
                            subsamplingScaleImageView.visibleFileRect(rect)
                            if (rect.top == 0 && rect.bottom == resourceHeight) {
                                topOrBottom = IMAGE_POSITION_DEFAULT
                                isScaled = false
                            } else if (rect.top == 0 && rect.bottom < resourceHeight) {
                                topOrBottom = IMAGE_POSITION_TOP
                                isScaled = true
                            } else if (rect.top > 0 && rect.bottom == resourceHeight) {
                                topOrBottom = IMAGE_POSITION_BOTTOM
                                isScaled = true
                            } else {
                                topOrBottom = IMAGE_POSITION_DEFAULT
                                isScaled = true
                            }
                            isInitState = false
                        }
                    })
                }
            }
        }

    }
}