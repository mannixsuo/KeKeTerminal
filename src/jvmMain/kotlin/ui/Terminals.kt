package ui

import terminal.Terminal

class Terminals {
    private val selection = SingleSelection()

    val terminals: ArrayList<Terminal> = ArrayList()

    var active: Int = 0

    fun addNewTerminal(newTerminal: Terminal) {
        terminals.add(newTerminal)
        newTerminal.selection = selection
        active = active++
    }
}
