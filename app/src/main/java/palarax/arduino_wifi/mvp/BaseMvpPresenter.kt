package palarax.arduino_wifi.mvp


/**
 * Created by Ithai on 17/11/2017.
 */
interface BaseMvpPresenter<in V : BaseMvpView> {

    fun attachView(view: V)

    fun detachView()
}