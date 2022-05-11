package ru.storozh.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import ru.storozh.delegates.FragmentArgument
import ru.storozh.delegates.FragmentArgumentNullable
import ru.storozh.delegates.FragmentViewBinding
import kotlin.properties.ReadWriteProperty

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBinding(this, viewBindingFactory)

fun <T : ViewModel> Fragment.obtainViewModel(
    owner: ViewModelStoreOwner,
    viewModelClass: Class<T>,
    viewmodelFactory: ViewModelProvider.Factory
) = ViewModelProvider(owner, viewmodelFactory)[viewModelClass]

fun <T : Any> argument(): ReadWriteProperty<Fragment, T> = FragmentArgument()
fun <T : Any> argumentNullable(): ReadWriteProperty<Fragment, T?> =
    FragmentArgumentNullable()

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}