package App;

/**
 * StubHubApiScope
 * - represents the available levels of scope for the StubHub Api endpoints
 */
public enum StubHubApiScope {
    PROD("production"),
    SANDBOX("sandbox");

    String type;

    private StubHubApiScope(String type) {
        this.type = type;
    }
}
