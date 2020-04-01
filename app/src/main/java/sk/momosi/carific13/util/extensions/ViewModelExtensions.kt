package sk.momosi.carific13.util.extensions

import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified VM : ViewModel> Fragment.provideViewModel(crossinline block: () -> VM): VM {
    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <A : ViewModel> create(modelClass: Class<A>): A {
            @Suppress("UNCHECKED_CAST")
            return block() as A
        }
    }).get(VM::class.java)
}

inline fun <reified VM : ViewModel> AppCompatActivity.provideViewModel(crossinline block: () -> VM): VM {
    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <A : ViewModel> create(modelClass: Class<A>): A {
            @Suppress("UNCHECKED_CAST")
            return block() as A
        }
    }).get(VM::class.java)
}

inline fun <reified VM : ViewModel> Fragment.provideViewModel(@Nullable viewModelFactory: ViewModelProvider.Factory? = null): VM {
    return ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)
}

inline fun <reified VM : ViewModel> Fragment.provideViewModelOfParentFragment(@Nullable viewModelFactory: ViewModelProvider.Factory? = null): VM {
    return ViewModelProviders.of(this.parentFragment!!, viewModelFactory).get(VM::class.java)
}

inline fun <reified VM : ViewModel> AppCompatActivity.provideViewModel(@Nullable viewModelFactory: ViewModelProvider.Factory? = null): VM {
    return ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)
}