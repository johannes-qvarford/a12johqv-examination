namespace a12johqv.Examination.Application
{
    using System;
    using System.Linq;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Engine;

    public class Program
    {
        public static void Main(string[] args)
        {
            var loader = new CasebaseLoader();
            var first = new ChessCbrEngine(loader.LoadCasebase(0));
            var second = new ChessCbrEngine(loader.LoadCasebase(1));
            var random = new Random();
            var results = Enumerable.Range(0, 100).Select(_ => PlayGame(first, second, random)).ToArray();
            int a = 0;
        }

        private static Result PlayGame(ChessCbrEngine first, ChessCbrEngine second, Random random)
        {
            Position position = Position.Initial;
            var current = first;
            var other = second;

            while (position.CurrentResult == Result.Undecided)
            {
                var move = current.DecideMove(position, random);
                position = position.ByMove(move);

                Swap(ref current, ref other);
            }
            return position.CurrentResult;
        }

        private static void Swap<T>(ref T a, ref T b)
        {
            var temp = a;
            a = b;
            b = temp;
        }
    }
}
