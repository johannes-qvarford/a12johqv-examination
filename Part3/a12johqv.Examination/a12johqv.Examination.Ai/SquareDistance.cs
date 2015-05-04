namespace a12johqv.Examination.Ai
{
    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Core;

    /// Square distance utility functions.
    public static class SquareDistance
    {
        public static double NormalizedManhattanDistance(Square a, Square b)
        {
            const int MaxManhattanDistance = 14;
            return (MathUtility.Distance(a.Column, b.Column) + MathUtility.Distance(a.Row, b.Row)) / MaxManhattanDistance;
        }
    }
}