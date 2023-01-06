package chess
import kotlin.system.exitProcess

const val ROWS = "087654321"
const val COLUMNS = " abcdefgh "

class Setup {
    var player1 = ""
    var player2 = ""
    var playersTurn = 1
    var playersTurnName = ""
    var symbol = "W"
    var pawn = "White"
    var enPassantActive = 0
    var enPassantInput = ""
    var enPassantDone = 0
    var input = ""
    var board: MutableList<MutableList<String>> = mutableListOf(
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("8 ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|"),
        mutableListOf("7 ", "| B ", "| B ", "| B ", "| B ", "| B ", "| B ", "| B ", "| B ", "|"),
        mutableListOf("6 ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|"),
        mutableListOf("5 ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|"),
        mutableListOf("4 ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|"),
        mutableListOf("3 ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|"),
        mutableListOf("2 ", "| W ", "| W ", "| W ", "| W ", "| W ", "| W ", "| W ", "| W ", "|"),
        mutableListOf("1 ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|   ", "|"),
        mutableListOf("    ", "a   ", "b   ", "c   ", "d   ", "e   ", "f   ", "g   ", "h")
    )
}

fun main() {
    println("Pawns-Only Chess")
    val game = Setup()
    println("First Player's name:")
    game.player1 = readln()
    println("Second Player's name:")
    game.player2 = readln()
    boardPrint(game)
    playerInput(game)
}

fun playerInput(game: Setup, str: String = "") {
    if ("Invalid" in str) println("$str Input")
    if (game.playersTurn == 1) {
        game.playersTurnName = game.player1
        game.symbol = "W"
        game.pawn = "White"
    } else {
        game.playersTurnName = game.player2
        game.symbol = "B"
        game.pawn = "Black"
    }

    // No pawns left / Draw
    if (gameResultCheck(game, "depleted")) {
        if (game.pawn == "White") println("Black Wins!\n" + "Bye!")
        else println("White Wins!\n" + "Bye!")
        exitProcess(2)
    } else if (gameResultCheck(game, "stalemate")) {
        println("Stalemate!\n" + "Bye!")
        exitProcess(3)
    }

    println("${game.playersTurnName}'s turn:")
    game.input = readln()
    if (game.input == "exit") {
        println("Bye!")
        exitProcess(1)
    }
    val regex = Regex("""[a-h][1-8][a-h][1-8]""")
    if (!game.input.matches(regex)) {
        playerInput(game, "Invalid Regex")
    }

    // Basic coordinates check
    if (inputIsValid(game, game.input)) boardUpdate(game) else playerInput(game, "Invalid")
}

fun inputIsValid(game: Setup, input: String = game.input, test: String = ""): Boolean {
    // Null check
    if ("null" in input) return false
    // Start square check
    if (game.board[ROWS[input[1].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
            it == input[0] }] != "| ${game.symbol} ") {
        println("No ${game.pawn} pawn at ${input[0]}${input[1]}")
        playerInput(game)
    }
    // White
    if (game.symbol == "W") {
        if (COLUMNS.indexOfFirst { it == input[0] } == COLUMNS.indexOfFirst { it == input[2] }) {
            // Forward
            if (game.board[ROWS[input[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
                    it == input[2] }] == "|   ") {
                if (input[3] == input[1] + 1) {
                    if (test != "test") game.enPassantActive = 0
                    return true
                } else if (input[3] == input[1] + 2 && input[1] == ROWS[7] &&
                    game.board[ROWS[input[3].digitToInt() - 1].digitToInt()][COLUMNS.indexOfFirst {
                        it == input[2] }] == "|   ") {
                    if (test != "test") {
                        game.enPassantActive = 1
                        game.enPassantInput = game.input
                    }
                    return true
                }
            }
            // Capture
        } else if (input[3] == input[1] + 1 &&
            game.board[ROWS[input[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
                it == input[2] }] == "| B " &&
            (COLUMNS.indexOfFirst { it == input[0] } + 1 == COLUMNS.indexOfFirst { it == input[2] } ||
                    COLUMNS.indexOfFirst { it == input[0] } - 1 == COLUMNS.indexOfFirst { it == input[2] })) {
            if (test != "test") game.enPassantActive = 0
            return true
            // En passant
        } else if (game.enPassantActive == 1) {
            if ((input[0] == COLUMNS[COLUMNS.indexOfFirst { it == game.enPassantInput[2] } + 1] ||
                        input[0] == COLUMNS[COLUMNS.indexOfFirst { it == game.enPassantInput[2] } - 1]) &&
                (ROWS[input[1].digitToInt()] == ROWS[game.enPassantInput[3].digitToInt()]) &&
                game.board[ROWS[input[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
                    it == input[2] }] == "|   ") {
                if (test != "test") game.enPassantDone = 1
                return true
            }
        }
    }
    // Black
    if (game.symbol == "B") {
        if (COLUMNS.indexOfFirst { it == input[0] } == COLUMNS.indexOfFirst { it == input[2] }) {
            // Forward
            if (game.board[ROWS[input[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
                    it == input[2] }] == "|   ") {
                if (input[3] == input[1] - 1) {
                    if (test != "test") game.enPassantActive = 0
                    return true
                } else if (input[3] == input[1] - 2 && input[1] == ROWS[2] &&
                    game.board[ROWS[input[3].digitToInt() + 1].digitToInt()][COLUMNS.indexOfFirst {
                        it == input[2] }] == "|   ") {
                    if (test != "test") {
                        game.enPassantActive = 1
                        game.enPassantInput = game.input
                    }
                    return true
                }
            }
            // Capture
        } else if (input[3] == input[1] - 1 &&
            game.board[ROWS[input[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
                it == input[2] }] == "| W " &&
            (COLUMNS.indexOfFirst { it == input[0] } + 1 == COLUMNS.indexOfFirst { it == input[2] } ||
                    COLUMNS.indexOfFirst { it == input[0] } - 1 == COLUMNS.indexOfFirst { it == input[2] })) {
            if (test != "test") game.enPassantActive = 0
            return true
            // En passant
        } else if (game.enPassantActive == 1) {
            if ((input[0] == COLUMNS[COLUMNS.indexOfFirst { it == game.enPassantInput[2] } + 1] ||
                        input[0] == COLUMNS[COLUMNS.indexOfFirst { it == game.enPassantInput[2] } - 1]) &&
                (ROWS[input[1].digitToInt()] == ROWS[game.enPassantInput[3].digitToInt()])) {
                if (test != "test") game.enPassantDone = 1
                return true
            }
        }
    }
    return false
}

fun boardUpdate(game: Setup) {
    if (game.enPassantDone == 1) {
        game.board[ROWS[game.input[1].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
            it == game.input[0] }] = "|   "
        game.board[ROWS[game.input[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
            it == game.input[2] }] = "| ${game.symbol} "
        game.board[ROWS[game.enPassantInput[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
            it == game.enPassantInput[2] }] = "|   "
        game.enPassantActive = 0
        game.enPassantDone = 0
    } else {
        game.board[ROWS[game.input[1].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
            it == game.input[0] }] = "|   "
        game.board[ROWS[game.input[3].digitToInt()].digitToInt()][COLUMNS.indexOfFirst {
            it == game.input[2] }] = "| ${game.symbol} "
    }

    // Across win / Draw
    if (gameResultCheck(game, "across")) {
        boardPrint(game)
        println("${game.pawn} Wins!\n" + "Bye!")
        exitProcess(2)
    } else if (gameResultCheck(game, "stalemate")) {
        boardPrint(game)
        println("Stalemate!\n" + "Bye!")
        exitProcess(3)
    }

    boardPrint(game)
    game.playersTurn = if (game.playersTurn == 1) 2 else 1
    playerInput(game)
}

fun gameResultCheck(game: Setup, endType: String): Boolean {
    if (endType == "across") {
        if (game.input[3] == '8' || game.input[3] == '1') {
            return true
        }
    } else if (endType == "depleted") {
        for (list in 1..ROWS.lastIndex) {
            if ("| ${game.symbol} " in game.board[list]) {
                return false
            } else if (list == ROWS.lastIndex) {
                return true
            }
        }
    } else if (endType == "stalemate") {
        for (list in 1..ROWS.lastIndex) {
            for (str in 1 until game.board[list].lastIndex) {
                if ("| ${game.symbol} " == game.board[list][str]) {
                    if (game.symbol == "W") {
                        if (
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str] + ROWS[list - 1].toString(), test = "test") ||
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str] + ROWS.getOrNull(list - 2)?.toString(), test = "test") ||
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str + 1] + ROWS[list - 1].toString(), test = "test") ||
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str - 1] + ROWS[list - 1 ].toString(), test = "test")
                        ) {
                            return false
                        }
                    } else if (game.symbol == "B") {
                        if (
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str] + ROWS[list + 1].toString(), test = "test") ||
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str] + ROWS.getOrNull(list + 2)?.toString(), test = "test") ||
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str + 1] + ROWS[list + 1].toString(), test = "test") ||
                            inputIsValid(game, COLUMNS[str] + ROWS[list].toString() + COLUMNS[str - 1] + ROWS[list + 1 ].toString(), test = "test")
                        ) {
                            return false
                        }
                    }
                } else if (list == ROWS.lastIndex && str == game.board[list].lastIndex - 1) return true
            }
        }
    }
    return false
}

fun boardPrint(game: Setup) {
    for (i in 1..game.board.lastIndex) {
        println("${game.board[0][0]}\n" + game.board[i].joinToString(""))
    }
}