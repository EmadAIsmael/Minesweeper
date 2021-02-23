package minesweeper

import kotlin.random.Random


class MineField {

    val rows = 9
    val columns = 9
    var field = Array(rows) { Array(columns) { '.' } }

    fun setMine(x: Int, y: Int) {
        if (x in 1..rows && y in 1..columns)
            field[x-1][y-1] = 'X'
        else
            println("row $x and column $y should be from 1 to 9")
    }

    fun isFreeCell(r: Int, c: Int) = field[r - 1][c - 1] == '.'

    fun newField() {
        print("How many mines do you want on the field? > ")
        val numMines = readLine()!!.toInt()

        field = Array(rows) { Array(columns) { '.' } }
        var count = 0
        while (count < numMines) {
            val r = Random.nextInt(1, rows + 1)
            val c = Random.nextInt(1, columns + 1)

            if (isFreeCell(r, c)) {
                setMine(r, c)
                count++
            }
        }
    }

    override fun toString(): String {
        var output = ""
        for (r in 1..rows) {
            for (c in 1..columns)
                output += field[r - 1][c - 1]
            output += "\n"
        }

        return output
    }
}

fun main() {
    val mf = MineField()

    mf.newField()
    println(mf)
}
