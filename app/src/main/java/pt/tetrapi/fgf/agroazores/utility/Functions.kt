package pt.tetrapi.fgf.agroazores.utility

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.nando.debug.Debug
import java.text.DecimalFormat
import java.text.Format
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}

fun hideKeyboard(fragment: Fragment) {
    val imm = fragment.requireActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(fragment.requireActivity().currentFocus?.windowToken, 0)
}

fun showKeyboard(activity: Activity, view: View) {
    val imm: InputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun showKeyboard(fragment: Fragment, view: View) {
    val imm: InputMethodManager = fragment.requireActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun toDp(value: Int): Int = (value / Resources.getSystem().displayMetrics.density).toInt()
fun toPx(value: Int): Int = (value * Resources.getSystem().displayMetrics.density).toInt()

fun addOpacityToColor(color: String, opacity: String): Int {
    var mColor = color
    if (mColor.contains("#")) {
        mColor = mColor.substring(1)
    }
    mColor = "#$opacity$mColor"
    return Color.parseColor(mColor)
}

fun Double.round2Decimals(): String {
    val string = String.format("%.2f", this)
    val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("EUR")
    var finalValue = format.format(this)
    finalValue += string.substring(string.length - 3, string.length)
    return finalValue
}

fun getDate(dateString: String, pattern: String = "dd, MMMM yyyy"): Calendar {
    val date = if (dateString.isEmpty()) {
        Calendar.getInstance().time
    } else {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        format.parse(dateString)!!
    }
    val cal: Calendar = Calendar.getInstance()
    cal.time = date
    return cal
}

fun translateDate(year: Int, monthOfYear: Int, dayOfMonth: Int, pattern: String = "dd, MMMM yyyy"): String {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, monthOfYear)
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    val inFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    cal.time = inFormat.parse("$dayOfMonth/$monthOfYear/$year")!!
    val outFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return outFormat.format(cal.time)
}

fun translateDate(cal: Calendar, pattern: String = "dd, MMMM yyyy"): String {
    val year = cal.get(Calendar.YEAR)
    val monthOfYear = cal.get(Calendar.MONTH) + 1
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val inFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    cal.time = inFormat.parse("$dayOfMonth/$monthOfYear/$year")!!
    val outFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return outFormat.format(cal.time)
}

fun getTimezonePattern(): String = "yyyy-MM-dd'T'HH:mm:ss.SSS"