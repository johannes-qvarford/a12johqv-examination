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

        public static double SimilarityByManhattanDistanceOfFromAndToSquares(Move a, Move b)
        {
            return ManhattanDistance(a.From, b.From) + ManhattanDistance(a.To, b.To);
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
