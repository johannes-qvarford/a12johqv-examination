namespace a12johqv.Examination.Chess
{
    using System;

    /// A square that represents a discrete location on a chess board.
    /// It can be described with a zero-based row/rank and a column/file, or a square index.
    /// A square index is zero-based and starts on the first row and column, and increases by column.
    /// When it passes the last column it wraps around to the new row.
    /// 
    /// A square's string representation is its representation in algebraic notation.
    /// First comes a letter from a to h that represents the rank, and then a number that represents the row.
    /// "c2" therefor represents a square with column 2 (zero-based) and row 1 (zer-based).
    public struct Square : IEquatable<Square>
    {
        private readonly int row;

        private readonly int column;

        private Square(int row, int column)
        {
            this.row = row;
            this.column = column;
        }

        public int Row
        {
            get { return this.row; }
        }

        public int Column
        {
            get { return this.column; }
        }

        public int SquareIndex
        {
            get { return (this.row * 8) + this.column; }
        }

        public static Square FromRowAndColumn(int row, int column)
        {
            if (!IsValidRowOrColumn(row))
            {
                throw new ArgumentException("Row has to be in [0, 8)", "row");
            }
            else if (!IsValidRowOrColumn(column))
            {
                throw new ArgumentException("Column has to be in [0, 8)", "column");
            }
            else
            {
                return new Square(row, column);
            }
        }

        public static Square FromSquareIndex(int squareIndex)
        {
            if (!IsValidSquareIndex(squareIndex))
            {
                throw new ArgumentException("Square index has to be in [0, 64)", "squareIndex");
            }
            else
            {
                return new Square(squareIndex / 8, squareIndex % 8);
            }
        }

        public static Square FromString(string serialized)
        {
            if (serialized == null || serialized.Length != 2)
            {
                throw new ArgumentException("Serialized string needs to be two chracters long", serialized);
            }
            else
            {
                char rowCharacter = serialized[1];
                char columnCharacter = serialized[0];
                int row = CharacterToRow(rowCharacter);
                int column = CharacterToColumn(columnCharacter);
                try
                {
                    return Square.FromRowAndColumn(row, column);
                }
                catch (ArgumentException exception)
                {
                    throw new ArgumentException("Serialized string has wrong format", "serialized", exception);
                }
            }
        }

        private static bool IsValidRowOrColumn(int rowOrColumn)
        {
            return rowOrColumn >= 0 && rowOrColumn < 8;
        }

        private static bool IsValidSquareIndex(int squareIndex)
        {
            return squareIndex >= 0 && squareIndex < 8 * 8;
        }

        public bool Equals(Square square)
        {
            return this.SquareIndex.Equals(square.SquareIndex);
        }

        public override bool Equals(object obj)
        {
            Square? square = obj as Square?;
            return square.HasValue && this.Equals(square.Value);
        }

        public override int GetHashCode()
        {
            return this.SquareIndex.GetHashCode();
        }

        public override string ToString()
        {
            return string.Format("{0}{1}", ColumnToCharacter(this.column), RowToCharacter(this.row));
        }

        private static char RowToCharacter(int column)
        {
            return (char)('1' + column);
        }

        private static int CharacterToRow(char character)
        {
            return character - '1';
        }

        private static char ColumnToCharacter(int row)
        {
            return (char)('a' + row);
        }

        private static int CharacterToColumn(char character)
        {
            return character - 'a';
        }
    }
}