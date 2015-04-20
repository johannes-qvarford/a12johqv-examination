namespace a12johqv.Examination.Study
{
    using a12johqv.Examination.Ai;

    public struct StudyReport
    {
        private readonly string whitePlayerName;

        private readonly string blackPlayerName;

        private readonly Weights weights;

        private readonly GameReport gameReport;

        private StudyReport(GameReport gameReport, Weights weights, string whitePlayerName, string blackPlayerName)
            : this()
        {
            this.weights = weights;
            this.whitePlayerName = whitePlayerName;
            this.blackPlayerName = blackPlayerName;
            this.gameReport = gameReport;
        }

        public string WhitePlayerName { get { return this.whitePlayerName; } }

        public string BlackPlayerName { get { return this.blackPlayerName; } }

        public Weights Weights { get { return this.weights; } }

        public GameReport GameReport { get { return this.gameReport; } }

        public static StudyReport FromGameReportAndPlayerNamesAndWeights(GameReport gameReport, string whitePlayerName, string blackPlayerName, Weights weights)
        {
            return new StudyReport(
                gameReport: gameReport,
                whitePlayerName: whitePlayerName,
                blackPlayerName: blackPlayerName,
                weights: weights);
        }
    }
}