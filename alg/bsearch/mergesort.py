import math
import unittest


class MyTestCase(unittest.TestCase):
    def test_sort_zero_size_array(self):
        search = MergeSearch([])
        self.assertEqual([], search.sort())

    def test_sort_single_size_array(self):
        search = MergeSearch([1])
        self.assertEqual([1], search.sort())

    def test_sort_even_size_array(self):
        array = [3, 1, 10, 5, 5, 0]
        search = MergeSearch(array)
        self.assertEqual(sorted(array), search.sort())

    def test_sort_odd_size_array(self):
        array = [7, 1, 2, 9, -2]
        search = MergeSearch(array)
        self.assertEqual(sorted(array), search.sort())

    def test_sort_big_size_array(self):
        array = [7, 1, 2, 9, -2, -5, 5, 3, 5, 0, -10]
        search = MergeSearch(array)
        self.assertEqual(sorted(array), search.sort())


class MergeSearch:
    def __init__(self, array):
        self.array = array

    def sort(self):
        if len(self.array) <= 1:
            return self.array
        return self.merge_sort(self.array)

    def merge_sort(self, arr):
        left = 0
        right = len(arr) - 1
        if right == left:
            return [arr[left]]
        middle_index = int(math.floor((right - left) / 2))
        left_arr = self.merge_sort(arr[left:middle_index + 1])
        right_arr = self.merge_sort(arr[middle_index + 1:right + 1])
        return self.merge_arrays(left_arr, right_arr)

    def merge_arrays(self, arr1, arr2):
        if len(arr1) == 0:
            return arr2
        if len(arr2) == 0:
            return arr1
        result = []
        i1 = 0
        i2 = 0
        while i1 < len(arr1) or i2 < len(arr2):
            if i1 == len(arr1):
                result += arr2[i2:]
                break
            elif i2 == len(arr2):
                result += arr1[i1:]
                break
            elif arr1[i1] < arr2[i2]:
                result.append(arr1[i1])
                i1 += 1
            else:
                result.append(arr2[i2])
                i2 += 1
        return result


if __name__ == '__main__':
    unittest.main()
