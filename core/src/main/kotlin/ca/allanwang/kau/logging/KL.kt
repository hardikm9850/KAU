package ca.allanwang.kau.logging

import ca.allanwang.kau.BuildConfig

/**
 * Created by Allan Wang on 2017-06-19.
 *
 * Internal KAU logger
 */
object KL : KauLogger("KAU", { BuildConfig.DEBUG })