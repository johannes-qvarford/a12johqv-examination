namespace a12johqv.Examination.Ai
{
    /// A player that can play a chess match.
    /// It includes the player's name, index on the ranking scale,
    /// its elo-ranking and the filename of the pgn file that
    /// contains its recorded matches.
    public struct Player
    {
        private readonly string name;

        private readonly int index;

        private readonly int ranking;

        private readonly string filename;

        public Player(string name, int index, int ranking, string filename)
        {
            this.name = name;
            this.index = index;
            this.ranking = ranking;
            this.filename = filename;
        }

        public string Name { get { return this.name; } }

        public int Index { get { return this.index; } }

        public int Ranking { get { return this.ranking; } }

        public string Filename { get { return this.filename; } }
    }
}