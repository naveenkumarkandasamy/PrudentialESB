package org.pru.esb.beans;

import java.util.HashMap;
import java.util.Map;

public class PolicyMapper {
	public Map<String, Object> getPolicy(Policy claim) {
		Map<String, Object> answer = new HashMap<String, Object>();
		answer.put("applicationNumber", claim.getApplicationNumber());
		answer.put("agentCode", claim.getAgentcode());
		answer.put("branch", claim.getBranch());
		answer.put("sumassured", claim.getSumassured());
		answer.put("premium", claim.getPremium());
		answer.put("capturedDate", claim.getCapturedDate());
		answer.put("policyDate", claim.getPolicydate());
		return answer;
	}
}
