namespace a12johqv.Examination.ChessEngine
{
    using a12johqv.Examination.Chess;

    public static class SquareContentSimilarityComparerUtility
    {
        public static double SimilarityByEqualColorThenByEqualPieceType(SquareContent a, SquareContent b)
        {
            if (a.IsEmpty && b.IsEmpty)
            {
                return 1.0;
            }
            else if (a.IsEmpty && !b.IsEmpty)
            {
                return 0.5;
            }
            else
            {
                return (a.ColorOnSquare == b.ColorOnSquare ? 0.75 : 0)
                    + (a.PieceTypeOnSquare == b.PieceTypeOnSquare ? 0.25 : 0);
            }
        }
    }
}
