@file:Suppress("NOTHING_TO_INLINE")

package ca.allanwang.kau.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.View
import ca.allanwang.kau.R
import com.mikepenz.iconics.typeface.IIcon
import org.jetbrains.anko.contentView

/**
 * Created by Allan Wang on 2017-06-21.
 */

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class KauActivity

/**
 * Helper class to launch an activity for result
 * Counterpart of [Activity.startActivityForResult]
 * For starting activities without result, see [startActivity]
 */
@Suppress("DEPRECATION")
inline fun <reified T : Activity> Activity.startActivityForResult(
        requestCode: Int,
        bundleBuilder: Bundle.() -> Unit = {},
        intentBuilder: Intent.() -> Unit = {}
) = startActivityForResult(T::class.java, requestCode, bundleBuilder, intentBuilder)

@Deprecated("Use reified generic instead of passing class",
        ReplaceWith("startActivityForResult<T>(requestCode, bundleBuilder, intentBuilder)"),
        DeprecationLevel.WARNING)
inline fun <T : Activity> Activity.startActivityForResult(
        clazz: Class<T>,
        requestCode: Int,
        bundleBuilder: Bundle.() -> Unit = {},
        intentBuilder: Intent.() -> Unit = {}) {
    val intent = Intent(this, clazz)
    intent.intentBuilder()
    val bundle = Bundle()
    bundle.bundleBuilder()
    startActivityForResult(intent, requestCode, if (bundle.isEmpty) null else bundle)
}

/**
 * Restarts an activity from itself with a fade animation
 * Keeps its existing extra bundles and has a intentBuilder to accept other parameters
 */
inline fun Activity.restart(intentBuilder: Intent.() -> Unit = {}) {
    val i = Intent(this, this::class.java)
    val oldExtras = intent.extras
    if (oldExtras != null)
        i.putExtras(oldExtras)
    i.intentBuilder()
    startActivity(i)
    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out) //No transitions
    finish()
    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out)
}


/**
 * Force restart an entire application
 */
@RequiresApi(Build.VERSION_CODES.M)
inline fun Activity.restartApplication() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    val pending = PendingIntent.getActivity(this, 666, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (buildIsMarshmallowAndUp)
        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 100, pending)
    else
        alarm.setExact(AlarmManager.RTC, System.currentTimeMillis() + 100, pending)
    finish()
    System.exit(0)
}

fun Activity.finishSlideOut() {
    finish()
    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_slide_out_right_top)
}

inline var Activity.navigationBarColor: Int
    @SuppressLint("NewApi")
    get() = if (buildIsLollipopAndUp) window.navigationBarColor else Color.BLACK
    @SuppressLint("NewApi")
    set(value) {
        if (buildIsLollipopAndUp) window.navigationBarColor = value
    }

inline var Activity.statusBarColor: Int
    @SuppressLint("NewApi")
    get() = if (buildIsLollipopAndUp) window.statusBarColor else Color.BLACK
    @SuppressLint("NewApi")
    set(value) {
        if (buildIsLollipopAndUp) window.statusBarColor = value
    }

inline var Activity.statusBarLight: Boolean
    @SuppressLint("InlinedApi")
    get() = if (buildIsMarshmallowAndUp) window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR > 0 else false
    @SuppressLint("InlinedApi")
    set(value) {
        if (buildIsMarshmallowAndUp) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                    if (value) flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    else flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

/**
 * Themes the base menu icons and adds iicons programmatically based on ids
 *
 * Call in [Activity.onCreateOptionsMenu]
 */
fun Context.setMenuIcons(menu: Menu, @ColorInt color: Int = Color.WHITE, vararg iicons: Pair<Int, IIcon>) {
    iicons.forEach { (id, iicon) ->
        menu.findItem(id).icon = iicon.toDrawable(this, sizeDp = 18, color = color)
    }
}

inline fun Activity.hideKeyboard() = currentFocus.hideKeyboard()

inline fun Activity.showKeyboard() = currentFocus.showKeyboard()

inline fun Activity.snackbar(text: String, duration: Int = Snackbar.LENGTH_LONG, noinline builder: Snackbar.() -> Unit = {}) = contentView!!.snackbar(text, duration, builder)

inline fun Activity.snackbar(@StringRes textId: Int, duration: Int = Snackbar.LENGTH_LONG, noinline builder: Snackbar.() -> Unit = {}) = contentView!!.snackbar(textId, duration, builder)