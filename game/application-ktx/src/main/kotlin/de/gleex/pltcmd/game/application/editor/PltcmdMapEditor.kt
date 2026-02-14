package de.gleex.pltcmd.game.application.editor

import ktx.app.KtxGame
import ktx.app.KtxScreen

class PltcmdMapEditor : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(MapEditorScreen())
        setScreen<MapEditorScreen>()
    }
}
