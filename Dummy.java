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
