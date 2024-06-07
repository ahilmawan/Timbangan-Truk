package id.ahilmawan.weightbridge.ui.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.annotation.StringRes
import id.ahilmawan.weightbridge.R

class ProgressDialog(
    context: Context,
    private val title: String = "",
    private val content: String = "",
) : Dialog(context) {

    private lateinit var contentView: TextView

    constructor(context: Context, @StringRes title: Int, @StringRes content: Int) :
            this(context, context.getString(title), context.getString(content))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (title.isBlank()) requestWindowFeature(Window.FEATURE_NO_TITLE)
        else setTitle(title)

        setContentView(R.layout.dialog_progress)

        contentView = findViewById(R.id.contentView)

        if (content.isNotBlank()) {
            contentView.text = content
        }

        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}
