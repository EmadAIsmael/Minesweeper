package minesweeper

import kotlin.random.Random

class MineField {

    private val rows = 9
    private val columns = 9
    private var field = Array(rows) { Array(columns) { Cell() } }
    private var numMines = 0

    data class Cell(var content: String = ".", var marked: Boolean = false) {
        fun isFree() = content == "."
        fun isMine() = content == "X"
        fun isNumber() = content in arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
        fun isMarked() = marked
        fun isNotMarked() = !isMarked()
        fun toggleMark() {
            marked = !isMarked()
        }
    }


    private fun setMine(r: Int, c: Int) {
        if (isValidCell(r, c))
            field[r - 1][c - 1].content = "X"
        else
            println("row $r should be from 1 to $rows and column $c should be from 1 to $columns")
    }

    //private fun isFreeCell(r: Int, c: Int) = field[r - 1][c - 1] != "X"

    //private fun isMineCell(r: Int, c: Int) = field[r - 1][c - 1] == "X"

    private fun isValidCell(r: Int, c: Int) = r in 1..rows && c in 1..columns

    private fun setCellHint(r: Int, c: Int) {
        if (!this.field[r - 1][c - 1].isFree()) return
        val neighbors = arrayOf(
            Pair(r - 1, c),
            Pair(r - 1, c + 1),
            Pair(r, c + 1),
            Pair(r + 1, c + 1),
            Pair(r + 1, c),
            Pair(r + 1, c - 1),
            Pair(r, c - 1),
            Pair(r - 1, c - 1)
        )
        var numMines = 0
        repeat(8) {
            val row = neighbors[it].first - 1
            val col = neighbors[it].second - 1
            if (isValidCell(neighbors[it].first, neighbors[it].second) &&
                this.field[row][col].isMine()
            )
                numMines++
        }
        if (numMines > 0)
            field[r - 1][c - 1].content = numMines.toString()
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

            if (field[r - 1][c - 1].isFree()) {
                setMine(r, c)
                count++
            }
        }
        setFieldHints()
    }

    fun play() {
        newField()
        println(this)

        while (true) {
            print("Set/delete mine marks (x and y coordinates): ")
            val (col, row) = readLine()!!.split(" ")

            val cell = field[row.toInt() - 1][col.toInt() - 1]
            if (cell.isNumber()) {
                println("There is a number here!")
                continue
            } else if (cell.isMarked()) cell.toggleMark()
            else if (cell.isFree() || cell.isMine()) cell.toggleMark()

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
        for (r in 1..rows)
            for (c in 1..columns) {
                val cell = field[r - 1][c - 1]
                if (cell.isMine() && cell.isMarked()) correct++
                if (!cell.isMine() && cell.isMarked()) incorrect++
            }
        return correct == numMines && incorrect == 0
    }


    override fun toString(): String {
        var output = " |123456789|\n-|---------|\n"
        for (r in 1..rows) {
            output += "$r|"
            for (c in 1..columns) {
                val cell = field[r - 1][c - 1]
                output += when {
                    cell.isMine() && cell.isNotMarked() -> "."
                    cell.isMarked() -> "*"
                    else -> field[r - 1][c - 1].content
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
