// ****** It's better to write code in your local code editor and paste it back here *********

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Solution implements Q03CustomerIssueAssignerInterface {
    private Helper03 helper;

    private final ConcurrentHashMap<String, Issue> issuesById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Agent> agentsById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, AssignmentStrategy> strategies = new ConcurrentHashMap<>();
    private volatile List<String> issueTypes = new ArrayList<>();

    public Solution() {
    }

    /**
     * use helper.print("") and helper.println("") for logging
     * instead of System.out.println() else your logs won't be visible
     */
    public void init(List<String> issueTypes, Helper03 helper) {
        this.helper = helper;
        // helper.println("Customer issue resolution system initialized");

        this.issueTypes = new ArrayList<>();

        if (issueTypes != null) {
            for (String type : issueTypes) {
                this.issueTypes.add(type == null ? "" : type.trim().toLowerCase());
            }
        }

        issuesById.clear();
        agentsById.clear();
        strategies.clear();

        strategies.put(0, new LowestOpenIssuesStrategy());
        strategies.put(1, new MostExperiencedStrategy());
        strategies.put(2, new LeastOpenIssueForTypeStrategy());
    }

    /**
     * returns "issue created" or "issue already exists" or
     * "invalid issue type" : in case issueType is not found in issueTypes list
     * passed in init() method.
     */
    public String createIssue(String issueId, String orderId, int issueType, String description) {

        if (!isValidIssueType(issueType)) {
            return "invalid issue type";
        }

        if (issuesById.get(issueId) != null) {
            return "issue already exists";
        }

        Issue newIssue = new Issue(issueId, orderId, issueType, description);
        Issue existing = issuesById.putIfAbsent(issueId, newIssue);
        if (existing != null) {
            return "issue already exists";
        }

        return "issue created";
    }

    // returns "success" or "agent already exists"
    public String addAgent(String agentId, List<Integer> expertise) {
        if (agentsById.get(agentId) != null) {
            return "agent already exists";
        }

        Set<Integer> validatedExpertise = new HashSet<>();
        if (expertise != null) {
            for (Integer type : expertise) {
                if (type != null && isValidIssueType(type)) {
                    validatedExpertise.add(type);
                }
            }
        }

        Agent agent = new Agent(agentId, validatedExpertise);
        Agent existing = agentsById.putIfAbsent(agentId, agent);
        if (existing != null) {
            return "agent already exists";
        }

        return "success";
    }

    /**
     * returns id of the agent or "issue doesn't exist" or
     * "issue already assigned" or "agent with expertise doesn't exist"
     */
    public String assignIssue(String issueId, int assignStrategy) {

        Issue issue = issuesById.get(issueId);
        if (issue == null) {
            return "issue doesn't exist";
        }

        AssignmentStrategy assignmentStrategy = strategies.get(assignStrategy);
        if (assignmentStrategy == null) {
            return "agent with expertise doesn't exist";
        }

        synchronized (issue) {
            if (issue.status != IssueStatus.OPEN) {
                return "issue already assigned";
            }

            List<Agent> eligibleAgents = new ArrayList<>();
            for (Agent agent : agentsById.values()) {
                if (agent.hasExpertise(issue.issueType)) {
                    eligibleAgents.add(agent);
                }
            }

            if (eligibleAgents.isEmpty()) {
                return "agent with expertise doesn't exist";
            }

            Agent selected = assignmentStrategy.selectAgent(issue, eligibleAgents);
            if (selected == null) {
                return "agent with expertise doesn't exist";
            }

            synchronized (selected) {
                if (issue.status != IssueStatus.OPEN) {
                    return "issue already assigned";
                }

                issue.assignedAgentId = selected.agentId;
                issue.status = IssueStatus.ASSIGNED;

                selected.totalOpenIssues.incrementAndGet();
                selected.getOpenByTypeCounter(issue.issueType).incrementAndGet();
            }

            return selected.agentId;
        }
    }

    /**
     * - resolution is credited to the agent who was assigned the issue with
     * [issueId]
     * 
     * @param issueId will refer to an existing issue. will always be valid.
     */
    public void resolveIssue(String issueId, String resolution) {

        Issue issue = issuesById.get(issueId);
        if (issue == null) {
            return;
        }

        synchronized (issue) {
            if (issue.status != IssueStatus.ASSIGNED || issue.assignedAgentId == null) {
                return;
            }

            Agent agent = agentsById.get(issue.assignedAgentId);
            if (agent == null) {
                return;
            }

            synchronized (agent) {
                if (issue.status != IssueStatus.ASSIGNED) {
                    return;
                }

                issue.status = IssueStatus.RESOLVED;
                issue.resolution = resolution;

                decrement(agent.totalOpenIssues);
                decrement(agent.getOpenByTypeCounter(issue.issueType));
                agent.getResolvedByTypeCounter(issue.issueType).incrementAndGet();
                agent.resolvedHistory.add(issue.issueId);

            }
        }

    }

    /**
     * returns a list of issueId's assigned to agent with which are in resolved
     * state now
     * - return an empty list in case agent doesn't exists or no issue has been
     * resolved yet by the agent
     */
    public List<String> getAgentHistory(String agentId) {
        Agent agent = agentsById.get(agentId);
        if (agent == null) {
            return new ArrayList<>();
        }

        synchronized (agent) {
            return new ArrayList<>(agent.resolvedHistory);
        }
    }

    private boolean isValidIssueType(int issueType) {
        return issueType >= 0 && issueType < issueTypes.size();
    }

    private void decrement(AtomicInteger counter) {
        while (true) {
            int current = counter.get();
            if (current == 0) {
                return;
            }

            if (counter.compareAndSet(current, current - 1)) {
                return;
            }
        }
    }

    private enum IssueStatus {
        OPEN,
        ASSIGNED,
        RESOLVED
    }

    private static class Issue {
        private final String issueId;
        private final String orderId;
        private final int issueType;
        private final String description;

        private volatile IssueStatus status;
        private volatile String assignedAgentId;
        private volatile String resolution;

        public Issue(String issueId, String orderId, int issueType, String description) {
            this.issueId = issueId;
            this.orderId = orderId;
            this.issueType = issueType;
            this.description = description;
            this.status = IssueStatus.OPEN;
            this.assignedAgentId = null;
            this.resolution = null;
        }
    }

    private static class Agent {
        private final String agentId;
        private final Set<Integer> expertise;

        private AtomicInteger totalOpenIssues;
        private ConcurrentHashMap<Integer, AtomicInteger> openByType;
        private ConcurrentHashMap<Integer, AtomicInteger> resolvedByType;

        private List<String> resolvedHistory;

        public Agent(String agentId, Set<Integer> expertise) {
            this.agentId = agentId;
            this.expertise = new HashSet<>(expertise);
            this.totalOpenIssues = new AtomicInteger(0);
            this.openByType = new ConcurrentHashMap<>();
            this.resolvedByType = new ConcurrentHashMap<>();
            this.resolvedHistory = new ArrayList<>();

            for (Integer type : expertise) {
                this.openByType.put(type, new AtomicInteger(0));
                this.resolvedByType.put(type, new AtomicInteger(0));
            }
        }

        private boolean hasExpertise(int issueType) {
            return expertise.contains(issueType);
        }

        private int getTotalOpenIssues() {
            return totalOpenIssues.get();
        }

        private int getResolvedIssuesForType(int issueType) {
            return resolvedByType.get(issueType).get();
        }

        private int getOpenIssuesForType(int issueType) {
            return openByType.get(issueType).get();
        }

        private AtomicInteger getOpenByTypeCounter(int issueType) {
            return openByType.computeIfAbsent(issueType, k -> new AtomicInteger(0));
        }

        private AtomicInteger getResolvedByTypeCounter(int issueType) {
            return resolvedByType.computeIfAbsent(issueType, k -> new AtomicInteger(0));
        }
    }

    private interface AssignmentStrategy {
        Agent selectAgent(Issue issue, List<Agent> eligibleAgents);
    }

    private static class LowestOpenIssuesStrategy implements AssignmentStrategy {

        @Override
        public Solution.Agent selectAgent(Solution.Issue issue, List<Solution.Agent> eligibleAgents) {
            Agent best = null;

            int bestValue = Integer.MAX_VALUE;

            for (Agent agent : eligibleAgents) {
                int total = agent.getTotalOpenIssues();
                if (best == null || total < bestValue) {
                    best = agent;
                    bestValue = total;
                }
            }

            return best;
        }

    }

    private static class MostExperiencedStrategy implements AssignmentStrategy {

        @Override
        public Solution.Agent selectAgent(Solution.Issue issue, List<Solution.Agent> eligibleAgents) {

            Agent best = null;

            int bestValue = Integer.MIN_VALUE;

            for (Agent agent : eligibleAgents) {
                int resolved = agent.getResolvedIssuesForType(issue.issueType);
                if (best == null || resolved < bestValue) {
                    best = agent;
                    bestValue = resolved;
                }
            }

            return best;
        }

    }

    private static class LeastOpenIssueForTypeStrategy implements AssignmentStrategy {

        @Override
        public Solution.Agent selectAgent(Solution.Issue issue, List<Solution.Agent> eligibleAgents) {
            Agent best = null;

            int bestValue = Integer.MIN_VALUE;

            for (Agent agent : eligibleAgents) {
                int open = agent.getOpenIssuesForType(issue.issueType);
                if (best == null || open < bestValue) {
                    best = agent;
                    bestValue = open;
                }
            }

            return best;
        }

    }
}