namespace a12johqv.Examination.Core
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    /// Extention methods for System.Linq.IEnumerable.
    public static class EnumerableUtility
    {
        public static IEnumerable<TElement> Cycle<TElement>(this IEnumerable<TElement> sequence)
        {
            var array = sequence.ToArray();
            while (true)
            {
                foreach (var element in array)
                {
                    yield return element;
                }
            }
        }

        public static IEnumerable<Tuple<TElement, TElement>> Pairs<TElement>(IEnumerable<TElement> sequence)
        {
            var it = sequence.GetEnumerator();
            while (true)
            {
                if (!it.MoveNext())
                {
                    yield break;
                }
                var first = it.Current;

                if (!it.MoveNext())
                {
                    yield break;
                }
                var second = it.Current;

                yield return Tuple.Create(first, second);
            }
        }
    }
}