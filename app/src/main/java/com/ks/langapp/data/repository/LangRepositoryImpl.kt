package com.ks.langapp.data.repository

import com.ks.langapp.data.database.LangDatabaseDao
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import kotlinx.coroutines.flow.Flow

class LangRepositoryImpl(
    private val dao: LangDatabaseDao
) : LangRepository {

    override fun getAllDecks(): Flow<List<Deck>> {
        return dao.getAllDecks()
    }

    override fun getCardsOfADeck(deckId: Long): Flow<List<Card>> {
        return dao.getCardsOfADeck(deckId)
    }

    override suspend fun getCard(cardId: Long): Card? {
        return dao.getCard(cardId)
    }

    override suspend fun saveCard(card: Card) {
        dao.insert(card)
    }

    override suspend fun deleteCard(card: Card) {
        dao.deleteCard(card.cardId)
    }
}