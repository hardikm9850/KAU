package ca.allanwang.kau.kpref.activity.items

import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.R

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Just text with the core options. Extends base preference but has an empty getter and setter
 * Useful replacement of [KPrefText] when nothing is displayed on the right side,
 * and when the preference is completely handled by the click
 *
 */
open class KPrefPlainText(open val builder: KPrefPlainTextBuilder) : KPrefItemBase<Unit>(builder) {

    override fun KClick<Unit>.defaultOnClick() = Unit

    class KPrefPlainTextBuilder(
            globalOptions: GlobalOptions,
            titleId: Int
    ) : BaseContract<Unit> by BaseBuilder(globalOptions, titleId, {}, {})

    override fun getType(): Int = R.id.kau_item_pref_plain_text

}