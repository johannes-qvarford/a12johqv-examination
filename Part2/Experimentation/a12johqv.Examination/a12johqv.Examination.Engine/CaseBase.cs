namespace a12johqv.Examination.Engine
{
    using System.Collections.Generic;
    using System.Collections.Immutable;

    public class CaseBase
    {
        private readonly IReadOnlyList<Case> cases;

        private CaseBase(IReadOnlyList<Case> cases)
        {
            this.cases = cases;
        }

        public static CaseBase FromCases(IEnumerable<Case> cases)
        {
            return new CaseBase(ImmutableList.CreateRange(cases));
        }
    }
}