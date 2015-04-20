namespace a12johqv.Examination.Ai
{
    using System;

    /// A report of a match for a player.
    /// It contains the player, its average problem similarity and average adapted move similarity.
    public class PlayerReport
    {
        private readonly Player player;

        private readonly double problemSimilaritySum;

        private readonly double moveSimilaritySum;

        private readonly int moreThanOneBestMatches;

        private readonly int moveCount;

        private readonly Weights weights;

        private readonly TimeSpan decisionTimeOfFirst40Moves;

        private PlayerReport(Player player, double problemSimilaritySum, double moveSimilaritySum, int moreThanOneBestMatches, int moveCount, Weights weights, TimeSpan decisionTimeOfFirst40Moves)
        {
            this.player = player;
            this.problemSimilaritySum = problemSimilaritySum;
            this.moveSimilaritySum = moveSimilaritySum;
            this.moveCount = moveCount;
            this.weights = weights;
            this.decisionTimeOfFirst40Moves = decisionTimeOfFirst40Moves;
            this.moreThanOneBestMatches = moreThanOneBestMatches;
        }

        public Player Player
        {
            get { return this.player; }
        }

        public double AverageProblemSimilarity
        {
            get { return this.problemSimilaritySum / this.moveCount; }
        }

        public double AverageMoveSimilarity
        {
            get { return this.moveSimilaritySum / this.moveCount; }
        }

        public double AverageMoreThanOneBestMatches
        {
            get { return this.moreThanOneBestMatches / (double)this.moveCount; }
        }

        public Weights Weights
        {
            get { return this.weights; }
        }

        public TimeSpan DecisionTimeOfFirst40Moves
        {
            get { return this.decisionTimeOfFirst40Moves; }
        }

        public PlayerReport WithCaseResult(double problemSimilarity, double moveSimilarity, bool moreThanOneBestMatch)
        {
            return new PlayerReport(
                this.player,
                this.problemSimilaritySum + problemSimilarity,
                this.moveSimilaritySum + moveSimilarity,
                this.moreThanOneBestMatches + (moreThanOneBestMatch ? 1 : 0),
                this.moveCount + 1,
                this.weights,
                this.decisionTimeOfFirst40Moves);
        }

        public PlayerReport WithDecisionTimeOfFirst40Moves(TimeSpan timeSpan)
        {
            return new PlayerReport(
                this.player,
                this.problemSimilaritySum,
                this.moveSimilaritySum,
                this.moreThanOneBestMatches,
                this.moveCount,
                this.weights,
                timeSpan);
        }

        public static PlayerReport FromPlayerAndWeights(Player player, Weights weights)
        {
            return new PlayerReport(player, 0, 0, 0, 0, weights, TimeSpan.Zero);

        }
    }
}