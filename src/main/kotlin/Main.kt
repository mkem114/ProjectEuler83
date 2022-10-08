import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

fun main() {
    val file =  File("src/main/resources/matrix.csv")
//    val file =  File("src/main/resources/small-matrix.csv")

    val weights: List<List<Int>> = csvReader()
        .readAll(file.readText())
        .map { stringList -> stringList.map { it.toInt() } }

    val height = weights.size
    val width = weights[0].size
    val startPoint = Node(0,0)
    val destination = Node(height -1, width -1)

    val cumulativeWeights = MutableList(height) { MutableList(width) { Int.MAX_VALUE } }
    cumulativeWeights[startPoint.row][startPoint.col] = weights[startPoint.row][startPoint.col]

    val visited: MutableSet<Node> = HashSet()
    val unvisited: MutableSet<Node> = HashSet()
    unvisited.add(startPoint)

    fun isVisited(node: Node): Boolean {
        return visited.contains(node)
    }

    fun inBounds(node: Node): Boolean {
        return node.row >= 0 && node.col >= 0 && node.row < height && node.col < width
    }

    fun findNextNode(): Node {
        val nextNode = unvisited.minBy { cumulativeWeights[it.row][it.col] }
        unvisited.remove(nextNode)
        return nextNode
    }

    fun considerNeighbour(pos: Node, current: Node, currentWeight: Int) {
        if (inBounds(pos) && !isVisited(pos)) {
            unvisited.add(pos)
            val candidateWeight = weights[pos.row][pos.col] + currentWeight
            if (candidateWeight < cumulativeWeights[pos.row][pos.col]) {
                cumulativeWeights[pos.row][pos.col] = candidateWeight
            }
        }
    }

    while (!isVisited(destination)) {
        val current = findNextNode()
        val currentWeight = cumulativeWeights[current.row][current.col]

        val right = Node(current.row, current.col + 1)
        considerNeighbour(right, current, currentWeight)

        val down = Node(current.row + 1, current.col)
        considerNeighbour(down, current, currentWeight)

        val left = Node(current.row, current.col - 1)
        considerNeighbour(left, current, currentWeight)

        val up = Node(current.row - 1, current.col)
        considerNeighbour(up, current, currentWeight)

        visited.add(current)
    }

    println(cumulativeWeights[destination.row][destination.col])
}
data class Node(val row: Int, val col: Int) {}
