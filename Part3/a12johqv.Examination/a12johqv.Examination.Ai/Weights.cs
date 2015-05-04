namespace a12johqv.Examination.Ai
{
    /// Weights used in move adaption.
    public struct Weights
    {
        private readonly double moveWeight;

        private readonly double distanceWeight;

        private readonly double squareContentWeight;

        public Weights(
            double moveWeight,
            double distanceWeight,
            double squareContentWeight)
        {
            this.moveWeight = moveWeight;
            this.squareContentWeight = squareContentWeight;
            this.distanceWeight = distanceWeight;
        }

        public double MoveWeight { get { return this.moveWeight; } }

        public double DistanceWeight { get { return this.distanceWeight; } }

        public double SquareContentWeight { get { return this.squareContentWeight; } }
    }
}
