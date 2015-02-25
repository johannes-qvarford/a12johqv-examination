namespace a12johqv.Examination.Chess
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;

    public static class PieceTypeUtility
    {
        private static readonly IDictionary<PieceType, char> PieceTypeCharacterRepresentation =
            new Dictionary<PieceType, char>
                {
                    { PieceType.Pawn, 'p' },
                    { PieceType.Rook, 'r' },
                    { PieceType.Knight, 'n' },
                    { PieceType.Bishop, 'b' },
                    { PieceType.Queen, 'q' },
                    { PieceType.King, 'k' }
                };

        public static char AsCharacter(this PieceType pieceType)
        {
            return PieceTypeCharacterRepresentation[pieceType];
        }

        public static PieceType FromCharacter(char character)
        {
            char normalizedCharacter = character.ToString(CultureInfo.InvariantCulture).ToLower()[0];
            try
            {
                return PieceTypeCharacterRepresentation.Single(kv => kv.Value == normalizedCharacter).Key;
            }
            catch (InvalidOperationException exception)
            {
                throw new ArgumentException("Character is not a representation of a valid piece", "character", exception);
            }
        }
    }
}