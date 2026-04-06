## Entities

- Issue: issueId, orderId, issueType, description, status, assignedAgentId, resolution

- Agent: agentId, expertise, totalOpenIssues, openByType, resolvedByType, resolvedHistory

- AssignmentStrategy -> interface -> Strategy Design Pattern
  - LowestOpenIssuesStrategy
  - MostExperiencedStrategy
  - LeastOpenIssueForTypeStrategy

- Solution -> Facade Design Pattern