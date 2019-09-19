package cz.dmn.display.mynotes

import cz.dmn.display.mynotes.mvvm.CoroutineContextProvider
import kotlinx.coroutines.Dispatchers

class TestCoroutineContextProvider : CoroutineContextProvider {
    override val Main = Dispatchers.Unconfined
    override val IO = Dispatchers.Unconfined
    override val Default = Dispatchers.Unconfined
}