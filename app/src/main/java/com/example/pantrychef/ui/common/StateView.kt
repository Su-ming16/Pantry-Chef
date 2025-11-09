package com.example.pantrychef.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible

class StateView(
    private val contentView: View,
    private val loadingView: View,
    private val emptyView: View,
    private val errorView: View
) {
    
    fun showLoading() {
        contentView.isVisible = false
        loadingView.isVisible = true
        emptyView.isVisible = false
        errorView.isVisible = false
    }
    
    fun showContent() {
        contentView.isVisible = true
        loadingView.isVisible = false
        emptyView.isVisible = false
        errorView.isVisible = false
    }
    
    fun showEmpty(description: String? = null) {
        contentView.isVisible = false
        loadingView.isVisible = false
        emptyView.isVisible = true
        errorView.isVisible = false
        
        if (description != null) {
            val tvDescription = emptyView.findViewById<android.widget.TextView>(
                com.example.pantrychef.R.id.tvEmptyDescription
            )
            tvDescription?.text = description
        }
    }
    
    fun showError(onRetry: () -> Unit) {
        contentView.isVisible = false
        loadingView.isVisible = false
        emptyView.isVisible = false
        errorView.isVisible = true
        
        val retryButton = errorView.findViewById<android.widget.Button>(
            com.example.pantrychef.R.id.btnRetry
        )
        retryButton?.setOnClickListener { onRetry() }
    }
}

fun ViewGroup.createStateView(
    contentView: View,
    loadingLayoutId: Int = com.example.pantrychef.R.layout.view_state_loading,
    emptyLayoutId: Int = com.example.pantrychef.R.layout.view_state_empty,
    errorLayoutId: Int = com.example.pantrychef.R.layout.view_state_error
): StateView {
    val loadingView = View.inflate(context, loadingLayoutId, null)
    loadingView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    
    val emptyView = View.inflate(context, emptyLayoutId, null)
    emptyView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    
    val errorView = View.inflate(context, errorLayoutId, null)
    errorView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    
    addView(loadingView)
    addView(emptyView)
    addView(errorView)
    
    loadingView.isVisible = false
    emptyView.isVisible = false
    errorView.isVisible = false
    
    return StateView(contentView, loadingView, emptyView, errorView)
}

