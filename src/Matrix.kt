class Matrix<T>(val elements: Array<Array<T>>) {
    init {
        var numberOfColumns: Int? = null
        elements.forEach { element ->
            if (numberOfColumns == null) {
                numberOfColumns = element.size
            }
            if (numberOfColumns != element.size) {
                throw IllegalArgumentException("Rows are not of equal size")
            }
        }
    }

    fun element(row: Int, column: Int): T? {
        return elements[row][column]
    }
}
