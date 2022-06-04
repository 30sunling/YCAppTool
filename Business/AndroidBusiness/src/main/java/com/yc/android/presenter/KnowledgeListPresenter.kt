package com.yc.android.presenter
import com.yc.android.contract.KnowledgeListContract
import com.yc.android.model.helper.AndroidHelper
import com.yc.android.network.BaseSchedulerProvider
import com.yc.android.network.ResponseTransformer
import com.yc.android.network.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class KnowledgeListPresenter : KnowledgeListContract.Presenter {

    private var mView: KnowledgeListContract.View
    private var scheduler: BaseSchedulerProvider? = null
    private var page: Int = 0
    private var isRequest: Boolean = false

    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }


    constructor(androidView: KnowledgeListContract.View){
        this.mView = androidView
        scheduler = SchedulerProvider.getInstance()
    }


    override fun subscribe() {


    }

    override fun unSubscribe() {
        compositeDisposable.dispose()
    }

    override fun getKnowledgeList(id: Int, refresh: Boolean) {
        if (isRequest) {
            return
        }
        isRequest = true

        if (refresh) {
            page = 0
        }

        val instance = AndroidHelper.instance()
        val disposable: Disposable = instance.getKnowledgeList(page, id)
                .compose(ResponseTransformer.handleResult())
                .compose(scheduler?.applySchedulers())
                .subscribe(
                        { bean ->
                            isRequest = false
                            mView.loadAllArticles(bean, refresh)
                        },
                        { t ->
                            isRequest = false
                            mView.getKnowledgeFail(t.message!!, refresh)
                        }
                )
        compositeDisposable.add(disposable)
    }

}
