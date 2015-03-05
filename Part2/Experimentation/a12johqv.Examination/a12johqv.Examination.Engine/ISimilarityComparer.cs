namespace a12johqv.Examination.ChessEngine
{
    using a12johqv.Examination.Chess;

    public interface ISimilarityComparer<T>
    {
        double GetSimilarity(ref Position first, ref Position second);
    }
}