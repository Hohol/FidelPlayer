package fidel.logic;

import java.util.List;

import fidel.common.Command;

class MovesAndEvaluation {
    final List<Command> moves;
    final double evaluation;

    MovesAndEvaluation(List<Command> moves, double evaluation) {
        this.moves = moves;
        this.evaluation = evaluation;
    }
}
