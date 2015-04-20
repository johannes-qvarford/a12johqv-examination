namespace a12johqv.Examination.Chess
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Linq;

    public unsafe struct BarePosition : IEnumerable<SquareContent>, IEquatable<BarePosition>
    {
        private fixed byte squares[64];

        private BarePosition(byte* from)
        {
            fixed (byte* to = this.squares)
            {
                CopySquareArray(from: from, to: to);
            }
        }

        public byte* Bytes
        {
            get { fixed (byte* b = this.squares) return b; }
        }

        public SquareContent this[int index]
        {
            get
            {
                fixed (byte* pointer = this.squares)
                {
                    return SquareContent.FromByte(*(pointer + index));
                }
                
            }
            set
            {
                fixed (byte* pointer = this.squares)
                {
                    *(pointer + index) = value.Representation;
                }
            }
        }

        public SquareContent this[Square square]
        {
            get { return this[square.SquareIndex]; }
            set { this[square.SquareIndex] = value; }
        }

        public static BarePosition FromSquareContents(IList<SquareContent> squares)
        {
            byte* stackSquares = stackalloc byte[64];
            for (int i = 0; i < 64; i++)
            {
                stackSquares[i] = squares[i].Representation;
            }
            return new BarePosition(stackSquares);
        }

        public static BarePosition FromBarePosition(ref BarePosition barePosition)
        {
            fixed (byte* from = barePosition.squares)
            {
                return new BarePosition(from: from);
            }
        }

        private static void CopySquareArray(byte* from, byte* to)
        {
            for (int i = 0; i < 64; i++)
            {
                *(to + i) = *(from + i);
            }
        }

        public IEnumerator<SquareContent> GetEnumerator()
        {
            var destination = new SquareContent[64];
            for (int i = 0; i < 64; i++)
            {
                destination[i] = this[i];
            }
            return destination.Cast<SquareContent>().GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        public bool Equals(BarePosition other)
        {
            fixed (byte* thisPointer = this.squares)
            {
                {
                    for (int i = 0; i < 64; i++)
                    {
                        if (*(thisPointer + i) != other.squares[i])
                        {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        public override bool Equals(object obj)
        {
            return obj is BarePosition && this.Equals((BarePosition)obj);
        }


        public override int GetHashCode()
        {
            unchecked
            {
                int hash = (int)2166136261;
                fixed (byte* pointer = this.squares)
                {
                    for (int i = 0; i < 64; i++)
                    {
                        hash = hash * 16777619 ^ *(pointer + i);
                    }
                }
                return hash;
            }
        }
    }
}