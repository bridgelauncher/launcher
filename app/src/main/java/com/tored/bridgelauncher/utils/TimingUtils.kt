package com.tored.bridgelauncher.utils

import kotlin.time.Duration
import kotlin.time.measureTimedValue

/** Runs the get function, stores the value, measures how much time it took, invoke a callback with the measured duration, then returns the value. */
inline fun <T> getValueAndUseDuration(
    get: () -> T,
    run: (dur: Duration) -> Unit,
): T
{
    return measureTimedValue(get).let { (v, dur) -> run(dur); v }
}