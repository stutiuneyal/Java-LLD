import java.util.List;

public interface Q03CustomerIssueAssignerInterface {
    void init(List<String> issueTypes, Helper03 helper);

    String createIssue(String issueId, String orderId, int issueType, String description);

    String addAgent(String agentId, List<Integer> expertise);

    String assignIssue(String issueId, int assignDecisionType);

    void resolveIssue(String issueId, String resolution);

    List<String> getAgentHistory(String agentId);
}
