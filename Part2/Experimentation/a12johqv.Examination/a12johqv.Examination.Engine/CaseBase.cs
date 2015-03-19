namespace a12johqv.Examination.Engine
{
    using System.Collections.Generic;
    using System.Collections.Immutable;
    using System.Diagnostics.Contracts;
    using System.Linq;

    using a12johqv.Examination.ChessEngine;

    public struct Casebase<TProblem, TSolution>
    {
        private readonly IList<Case<TProblem, TSolution>> cases;

        public Casebase(IList<Case<TProblem, TSolution>> cases)
        {
            this.cases = cases;
        }

        public void AddCase(Case<TProblem, TSolution> @case)
        {
            this.cases.Add(@case);
        }

        public Case<TProblem, TSolution> FindMostSimilarCase(TProblem problem, SimilarityComparer<TProblem> comparer)
        {
            Contract.Assert(this.cases.Count > 1);
            return
                this.cases.Select(@case => new { Case = @case, Similarity = comparer(@case.Problem, problem) })
                    .Aggregate((bestPair, newPair) => bestPair.Similarity > newPair.Similarity ? bestPair : newPair)
                    .Case;
        }
    }

    public static class Casebase
    {
        public static Casebase<TProblem, TSolution> FromCases<TProblem, TSolution>(IEnumerable<Case<TProblem, TSolution>> cases)
        {
            return new Casebase<TProblem, TSolution>(cases.ToList());
        }
    }
}