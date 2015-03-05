namespace a12johqv.Examination.ChessEngine
{
    using System;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Engine;

    public class CbrEngine
    {
        private readonly CaseBase caseBase;

        private CbrEngine(CaseBase caseBase)
        {
            this.caseBase = caseBase;
        }

        public static CbrEngine FromCaseBase(CaseBase caseBase, ISimilarityComparer<Case> comparer)
        {
            return new CbrEngine(caseBase);
        }

        public Move DecideMove(Position currentPosition, Color color)
        {
            throw new NotImplementedException();
        }
    }
}
