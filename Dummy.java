public class Solution {
    public int solve(int[] A, int[] B, int C) {
        int n = A.length;
        
        int[] dp = new int[C + 1];
        
        for (int i = 0; i < n; i++) {
            // Traverse backwards to avoid reusing same item
            for (int w = C; w >= B[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - B[i]] + A[i]);
            }
        }
        
        return dp[C];
    }
}
----------+

public class Solution {
    public int solve(int A, int[] B, int[] C) {
        int n = B.length;
        
        // dp[w] = max value for capacity w
        int[] dp = new int[A + 1];
        
        // Loop over all capacities
        for (int w = 0; w <= A; w++) {
            // Try picking every item
            for (int i = 0; i < n; i++) {
                if (C[i] <= w) {
                    dp[w] = Math.max(dp[w], dp[w - C[i]] + B[i]);
                }
            }
        }
        
        return dp[A];
    }
}
----------

import java.util.*;

public class Solution {
    class Item {
        int value;
        int weight;
        double ratio;

        Item(int v, int w) {
            value = v;
            weight = w;
            ratio = (double) v / w;
        }
    }

    public int solve(int[] A, int[] B, int C) {
        int n = A.length;

        Item[] items = new Item[n];

        // Create items
        for (int i = 0; i < n; i++) {
            items[i] = new Item(A[i], B[i]);
        }

        // Sort by ratio descending
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio, a.ratio));

        double totalValue = 0.0;

        for (int i = 0; i < n; i++) {
            if (C == 0) break;

            if (items[i].weight <= C) {
                // Take full item
                totalValue += items[i].value;
                C -= items[i].weight;
            } else {
                // Take fraction
                totalValue += items[i].ratio * C;
                C = 0;
            }
        }

        // Return floor(ans * 100)
        return (int) (totalValue * 100);
    }
}
