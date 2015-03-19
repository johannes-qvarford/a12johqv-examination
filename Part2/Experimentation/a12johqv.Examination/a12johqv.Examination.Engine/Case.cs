namespace a12johqv.Examination.Engine
{
    using a12johqv.Examination.Chess;

    public struct Case<TProblem, TSolution>
    {
        private readonly TProblem problem;

        private readonly TSolution solution;

        public Case(TProblem problem, TSolution solution)
        {
            this.problem = problem;
            this.solution = solution;
        }

        public TProblem Problem
        {
            get { return this.problem; }
        }

        public TSolution Solution
        {
            get { return this.solution; }
        }
    }

    public static class Case
    {
        public static Case<TProblem, TSolution> FromProblemAndSolution<TProblem, TSolution>(TProblem problem, TSolution solution)
        {
            return new Case<TProblem, TSolution>(problem, solution);
        }
    }
}