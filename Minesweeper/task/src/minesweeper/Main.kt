package minesweeper

import kotlin.random.Random
import minesweeper.MineField.CellState.*

class MineField {

    private val rows = 9
    private val columns = 9
    private var field = Array(rows) { Array(columns) { Cell() } }
    private var numMines = 0
    private val ExploredSymbol = "/"

    data class Cell(
        var hint: String = "0",
        var state: Int = UNEXPLORED.id
    ) {

        fun setHasMinesAround() = (state or HAS_MINES_AROUND.id).also { state = it }
        fun hasMinesAround() = (state and HAS_MINES_AROUND.id) == HAS_MINES_AROUND.id
        fun hasNoMinesAround() = (state and HAS_MINES_AROUND.id) == 0

        fun setHasMine() = (state or HAS_MINE.id).also { state = it }
        fun hasMine() = (state and HAS_MINE.id) == HAS_MINE.id
        fun hasNoMine() = (state and HAS_MINE.id) == 0

        fun setUnExplored() = (state or UNEXPLORED.id).also { state = it }
        fun setExplored() = (state xor UNEXPLORED.id).also { state = it }
        fun isUnExplored() = (state and UNEXPLORED.id) == UNEXPLORED.id
        fun isExplored() = (state and UNEXPLORED.id) == 0

        fun setMarked() = (state or MARKED.id).also { state = it }
        fun unsetMarked() = (state xor MARKED.id).also { state = it }
        fun isMarked() = (state and MARKED.id) == MARKED.id
        fun isUnMarked() = (state and MARKED.id) == 0
        fun toggleMarkedState() {
            if (state and MARKED.id == 0)
                setMarked()
            else if (state and MARKED.id == MARKED.id)
                unsetMarked()
        }
    }

    enum class CellState(val id: Int, val symbol: String) {
        UNEXPLORED(1, "."),
        HAS_MINE(2, "X"),
        HAS_MINES_AROUND(4, "0"),
        MARKED(8, "*");
    }

    private fun setMine(r: Int, c: Int) {
        if (isValidCell(r, c)) {
            val cell = field[r - 1][c - 1]
            cell.setHasMine()
        }
        else
            println("row $r should be from 1 to $rows and column $c should be from 1 to $columns")
    }

    private fun isValidCell(r: Int, c: Int) = r in 1..rows && c in 1..columns

    private fun getNeighbors(r: Int, c: Int): Array<Pair<Int, Int>> {
        return arrayOf(
            Pair(r - 1, c),
            Pair(r - 1, c + 1),
            Pair(r, c + 1),
            Pair(r + 1, c + 1),
            Pair(r + 1, c),
            Pair(r + 1, c - 1),
            Pair(r, c - 1),
            Pair(r - 1, c - 1)
        )
    }

    private fun setCellHint(r: Int, c: Int) {
        val cell = this.field[r - 1][c - 1]
        if (cell.hasMine()) return
        val neighbors = getNeighbors(r, c)
        var numMines = 0
        repeat(8) {
            val row = neighbors[it].first - 1
            val col = neighbors[it].second - 1

            if (isValidCell(neighbors[it].first, neighbors[it].second)
            ) {
                val neighborCell = field[row][col]
                if (neighborCell.hasMine())
                    numMines++
            }
        }
        if (numMines > 0) {
            cell.hint = numMines.toString()
            cell.setHasMinesAround()
        }
    }

    private fun setFieldHints() {
        for (r in 1..rows)
            for (c in 1..columns)
                setCellHint(r, c)
    }

    private fun newField() {
        print("How many mines do you want on the field? > ")
        numMines = readLine()!!.toInt()

        field = Array(rows) { Array(columns) { Cell() } }
        var count = 0
        while (count < numMines) {
            val r = Random.nextInt(1, rows + 1)
            val c = Random.nextInt(1, columns + 1)

            val cell = field[r - 1][c - 1]
            if (cell.hasNoMine()) {
                setMine(r, c)
                cell.setUnExplored()
                count++
            }
        }
        setFieldHints()
    }

    private fun getCommand(): Triple<Int, Int, String> {
        /**
         * input:
         * a pair of cell coordinates (column followed by row) and
         * a command:
         * mine to mark or unmark a cell,
         * free to explore a cell.
         * e.g. 5 5 free
         *      4 7 mine
         */
        print("Set/unset mine marks or claim a cell as free: ")
        val (col, row, cmd) = readLine()!!.split(" ")

        return Triple(row.toInt(), col.toInt(), cmd)
    }

    private fun explore(r: Int, c: Int) {
        for (n in getNeighbors(r, c)) {
            val(row, col) = n
            if (isValidCell(row, col)) {
                val cell = field[row - 1][col - 1]
                if (cell.hasNoMine() and cell.hasNoMinesAround() and cell.isUnExplored()) {
                    cell.setExplored()
                    explore(row, col)
                } else if (cell.hasNoMine() and cell.hasMinesAround() and cell.isUnExplored()) {
                    cell.setExplored()
                }
            }
        }
    }

    fun play() {
        newField()
        println(this)

        while (true) {
            val (row, col, cmd) = getCommand()
            val cell = field[row - 1][col - 1]

            when (cmd) {
                "free" -> {                                                 // explore cell
                    when {
                        cell.hasNoMine() and cell.hasMinesAround() -> {     // 1 cell explored
                            cell.setExplored()
                        }
                        cell.hasMine() -> {                                 // failure
                            println(this)
                            println("You stepped on a mine and failed!")
                            break
                        }
                        cell.hasNoMine() and cell.hasNoMinesAround() -> {   // auto explore
                            cell.setExplored()
                            explore(row, col)
                        }
                    }
                }
                "mine" -> {                                                 // mark or unmark cell
                    cell.toggleMarkedState()
                }
            }

            println(this)
            if (isWinner()) {
                println("Congratulations! You found all the mines!")
                break
            }
        }
    }

    private fun isWinner(): Boolean {
        var correct = 0
        var incorrect = 0
        var unExploredAndHasMine = 0
        var totalUnExplored = 0
        for (r in 1..rows)
            for (c in 1..columns) {
                val cell = field[r - 1][c - 1]
                if (cell.hasMine() && cell.isMarked()) correct++
                if (!cell.hasMine() && cell.isMarked()) incorrect++
                if (cell.isUnExplored() and cell.hasMine())
                    unExploredAndHasMine++
                if (cell.isUnExplored())
                    totalUnExplored++
            }
        return (correct == numMines && incorrect == 0) ||
                (unExploredAndHasMine == numMines && totalUnExplored == unExploredAndHasMine)
    }

    override fun toString(): String {
        var output = " |123456789|\n-|---------|\n"
        for (r in 1..rows) {
            output += "$r|"
            for (c in 1..columns) {
                val cell = field[r - 1][c - 1]
                output += when {
                    // marked symbol: "*"
                    cell.isMarked() && cell.isExplored() and cell.hasMinesAround() -> cell.hint
                    cell.isMarked() && cell.hasNoMine() && cell.isExplored() -> ExploredSymbol
                    cell.isMarked() -> MARKED.symbol
                    // unexplored symbol: "."
                    cell.isUnExplored() -> UNEXPLORED.symbol
                    // number of mines around (hint): digit (..8)
                    cell.isExplored() and cell.hasMinesAround() -> cell.hint
                    // explored symbol: "/"
                    cell.isExplored() -> ExploredSymbol
                    // mine symbol: "X"
                    cell.hasMine() && cell.isExplored() -> HAS_MINE.symbol

                    else -> {}
                }
            }
            output += "|\n"
        }
        output += "-|---------|\n"

        return output
    }
}

fun main() {
    val mf = MineField()
    mf.play()
}
