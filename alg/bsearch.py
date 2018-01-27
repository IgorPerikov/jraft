import unittest
import math


class MyTestCase(unittest.TestCase):
    def test_not_found(self):
        bsearch = Bsearch()
        index = bsearch.search([1, 3, 5, 10], 4)
        self.assertEqual(-1, index)

    def test_found(self):
        bsearch = Bsearch()
        index = bsearch.search([1, 3, 5, 8], 5)
        self.assertEqual(2, index)


class Bsearch:
    def search(self, array, elem):
        if len(array) == 0:
            return -1
        return self.sub_search(array, 0, len(array) - 1, elem)

    def sub_search(self, array, left, right, elem):
        if left == right - 1:
            if array[left] == elem:
                return left
            if array[right] == elem:
                return right
            return -1
        if left == right:
            if array[left] == elem:
                return left
            return -1
        pivot_index = math.floor((left + right) / 2)
        pivot = array[pivot_index]
        if pivot == elem:
            return pivot_index
        elif pivot > elem:
            return self.sub_search(array, left, pivot_index - 1, elem)
        else:
            return self.sub_search(array, pivot_index + 1, right, elem)


if __name__ == '__main__':
    unittest.main()
