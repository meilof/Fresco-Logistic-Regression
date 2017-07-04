class Matrix<T>(val elements: Array<Array<T>>) {
    fun element(row: Int, column: Int): T? {
        return elements[row][column]
    }
}
