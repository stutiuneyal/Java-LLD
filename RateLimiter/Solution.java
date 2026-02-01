public class Solution {

    public static void main(String[] args) {
        RateLimiter rateLimiter = new RateLimiter();

        rateLimiter.addResource("search", "fixed-window-counter", "2,4");
        rateLimiter.addResource("upload", "sliding-window-counter", "2,3");
        rateLimiter.addResource("profile", "fixed-window-counter", "1,3");

        System.out.println(rateLimiter.isAllowed("search", 0));   // expected: true
        System.out.println(rateLimiter.isAllowed("upload", 1));   // expected: true
        System.out.println(rateLimiter.isAllowed("search", 2));   // expected: true
        System.out.println(rateLimiter.isAllowed("profile", 3));  // expected: true
        System.out.println(rateLimiter.isAllowed("upload", 4));   // expected: true
        System.out.println(rateLimiter.isAllowed("search", 5));   // expected: true
        System.out.println(rateLimiter.isAllowed("search", 6));   // expected: true
    }
}
