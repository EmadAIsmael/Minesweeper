package minesweeper


class MineField {

    val rows = 9
    val columns = 9
    val field = Array(9) { Array(9) { '.' } }

    fun setMine(x: Int, y: Int) {
        if (x in 1..rows && y in 1..columns)
            field[x-1][y-1] = 'X'
        else
            println("row $x and column $y should be from 1 to 9")
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

    mf.setMine(1,1)
    mf.setMine(5,5)
    mf.setMine(9,9)
    // mf.setMine(10, 56)
    mf.setMine(7, 5)

    println(mf)
}
