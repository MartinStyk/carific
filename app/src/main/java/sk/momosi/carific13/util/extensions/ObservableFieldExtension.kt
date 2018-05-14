package sk.momosi.carific13.util.extensions

import android.databinding.ObservableField

/**
 * @author Martin Styk
 * @date 24.04.2018.
 */
fun ObservableField<out Any>.isNotNull() = get() != null
fun ObservableField<out String>.isNotNullOrBlank() = get() != null && !get().isNullOrBlank()
fun ObservableField<out Any>.isNull() = get() == null
