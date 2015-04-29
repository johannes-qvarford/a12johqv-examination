namespace a12johqv.Examination.Ai
{
    /// Weights used in move adaption.
    public struct Weights
    {
        private readonly double moveWeight;

        private readonly double squareContentWeight;

        private readonly double distanceWeight;

        public Weights(
            double moveWeight,
            double squareContentWeight,
            double distanceWeight)
        {
            this.moveWeight = moveWeight;
            this.squareContentWeight = squareContentWeight;
            this.distanceWeight = distanceWeight;
        }

        public double MoveWeight { get { return this.moveWeight; } }

        public double SquareContentWeight { get { return this.squareContentWeight; } }

        public double DistanceWeight { get { return this.distanceWeight; } }
    }
}
