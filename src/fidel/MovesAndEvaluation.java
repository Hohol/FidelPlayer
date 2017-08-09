package fidel;

import java.util.List;

class MovesAndEvaluation {
    final List<Command> moves;
    final double evaluation;

    MovesAndEvaluation(List<Command> moves, double evaluation) {
        this.moves = moves;
        this.evaluation = evaluation;
    }
}
