package le.cache.bis.services.impl.data;

public class Membership {

    private Long memberId;
    
    private String memberNumber;
    
    private String superFundGenEmprId;

    private Employee employee;
    
    public Membership() {
    }
    
    public Membership(Long memberId, String memberNumber, String superFundGenEmprId) {
        super();
        this.memberId = memberId;
        this.memberNumber = memberNumber;
        this.superFundGenEmprId = superFundGenEmprId;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getSuperFundGenEmprId() {
        return superFundGenEmprId;
    }

    public void setSuperFundGenEmprId(String superFundGenEmprId) {
        this.superFundGenEmprId = superFundGenEmprId;
    }          

    public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        if(this.getMemberNumber() != null) {
            b.append("MemberNumber " + memberNumber);
        }

        if(this.getSuperFundGenEmprId() != null) {
            b.append("TFN " + this.getSuperFundGenEmprId());
        }
        return b.toString();
    }
    
}
