namespace a12johqv.Examination.Ai
{
    using a12johqv.Examination.Chess;

    /// Square content similarity ca be described as:
    /// F(e, e) = 4
    /// F(e, t) = 2
    /// F(s, t) = (3 if s.Color = t.Color, 0 otherwise)
    ///         + (1 if s.Type = t.Type, 0 otherwise)
    /// where e is the empty square content, and s and t are square contents.
    public static class SquareContentSimilarity
    {
        public static int Similarity(SquareContent a, SquareContent b)
        {
            if (a.IsEmpty && b.IsEmpty)
            {
                return 4;
            }
            else if (a.IsEmpty || b.IsEmpty)
            {
                return 2;
            }
            else
            {
                return (a.ColorOnSquare == b.ColorOnSquare ? 3 : 0)
                    + (a.PieceTypeOnSquare == b.PieceTypeOnSquare ? 1 : 0);
            }
        }
    }
}