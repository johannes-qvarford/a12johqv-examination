namespace a12johqv.Examination.Ai
{
    /// A report of a match for a player.
    /// It contains the player, its average problem similarity and average adapted move similarity.
    public class PlayerReport
    {
        private readonly Player player;

        private readonly double problemSimilaritySum;

        private readonly double moveSimilaritySum;

        private readonly int moveCount;

        private PlayerReport(Player player, double problemSimilaritySum, double moveSimilaritySum, int moveCount)
        {
            this.player = player;
            this.problemSimilaritySum = problemSimilaritySum;
            this.moveSimilaritySum = moveSimilaritySum;
            this.moveCount = moveCount;
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

        public PlayerReport WithCaseResult(double problemSimilarity, double moveSimilarity)
        {
            return new PlayerReport(this.player, this.problemSimilaritySum + problemSimilarity, this.moveSimilaritySum + moveSimilarity, this.moveCount + 1);
        }

        public static PlayerReport FromPlayer(Player player)
        {
            return new PlayerReport(player, 0, 0, 0);
        }
    }
}