package de.gleex.pltcmd.game.application.korge.module

import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korinject.AsyncInjector
import de.gleex.pltcmd.game.application.korge.intro.IntroScene
import de.gleex.pltcmd.game.application.korge.mainmenu.MainMenuScene
import kotlin.reflect.KClass

/**
 * The module used for the normal game start.
 */
object PltCmdModule: Module() {
    override val mainScene: KClass<out Scene> = IntroScene::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { IntroScene() }
        mapPrototype { MainMenuScene() }
    }
}