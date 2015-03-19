namespace a12johqv.Examination.Application
{
    using System;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Engine;

    public class Program
    {
        public static void Main(string[] args)
        {
            var loader = new CasebaseLoader();
            var first = new ChessCbrEngine(loader.LoadCasebase(0));
            var second = new ChessCbrEngine(loader.LoadCasebase(1));


            Position position = Position.Initial;
            var current = first;
            var other = second;
            while (position.CurrentResult == Result.Undecided)
            {
                var move = current.DecideMove(position);
                position.ByMove(move);
                Swap(ref current, ref other);
            }
            int a = 0;
        }

        private static void Swap<T>(ref T a, ref T b)
        {
            var temp = a;
            a = b;
            b = temp;
        }
    }
}
