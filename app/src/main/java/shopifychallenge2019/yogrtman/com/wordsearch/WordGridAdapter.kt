package shopifychallenge2019.yogrtman.com.wordsearch

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView

class WordGridAdapter(val mContext: Context, val onWordFoundListener: OnWordFoundListener) : BaseAdapter() {
    companion object {
        val NO_WORD_ASSIGNED = -1
    }

    val maxWords = 10
    lateinit var mWordList: List<String>
    var numRows: Int = 15
    var numCols: Int = 15
    lateinit var mCells: Array<Array<Cell>>

    var lastCellCoords: Pair<Int, Int>? = null

    val fixedWords = listOf("KOTLIN", "SWIFT", "OBJECTIVEC",
            "VARIABLE", "JAVA", "MOBILE")

    constructor(mContext: Context, onWordFoundListener: OnWordFoundListener, rows: Int, cols: Int) : this(mContext, onWordFoundListener) {
        this.numRows = rows
        this.numCols = cols
    }

    constructor(mContext: Context, onWordFoundListener: OnWordFoundListener, wordList: ArrayList<String>, numRows: Int, numCols: Int) : this(mContext, onWordFoundListener, numRows, numCols) {
//        this.mWordList = buildWordList(wordList)
        this.mWordList = wordList
        generateCells()
    }

    fun onCellClicked(gridView: GridView, pos: Int, id: Long) {
        //if first selected cell, save this cell
        if (lastCellCoords == null) {
            lastCellCoords = Pair<Int, Int>(get2dX(pos), get2dY(pos))
        }
        else {
            val thisCellCoords = Pair<Int, Int>(get2dX(pos), get2dY(pos))

            //make sure two selected cells are in same row or same column
            if (!(thisCellCoords.first == lastCellCoords?.first ||
                            thisCellCoords.second == lastCellCoords?.second)) {
                return
            }
            //if clicking on the same cell, forget that cell
            else if (thisCellCoords.first == lastCellCoords?.first &&
                    thisCellCoords.second == lastCellCoords?.second) {
                lastCellCoords = null
                return
            }

            //build unit step increment to go from lastCellCoords to thisCellCoords
            val xStep = getUnitStep(lastCellCoords!!.first, thisCellCoords.first)
            val yStep = getUnitStep(lastCellCoords!!.second, thisCellCoords.second)

            //check that a full word was selected
            var x = lastCellCoords!!.first - xStep
            var y = lastCellCoords!!.second - yStep
            var word = ""
            while (!(x == thisCellCoords.first && y == thisCellCoords.second)) {
                x += xStep
                y += yStep
                val cell = gridView.getItemAtPosition(getPos(x, y)) as Cell
                word += cell.textView.text
            }
            if (isWordInList(word)) {
                //mark as valid word found
                var x = lastCellCoords!!.first - xStep
                var y = lastCellCoords!!.second - yStep
                while (!(x == thisCellCoords.first && y == thisCellCoords.second)) {
                    x += xStep
                    y += yStep
                    val cell = gridView.getItemAtPosition(getPos(x, y)) as Cell
                    cell.textView.background = mContext.getDrawable(R.drawable.circle_background)
                }
                onWordFoundListener.onWordFound(word)
            }
            lastCellCoords = null
        }
    }

    fun getUnitStep(a: Int, b: Int): Int {
        return if (a == b) 0 else (b-a)/Math.abs(b-a)
    }

    fun isWordInList(word: String): Boolean {
        Log.d("WordGridAdapter", "isWordInList: received $word, looking in $mWordList")
        return mWordList.contains(word) || mWordList.contains(word.reversed())
    }

//    fun buildWordList(wordList: ArrayList<String>?): List<String> {
//        val list = ArrayList<String>()
//        list.addAll(fixedWords)
//        if (wordList != null) {
//            wordList.shuffle()
//            list.addAll(wordList.subList(0, Math.min(maxWords - fixedWords.size, wordList.size)))
//        }
//        list.shuffle()
//        return list
//    }

    fun generateCells() {
        mCells = Array(numRows, {
            Array(numCols, {
                Cell(mContext, NO_WORD_ASSIGNED)
            })
        })

        for (k: Int in 0 until mWordList.size) {
            val word = mWordList[k]
            var startX: Int
            var startY: Int
            var gridDirection: Int
            var wordDirection: Int
            var step: Pair<Int, Int>
            val isReversed = (0..1).shuffled().first() == 1

            //loop until a clear spot for the word is generated
            do {
                startX = (1..(numRows-word.length)).shuffled().first() - 1
                startY = (1..(numCols-word.length)).shuffled().first() - 1
                gridDirection = (0..1).shuffled().first()
                wordDirection = (0..1).shuffled().first()
                step = when(gridDirection) {
                    0 -> Pair(1, 0)
                    1 -> Pair(0, 1)
                    else -> Pair(0, 0)
                }
                Log.d("WordGridAdapter", "Randomizing: word " + word + ", (" + startX + ", " + startY + "), " + gridDirection + " " + wordDirection)
            } while (!isWordAssignable(word.length, startX, startY, step))

            //assign word
            var x = startX
            var y = startY
            for(i: Int in 0 until word.length) {
                mCells[x][y] = Cell(mContext, k, i)
                mCells[x][y].textView.text = "" + getCellChar(mCells[x][y], isReversed)
                mCells[x][y].textView.setTextColor(mContext.getColor(R.color.colorAccent))
                x += step.first
                y += step.second
            }
        }

        for (i: Int in 0 until mCells.size) {
            var rowStr: String = ""
            for (j: Int in 0 until mCells[0].size) {
                if (mCells[i][j].wordIndex != NO_WORD_ASSIGNED) {
                    rowStr += mWordList[mCells[i][j].wordIndex][mCells[i][j].charIndex] + " "
                } else {
                    rowStr += "0 "
                }
            }
            Log.d("WordGridAdapter", rowStr)
        }
    }

    fun isWordAssignable(length: Int, startX: Int, startY: Int, step: Pair<Int, Int>) : Boolean {
        var x = startX
        var y = startY
        for(i: Int in 0 until length) {
            if (mCells[x][y].wordIndex != NO_WORD_ASSIGNED) {
                return false
            }
            x += step.first
            y += step.second
        }
        return true
    }

    fun get2dX(linearPos: Int): Int {
        return linearPos / numRows
    }

    fun get2dY(linearPos: Int): Int {
        return linearPos % numCols
    }

    fun getPos(x: Int, y: Int): Int {
        Log.d("WordGridAdapter", "getPos: $x, $y, returning " + ((x*numRows) + y))
        return (x*numRows) + (y)
    }

    fun getCellChar(wordIndex: Int, charIndex: Int, isReversed: Boolean = false): Char {
        if (wordIndex != NO_WORD_ASSIGNED) {
            return mWordList[wordIndex][if (!isReversed) charIndex else mWordList[wordIndex].length-charIndex-1].toUpperCase()
        } else {
            return ('A'..'Z').shuffled().first().toLowerCase()
        }
    }

    fun getCellChar(cell: Cell, isReversed: Boolean = false): Char {
        return getCellChar(cell.wordIndex, cell.charIndex, isReversed)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val x = get2dX(position)
        val y = get2dY(position)
        return mCells[x][y].textView
    }

    override fun getItem(position: Int): Cell {
        return mCells[get2dX(position)][get2dY(position)]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return this.numRows * this.numCols
    }

    class Cell(val mContext: Context, var wordIndex: Int, var charIndex: Int = 0) {
        var textView: TextView = TextView(mContext)
        init {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val baseView = inflater.inflate(R.layout.layout_cell, null)
            textView = baseView.findViewById<TextView>(R.id.txt_cell_letter)
            textView.text = "" + ('A'..'Z').shuffled().first()
        }
    }
}