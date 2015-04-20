namespace a12johqv.Examination.Ai
{
    using System;

    /// A player that can play a chess match.
    /// It includes the player's name, index on the rating scale,
    /// its elo-rating and the filename of the pgn file that
    /// contains its recorded games.
    public struct Player : IEquatable<Player>
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

        public bool Equals(Player other)
        {
            return this.name.Equals(other.name)
                && this.index.Equals(other.index)
                && this.ranking.Equals(other.ranking)
                && this.filename.Equals(other.filename);
        }

        public override bool Equals(object obj)
        {
            return obj is Player && this.Equals((Player)obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = (int)2166136261;
                hash = hash * 16777619 ^ this.name.GetHashCode();
                hash = hash * 16777619 ^ this.index.GetHashCode();
                hash = hash * 16777619 ^ this.ranking.GetHashCode();
                hash = hash * 16777619 ^ this.filename.GetHashCode();
                return hash;
            }
        }
    }
}