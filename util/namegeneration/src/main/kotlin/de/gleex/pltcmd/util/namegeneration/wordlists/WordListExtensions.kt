package de.gleex.pltcmd.util.namegeneration.wordlists

import de.gleex.kng.api.WordList
import de.gleex.kng.wordlist.asWordList

internal operator fun WordList.plus(otherWordlist: WordList): WordList =
    buildList {
        for(i in 0 until size) {
            add(get(i))
        }
        for(j in 0 until  otherWordlist.size) {
            add(otherWordlist[j])
        }
    }.asWordList()