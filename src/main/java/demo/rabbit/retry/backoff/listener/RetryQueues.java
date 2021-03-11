package demo.rabbit.retry.backoff.listener;

public class RetryQueues {
    private long initialInterval;
    private double factor;
    private long maxWait;
    private int maxRetry;

    public RetryQueues(long initialInterval, double factor, long maxWait, int maxRetry) {
        this.initialInterval = initialInterval;
        this.factor = factor;
        this.maxWait = maxWait;
        this.maxRetry = maxRetry;
    }

    public boolean retriesExhausted(int retry) {
        return retry >= maxRetry;
    }

    public long getTimeToWait(int retry) {
        double time = initialInterval * Math.pow(factor, retry);
        if (time > maxWait) {
            return maxWait;
        }

        return (long) time;
    }
}