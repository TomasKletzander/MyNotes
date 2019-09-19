package cz.dmn.display.mynotes.mvvm

import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {
    val Main: CoroutineContext
    val IO: CoroutineContext
    val Default: CoroutineContext
}