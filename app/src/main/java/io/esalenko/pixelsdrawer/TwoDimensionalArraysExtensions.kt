package io.esalenko.pixelsdrawer


fun createTwoDimensArray(
    N: Int,
    M: Int,
    col: (Int, Int) -> Unit
) {

    repeat(N) {_col ->
        repeat(M) {_row ->
            col.invoke(_col, _row)
        }
    }
}

inline fun <T> Array<out Array<T>>.iterateTwoDimensArray(cell: (T) -> Unit) {
    for (_col in this.indices) {
        for (_row in this.indices) {
            cell.invoke(this[_col][_row])
        }
    }
}

inline fun <T> Array<out Array<T>>.mapTwoDimensArray(cell: () -> T) {
    for (_col in this.indices) {
        for (_row in this.indices) {
            this[_col][_row] = cell.invoke()
        }
    }
}

fun repeatInclusive(times : Int, action : (Int) -> Unit) {
    for (i in 0..times) {
        action.invoke(i)
    }
}