namespace a12johqv.Examination.ChessEngine
{
    using System;
    using System.Linq;

    using a12johqv.Examination.Chess;

    public static class PositionSimilarityComparerUtility
    {
        public static SimilarityComparer<Position> CreateSimilarityByAverageOfSquareContentSimilarity(
            SimilarityComparer<SquareContent> squareContentSimilarityComparer)
        {
            return (a, b) => 
                a.SquareContentsFromLowRowAndColumn
                    .Zip(b.SquareContentsFromLowRowAndColumn, Tuple.Create)
                    .Select(t => squareContentSimilarityComparer(t.Item1, t.Item2))
                    .Average();
        }
    }
}
