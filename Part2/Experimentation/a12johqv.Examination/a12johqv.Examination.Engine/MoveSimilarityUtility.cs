namespace a12johqv.Examination.ChessEngine
{
    using System;

    using a12johqv.Examination.Chess;

    public static class MoveSimilarityUtility
    {
        public static SimilarityComparer<Move> CreateSimilarityBySquareContentOnFromSquareSimilarity(Position position, SimilarityComparer<SquareContent> comparer)
        {
            return (a, b) => comparer(position[a.From], position[b.From]);
        }

        public static double SimilarityByInverseManhattanDistanceOfFromAndToSquares(Move a, Move b)
        {
            const int MaxManhattanDistance = 14;
            return (MaxManhattanDistance - (ManhattanDistance(a.From, b.From) + ManhattanDistance(a.To, b.To))) / (double)MaxManhattanDistance;
        }

        private static int ManhattanDistance(Square a, Square b)
        {
            return Distance(a.Column, b.Column) + Distance(a.Row, b.Row);
        }

        private static int Distance(int a, int b)
        {
            return Math.Abs(a - b);
        }
    }
}
