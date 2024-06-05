package de.gleex.pltcmd.util.namegeneration.wordlists

import de.gleex.kng.api.WordList
import de.gleex.kng.wordlist.asWordList

internal operator fun WordList.plus(otherWordlist: WordList): WordList =
    buildList {
        val thisWordList = this@plus
        for(i in 0 until thisWordList.size) {
            add(thisWordList[i])
        }
        for(j in 0 until  otherWordlist.size) {
            add(otherWordlist[j])
        }
    }.asWordList()