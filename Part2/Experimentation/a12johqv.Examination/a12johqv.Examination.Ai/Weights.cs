﻿namespace a12johqv.Examination.Ai
{
    /// Weights used in move adaption.
    public class Weights
    {
        private readonly double positionSquareContentSimilarity;

        private readonly double positionSquareWithSquareContentDistance;

        private readonly double moveInverseDistanceWeight;

        private readonly double moveSquareContentWeight;

        private readonly double moveSquareContentSourceWeight;

        private readonly double moveSquareContentTargetWeight;

        private readonly double moveInverseDistanceSourceWeight;

        private readonly double moveInverseDistanceTargetWeight;

        public Weights(
            double moveInverseDistanceWeight,
            double moveSquareContentWeight,
            double moveSquareContentSourceWeight,
            double moveSquareContentTargetWeight,
            double moveInverseDistanceSourceWeight,
            double moveInverseDistanceTargetWeight,
            double positionSquareContentSimilarity,
            double positionSquareWithSquareContentDistance)
        {
            this.moveInverseDistanceWeight = moveInverseDistanceWeight;
            this.moveSquareContentWeight = moveSquareContentWeight;
            this.moveSquareContentSourceWeight = moveSquareContentSourceWeight;
            this.moveSquareContentTargetWeight = moveSquareContentTargetWeight;
            this.moveInverseDistanceSourceWeight = moveInverseDistanceSourceWeight;
            this.moveInverseDistanceTargetWeight = moveInverseDistanceTargetWeight;
            this.positionSquareContentSimilarity = positionSquareContentSimilarity;
            this.positionSquareWithSquareContentDistance = positionSquareWithSquareContentDistance;
        }

        public double MoveInverseDistanceWeight { get { return this.moveInverseDistanceWeight; } }

        public double MoveSquareContentWeight { get { return this.moveSquareContentWeight; } }

        public double MoveSquareContentSourceWeight { get { return this.moveSquareContentSourceWeight; } }

        public double MoveSquareContentTargetWeight { get { return this.moveSquareContentTargetWeight; } }

        public double MoveInverseDistanceSourceWeight { get { return this.moveInverseDistanceSourceWeight; } }

        public double MoveInverseDistanceTargetWeight { get { return this.moveInverseDistanceTargetWeight; } }

        public double PositionSquareContentSimilarity { get { return this.positionSquareContentSimilarity; } }

        public double PositionSquareWithSquareContentDistance { get { return this.positionSquareWithSquareContentDistance; } }
    }
}
