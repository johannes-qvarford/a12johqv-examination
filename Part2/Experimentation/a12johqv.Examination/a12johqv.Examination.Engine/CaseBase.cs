namespace a12johqv.Examination.Engine
{
    using System;
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

        public Case<TProblem, TSolution> FindMostSimilarCase(TProblem problem, SimilarityComparer<TProblem> comparer, Random random)
        {
            Contract.Assert(this.cases.Count > 1);
            var caseSimilarities = this.cases
                .Select(@case => new CaseSimilarity(@case, comparer(@case.Problem, problem)))
                .Aggregate(
                    ImmutableList.Create<CaseSimilarity>(),
                    (list, current) => !list.Any() || AreClose(list.First().Similarity, current.Similarity) ? list.Add(current) : list);
            return caseSimilarities[random.Next(maxValue: caseSimilarities.Count)].Case;
        }

        private static bool AreClose(double a, double b)
        {
            const double Epsilon = 0.001;
            return Math.Abs(a - b) < Epsilon;
        }

        private struct CaseSimilarity
        {
            private readonly Case<TProblem, TSolution> @case;

            private readonly double similarity;

            public CaseSimilarity(Case<TProblem, TSolution> @case, double similarity)
                : this()
            {
                this.@case = @case;
                this.similarity = similarity;
            }

            public Case<TProblem, TSolution> Case { get { return this.@case; } }

            public double Similarity { get { return this.similarity; } }
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