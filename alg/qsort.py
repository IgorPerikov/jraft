import unittest
import random


class Qsort:
    def __init__(self, array, reverse=False):
        self.array = array
        self.reverse = reverse

    def qsort(self) -> list:
        if len(self.array) < 2:
            return self.array
        self.sort(self.array, 0, len(self.array) - 1)
        return self.array

    def sort(self, array: list, l: int, r: int) -> None:
        if l < r:
            q = self.randomized_partition(array, l, r)
            self.sort(array, l, q - 1)
            self.sort(array, q + 1, r)

    def randomized_partition(self, array: list, l: int, r: int) -> int:
        pivot_index = random.randint(l, r)
        array[r], array[pivot_index] = array[pivot_index], array[r]
        pivot_element = array[r]
        i = l - 1
        for j in range(l, r):
            if self.compare(array[j], pivot_element):
                i += 1
                array[i], array[j] = array[j], array[i]
        array[r], array[i + 1] = array[i + 1], array[r]
        return i + 1

    def compare(self, a, b) -> bool:
        if self.reverse:
            return a >= b
        else:
            return a <= b


class MyTestCase(unittest.TestCase):
    def test_sort_zero_size_array(self):
        search = Qsort([])
        self.assertEqual([], search.qsort())

    def test_sort_single_size_array(self):
        search = Qsort([1])
        self.assertEqual([1], search.qsort())

    def test_sort_even_size_array(self):
        array = [3, 1, 10, 5, 5, 0]
        search = Qsort(array)
        self.assertEqual(sorted(array), search.qsort())

    def test_sort_odd_size_array(self):
        array = [7, 1, 2, 9, -2]
        search = Qsort(array)
        self.assertEqual(sorted(array), search.qsort())

    def test_sort_big_size_array(self):
        array = [7, 1, 2, 9, -2, -5, 5, 3, 5, 0, -10]
        search = Qsort(array)
        self.assertEqual(sorted(array), search.qsort())

    def test_sort_big_size_array_reversed(self):
        array = [7, 1, 2, 9, -2, -5, 5, 3, 5, 0, -10]
        search = Qsort(array, reverse=True)
        self.assertEqual(sorted(array, reverse=True), search.qsort())


if __name__ == '__main__':
    unittest.main()
