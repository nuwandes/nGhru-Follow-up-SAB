package org.nghru_pk.ghru.ui.common

import android.text.method.PasswordTransformationMethod
import android.view.View

class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }
}