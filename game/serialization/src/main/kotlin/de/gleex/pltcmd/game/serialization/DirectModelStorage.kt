package de.gleex.pltcmd.game.serialization

/**
 * Directly stores the model class without an immediate data type.
 */
abstract class DirectModelStorage<M : Any> : ModelStorage<M, M>() {

    override fun toDao(model: M) = model

    override fun toModel(dao: M) = dao
}