namespace a12johqv.Examination.Ai
{
    using a12johqv.Examination.Chess;

    /// Square content similarity ca be described as:
    /// F(e, e) = 1
    /// F(e, t) = 0.5
    /// F(s, t) = (0.75 if s.Color = t.Color, 0 otherwise)
    ///         + (0.25 if s.Type = t.Type, 0 otherwise)
    /// where e is the empty square content, and s and t are square contents.
    public static class SquareContentSimilarity
    {
        public static double Similarity(SquareContent a, SquareContent b)
        {
            if (a.IsEmpty && b.IsEmpty)
            {
                return 1.0;
            }
            else if (a.IsEmpty || b.IsEmpty)
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