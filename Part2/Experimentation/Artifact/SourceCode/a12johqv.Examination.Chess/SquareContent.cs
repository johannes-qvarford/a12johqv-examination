﻿namespace a12johqv.Examination.Chess
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
        private readonly PieceType pieceType;

        private readonly Color color;

        private readonly bool empty;

        private static readonly SquareContent EmptySquareContent = new SquareContent(emptyToken: 0);

        private SquareContent(PieceType pieceType, Color color)
        {
            this.pieceType = pieceType;
            this.color = color;
            this.empty = false;
        }

        // Structs cannot have empty constructor, using wortless token parameter to get around restriction.
        private SquareContent(int emptyToken)
        {
            this.empty = true;
            this.color = Color.White;
            this.pieceType = PieceType.Pawn;
        }

        public bool IsEmpty
        {
            get { return this.empty; }
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
                    return this.pieceType;
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
                    return this.color;
                }
            }
        }

        public static SquareContent Empty
        {
            get { return EmptySquareContent; }
        }

        public static SquareContent FromPieceAndColor(PieceType pieceType, Color color)
        {
            return new SquareContent(pieceType, color);
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
            return this.empty.Equals(other.empty)
                && this.color.Equals(other.color)
                && this.pieceType.Equals(other.pieceType);
        }

        public override bool Equals(object obj)
        {
            SquareContent? squareContent = obj as SquareContent?;
            return squareContent.HasValue
                && this.Equals(squareContent.Value);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = (int)2166136261;
                hash = hash * 16777619 ^ this.color.GetHashCode();
                hash = hash * 16777619 ^ this.pieceType.GetHashCode();
                hash = hash * 16777619 ^ this.empty.GetHashCode();
                return hash;
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
                string uncoloredPiece = this.pieceType.AsCharacter().ToString(CultureInfo.InvariantCulture);
                return this.color.IsCharacterRepresentationCapitalized() ? uncoloredPiece.ToUpper() : uncoloredPiece.ToLower();
            }
        }
    }
}