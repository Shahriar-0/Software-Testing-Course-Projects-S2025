import unittest

def calculate_discounted_price(price, discount_rate, min_purchase):
    if price <= 0 or discount_rate < 0 or discount_rate > 1 or min_purchase <= 0:
        return "Invalid input"
    elif price < min_purchase:
        return f"{price:.1f}"
    else:
        discounted_price = price * (1 - discount_rate)
        return f"{discounted_price:.1f}"


class TestCalculateDiscountedPrice(unittest.TestCase):

    # A0, B0, C0
    def test_a0_b0_c0(self):
        self.assertEqual(calculate_discounted_price(-10, -0.1, -50), "Invalid input")

    # A0, B1, C1
    def test_a0_b1_c1(self):
        self.assertEqual(calculate_discounted_price(-10, 0, 50), "Invalid input")

    # A0, B2, C0
    def test_a0_b2_c0(self):
        self.assertEqual(calculate_discounted_price(-10, 0.2, -50), "Invalid input")

    # A0, B3, C1
    def test_a0_b3_c1(self):
        self.assertEqual(calculate_discounted_price(-10, 1.5, 50), "Invalid input")

    # A1, B1, C0
    def test_a1_b1_c0(self):
        self.assertEqual(calculate_discounted_price(10, 0, -50), "Invalid input")

    # A1, B3, C0
    def test_a1_b3_c0(self):
        self.assertEqual(calculate_discounted_price(10, 1.5, -50), "Invalid input")

    # A1, B0, C1
    def test_a1_b0_c1(self):
        self.assertEqual(calculate_discounted_price(10, -0.1, 50), "Invalid input")

    # A1, B2, C1
    def test_a1_b2_c1(self):
        self.assertEqual(calculate_discounted_price(10, 0.2, 50), "10.0")

    # A2, B1, C0
    def test_a2_b1_c0(self):
        self.assertEqual(calculate_discounted_price(50, 0, -50), "Invalid input")

    # A2, B2, C0
    def test_a2_b2_c0(self):
        self.assertEqual(calculate_discounted_price(50, 0.2, -50), "Invalid input")

    # A2, B0, C1
    def test_a2_b0_c1(self):
        self.assertEqual(calculate_discounted_price(50, -0.1, 50), "Invalid input")

    # A2, B3, C1
    def test_a2_b3_c1(self):
        self.assertEqual(calculate_discounted_price(50, 1.5, 50), "Invalid input")


if __name__ == "__main__":
    unittest.main()
