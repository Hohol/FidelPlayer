package fidel.logic;

import java.util.List;

import fidel.common.Command;

class MovesAndEvaluation {
    final List<Command> moves;
    final double evaluation;
    final MoveGameState moveGameState;

    MovesAndEvaluation(List<Command> moves, double evaluation, MoveGameState moveGameState) {
        this.moves = moves;
        this.evaluation = evaluation;
        this.moveGameState = moveGameState;
    }
}
