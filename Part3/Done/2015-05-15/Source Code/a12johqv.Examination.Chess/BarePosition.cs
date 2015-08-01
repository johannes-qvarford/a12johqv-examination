namespace a12johqv.Examination.Chess
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Linq;

    /// A bare position without bookkeeping information;
    /// a board in other words.
    /// The board is represented as a tightly packed 64-element array of bytes.
    /// (Would be SquareContent if c# allowed declaration of fixed size arrays for user defined types).
    /// It's possible to index the board to get the content at a square, but it's also possible to get a pointer
    /// to the byte array and traverse it manually. This is nessesary for an optimization.
    /// 
    /// This class goes against pretty much every safety measure.
    /// It can be mutated through the byte pointer.
    /// There is no bounds checking so both indexing into the board
    /// and dererencing through a modified byte pointer is unsafe.
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
        }

        public SquareContent this[Square square]
        {
            get { return this[square.SquareIndex]; } 
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