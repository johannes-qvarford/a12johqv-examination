namespace a12johqv.Examination.Chess
{
    using System;
    using System.Globalization;
    using System.Linq;

    /// The content that can exist on square on a chess board.
    /// A square can either be empty, or contain a chess piece of a certain type and color.
    /// 
    /// Square content is represented as a string with a dot '.' if it's empty,
    /// or with a certain letter that indicates its piece type and color (see PieceTypeUtility and ColorUtility).
    public struct SquareContent : IEquatable<SquareContent>
    {
        private const byte EmptySquareRepresentation = 0x00;

        private const int BitsForEmptyOrNot = 1;

        private const int BitsForPieceType = 3;

        private const int BitsForColor = 1;

        private const int BitsToShiftForEmptyOrNot = 0;

        private const int BitsToShiftForPieceType = BitsToShiftForEmptyOrNot + BitsForEmptyOrNot;

        private const int BitsToShiftForColor = BitsToShiftForPieceType + BitsForPieceType;

        private const int MaskForShiftedPieceType = (1 << BitsForPieceType) - 1;

        private const int MaskForShiftedColor = (1 << BitsForColor) - 1;

        private const int MaskForShiftedEmptyOrNot = (1 << BitsForEmptyOrNot) - 1;

        private readonly byte representation;

        private static readonly SquareContent EmptySquareContent = new SquareContent(EmptySquareRepresentation);

        private SquareContent(byte representation)
        {
            this.representation = representation;
        }

        public byte Representation
        {
            get { return this.representation; }
        }

        public bool IsEmpty
        {
            get { return this.representation == EmptySquareRepresentation; }
        }

        public PieceType PieceTypeOnSquare
        {
            get
            {
                if (this.IsEmpty)
                {
                    throw new InvalidOperationException("Cannot call PieceTypeOnSquare on empty square.");
                }
                else
                {
                    int shifted = this.representation >> BitsToShiftForPieceType;
                    return (PieceType)(shifted & MaskForShiftedPieceType);
                }
            }
        }

        public Color ColorOnSquare
        {
            get
            {
                if (this.IsEmpty)
                {
                    throw new InvalidOperationException("Cannot call ColorOnSquare on empty square.");
                }
                else
                {
                    return (Color)((this.representation >> BitsToShiftForColor) & MaskForShiftedColor);
                }
            }
        }

        public static SquareContent Empty
        {
            get { return EmptySquareContent; }
        }

        public static SquareContent FromByte(byte representation)
        {
            return new SquareContent(representation);
        }

        public static SquareContent FromPieceAndColor(PieceType pieceType, Color color)
        {
            const int NotEmptyPart = 0x01;
            int pieceTypePart = (int)pieceType << 1;
            int colorPart = (int)color << 4;
            byte representation = (byte)(NotEmptyPart | pieceTypePart | colorPart);
            return new SquareContent(representation);
        }

        public static SquareContent FromString(string serialized)
        {
            if (serialized == null || serialized.Length != 1)
            {
                throw new ArgumentException("Serialized string was null or had wrong length.", serialized);
            }
            else
            {
                if (serialized == ".")
                {
                    return Empty;
                }
                else
                {
                    char character = serialized[0];
                    try
                    {
                        Color color = ColorUtility.ColorOfCharacter(character);
                        PieceType pieceType = PieceTypeUtility.FromCharacter(character);
                        return FromPieceAndColor(pieceType, color);
                    }
                    catch (Exception exception)
                    {
                        if (exception is InvalidOperationException || exception is ArgumentException)
                        {
                            throw new ArgumentException("Serialized string had wrong format.", "serialized", exception);
                        }
                        else
                        {
                            throw;
                        }
                    }
                }
            }
        }

        private static bool IsEnumValid<TEnum>(TEnum e)
        {
            return Enum.GetValues(typeof(TEnum))
                .Cast<TEnum>()
                .Contains(e);
        }

        public bool Equals(SquareContent other)
        {
            return this.representation == other.representation;
        }

        public override bool Equals(object obj)
        {
            return obj is SquareContent && this.Equals((SquareContent)obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return this.representation;
            }
        }

        public override string ToString()
        {
            if (this.IsEmpty)
            {
                return ".";
            }
            else
            {
                string uncoloredPiece = this.PieceTypeOnSquare.AsCharacter().ToString(CultureInfo.InvariantCulture);
                return this.ColorOnSquare.IsCharacterRepresentationCapitalized() ? uncoloredPiece.ToUpper() : uncoloredPiece.ToLower();
            }
        }
    }
}