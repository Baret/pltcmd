package de.gleex.pltcmd.game.application

import de.gleex.pltcmd.game.application.screens.FirstScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class Pltcmd : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

