package com.ycbjie.android.contract

import com.ycbjie.android.model.bean.ProjectListBean
import com.ycbjie.android.model.bean.TreeBean
import com.ycbjie.android.network.ResponseBean
import com.yc.library.base.mvp.BasePresenter
import com.yc.library.base.mvp.BaseView

interface AndroidProjectContract {

    interface View : BaseView {
        fun setProjectTreeSuccess(bean: List<TreeBean>)
        fun setProjectListByCidSuccess(bean: ResponseBean<ProjectListBean>, refresh: Boolean)
        fun setProjectListByCidError()
        fun setProjectListByCidNetError()
    }

    interface Presenter : BasePresenter {
        fun getProjectTree()
        fun getProjectTreeList(id: Int, b: Boolean)
    }

}
