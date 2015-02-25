namespace a12johqv.Examination.Engine
{
    using a12johqv.Examination.Chess;

    public struct Case
    {
        private readonly Position position;

        private readonly Move move;

        private Case(Position position, Move move)
        {
            this.position = position;
            this.move = move;
        }

        public Position Position
        {
            get { return this.position; }
        }

        public Move Move
        {
            get { return this.move; }
        }

        public static Case FromPositionAndMove(Position position, Move move)
        {
            return new Case(position, move);
        }
    }
}